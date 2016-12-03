package io.github.honeypot.logger;

import org.json.JSONObject;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by jackson on 11/29/16.
 */
public class ConsumerRegistry {
    private static final HashMap<String, LogConsumer> consumersMap = new HashMap<>();

    public void addConsumer(String name, LogConsumer consumer) {
        consumersMap.put(name, consumer);
    }

    public JSONObject getConsumerJson(String name) {
        JSONObject toRet = new JSONObject();
        LogConsumer consumer = consumersMap.get(name);
        if (consumer != null) {
            toRet.put(name, consumer.toJson());

            return toRet;
        }
        return null;
    }

    private static final ConsumerRegistry INSTANCE = new ConsumerRegistry();
    public static ConsumerRegistry getInstance() {
        return INSTANCE;
    }
}
