package io.github.honeypot;

import io.github.honeypot.exception.HoneypotException;
import io.github.honeypot.exception.HoneypotRuntimeException;
import io.github.honeypot.listener.SSHListener;
import io.github.honeypot.listener.TCPListener;
import io.github.honeypot.logger.*;
import io.github.honeypot.service.HTTPService;
import io.github.honeypot.service.IRCService;
import io.github.honeypot.service.SMTPService;
import io.github.honeypot.servlet.HistoryServlet;
import io.github.honeypot.servlet.RankServlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;

public class App implements ServletContextListener {
    private TCPListener tcpListener;
    private Thread tcpListenerThread;

    private SSHListener sshListener;
    private Thread sshListenerThread;

    public App() throws HoneypotException {
        try {
            HistoryServlet.loadStaticContext();
            RankServlet.loadStaticContext();

            /**
             * Initialize Listeners
             */
            tcpListener = new TCPListener();
            tcpListener.addService(16667, IRCService::new, ()->new Log(LogType.IRC_EVENT));
            tcpListener.addService(10025, SMTPService::new, ()->new Log(LogType.SMTP_EVENT));
            tcpListener.addService(12525, SMTPService::new, ()->new Log(LogType.SMTP_EVENT));
            tcpListener.addService(10080, HTTPService::new, ()->new Log(LogType.HTTP_EVENT));
            sshListener = new SSHListener(10022, ()->new Log(LogType.SSH_EVENT));

            tcpListener.addObserver(LogTap.getInstance());
            sshListener.addObserver(LogTap.getInstance());

            /**
             * Prepare threads
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
            LogTap.getInstance().reloadLogs();
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
}
