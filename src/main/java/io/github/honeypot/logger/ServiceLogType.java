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
    public String type() {
        return type;
    }
    public static ServiceLogType fromString(String text) {
        if (text != null) {
            for (ServiceLogType b : ServiceLogType.values()) {
                if (text.equalsIgnoreCase(b.type)) {
                    return b;
                }
            }
        }
        throw new IllegalArgumentException("Enum value for: " + text + " not found");
    }
}
