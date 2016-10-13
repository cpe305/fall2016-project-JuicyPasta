package io.github.honeypot.listener;

import io.github.honeypot.connection.TCPConnection;
import io.github.honeypot.service.Service;
import io.github.honeypot.service.ServiceFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by jackson on 10/6/16.
 */
/*
public class UDPListener implements Runnable {
    public static final int PACKET_SIZE = 65535;
    private static final int POOLSIZE = 100;
    private ExecutorService threadPool = Executors.newFixedThreadPool(POOLSIZE);

    private DatagramChannel udpChannel;
    private Selector selector = Selector.open();

    Map<Integer, Service> portMapping;

    public UDPListener() throws IOException {
        portMapping = new HashMap<>();

        udpChannel = DatagramChannel.open();
        udpChannel.configureBlocking(false);

        int ops = udpChannel.validOps();

        udpChannel.register(selector, ops, new ClientRecord());
    }

    public void addService(int port, ServiceFactory serv) throws IOException {
        portMapping.put(port, serv);
        System.out.println("GETTING" + port + " " + portMapping.get(port));
        udpChannel.socket().bind(new InetSocketAddress(port));
    }

    @Override
    public void run() {
        try {
            while (selector.isOpen()) {
                selector.select(); //blocks
                Set<SelectionKey> readyKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = readyKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    if (key.isReadable()) {
                        DatagramChannel currentChannel = (DatagramChannel)key.channel();
                        ClientRecord clientRecord = (ClientRecord) key.attachment();
                        clientRecord.buffer.clear();

                        SocketAddress address = currentChannel.receive(clientRecord.buffer);

                        DatagramSocket clientSocket = new DatagramSocket();

                        clientSocket.connect(address);


                        if (clientRecord.clientAddress != null) {  // Did we receive something?
                            // Register write with the selector
                            key.interestOps(SelectionKey.OP_WRITE);

                            System.out.println(clientRecord.clientAddress);

                        }

                    }
                }


            }
        } catch (IOException e) {

        }

    }
    static class ClientRecord {
        public SocketAddress clientAddress;
        public ByteBuffer buffer = ByteBuffer.allocate(PACKET_SIZE);
    }
}
*/
