package io.github.honeypot.servlet;

import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.util.LinkedList;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.github.honeypot.logger.EventDatabase;
import io.github.honeypot.logger.Log;
import io.github.honeypot.logger.ServiceLogType;

/**
 * Created by jackson on 10/15/16.
 */

@WebServlet(name = "LogServlet", urlPatterns = {"/log/*"})
public class LogServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String logType = null;
        try {
            logType = URLDecoder.decode(req.getPathInfo().substring(1), "UTF-8");
        } catch (Exception e) {
            System.err.println(e);
        }

        res.setContentType("application/json");

        JSONArray responseObj;

        switch(logType) {
            case "all":
                responseObj = listToJson(EventDatabase.getRecentEvents());
                break;

            case "ssh":
                responseObj = null;
                break;

            case "irc":
                responseObj = null;
                break;

            case "smpt":
                responseObj = null;
                break;

            default:
                return;

        }

        PrintWriter out = res.getWriter();
        out.print(responseObj);
        out.flush();
    }

    public JSONArray listToJson(LinkedList<Log> list) {
        JSONArray toRet = new JSONArray();
        for (Log l : list) {
            toRet.put(l.toJson());
        }
        return toRet;
    }
}
