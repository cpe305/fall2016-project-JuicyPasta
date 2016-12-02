package io.github.honeypot;

import io.github.honeypot.exception.HoneypotException;
import io.github.honeypot.exception.HoneypotRuntimeException;
import io.github.honeypot.listener.PersistanceListener;
import io.github.honeypot.listener.SSHListener;
import io.github.honeypot.listener.TCPListener;
import io.github.honeypot.logger.*;
import io.github.honeypot.service.HTTPService;
import io.github.honeypot.service.IRCService;
import io.github.honeypot.service.SMTPService;
import org.mockito.internal.debugging.LoggingListener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class App implements ServletContextListener {
    public static final int LOG_HISTORY = 1000;
    private TCPListener tcpListener;
    private Thread tcpListenerThread;

    private SSHListener sshListener;
    private Thread sshListenerThread;

    HistoryLogConsumer allConsumer;
    PersistanceListener persistenceObservable;

    public App() throws HoneypotException {
        try {

            /**
             * Initialize LogConsumers
             */
            PersistenceLogConsumer persistentConsumer = null;
            try {
                persistentConsumer = new PersistenceLogConsumer("Persistent consumer");
            } catch (IOException e) {
                throw new HoneypotException(e);
            }
            persistentConsumer.setAcceptAll();

            allConsumer = new HistoryLogConsumer("ALL", LOG_HISTORY * 10);
            allConsumer.setAcceptAll();

            LogConsumer httpConsumer = new HistoryLogConsumer("HTTP", LOG_HISTORY);
            httpConsumer.addAcceptableType(LogType.HTTP_EVENT);

            LogConsumer smtpConsumer = new HistoryLogConsumer("SMTP", LOG_HISTORY);
            smtpConsumer.addAcceptableType(LogType.SMTP_EVENT);

            LogConsumer ircConsumer = new HistoryLogConsumer("IRC", LOG_HISTORY);

            ircConsumer.addAcceptableType(LogType.IRC_EVENT);

            LogConsumer sshConsumer = new HistoryLogConsumer("SSH", LOG_HISTORY);
            sshConsumer.addAcceptableType(LogType.SSH_EVENT);

            LogConsumer topCountries = new RankedAttributeConsumer("TOP_COUNTRIES", "country");
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
            persistenceObservable = new PersistanceListener();

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
            PersistenceLogConsumer.reloadLogs(persistenceObservable::makeChange);
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
