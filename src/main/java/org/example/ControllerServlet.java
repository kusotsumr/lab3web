package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ControllerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        boolean hasCoords = req.getParameter("x") != null
                || req.getParameter("y") != null
                || req.getParameter("r") != null;

        if (hasCoords) {
            getServletContext().getNamedDispatcher("AreaCheckServlet").forward(req, resp);
        } else {
            req.getRequestDispatcher("/WEB-INF/views/main.jsp").forward(req, resp);
        }
    }
}
