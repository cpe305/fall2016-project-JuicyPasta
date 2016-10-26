package io.github.honeypot.service;

import io.github.honeypot.logger.EventLogger;

/**
 * Created by jackson on 10/10/16.
 */
public class IRCService extends Service {
  public String name;
  public String password;
  public String nick;
  public String channel;
  public String key;

  public IRCService() {
    super.serviceName = "IRCService";
  }

  public String hostname = ":org.honeypot.com:";

  @Override
  public String getPreamble() {
    String preamble = hostname + " NOTICE AUTH :*** Looking up your hostname...\n";
    preamble += hostname + " NOTICE AUTH :*** Found your hostname, welcome back";

    return preamble;
  }

  @Override
  public String feed(String input) {
    String command[] = input.split(" ");

    switch (command[0]) {
    case "PASS":
      if (command.length > 1)
        password = command[1];
      return null;

    case "NICK":
      if (command.length > 1)
        nick = command[1];
      return null;

    case "USER":
      if (command.length > 1)
        name = command[1];
      return null;

    case "JOIN":
      if (command.length > 1)
        channel = command[1];
      if (command.length > 2)
        key = command[2];

      return null;

    case "DIE":
      super.kill();

    case "PING":
    case "PONG":

    default:
      return notRegistered(command[0]);
    }
  }

  public String notRegistered(String msg) {
    return hostname + " 451 " + msg + ":You have not registered";
  }
}
