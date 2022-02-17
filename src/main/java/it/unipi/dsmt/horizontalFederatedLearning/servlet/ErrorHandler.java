package it.unipi.dsmt.horizontalFederatedLearning.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/ErrorHandler")
public class ErrorHandler extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processError(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processError(request, response);
    }

    private void processError(HttpServletRequest request,  HttpServletResponse response) throws IOException {
        Exception ex = (Exception) request.getAttribute("javax.servlet.error.exception");
        String servletName = (String) request.getAttribute("javax.servlet.error.servlet_name");
        if (servletName == null) {
            servletName = "Unknown";
        }
        request.setAttribute("error", "Servlet " + servletName + " has thrown an exception " + ex.getClass().getName() +  " : " + ex.getMessage());
        try {
            request.getRequestDispatcher("/pages/jsp/errorPage.jsp").forward(request, response);
        } catch (ServletException e) {
            e.printStackTrace();
        }
    }
}
