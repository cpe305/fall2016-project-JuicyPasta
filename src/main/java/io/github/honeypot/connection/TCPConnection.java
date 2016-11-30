package io.github.honeypot.connection;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import io.github.honeypot.logger.Log;
import io.github.honeypot.service.Service;

/**
 * Created by jackson on 10/5/16.
 */
public class TCPConnection extends Connection {
    Socket socket;
    BufferedReader in;
    PrintWriter out;
    boolean isAlive = true;

    public TCPConnection(Service service, Socket socket, BufferedReader in, PrintWriter out, Log log, Consumer<Object> notifyObservers) {
        super(log, notifyObservers);

        this.socket = socket;
        this.in = in;
        this.out = out;

        super.service = service;

        String preamble = service.getPreamble();
    }

    @Override
    public void write(String input) {
        if (!StringUtils.isEmpty(input)) {
            out.println(input);
        }
    }

    @Override
    public String read() throws IOException {
        String line = in.readLine();
        if (line == null) {
            this.isAlive = false;
        }
        return line;
    }

    @Override
    public boolean isAlive() {
        return isAlive;
    }

    @Override
    public void run() {
        super.run();
    }

    @Override
    public void close() throws IOException {
        this.socket.shutdownInput();
        this.socket.shutdownOutput();
        this.in.close();
        this.out.close();
        this.socket.close();
    }
}
