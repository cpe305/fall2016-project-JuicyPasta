package io.github.honeypot.logger;

import java.io.File;
import java.io.PrintWriter;

import io.github.honeypot.exception.HoneypotException;
import io.github.honeypot.exception.HoneypotRuntimeException;

/**
 * Created by jackson on 10/10/16.
 */
public class EventLogger {
    private static PrintWriter fileWriter;

    static {
        try {
            fileWriter = new PrintWriter(new File("logs/log.txt"));
        } catch (Exception e) {
            throw new HoneypotRuntimeException(e);
        }
    }

    public static synchronized void log(Log log) {
        log.end();
        String obj = log.toString();

        fileWriter.println(obj);
        fileWriter.flush();
    }
}
