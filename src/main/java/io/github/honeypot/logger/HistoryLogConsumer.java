package io.github.honeypot.logger;

import io.github.honeypot.exception.HoneypotRuntimeException;
import org.json.JSONArray;

import java.util.LinkedList;
import java.util.Observable;

/**
 * Created by jackson on 11/29/16.
 */
public class HistoryLogConsumer extends LogConsumer {
    private int listLength;
    private final JSONArray recentEvents = new JSONArray();

    public HistoryLogConsumer(int length) {
        this.listLength = length;
    }

    @Override
    public void update(Observable subject, Object data) {
        if (data instanceof Log) {
            Log log = (Log) data;
            if (super.shouldLog(log.getType())) {
                synchronized (recentEvents) {

                    recentEvents.put(log.toJson());
                    while (recentEvents.length() > listLength) {
                        recentEvents.remove(0);
                    }

                }
            }
        } else {
            throw new HoneypotRuntimeException("Observed something that is not a log");
        }
    }

    public JSONArray getRecentEvents() {
        return this.recentEvents;
    }

    @Override
    public JSONArray toJson() {
        return recentEvents;
    }
}
