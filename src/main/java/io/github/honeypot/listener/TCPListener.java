package io.github.honeypot.listener;

import org.apache.sshd.common.Factory;

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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.github.honeypot.connection.TCPConnection;
import io.github.honeypot.exception.HoneypotRuntimeException;
import io.github.honeypot.logger.Log;
import io.github.honeypot.service.Service;

/**
 * Created by jackson on 10/2/16.
 */
public class TCPListener implements Runnable, AutoCloseable {
    private static final int POOLSIZE = 100;
    private ExecutorService threadPool = Executors.newFixedThreadPool(POOLSIZE);

    private Selector selector = Selector.open();
    private ServerSocketChannel serverChannel;

    private Map<Integer, Factory<Service>> portMapping;

    public TCPListener() throws IOException {
        portMapping = new HashMap<>();
    }

    public void addService(int port, Factory<Service> serv) throws IOException {
        portMapping.put(port, serv);

        serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.socket().bind(new InetSocketAddress(port));
        int ops = serverChannel.validOps();

        serverChannel.register(selector, ops);
    }

    @Override
    public void close() throws IOException {
        selector.close();
        serverChannel.close();
    }

    @Override
    public void run() {
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
                        Service mockService = portMapping.get(localPort).create();
                        Log log = new Log(mockService.serviceName, clientSocket.getInetAddress());

                        threadPool.execute(new TCPConnection(mockService, clientSocket, in, out, log));
                    }
                }

            }
        } catch (IOException e) {
            throw new HoneypotRuntimeException(e);
        }
    }

}
