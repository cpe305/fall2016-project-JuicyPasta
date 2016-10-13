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

    @Override
    public final void contextInitialized(ServletContextEvent context) {
        //UDPListener udpListener = new UDPListener();
        //udpListener.addService(4000, new ServiceFactory("IRCService"));

        //Thread udpListenerThread = new Thread(udpListener);
        //udpListenerThread.start();

        try {

            TCPListener tcpListener = new TCPListener();
            tcpListener.addService(6667, new ServiceFactory("IRCService"));

            Thread tcpListenerThread = new Thread(tcpListener);
            tcpListenerThread.start();

        } catch (Exception e) {

        }
    }

    @Override
    public final void contextDestroyed(ServletContextEvent context) {

    }
}
