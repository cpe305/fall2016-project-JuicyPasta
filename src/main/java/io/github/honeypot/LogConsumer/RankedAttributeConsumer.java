package io.github.honeypot.LogConsumer;

import io.github.honeypot.LogConsumer.LogConsumer;
import io.github.honeypot.logger.Log;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Observable;

/**
 * Created by jackson on 11/29/16.
 */
public class RankedAttributeConsumer extends LogConsumer {
    private String attrToRank;
    private HashMap<Object, Integer> ranking;
    private int logCount;
    public RankedAttributeConsumer(String attrToRank) {
        this.attrToRank = attrToRank;
        this.ranking = new HashMap<>();
        this.logCount = 0;
    }

    @Override
    public void update(Observable subject, Object data) {
        if (data instanceof Log) {
            Log log = (Log) data;
            if (super.shouldLog(log.getType())) {
                logCount++;
                Object attributeValue = log.getProperty(attrToRank);
                if (attributeValue != null) {
                    synchronized (ranking) {
                        if (ranking.containsKey(attributeValue)) {
                            ranking.put(attributeValue, ranking.get(attributeValue)+1);
                        } else {
                            ranking.put(attributeValue, 1);
                        }
                    }
                }
            }
        }
    }

    public JSONObject getScores() {
        return new JSONObject(ranking);
    }

    public int getLogCount() {
        return logCount;
    }
}
