package io.github.honeypot.LogConsumer;

import io.github.honeypot.exception.HoneypotRuntimeException;
import io.github.honeypot.logger.Log;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Observable;

import static io.github.honeypot.constants.Constants.LOG_HISTORY;

/**
 * Created by jackson on 11/29/16.
 */
public class HistoryLogConsumer extends LogConsumer {
    private final JSONArray recentEvents = new JSONArray();
    private final Log[]logs = new Log[LOG_HISTORY];
    private int logCounter = 0;

    public HistoryLogConsumer() { }

    @Override
    public void update(Observable subject, Object data) {
        if (data instanceof Log) {
            Log log = (Log) data;
            if (super.shouldLog(log.getType())) {
                synchronized (recentEvents) {
                    synchronized (logs) {
                        logs[logCounter % LOG_HISTORY] = log;

                        JSONObject mapJson = log.toSmallJson();
                        mapJson.put("idx", logCounter);

                        recentEvents.put(mapJson);
                        while (recentEvents.length() > LOG_HISTORY) {
                            recentEvents.remove(0);
                        }
                        logCounter ++;
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
    public JSONObject getSpecificEvent(int idx) {
        if (idx >= 0 && idx > logCounter - LOG_HISTORY) {
            return logs[idx % LOG_HISTORY].toJson();
        } else {
            return null;
        }
    }

}
