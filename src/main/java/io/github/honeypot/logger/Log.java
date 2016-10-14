package io.github.honeypot.logger;

import org.json.JSONObject;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by jackson on 10/14/16.
 */
public class Log {
    private String eventType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private InetAddress address;
    private List<String> conversation;

    public Log(String eventType, InetAddress incomingAddress) {
        this.startTime = LocalDateTime.now();
        this.address = incomingAddress;

        this.conversation = new LinkedList<>();
        this.eventType = eventType;
    }

    public void addIncomingMessage(String in) {
        String str = String.format("INCOMING [%s]: %s", LocalDateTime.now().toString(), in);
        System.out.println(str);
        conversation.add(str);
    }
    public void addOutgoingMessage(String out) {
        String str = String.format("OUTGOING [%s]: %s", LocalDateTime.now().toString(), out);
        System.out.println(str);
        conversation.add(str);
    }

    public void end() {
        endTime = LocalDateTime.now();
    }

    public String toString() {
        String toRet = eventType + "\n";
        toRet += "CONVERSATION WITH " + address.toString() + "\n";
        for (String m : conversation) {
            toRet += "\t" + m + "\n";
        }
        toRet += "\n";
        return toRet;
    }

    public JSONObject toJson() {
        JSONObject toRet = new JSONObject();
        return toRet;
    }
}
