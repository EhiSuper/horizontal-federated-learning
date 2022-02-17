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
        System.out.println(firstFeature);
        String secondFeature = request.getParameter("secondFeature");
        System.out.println(secondFeature);
        request.setAttribute("firstFeature", firstFeature);
        request.setAttribute("secondFeature", secondFeature);
        request.setAttribute("numClients", request.getAttribute("numClients"));
        System.out.println(request.getAttribute("numClients"));
        request.setAttribute("rounds", request.getAttribute("rounds"));
        System.out.println(request.getAttribute("rounds"));
        request.setAttribute("experimentId", request.getAttribute("experimentId"));
        System.out.println( request.getAttribute("experimentId"));
        request.setAttribute("logExperiment", request.getAttribute("logExperiment"));
        System.out.println( request.getAttribute("logExperiment"));
        request.setAttribute("numMinClients", request.getAttribute("numMinClients"));
        System.out.println( request.getAttribute("numMinClients"));
        request.setAttribute("algorithm", request.getAttribute("algorithm"));
        System.out.println( request.getAttribute("algorithm"));
        request.setAttribute("numFeatures", request.getAttribute("numFeatures"));
        System.out.println( request.getAttribute("numfeatures"));
        request.setAttribute("time", request.getAttribute("time"));
        System.out.println( request.getAttribute("time"));
        String targetJSP = "/pages/jsp/run.jsp";
        RequestDispatcher requestDispatcher = request.getRequestDispatcher(targetJSP);
        requestDispatcher.forward(request, response);
    }
}