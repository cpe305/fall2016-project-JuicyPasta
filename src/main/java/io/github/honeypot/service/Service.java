package io.github.honeypot.service;

import io.github.honeypot.logger.Log;

/**
 * Created by jackson on 10/5/16.
 */
public abstract class Service {
    private String serviceName;
    private boolean alive;

    Log log;

    public Service() {
        this.alive = true;
    }
    public void attachLog(Log log) {
        this.log = log;
    }
    public Log getLog() {
        return this.log;
    }
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
    public String getServiceName() {
        return this.serviceName;
    }

    public boolean isAlive() {
        return alive;
    }

    public void kill() {
        this.alive = false;
    }

    abstract public String getPreamble();

    abstract public String feed(String input);
}
