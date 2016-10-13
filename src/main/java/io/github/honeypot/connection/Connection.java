package io.github.honeypot.connection;

import io.github.honeypot.logger.EventLogger;
import io.github.honeypot.service.Service;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by jackson on 10/2/16.
 */
public abstract class Connection implements Runnable {
    Service service;
    EventLogger logger;
    boolean hasTalked;

    public Connection(EventLogger logger) {
        this.logger = logger;
        this.hasTalked = false;
    }

    public abstract String read() throws TimeoutException, IOException;
    public abstract void write(String data) throws IOException;
    public abstract void close() throws IOException;
    public abstract boolean isAlive();

    public void run() {
        try {
            while(service.isAlive() && this.isAlive()) {
                if (!hasTalked) {
                    hasTalked = true;

                    String preamble = service.getPreamble();
                    if (!StringUtils.isEmpty(preamble)) {
                        logger.addOutgoingMessage(preamble);
                        write(preamble);
                    }
                }

                String contents = read();

                if (!StringUtils.isEmpty(contents)) {

                    logger.addIncomingMessage(contents);
                    String output = service.feed(contents);

                    if (!StringUtils.isEmpty(output)) {
                        logger.addOutgoingMessage(output);
                    }
                    write(output);
                }
            }

            close();
        } catch (TimeoutException | IOException e) {
            throw new RuntimeException(e);
        }

    }
}
