package io.github.honeypot.servlet;

import org.apache.commons.io.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by jackson on 10/14/16.
 */
public class LogServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getContextPath();
        System.out.println(path);

        try (FileInputStream in = new FileInputStream("logs/" + path);
             ServletOutputStream out = resp.getOutputStream()) {
            IOUtils.copy(in, out);
        }
    }

}
