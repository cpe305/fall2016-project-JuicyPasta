package io.github.honeypot.logger;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by jackson on 10/14/16.
 */

// TODO: refactor this so that there is a list of LogType 'tags' and move all of the other attributes into properties
// TODO: use some sort of JSON parser
public class Log implements Serializable {
    private LogType type;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private InetAddress address;
    private int localPort;
    private int remotePort;
    private List<String> conversation;
    private Map<String, String> properties;

    public Log(LogType type) {
        this.type = type;
        this.startTime = LocalDateTime.now();
        this.conversation = new LinkedList<>();
        this.properties = new HashMap<>();
    }

    public Log(JSONObject obj) {
        if (obj.has("event-type")) {
            this.type = LogType.fromString(obj.getString("event-type"));
            obj.remove("event-type");
        }

        if (obj.has("event-description")) {
            this.description = obj.getString("event-description");
            obj.remove("event-description");
        }

        if (obj.has("start-time")) {
            this.startTime = LocalDateTime.parse(obj.getString("start-time"), DateTimeFormatter.ISO_DATE_TIME);
            obj.remove("start-time");
        }

        if (obj.has("end-time")) {
            this.startTime = LocalDateTime.parse(obj.getString("end-time"), DateTimeFormatter.ISO_DATE_TIME);
            obj.remove("end-time");
        }

        if (obj.has("address")) {
            try {
                this.address = InetAddress.getByName(obj.getString("address"));
                obj.remove("address");
            } catch (UnknownHostException e) {
                System.err.println(e);
            }
        }

        if (obj.has("local-port")) {
            this.localPort = obj.getInt("local-port");
            obj.remove("local-port");
        }

        if (obj.has("remote-port")) {
            this.remotePort = obj.getInt("remote-port");
            obj.remove("remote-port");
        }

        if (obj.has("conversation")) {
            JSONArray conversationJson = obj.getJSONArray("conversation");
            List<String> conversation = new LinkedList<>();
            for (Object o : conversationJson) {
                conversation.add(String.valueOf(o));
            }
            this.conversation = conversation;
            obj.remove("conversation");
        }

        properties = new HashMap<>();
        for (String key : obj.keySet()) {
            properties.put(key, obj.getString(key));
        }
    }
    public LogType getType() {
        return this.type;
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

    public void setInetAddress(InetAddress address) {
        this.address = address;
        addLocation();
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

    private static final String geoLookupIpTemplate = "http://freegeoip.net/json/{0}";

    private void addLocation() {
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

    public JSONObject toJson() {
        JSONObject toRet = new JSONObject();
        toRet.put("event-type", type.type());
        toRet.put("event-description", description);
        toRet.put("start-time", startTime);
        toRet.put("end-time", endTime);
        toRet.put("address", address.getHostAddress());
        toRet.put("local-port", localPort);
        toRet.put("remote-port", remotePort);
        toRet.put("conversation", conversation);
        properties.forEach((key, value) -> toRet.put(key, value));
        return toRet;
    }

    public JSONObject toSmallJson() {
        JSONObject mapJson = new JSONObject();
        mapJson.put("lon", this.properties.get("longitude"));
        mapJson.put("lat", this.properties.get("latitude"));
        mapJson.put("type", this.getType());
        mapJson.put("addr", this.address.getHostAddress());
        mapJson.put("port", remotePort);
        return mapJson;
    }
}
