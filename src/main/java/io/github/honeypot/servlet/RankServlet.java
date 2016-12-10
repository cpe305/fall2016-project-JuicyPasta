package io.github.honeypot.servlet;

import io.github.honeypot.logger.LogTap;
import io.github.honeypot.logger.LogType;
import io.github.honeypot.LogConsumer.RankedAttributeConsumer;

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
public class RankServlet extends HttpServlet {
    public enum RankEnum{
        COUNTRIES("Top Countries"), SSH_CREDS("SSH Username");

        private String name;
        RankEnum(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }
    }

    private static EnumMap<RankEnum, RankedAttributeConsumer> consumerMap;

    static {
        loadStaticContext();
    }

    public synchronized static void loadStaticContext() {
        if (consumerMap == null) {
            consumerMap = new EnumMap<>(RankEnum.class);

            RankedAttributeConsumer countryConsumer = new RankedAttributeConsumer("country");
            countryConsumer.setAcceptAll();
            consumerMap.put(RankEnum.COUNTRIES, countryConsumer);

            RankedAttributeConsumer sshConsumer = new RankedAttributeConsumer("username");
            sshConsumer.addAcceptableType(LogType.SSH_EVENT);
            consumerMap.put(RankEnum.SSH_CREDS, sshConsumer);

            consumerMap.values().forEach(LogTap.getInstance()::addObserver);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            RankEnum consumerType = RankEnum.valueOf(URLDecoder.decode(req.getPathInfo().substring(1), "UTF-8"));

            RankedAttributeConsumer consumer = consumerMap.get(consumerType);

            PrintWriter out = res.getWriter();
            res.setContentType("application/json");

            consumer.getScores().write(out);

            out.flush();

        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
