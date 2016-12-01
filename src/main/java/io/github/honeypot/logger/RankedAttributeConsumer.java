package io.github.honeypot.logger;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Observable;

/**
 * Created by jackson on 11/29/16.
 */
public class RankedAttributeConsumer extends LogConsumer {
    private String attrToRank;
    private HashMap<Object, Integer> ranking;

    public RankedAttributeConsumer(String name, String attrToRank) {
        super(name);
        this.attrToRank = attrToRank;
        this.ranking = new HashMap<>();
    }

    @Override
    public void update(Observable subject, Object data) {
        if (data instanceof Log) {
            Log log = (Log) data;
            if (super.shouldLog(log.type)) {
                Object attributeValue = log.getProperty(attrToRank);
                if (attributeValue != null) {
                    synchronized (ranking) {
                        if (ranking.containsKey(attributeValue)) {
                            ranking.put(attributeValue, ranking.get(attributeValue));
                        } else {
                            ranking.put(attributeValue, 1);
                        }
                    }
                }
            }
        }
    }

    @Override
    public JSONArray toJson() {

        return null;
    }

    @Override
    public ConsumerType getType() {
        return ConsumerType.ATTRIBUTE_CONSUMER;
    }
}
