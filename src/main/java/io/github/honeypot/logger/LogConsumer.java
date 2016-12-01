package io.github.honeypot.logger;

import org.json.JSONArray;

import java.util.Observer;
import java.util.TreeSet;

/**
 * Created by jackson on 11/29/16.
 */
public abstract class LogConsumer implements Observer {
    private TreeSet<LogType> acceptableTypes;
    private boolean acceptAll;
    private String name;

    LogConsumer(String name) {
        this.acceptableTypes = new TreeSet<>();
        this.name = name;
        this.acceptAll = false;
    }

    public String getName() {
        return this.name;
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

    public abstract JSONArray toJson();

    public abstract ConsumerType getType();
}
