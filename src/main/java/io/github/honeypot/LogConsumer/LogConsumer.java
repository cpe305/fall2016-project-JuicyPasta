package io.github.honeypot.LogConsumer;

import io.github.honeypot.logger.LogType;
import org.json.JSONArray;

import java.util.HashSet;
import java.util.Observer;
import java.util.TreeSet;

/**
 * Created by jackson on 11/29/16.
 */
public abstract class LogConsumer implements Observer {
    private final HashSet<LogType> acceptableTypes = new HashSet<>();
    private boolean acceptAll;

    LogConsumer() {
        this.acceptAll = false;
    }

    public void setAcceptAll() {
        this.acceptAll = true;
    }

    public void addAcceptableType(LogType type) {
        acceptableTypes.add(type);
    }

    boolean shouldLog(LogType type) {
        return acceptAll || acceptableTypes.contains(type);
    }
}
