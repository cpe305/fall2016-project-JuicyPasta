package io.github.honeypot.logger;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Created by jackson on 10/14/16.
 */
public class Log implements Serializable {
    LogType type;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private InetAddress address;
    private int localPort;
    private int remotePort;
    private List<String> conversation;
    private Map<String, String> properties;

    public Log(LogType type, InetAddress incomingAddress) {
        this.type = type;
        this.startTime = LocalDateTime.now();
        this.address = incomingAddress;

        this.conversation = new LinkedList<>();

        this.properties = new HashMap<>();

        addLocation();
    }

    public void setDescription(String desc) {
        this.description = desc;
    }

    public void addProperty(String key, String value) {
        properties.put(key, value);
    }

    public Object getProperty(String key) {
        if (this.properties.containsKey(key)) {
            return this.properties.get(key);
        } else {
            return null;
        }
    }

    public void setLocalPort(int port) {
        this.localPort = port;
    }

    public void setRemotePort(int port) {
        this.remotePort = port;
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
            String country = info.getString("country_name");
            String city = info.getString("city");

            properties.put("longitude", Double.toString(longitude));
            properties.put("latitude", Double.toString(latitude));
            properties.put("country", country);
            properties.put("city", city);


        } catch (IOException e) {
            System.err.println(e);
        }
    }

    @Override
    public String toString() {
        StringBuilder toRet = new StringBuilder();
        toRet.append("event description: " + description + "\n");
        toRet.append("start time: " + startTime + "\n");
        toRet.append("end time: " + endTime + "\n");
        toRet.append("address  " + address.getHostAddress() + "\n");

        properties.forEach((key, value) -> toRet.append(key + ": " + value + "\n"));

        return toRet.toString();
    }

    public JSONObject toJson() {
        JSONObject toRet = new JSONObject();
        toRet.put("event-type", type.type());
        toRet.put("event-description", description);
        toRet.put("start-time", startTime);
        toRet.put("end-time", endTime);
        toRet.put("address", address.getHostAddress());
        toRet.put("local-port", localPort);
        toRet.put("remote-port", remotePort);

        properties.forEach((key, value) -> toRet.put(key, value));
        return toRet;
    }
}
