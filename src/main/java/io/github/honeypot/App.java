package io.github.honeypot;

import io.github.honeypot.exception.HoneypotException;
import io.github.honeypot.exception.HoneypotRuntimeException;
import io.github.honeypot.listener.PersistenceListener;
import io.github.honeypot.listener.SSHListener;
import io.github.honeypot.listener.TCPListener;
import io.github.honeypot.logger.*;
import io.github.honeypot.service.HTTPService;
import io.github.honeypot.service.IRCService;
import io.github.honeypot.service.SMTPService;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import static io.github.honeypot.constants.Constants.LOG_HISTORY;

public class App implements ServletContextListener {
    private TCPListener tcpListener;
    private Thread tcpListenerThread;

    private SSHListener sshListener;
    private Thread sshListenerThread;

    private HistoryLogConsumer allConsumer;
    private PersistenceListener persistenceObservable;

    public App() throws HoneypotException {
        try {

            /**
             * Initialize LogConsumers
             */
            PersistenceLogReader persistentConsumer = new PersistenceLogReader();
            persistentConsumer.setAcceptAll();

            allConsumer = new HistoryLogConsumer(LOG_HISTORY * 10);
            allConsumer.setAcceptAll();

            LogConsumer httpConsumer = new HistoryLogConsumer(LOG_HISTORY);
            httpConsumer.addAcceptableType(LogType.HTTP_EVENT);

            LogConsumer smtpConsumer = new HistoryLogConsumer(LOG_HISTORY);
            smtpConsumer.addAcceptableType(LogType.SMTP_EVENT);

            LogConsumer ircConsumer = new HistoryLogConsumer(LOG_HISTORY);
            ircConsumer.addAcceptableType(LogType.IRC_EVENT);

            LogConsumer sshConsumer = new HistoryLogConsumer(LOG_HISTORY);
            sshConsumer.addAcceptableType(LogType.SSH_EVENT);

            LogConsumer topCountries = new RankedAttributeConsumer("country");
            topCountries.setAcceptAll();

            /**
             * Register LogConsumers
             */
            ConsumerRegistry registry = ConsumerRegistry.getInstance();
            registry.addConsumer("ALL", allConsumer);
            registry.addConsumer("HTTP", httpConsumer);
            registry.addConsumer("SMTP", smtpConsumer);
            registry.addConsumer("SSH", sshConsumer);
            registry.addConsumer("IRC", ircConsumer);
            registry.addConsumer("TOP_COUNTRIES", topCountries);

            /**
             * Initialize Listeners
             */
            persistenceObservable = new PersistenceListener();

            tcpListener = new TCPListener();
            tcpListener.addService(16667, IRCService::new, ()->new Log(LogType.IRC_EVENT));
            tcpListener.addService(10025, SMTPService::new, ()->new Log(LogType.SMTP_EVENT));
            tcpListener.addService(12525, SMTPService::new, ()->new Log(LogType.SMTP_EVENT));
            tcpListener.addService(10080, HTTPService::new, ()->new Log(LogType.HTTP_EVENT));

            sshListener = new SSHListener(10022, ()->new Log(LogType.SSH_EVENT));

            /**
             * Attach LogConsumers to Listeners
             */
            // loads everything from a log file
            addObservers(persistenceObservable, topCountries, allConsumer, httpConsumer, smtpConsumer, ircConsumer, sshConsumer);
            addObservers(sshListener, topCountries, persistentConsumer, allConsumer, sshConsumer);
            addObservers(tcpListener, topCountries, persistentConsumer, allConsumer, httpConsumer, smtpConsumer, ircConsumer);

            /**
             * Start Listeners
             */
            tcpListenerThread = new Thread(tcpListener);
            sshListenerThread = new Thread(sshListener);

        } catch (IOException e) {
            throw new HoneypotException(e);
        }
    }

    @Override
    public final void contextInitialized(ServletContextEvent context) {

        // Load old log files
        try {
            persistenceObservable.reloadLogs();
        } catch (IOException e) {
            throw new HoneypotRuntimeException(e);
        }

        tcpListenerThread.start();
        sshListenerThread.start();
    }

    @Override
    public final void contextDestroyed(ServletContextEvent context) {

        try {
            tcpListener.close();
        } catch (Exception e) {
            throw new HoneypotRuntimeException(e);
        }
        try {
            sshListener.close();
        } catch (Exception e) {
            throw new HoneypotRuntimeException(e);
        }
    }

    public static void addObservers(Observable observable, Observer... observers) {
        for (Observer observer : observers)
            observable.addObserver(observer);
    }
}
