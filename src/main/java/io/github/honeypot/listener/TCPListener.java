package io.github.honeypot.listener;

import io.github.honeypot.connection.TCPConnection;
import io.github.honeypot.service.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by jackson on 10/2/16.
 */
public class TCPListener implements Runnable {
    private static final int POOLSIZE = 100;
    private ExecutorService threadPool = Executors.newFixedThreadPool(POOLSIZE);

    ServerSocketChannel server;
    Selector selector = Selector.open();

    Map<Integer, Service> portMapping;

    public TCPListener() throws IOException {
        portMapping = new HashMap<>();

        server = ServerSocketChannel.open();
        server.configureBlocking(false);

        int ops = server.validOps();

        //register the selector with the serverchannel
        server.register(selector, ops, null);
    }

    public void addService(int port, Service serv) throws IOException {
        portMapping.put(port, serv);
        System.out.println("GETTING" + port + " " + portMapping.get(port));
        server.socket().bind(new InetSocketAddress(port));
    }

    @Override
    public void run() {
        try {
            while (selector.isOpen()) {
                selector.select();
                Set readyKeys = selector.selectedKeys();
                Iterator iterator = readyKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = (SelectionKey) iterator.next();
                    if (key.isAcceptable()) {
                        SocketChannel client = server.accept();
                        Socket clientSocket = client.socket();

                        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                        int port = clientSocket.getPort();
                        int localPort = clientSocket.getLocalPort();

                        System.out.println("[*] tcp connection from port: " + port + " to: " + localPort);


                        threadPool.execute(new TCPConnection(portMapping.get(localPort), clientSocket, in, out));
                    }
                }


            }
        } catch (IOException e) {

        }
    }

}
