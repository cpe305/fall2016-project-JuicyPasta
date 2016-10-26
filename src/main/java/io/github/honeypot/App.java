package io.github.honeypot;

import io.github.honeypot.listener.TCPListener;
import io.github.honeypot.service.ServiceFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;

/**
 * Hello world!
 *
 */
public class App implements ServletContextListener {
    private TCPListener tcpListener;
    private Thread tcpListenerThread;

    public App() throws Exception {
        //UDPListener udpListener = new UDPListener();
        //udpListener.addService(4000, new ServiceFactory("IRCService"));
        //Thread udpListenerThread = new Thread(udpListener);

        tcpListener = new TCPListener();
        tcpListener.addService(6667, new ServiceFactory("IRCService"));
        tcpListener.addService(6668, new ServiceFactory("SMTPService"));
        tcpListener.addService(6677, new ServiceFactory("SSHService"));
        tcpListenerThread = new Thread(tcpListener);
    }

    @Override
    public final void contextInitialized(ServletContextEvent context) {
        tcpListenerThread.start();
    }

    @Override
    public final void contextDestroyed(ServletContextEvent context) {

    }
}
