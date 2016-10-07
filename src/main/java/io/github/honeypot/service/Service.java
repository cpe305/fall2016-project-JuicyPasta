package io.github.honeypot.service;

/**
 * Created by jackson on 10/5/16.
 */
public class Service {
    private boolean alive;
    private boolean isFirst;

    public Service() {
        this.alive = true;
        this.isFirst = true;
    }

    public boolean isAlive() {
        return alive;
    }

    public String feed (String input) {
        alive = false;
        return "im a service";
    }
}
