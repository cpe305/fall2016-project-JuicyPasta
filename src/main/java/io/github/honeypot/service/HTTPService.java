package io.github.honeypot.service;

import io.github.honeypot.logger.ServiceLogType;

/**
 * Created by jackson on 11/16/16.
 */
public class HTTPService extends Service{
    private static ServiceLogType logType = ServiceLogType.HTTP_EVENT;

    @Override
    public ServiceLogType getLogType() {
        return logType;
    }
    @Override
    public String getPreamble() {
        return null;
    }

    @Override
    public String feed(String input) {
        System.out.println(input);
//        String res = ""
//                " HTTP/1.1 302 Found " /
//        "Location: http://www.iana.org/domains/example/";
        return "";
    }
}
