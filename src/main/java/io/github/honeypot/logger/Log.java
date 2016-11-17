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
    private String desc;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private InetAddress address;
    private int localPort;
    private int remotePort;
    private List<String> conversation;
    private Map<String, String> properties;

    public Log(ServiceLogType type, InetAddress incomingAddress) {
        this.type = type;
        this.startTime = LocalDateTime.now();
        this.address = incomingAddress;

        this.conversation = new LinkedList<>();

        this.properties = new TreeMap<>();

        addLocation();
    }

    public void setDescription(String desc) {
        this.desc = desc;
    }

    public void addProperty(String key, String value) {
        properties.put(key, value);
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

            properties.put("longitude", Double.toString(longitude));
            properties.put("latitude", Double.toString(latitude));

        } catch (IOException e) {
            System.err.println(e);
        }
    }

    @Override
    public String toString() {
        StringBuilder toRet = new StringBuilder();
        toRet.append("event description: " + desc + "\n");
        toRet.append("start time: " + startTime + "\n");
        toRet.append("end time: " + endTime + "\n");
        toRet.append("address  " + address.getHostAddress() + "\n");

        properties.forEach((key, value) -> toRet.append(key + ": " + value + "\n"));

        return toRet.toString();
    }

    public JSONObject toJson() {
        JSONObject toRet = new JSONObject();
        toRet.put("event-type", type.type());
        toRet.put("event-description", desc);
        toRet.put("start-time", startTime);
        toRet.put("end-time", endTime);
        toRet.put("address", address.getHostAddress());
        toRet.put("local-port", localPort);
        toRet.put("remote-port", remotePort);

        properties.forEach((key, value) -> toRet.put(key, value));
        return toRet;
    }
}
