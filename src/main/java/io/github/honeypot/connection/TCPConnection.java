package io.github.honeypot.connection;

import io.github.honeypot.service.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.TimeoutException;

/**
 * Created by jackson on 10/5/16.
 */
public class TCPConnection extends Connection {
    Socket socket;
    BufferedReader in;
    PrintWriter out;

    public TCPConnection(Service service, Socket socket, BufferedReader in, PrintWriter out) {
        this.socket = socket;
        this.in = in;
        this.out = out;

        super.service = service;
    }

    @Override
    public void write(String input) {
        out.write(input);
    }

    @Override
    public String read() throws IOException, TimeoutException{
        String contents = "";

        /*
        String next = null;
        do {
            next = in.readLine();
            contents += next;
        } while(next != null);

        return contents;
        */
        return in.readLine();
    }

    @Override
    public void close() throws IOException {
        this.in.close();
        this.out.close();
        this.socket.close();
    }
}
