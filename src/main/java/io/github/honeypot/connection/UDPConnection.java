package io.github.honeypot.connection;

import io.github.honeypot.service.Service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;

/**
 * Created by jackson on 10/6/16.
 */
public class UDPConnection extends Connection {
    public static final int PACKET_SIZE = 65535;

    DatagramSocket socket;
    InetAddress remoteAddr;

    byte[] inputBuffer = new byte[PACKET_SIZE];
    ByteBuffer buffer;

    public UDPConnection(Service service, DatagramSocket socket, ByteBuffer initBuffer) {
        this.socket = socket;
        super.service = service;
        this.remoteAddr = socket.getInetAddress();

        this.buffer = initBuffer.duplicate();
    }

    @Override
    public void write(String input) throws IOException{
        byte[] bytes = input.getBytes();
        DatagramPacket outPacket = new DatagramPacket(bytes, bytes.length);

        socket.send(outPacket);
    }

    @Override
    public String read() throws IOException, TimeoutException {

        DatagramPacket input = new DatagramPacket(inputBuffer, PACKET_SIZE);
        socket.receive(input);

        Arrays.fill(inputBuffer, (byte)0);

        return new String(input.getData());
    }

    @Override
    public void close() throws IOException {
        this.socket.close();
    }
}
