package io.github.honeypot.logger;

import java.util.EnumMap;
import java.util.LinkedList;

/**
 * Created by jackson on 11/2/16.
 */
public class EventDatabase {
    public static final int LOG_LENGTH = 1000;

    private static LinkedList<Log> recentEvents;
    private static EnumMap<ServiceLogType, LinkedList<Log>> database;

    static {
        recentEvents = new LinkedList<>();
        database = new EnumMap<>(ServiceLogType.class);

        for(ServiceLogType k : ServiceLogType.values()) {
            database.put(k, new LinkedList<>());
        }

        database.forEach((key, value) -> {
            System.out.println(key + " : " + value.size());
        });
    }

    public static void logEvent(Log log) {
        ServiceLogType type = log.type;

        synchronized (type) {
            LinkedList<Log> logs = database.get(type);
            logs.add(log);
            while (logs.size() > LOG_LENGTH) {
                logs.removeLast();
            }
        }
        synchronized (recentEvents) {
            recentEvents.add(log);
            System.out.println("ADDING: ");
            System.out.println(log.toString());
            System.out.println();
            while (recentEvents.size() > LOG_LENGTH) {
                recentEvents.removeLast();
            }
        }
    }

    public static LinkedList<Log> getRecentEvents() {
        return recentEvents;
    }

    public static LinkedList<Log> getServiceEvents(ServiceLogType type) {
        return database.get(type);
    }
}
