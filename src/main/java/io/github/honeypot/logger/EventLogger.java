package io.github.honeypot.logger;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by jackson on 10/10/16.
 */
public class EventLogger {
    public LocalDateTime startTime;
    public LocalDateTime endTime;

    public InetAddress address;

    private List<String> conversation;

    public EventLogger(InetAddress incomingAddress) {
        startTime = LocalDateTime.now();
        this.address = incomingAddress;

        this.conversation = new LinkedList<>();
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
        String toRet = "CONVERSATION WITH " + address.toString() + "\n";
        for (String m : conversation) {
            toRet += "\t" + m + "\n";
        }
        toRet += "\n";
        return toRet;
    }
}
