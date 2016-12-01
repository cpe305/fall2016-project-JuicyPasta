package io.github.honeypot.exception;

/**
 * Created by jackson on 11/2/16.
 */
public class HoneypotRuntimeException extends RuntimeException {
    public HoneypotRuntimeException(Exception e) {
        super(e);
    }

    public HoneypotRuntimeException(String s) {
        super(s);
    }
}
