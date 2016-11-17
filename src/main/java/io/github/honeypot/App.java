package io.github.honeypot;

import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import io.github.honeypot.exception.HoneypotException;
import io.github.honeypot.exception.HoneypotRuntimeException;
import io.github.honeypot.listener.SSHListener;
import io.github.honeypot.listener.TCPListener;
import io.github.honeypot.logger.EventDatabase;
import io.github.honeypot.service.HTTPService;
import io.github.honeypot.service.IRCService;
import io.github.honeypot.service.SMTPService;

public class App implements ServletContextListener {
    private TCPListener tcpListener;
    private SSHListener sshListener;
    private Thread tcpListenerThread;

    public App() throws HoneypotException {
        try {

            tcpListener = new TCPListener();
            tcpListener.addService(6667, IRCService::new);
            tcpListener.addService(25, SMTPService::new);
            tcpListener.addService(2525, SMTPService::new);
            tcpListener.addService(80, HTTPService::new);
            tcpListenerThread = new Thread(tcpListener);

            sshListener = new SSHListener(22);
        } catch (IOException e) {
            throw new HoneypotException(e);
        }
    }

    @Override
    public final void contextInitialized(ServletContextEvent context) {
        try {
            tcpListenerThread.start();
            sshListener.start();
        } catch (IOException e) {
            throw new HoneypotRuntimeException(e);
        }
    }

    @Override
    public final void contextDestroyed(ServletContextEvent context) {
        try {
            sshListener.close();
            tcpListener.close();
        } catch (IOException e) {
            throw new HoneypotRuntimeException(e);
        }

    }
}
