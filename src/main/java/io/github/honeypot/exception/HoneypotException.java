package io.github.honeypot.exception;

/**
 * Created by jackson on 11/2/16.
 */
public class HoneypotException extends Exception {
    public HoneypotException(Exception e) {
        super(e);
    }
}
