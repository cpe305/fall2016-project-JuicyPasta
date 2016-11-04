package io.github.honeypot.logger;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by jackson on 10/14/16.
 */
public class Log {
    ServiceLogType type;
    private String eventType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private InetAddress address;
    private List<String> conversation;
    private Map<String, String> properties;

    public Log(ServiceLogType type, InetAddress incomingAddress) {
        this.type = type;
        this.startTime = LocalDateTime.now();
        //this.address = incomingAddress;
        try {
            this.address = InetAddress.getByName("216.58.195.228");
        } catch (Exception ignored) {
        }

        this.conversation = new LinkedList<>();
        this.eventType = eventType;

        this.properties = new TreeMap<>();

        addLocation();
    }

    public void addProperty(String key, String value) {
        properties.put(key, value);
    }

    public void addIncomingMessage(String in) {
        String str = String.format("< [%s]: %s", LocalDateTime.now().toString(), in);
        conversation.add(str);
    }

    public void addOutgoingMessage(String out) {
        String str = String.format("> [%s]: %s", LocalDateTime.now().toString(), out);
        conversation.add(str);
    }

    public void end() {
        endTime = LocalDateTime.now();
    }

    public static String geoLookupIpTemplate = "http://freegeoip.net/json/{0}";

    public void addLocation() {
        try {
            URLConnection conn = new URL(MessageFormat.format(geoLookupIpTemplate, address.getHostAddress())).openConnection();
            JSONTokener tokener = new JSONTokener(conn.getInputStream());
            JSONObject info = new JSONObject(tokener);

            double latitude = info.getDouble("latitude");
            double longitude = info.getDouble("longitude");

            properties.put("longitude", Double.toString(longitude));
            properties.put("latitude", Double.toString(latitude));

        } catch (IOException e) {
            System.err.println(e);
        }
    }

    @Override
    public String toString() {
        StringBuilder toRet = new StringBuilder();
        toRet.append("event type: " + eventType + "\n");
        toRet.append("start time: " + startTime + "\n");
        toRet.append("end time: " + endTime + "\n");
        toRet.append("address  " + address.getHostAddress() + "\n");

        properties.forEach((key, value) -> toRet.append(key + ": " + value + "\n"));

        return toRet.toString();


        /*
        StringBuilder toRet = new StringBuilder(eventType + "::" + address.toString() + "\n");
        for (String m : conversation) {
            StringBuilder toAppend = new StringBuilder();
            for (int i = 0; i < m.length(); i++) {
                toAppend.append(Character.toString(m.charAt(i)));
                if (m.charAt(i) == '\n') {
                    toAppend.append("\t\t\t\t\t");
                }
            }
            toRet.append('\t');
            toRet.append(toAppend);
            toRet.append('\n');
        }
        toRet.append('\n');
        */

    }

    public JSONObject toJson() {
        JSONObject toRet = new JSONObject();
        toRet.put("event type", eventType);
        toRet.put("start time", startTime);
        toRet.put("end time", endTime);
        toRet.put("address", address.getHostAddress());

        properties.forEach((key, value) -> toRet.put(key, value));
        return toRet;
    }
}
