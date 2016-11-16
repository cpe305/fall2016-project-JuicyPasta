package io.github.honeypot.logger;

/**
 * Created by jackson on 11/2/16.
 */
public enum ServiceLogType {
    SSH_EVENT("SSH"),
    IRC_EVENT("IRC"),
    SMTP_EVENT("SMTP"),
    HTTP_EVENT("HTTP");

    private final String type;
    ServiceLogType(String type) {
        this.type = type;
    }
    String type() {
        return type;
    }

}
