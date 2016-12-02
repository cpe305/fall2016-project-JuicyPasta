package io.github.honeypot.listener;

import io.github.honeypot.connection.TCPConnection;
import io.github.honeypot.exception.HoneypotRuntimeException;
import io.github.honeypot.logger.Log;
import io.github.honeypot.logger.LogFactory;
import io.github.honeypot.service.Service;
import io.github.honeypot.service.ServiceFactory;
import org.apache.sshd.common.Factory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by jackson on 10/2/16.
 */
public class TCPListener extends Listener {
    private static final int POOLSIZE = 10;
    private ExecutorService threadPool = Executors.newFixedThreadPool(POOLSIZE);

    private Selector selector = Selector.open();
    private ServerSocketChannel serverChannel;
    private boolean isRunning = true;

    private class FactoryPack {
        ServiceFactory serviceFactory;
        LogFactory logFactory;
        public FactoryPack(ServiceFactory serviceFactory, LogFactory logFactory) {
            this.serviceFactory = serviceFactory;
            this.logFactory = logFactory;
        }
    }

    private Map<Integer, FactoryPack> portMapping;

    public TCPListener() throws IOException {
        portMapping = new HashMap<>();
    }

    public void addService(int port, ServiceFactory serviceFactory, LogFactory logFactory) throws IOException {
        portMapping.put(port, new FactoryPack(serviceFactory, logFactory));

        serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.socket().bind(new InetSocketAddress(port));
        serverChannel.register(selector, serverChannel.validOps());
    }

    @Override
    public void close() throws IOException {
        isRunning = false;
        threadPool.shutdown();
        serverChannel.close();
        selector.close();
    }

    @Override
    public void run() {
        try {
            while (isRunning && selector.isOpen()) {
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

                        FactoryPack factories = portMapping.get(localPort);

                        Log log = factories.logFactory.create();
                        Service service = factories.serviceFactory.create();
                        service.attachLog(log);

                        log.setInetAddress(clientSocket.getInetAddress());
                        log.setLocalPort(localPort);
                        log.setRemotePort(port);

                        threadPool.execute(new TCPConnection(service, clientSocket, in, out, log, this::triggerObservers));
                    }
                }

            }
        } catch (IOException | ClosedSelectorException e) {
            throw new HoneypotRuntimeException(e);
        }
    }

    public synchronized void triggerObservers(Object o) {
        setChanged();
        notifyObservers(o);
    }
}
