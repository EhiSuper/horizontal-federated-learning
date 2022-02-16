package it.unipi.dsmt.horizontalFederatedLearning.filters;

import it.unipi.dsmt.horizontalFederatedLearning.service.db.ConfigurationService;
import it.unipi.dsmt.horizontalFederatedLearning.service.db.LevelDB;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@WebFilter("/AdminPage")
public class ValidationFilter implements Filter {
    private final LevelDB myLevelDb = LevelDB.getInstance();
    private final ConfigurationService myConfigurationService = new ConfigurationService(myLevelDb);


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        if(request.getParameter("update") == null) {
            chain.doFilter(req, res);
            return;
        }
        HashMap<String, String> messages = new HashMap<>();
        request.setAttribute("messages", messages);
        HashMap<String, String> valuesGeneral = new HashMap<>();
        request.setAttribute("valuesGeneral", valuesGeneral);
        HashMap<String, String> valuesKMeans = new HashMap<>();
        request.setAttribute("valuesKMeans", valuesKMeans);
        valuesGeneral = myConfigurationService.retrieveGeneral();
        if (valuesGeneral != null && !valuesGeneral.isEmpty()) {
            String[] clients = valuesGeneral.remove("ClientsHostnames").split(",");
            List<String> clientsHostnames = Arrays.asList(clients);
            request.setAttribute("valuesGeneral", valuesGeneral);
            request.setAttribute("hostnames", clientsHostnames);
        } else {
            request.setAttribute("valuesGeneral", new HashMap<>());
            request.setAttribute("hostnames", new ArrayList<>());
        }
        valuesKMeans = myConfigurationService.retrieveSpecific("kmeans");
        if (valuesKMeans != null && !valuesKMeans.isEmpty()) {
            request.setAttribute("valuesKMeans", valuesKMeans);
        } else {
            request.setAttribute("valuesKMeans", new HashMap<>());
        }
        String numberOfClients = request.getParameter("NumberOfClients");
        if (numberOfClients == null || numberOfClients.trim().isEmpty()) {
            messages.put("NumberOfClients", "Please enter NumberOfClients");
        } else if (!numberOfClients.matches("\\d+")) {
            messages.put("NumberOfClients", "Please enter digits only");
        }

        if (request.getParameterValues("ClientsHostnames") != null) {
            List<String> clientsHostnames = Arrays.asList(request.getParameterValues("ClientsHostnames"));
            List<String> actualClientsHostnames = new ArrayList<>();
            String hostnameValue = "";
            for (String hostname : clientsHostnames) {
                if (!hostname.isEmpty()) {
                    hostnameValue = hostnameValue.equals("") ? hostname : hostnameValue + "," + hostname;
                    actualClientsHostnames.add(hostname);
                    if (!hostname.matches("\\w+@localhost") && !hostname.matches("\\w+@\\d{1,3}[.]\\d{1,3}[.]\\d{1,3}[.]\\d{1,3}")) {
                        messages.put("ClientsHostnames", "Please check clients, some of them are not correct");
                    }
                }
            }
            if (actualClientsHostnames.isEmpty()) {
                messages.put("ClientsHostnames", "Please enter at least one client");
            } else {
                if (numberOfClients.matches("\\d+") && actualClientsHostnames.size() < Integer.parseInt(numberOfClients)) {
                    messages.put("ClientsHostnames", "Please enter at least as many hostnames as the number of clients");
                }
            }
        } else {
            messages.put("ClientsHostnames", "Please enter at least one client");
        }
        String randomClientsSeed = request.getParameter("RandomClientsSeed");
        if (randomClientsSeed == null || randomClientsSeed.trim().isEmpty()) {
            messages.put("RandomClientsSeed", "Please enter RandomClientsSeed");
        } else if (!randomClientsSeed.matches("\\d+")) {
            messages.put("RandomClientsSeed", "Please enter digits only");
        }
        String maxNumberRound = request.getParameter("MaxNumberRound");
        if (maxNumberRound == null || maxNumberRound.trim().isEmpty()) {
            messages.put("MaxNumberRound", "Please enter MaxNumberRound");
        } else if (!maxNumberRound.matches("\\d+")) {
            messages.put("MaxNumberRound", "Please enter digits only");
        }
        String maxAttemptsClientCrash = request.getParameter("MaxAttemptsClientCrash");
        if (maxAttemptsClientCrash == null || maxAttemptsClientCrash.trim().isEmpty()) {
            messages.put("MaxAttemptsClientCrash", "Please enter MaxAttemptsClientCrash");
        } else if (!maxAttemptsClientCrash.matches("\\d+")) {
            messages.put("MaxAttemptsClientCrash", "Please enter digits only");
        }
        String maxAttemptsServerCrash = request.getParameter("MaxAttemptsServerCrash");
        if (maxAttemptsServerCrash == null || maxAttemptsServerCrash.trim().isEmpty()) {
            messages.put("MaxAttemptsServerCrash", "Please enter MaxAttemptsServerCrash");
        } else if (!maxAttemptsServerCrash.matches("\\d+")) {
            messages.put("MaxAttemptsServerCrash", "Please enter digits only");
        }
        String maxAttemptsOverallCrash = request.getParameter("MaxAttemptsOverallCrash");
        if (maxAttemptsOverallCrash == null || maxAttemptsOverallCrash.trim().isEmpty()) {
            messages.put("MaxAttemptsOverallCrash", "Please enter MaxAttemptsOverallCrash");
        } else if (!maxAttemptsOverallCrash.matches("\\d+")) {
            messages.put("MaxAttemptsOverallCrash", "Please enter digits only");
        }
        String mode = request.getParameter("Mode");
        if (mode == null || mode.trim().isEmpty()) {
            messages.put("Mode", "Please enter Mode");
        } else if (!mode.matches("\\d+")) {
            messages.put("Mode", "Please enter digits only");
        }
        if(messages.size() != 0){
            String targetJSP = "/pages/jsp/adminPage.jsp";
            RequestDispatcher requestDispatcher = request.getRequestDispatcher(targetJSP);
            requestDispatcher.forward(request, response);
        } else {
            chain.doFilter(req, res);
        }
    }

    @Override
    public void destroy() {

    }
}