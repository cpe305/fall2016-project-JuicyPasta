package io.github.honeypot;

import java.io.*;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import io.github.honeypot.exception.HoneypotException;
import io.github.honeypot.exception.HoneypotRuntimeException;
import io.github.honeypot.listener.SSHListener;
import io.github.honeypot.listener.TCPListener;
import io.github.honeypot.logger.*;
import io.github.honeypot.service.HTTPService;
import io.github.honeypot.service.IRCService;
import io.github.honeypot.service.SMTPService;

public class App implements ServletContextListener {
    private TCPListener tcpListener;
    private Thread tcpListenerThread;

    private SSHListener sshListener;
    private Thread sshListenerThread;

    HistoryLogConsumer allConsumer;
    PersistanceObservable persistenceObservable;
    public App() throws HoneypotException {
        try {
            persistenceObservable = new PersistanceObservable();

            allConsumer = new HistoryLogConsumer(10000);
            allConsumer.addAcceptableType(LogType.HTTP_EVENT);
            allConsumer.addAcceptableType(LogType.SSH_EVENT);
            allConsumer.addAcceptableType(LogType.SMTP_EVENT);
            allConsumer.addAcceptableType(LogType.IRC_EVENT);
            allConsumer.setName("ALL");

            LogConsumer httpConsumer = new HistoryLogConsumer(1000);
            httpConsumer.addAcceptableType(LogType.HTTP_EVENT);
            httpConsumer.setName("HTTP");

            LogConsumer smtpConsumer = new HistoryLogConsumer(1000);
            smtpConsumer.addAcceptableType(LogType.SMTP_EVENT);
            smtpConsumer.setName("SMTP");

            LogConsumer ircConsumer = new HistoryLogConsumer(1000);
            ircConsumer.addAcceptableType(LogType.IRC_EVENT);
            ircConsumer.setName("IRC");

            LogConsumer sshConsumer = new HistoryLogConsumer(1000);
            sshConsumer.addAcceptableType(LogType.SSH_EVENT);
            sshConsumer.setName("SSH");

            persistenceObservable.addObserver(httpConsumer);
            persistenceObservable.addObserver(smtpConsumer);
            persistenceObservable.addObserver(ircConsumer);
            persistenceObservable.addObserver(sshConsumer);
            persistenceObservable.addObserver(allConsumer);

            ConsumerRegistry.addConsumer(allConsumer);
            ConsumerRegistry.addConsumer(httpConsumer);
            ConsumerRegistry.addConsumer(smtpConsumer);
            ConsumerRegistry.addConsumer(sshConsumer);
            ConsumerRegistry.addConsumer(ircConsumer);

            LogConsumer sshRankedByCountry = new RankedAttributeConsumer("country");
            LogConsumer sshRankedByCredentials = new RankedAttributeConsumer("credentials");

            tcpListener = new TCPListener();
            tcpListener.addService(16667, IRCService::new);
            tcpListener.addService(10025, SMTPService::new);
            tcpListener.addService(12525, SMTPService::new);
            tcpListener.addService(10080, HTTPService::new);

            tcpListener.addObserver(allConsumer);
            tcpListener.addObserver(httpConsumer);
            tcpListener.addObserver(smtpConsumer);
            tcpListener.addObserver(ircConsumer);

            sshListener = new SSHListener(10022);
            sshListener.addObserver(sshConsumer);
            sshListener.addObserver(allConsumer);
            sshListener.addObserver(sshRankedByCountry);
            sshListener.addObserver(sshRankedByCredentials);

            tcpListenerThread = new Thread(tcpListener);
            sshListenerThread = new Thread(sshListener);

        } catch (IOException e) {
            throw new HoneypotException(e);
        }
    }

    @Override
    public final void contextInitialized(ServletContextEvent context) {
        tcpListenerThread.start();
        sshListenerThread.start();

        try {
            FileInputStream fin = new FileInputStream("/var/log/honeypot/log.ser");
            ObjectInputStream ois = new ObjectInputStream(fin);
            List<Log> logs = (List<Log>) ois.readObject();

            for (Log log : logs) {
                System.out.println(log.toString());
                persistenceObservable.makeChange(log);
            }
        }catch (IOException|ClassNotFoundException e) {
            System.err.print(e);
        }


    }

    @Override
    public final void contextDestroyed(ServletContextEvent context) {

        try {
            List<Log> logs = allConsumer.recentEvents;
            FileOutputStream fout = new FileOutputStream("/var/log/honeypot/log.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(logs);

            tcpListener.close();
            sshListener.close();
        } catch (IOException e) {
            throw new HoneypotRuntimeException(e);
        }

    }
}
