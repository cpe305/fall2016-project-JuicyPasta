package io.github.honeypot.logger;

import org.json.JSONObject;

import java.io.File;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by jackson on 10/10/16.
 */
public class EventLogger {
    private static PrintWriter fileWriter;

    static {
        try {
            fileWriter = new PrintWriter(new File("logs/logfile"));
        } catch (Exception ignored) {}
        PrintWriter fileWriter;
    }

    public static synchronized void log(Log log) {
        String obj = log.toString();

        fileWriter.println(obj);
    }
}
