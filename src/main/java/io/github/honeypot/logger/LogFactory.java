package io.github.honeypot.logger;

/**
 * Created by jackson on 11/30/16.
 */
@FunctionalInterface
public interface LogFactory<T extends Log> {
    T create();
}
