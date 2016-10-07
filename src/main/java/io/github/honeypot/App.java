package io.github.honeypot;

import io.github.honeypot.listener.TCPListener;
import io.github.honeypot.listener.UDPListener;
import io.github.honeypot.service.Service;

import java.io.IOException;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) throws IOException {


        UDPListener udpListener = new UDPListener();
        udpListener.addService(4000, new Service());

        Thread udpListenerThread = new Thread(udpListener);
        udpListenerThread.start();


        TCPListener tcpListener = new TCPListener();
        tcpListener.addService(8000, new Service());

        Thread tcpListenerThread = new Thread(tcpListener);
        tcpListenerThread.start();

    }

}
