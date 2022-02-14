package it.unipi.dsmt.horizontalFederatedLearning.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.Map;

@WebServlet(name = "Home/Features", value = "/Home/Features")
public class Features extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String firstFeature = request.getParameter("firstFeature");
        String secondFeature = request.getParameter("secondFeature");
        request.setAttribute("firstFeature", firstFeature);
        request.setAttribute("secondFeature", secondFeature);
        request.setAttribute("numClients", request.getAttribute("numClients"));
        request.setAttribute("rounds", request.getAttribute("rounds"));
        request.setAttribute("experimentId", request.getAttribute("experimentId"));
        request.setAttribute("logExperiment", request.getAttribute("logExperiment"));
        request.setAttribute("numMinClients", request.getAttribute("numMinClients"));
        request.setAttribute("algorithm", request.getAttribute("algorithm"));
        String targetJSP = "/pages/jsp/home.jsp";
        RequestDispatcher requestDispatcher = request.getRequestDispatcher(targetJSP);
        requestDispatcher.forward(request, response);
    }
}
