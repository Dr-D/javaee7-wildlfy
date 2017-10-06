package com.drd.wildfly.helloworld;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author darrenp - 2017-10-06.
 */

@WebServlet("/test")
public class TestServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String CONTENT_TYPE = "text/html;charset=UTF-8";
    private static final String MESSAGE = "<!DOCTYPE html><html>" +
            "<head><title>Hello!</title></head>" +
            "<body>Hello World WildFly</body>" +
            "</html>";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType(CONTENT_TYPE);
        try (PrintWriter out = resp.getWriter()){
            out.println(MESSAGE);
        }
    }
}
