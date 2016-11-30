package io.github.honeypot.logger;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.TreeSet;

/**
 * Created by jackson on 11/29/16.
 */
public abstract class LogConsumer implements Observer {
    TreeSet<LogType> acceptableTypes;
    String name;

    public LogConsumer() {
        this.acceptableTypes = new TreeSet<>();
    }
    public void setName(String name) {
        this.name = name;
    }
    public void addAcceptableType(LogType type) {
        acceptableTypes.add(type);
    }
    public void removeAcceptableType(LogType type) {
        acceptableTypes.remove(type);
    }
    public boolean shouldLog(LogType type) {
        return acceptableTypes.contains(type);
    }
    public abstract JSONArray toJson();
    public abstract ConsumerType getType();
}
