package it.unipi.dsmt.horizontalFederatedLearning.servlet;

import it.unipi.dsmt.horizontalFederatedLearning.service.db.ConfigurationService;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.*;

@WebServlet(name = "AdminPage", value = "/AdminPage")
public class AdminPage extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String, String> messages = new HashMap<>();
        request.setAttribute("messages", messages);
        Map<String, String> valuesGeneral = ConfigurationService.retrieveGeneral();
        if (!valuesGeneral.isEmpty()) {
            String[] clients = valuesGeneral.remove("ClientsHostnames").split(",");
            List<String> clientsHostnames = Arrays.asList(clients);
            request.setAttribute("valuesGeneral", valuesGeneral);
            request.setAttribute("hostnames", clientsHostnames);
        } else {
            request.setAttribute("valuesGeneral", new HashMap<>());
            request.setAttribute("hostnames", new ArrayList<>());
        }
        Map<String, String> valuesKMeans = ConfigurationService.retrieveSpecific("kmeans");
        if (!valuesKMeans.isEmpty()) {
            request.setAttribute("valuesKMeans", valuesKMeans);
        } else {
            request.setAttribute("valuesKMeans", new HashMap<>());
        }
        String targetJSP = "/pages/jsp/adminPage.jsp";
        RequestDispatcher requestDispatcher = request.getRequestDispatcher(targetJSP);
        requestDispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HashMap<String, String> valuesGeneral = new HashMap<>();
        request.setAttribute("valuesGeneral", valuesGeneral);
        HashMap<String, String> valuesKMeans = new HashMap<>();
        request.setAttribute("valuesKMeans", valuesKMeans);
        String numberOfClients = request.getParameter("NumberOfClients");
        valuesGeneral.put("NumberOfClients", numberOfClients);
        if (request.getParameterValues("ClientsHostnames") != null) {
            List<String> clientsHostnames = Arrays.asList(request.getParameterValues("ClientsHostnames"));
            List<String> actualClientsHostnames = new ArrayList<>();
            String hostnameValue = "";
            for (String hostname : clientsHostnames) {
                if (!hostname.isEmpty()) {
                    hostnameValue = hostnameValue.equals("") ? hostname : hostnameValue + "," + hostname;
                    actualClientsHostnames.add(hostname);
                }
            }
            request.setAttribute("hostnames", actualClientsHostnames);
            valuesGeneral.put("ClientsHostnames", hostnameValue);
        }
        String randomClientsSeed = request.getParameter("RandomClientsSeed");
        valuesGeneral.put("RandomClientsSeed", randomClientsSeed);
        String maxNumberRound = request.getParameter("MaxNumberRound");
        valuesGeneral.put("MaxNumberRound", maxNumberRound);
        String maxAttemptsClientCrash = request.getParameter("MaxAttemptsClientCrash");
        valuesGeneral.put("MaxAttemptsClientCrash", maxAttemptsClientCrash);
        String maxAttemptsServerCrash = request.getParameter("MaxAttemptsServerCrash");
        valuesGeneral.put("MaxAttemptsServerCrash", maxAttemptsServerCrash);
        String maxAttemptsOverallCrash = request.getParameter("MaxAttemptsOverallCrash");
        valuesGeneral.put("MaxAttemptsOverallCrash", maxAttemptsOverallCrash);
        String mode = request.getParameter("Mode");
        valuesGeneral.put("Mode", mode);
        ConfigurationService.insertGeneral(valuesGeneral);
        ConfigurationService.insertSpecific(valuesKMeans, "kmeans");
        String targetJSP = "/pages/jsp/adminPage.jsp";
        RequestDispatcher requestDispatcher = request.getRequestDispatcher(targetJSP);
        requestDispatcher.forward(request, response);
    }
}

