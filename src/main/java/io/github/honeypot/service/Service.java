package io.github.honeypot.service;

import io.github.honeypot.logger.EventLogger;

/**
 * Created by jackson on 10/5/16.
 */
public abstract class Service {
  public String serviceName;

  private boolean alive;
  private boolean isFirst;

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

  abstract public String getPreamble();

  abstract public String feed(String input);
}
