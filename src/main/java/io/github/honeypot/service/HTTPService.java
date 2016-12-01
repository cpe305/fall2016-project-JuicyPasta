package io.github.honeypot.service;

/**
 * Created by jackson on 11/16/16.
 */
public class HTTPService extends Service {
    // Client talks first in HTTP
    @Override
    public String getPreamble() {
        return null;
    }

    @Override
    public String feed(String input) {
        super.getLog().addProperty("content-length", String.valueOf(input.length()));

        String toRet = "" +
                "HTTP/1.1 302 Found\n" +
                "Location: https://www.google.com\n" +
                "Content-Type: text/html; charset=UTF-8\n" +
                "content-length:226\n" +
                "Accept-Encoding: gzip, deflate, sdch, br\n" +
                "Accept-Language: en-US,en;q=0.8\n";

        return toRet;
    }
}
