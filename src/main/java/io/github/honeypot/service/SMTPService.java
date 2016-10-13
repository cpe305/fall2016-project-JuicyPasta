package io.github.honeypot.service;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.time.LocalDateTime;

/**
 * Created by jackson on 10/10/16.
 */
public class SMTPService extends Service {

    public String hostname = ":org.honeypot.com:";

    @Override
    public String getPreamble() {
        String date = DateFormatUtils.SMTP_DATETIME_FORMAT.format(LocalDateTime.now());
        String preamble = String.format("%d %s ESMTP server ready %s", 220, hostname, date);

        return preamble;
    }

    @Override
    public String feed(String input) {
        String command[] = input.split(" ");

        switch(command[0]) {
            case "HELO":
                if (command.length > 1) {
                    //clientHostname = command[1];
                    return "250 Hello " + command[1];
                } else {
                    return null;
                }

            case "":
                return null;

            case "USER":
                return null;

            case "JOIN":
                return null;

            case "QUIT":
                super.kill();
                return "221";

            case "NOOP":

            default:
                return null;
        }
    }
}
