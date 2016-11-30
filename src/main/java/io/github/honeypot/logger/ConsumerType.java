package io.github.honeypot.logger;

/**
 * Created by jackson on 11/29/16.
 */
public enum ConsumerType {
    HISTORY_CONSUMER("HISTORY"),
    ATTRIBUTE_CONSUMER("ATTRIBUTE");

    private final String type;
    ConsumerType(String type) {
        this.type = type;
    }
    public String type() {
        return type;
    }
    public static ConsumerType fromString(String text) {
        if (text != null) {
            for (ConsumerType b : ConsumerType.values()) {
                if (text.equalsIgnoreCase(b.type)) {
                    return b;
                }
            }
        }
        throw new IllegalArgumentException("Enum value for: " + text + " not found");
    }

}
