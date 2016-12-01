package io.github.honeypot.logger;

/**
 * Created by jackson on 11/2/16.
 */
public enum LogType {
    SSH_EVENT("SSH"),
    IRC_EVENT("IRC"),
    SMTP_EVENT("SMTP"),
    HTTP_EVENT("HTTP");

    private final String type;

    LogType(String type) {
        this.type = type;
    }

    public String type() {
        return type;
    }

    public static LogType fromString(String text) {
        if (text != null) {
            for (LogType b : LogType.values()) {
                if (text.equalsIgnoreCase(b.type)) {
                    return b;
                }
            }
        }
        throw new IllegalArgumentException("Enum value for: " + text + " not found");
    }
}
