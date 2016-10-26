package io.github.honeypot.servlet;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.Files;

/**
 * Created by jackson on 10/15/16.
 */

@WebServlet(name = "LogServlet", urlPatterns = {"/logs/*"})
public class LogServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String filename = URLDecoder.decode(req.getPathInfo().substring(1), "UTF-8");

        try (ServletOutputStream out = res.getOutputStream()) {
            File inFile = new File("logs/" + filename);

            if (inFile.exists()) {

                res.setHeader("Content-Type", getServletContext().getMimeType(filename));
                res.setHeader("Content-Length", String.valueOf(inFile.length()));
                res.setHeader("Content-Disposition", "inline; filename=\"" + inFile.getName() + "\"");

                res.setContentType("text/plain");
                Files.copy(inFile.toPath(), out);

                res.sendError(200);
            } else {

                res.sendError(404);

            }
        }
    }
}
