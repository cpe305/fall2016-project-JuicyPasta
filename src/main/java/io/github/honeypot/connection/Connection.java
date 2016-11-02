package io.github.honeypot.connection;

import org.apache.commons.lang3.StringUtils;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

import io.github.honeypot.exception.HoneypotRuntimeException;
import io.github.honeypot.logger.EventLogger;
import io.github.honeypot.logger.Log;
import io.github.honeypot.service.Service;

/**
 * Created by jackson on 10/2/16.
 */
public abstract class Connection implements Runnable, Closeable {
    Service service;
    Log log;
    boolean hasTalked;

    public Connection(Log log) {
        this.log = log;
        this.hasTalked = false;
    }

    public abstract String read() throws IOException;

    public abstract void write(String data) throws IOException;

    public abstract boolean isAlive();

    @Override
    public void run() {
        try {
            while (service.isAlive() && this.isAlive()) {
                if (!hasTalked) {
                    hasTalked = true;

                    String preamble = service.getPreamble();
                    if (!StringUtils.isEmpty(preamble)) {
                        log.addOutgoingMessage(preamble);
                        write(preamble);
                    }
                }

                String contents = read();

                if (!StringUtils.isEmpty(contents)) {

                    log.addIncomingMessage(contents);
                    String output = service.feed(contents);

                    if (!StringUtils.isEmpty(output)) {
                        log.addOutgoingMessage(output);
                    }
                    write(output);
                }
            }

            EventLogger.log(log);

            close();
        } catch (IOException e) {
            throw new HoneypotRuntimeException(e);
        }

    }
}
