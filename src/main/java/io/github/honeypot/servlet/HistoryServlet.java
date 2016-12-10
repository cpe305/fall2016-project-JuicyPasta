package io.github.honeypot.servlet;

import io.github.honeypot.LogConsumer.HistoryLogConsumer;
import io.github.honeypot.logger.LogTap;
import io.github.honeypot.logger.LogType;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.EnumMap;

/**
 * Created by jackson on 12/8/16.
 */
public class HistoryServlet extends HttpServlet {
    public enum HistoryEnum{
        ALL, IRC, SSH, SMTP, HTTP
    }

    private static EnumMap<HistoryEnum, HistoryLogConsumer> consumerMap;

    static {
        loadStaticContext();
    }
    public synchronized static void loadStaticContext() {
        if (consumerMap == null) {
            consumerMap = new EnumMap<>(HistoryEnum.class);

            HistoryLogConsumer allConsumer = new HistoryLogConsumer();
            allConsumer.setAcceptAll();
            consumerMap.put(HistoryEnum.ALL, allConsumer);

            HistoryLogConsumer httpConsumer = new HistoryLogConsumer();
            httpConsumer.addAcceptableType(LogType.HTTP_EVENT);
            consumerMap.put(HistoryEnum.HTTP, httpConsumer);

            HistoryLogConsumer smtpConsumer = new HistoryLogConsumer();
            smtpConsumer.addAcceptableType(LogType.SMTP_EVENT);
            consumerMap.put(HistoryEnum.SMTP, smtpConsumer);

            HistoryLogConsumer ircConsumer = new HistoryLogConsumer();
            ircConsumer.addAcceptableType(LogType.IRC_EVENT);
            consumerMap.put(HistoryEnum.IRC, ircConsumer);

            HistoryLogConsumer sshConsumer = new HistoryLogConsumer();
            sshConsumer.addAcceptableType(LogType.SSH_EVENT);
            consumerMap.put(HistoryEnum.SSH, sshConsumer);

            consumerMap.values().forEach(LogTap.getInstance()::addObserver);
        }
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            String queryString = req.getQueryString();
            HistoryEnum consumerType = HistoryEnum.valueOf(URLDecoder.decode(req.getPathInfo().substring(1), "UTF-8"));

            HistoryLogConsumer consumer = consumerMap.get(consumerType);

            PrintWriter out = res.getWriter();
            res.setContentType("application/json");

            if (queryString != null) {
                int idx = Integer.valueOf(queryString.substring(4));
                JSONObject log = consumer.getSpecificEvent(idx);
                if (log != null) {
                    log.write(out);
                }
            } else {
                consumer.getRecentEvents().write(out);
            }

            out.flush();

        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
