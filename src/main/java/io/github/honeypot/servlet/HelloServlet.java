package io.github.honeypot.servlet;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.Files;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "MyServlet", urlPatterns = {"/*"})
public class HelloServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String filename = null;
        try {
            filename = URLDecoder.decode(req.getPathInfo().substring(1), "UTF-8");
        } catch (Exception e) {
            System.err.println(e);
        }

        try (ServletOutputStream out = res.getOutputStream()) {
            File inFile = new File("src/main/webapp/static/" + filename);
            System.out.println(inFile.getAbsolutePath());
            if (inFile.exists()) {

                res.setHeader("Content-Type", getServletContext().getMimeType(filename));
                res.setHeader("Content-Length", String.valueOf(inFile.length()));
                res.setHeader("Content-Disposition", "inline; filename=\"" + inFile.getName() + "\"");

                res.setContentType("text/html");
                Files.copy(inFile.toPath(), out);

            } else {

                res.sendError(404);

            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }

}
