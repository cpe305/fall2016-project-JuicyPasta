package io.github.honeypot.servlet;

import io.github.honeypot.logger.ConsumerRegistry;
import io.github.honeypot.logger.ConsumerType;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;

/**
 * Created by jackson on 10/15/16.
 */

public class LogServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String consumerType = null;
        try {
            consumerType = URLDecoder.decode(req.getPathInfo().substring(1), "UTF-8");
        } catch (Exception e) {
            System.err.println(e);
        }

        res.setContentType("application/json");
        JSONObject responseObj = null;

        responseObj = ConsumerRegistry.getInstance().getConsumerJson(consumerType);

        if (responseObj != null) {
            PrintWriter out = res.getWriter();
            out.print(responseObj);
            out.flush();
        }
    }
}
