package io.github.honeypot.service;

import io.github.honeypot.logger.Log;
import io.github.honeypot.logger.LogType;
import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * Created by jackson on 10/10/16.
 */
public class SMTPService extends Service {
    public SMTPService() {
        super.setServiceName("SMTPService");
    }

    private String hostname = ":org.honeypot.com:";

    @Override
    public String getPreamble() {
        String date = DateFormatUtils.SMTP_DATETIME_FORMAT.format(System.currentTimeMillis());
        String preamble = String.format("%d %s ESMTP server ready %s", 220, hostname, date);

        return preamble;
    }

    @Override
    public String feed(String input) {
        String command[] = input.split(" ");

        switch (command[0]) {
            case "HELO":
                if (command.length > 1) {
                    super.getLog().addProperty("client host name", command[1]);
                    return "250 Hello " + command[1];
                } else {
                    return null;
                }

            case "QUIT":
                super.kill();
                return "221";

            case "":
            case "NOOP":
            case "USER":
            case "JOIN":

            default:
                return null;
        }
    }
}
