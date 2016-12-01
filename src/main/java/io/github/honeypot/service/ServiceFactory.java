package io.github.honeypot.service;

/**
 * Created by jackson on 11/30/16.
 */

@FunctionalInterface
public interface ServiceFactory<T extends Service> {
    T create();
}
