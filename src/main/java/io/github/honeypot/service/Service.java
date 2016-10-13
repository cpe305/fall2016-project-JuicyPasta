package io.github.honeypot.service;

import io.github.honeypot.logger.EventLogger;

/**
 * Created by jackson on 10/5/16.
 */
public class Service {
    private boolean alive;
    private boolean isFirst;

    private EventLogger logger;

    public Service() {
        this.alive = true;
        this.isFirst = true;
    }

    public boolean isAlive() {
        return alive;
    }

    public void kill() {
        this.alive = false;
    }

    public String getPreamble() {
        return null;
    }

    public String feed (String input) {
        alive = false;
        return "im a service";
    }
}
