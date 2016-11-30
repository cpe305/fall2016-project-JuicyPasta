package io.github.honeypot.logger;

import org.json.JSONObject;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by jackson on 11/29/16.
 */
public class ConsumerRegistry {
    public static EnumMap<ConsumerType, List<LogConsumer>> consumersMap;

    static {
        consumersMap = new EnumMap<>(ConsumerType.class);

        for(ConsumerType k : ConsumerType.values()) {
            consumersMap.put(k, new LinkedList<>());
        }
    }

    public static void addConsumer(LogConsumer consumer) {
        List consumersList  = consumersMap.get(consumer.getType());
        consumersList.add(consumer);
    }
    public static JSONObject getConsumerJsons(ConsumerType type) {
        List<LogConsumer> consumersList = consumersMap.get(type);
        JSONObject toRet = new JSONObject();
        for (LogConsumer consumer : consumersList) {
            System.out.println("NAME:"+ consumer.name);
            toRet.put(consumer.name, consumer.toJson());
        }

        return toRet;
    }
}
