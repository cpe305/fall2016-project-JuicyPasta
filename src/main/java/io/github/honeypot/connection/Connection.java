package io.github.honeypot.connection;

import io.github.honeypot.service.Service;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by jackson on 10/2/16.
 */
public abstract class Connection implements Runnable {
    Service service;

    public abstract String read() throws TimeoutException, IOException;
    public abstract void write(String data) throws IOException;

    public abstract void close() throws IOException;

    public void run() {
        try {
            while(service.isAlive()) {
                System.out.println("trying to read");
                String contents = read();
                if (!StringUtils.isEmpty(contents)) {
                    System.out.println("[*] Contents: " + contents);
                    write(service.feed(contents));
                }
            }

        } catch (TimeoutException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
