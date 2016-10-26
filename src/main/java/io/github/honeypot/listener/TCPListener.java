package io.github.honeypot.listener;

import io.github.honeypot.connection.TCPConnection;
import io.github.honeypot.logger.EventLogger;
import io.github.honeypot.logger.Log;
import io.github.honeypot.service.Service;
import io.github.honeypot.service.ServiceFactory;

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

    private Selector selector = Selector.open();

    private Map<Integer, ServiceFactory> portMapping;

    public TCPListener() throws IOException {
        portMapping = new HashMap<>();
    }

    public void addService(int port, ServiceFactory serv) throws IOException {
        portMapping.put(port, serv);

        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.socket().bind(new InetSocketAddress(port));
        int ops = serverChannel.validOps();

        serverChannel.register(selector, ops);
    }


    @Override
    public void run() {
        System.out.println("TCPListener listening on...");
        portMapping.forEach((port, fact) -> System.out.println("\t" + port + " " + fact.type));
        System.out.println();

        try {
            while (selector.isOpen()) {
                selector.select();
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    if (key.isAcceptable()) {

                        SocketChannel client = ((ServerSocketChannel) key.channel()).accept();

                        if (client == null)
                            continue;

                        Socket clientSocket = client.socket();

                        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                        int port = clientSocket.getPort();
                        int localPort = clientSocket.getLocalPort();

                        System.out.println("[*] tcp incoming " + port + " -> " + localPort);
                        Service mockService = portMapping.get(localPort).getInstance();
                        Log log = new Log(mockService.serviceName, clientSocket.getInetAddress());

                        threadPool.execute(new TCPConnection(mockService, clientSocket, in, out, log));
                    }
                }


            }
        } catch (IOException e) {

        }
    }

}
