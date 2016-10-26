package io.github.honeypot.service;

/**
 * Created by jackson on 10/10/16.
 */
public class ServiceFactory {
  public String type;

  public ServiceFactory(String type) {
    this.type = type;
  }

  public Service getInstance() {
    switch (type) {
      case "IRCService":
        return new IRCService();

      case "SMTPService":
        return new SMTPService();

      case "SSHService":
        return new SSHService();
    }

    return null;
  }
}
