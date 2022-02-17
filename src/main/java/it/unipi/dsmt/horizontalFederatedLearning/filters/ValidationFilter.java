package it.unipi.dsmt.horizontalFederatedLearning.filters;

import it.unipi.dsmt.horizontalFederatedLearning.service.db.ConfigurationService;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@WebFilter("/AdminPage")
public class ValidationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
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
        valuesGeneral = ConfigurationService.retrieveGeneral();
        if (!valuesGeneral.isEmpty()) {
            String[] clients = valuesGeneral.remove("ClientsHostnames").split(",");
            List<String> clientsHostnames = Arrays.asList(clients);
            request.setAttribute("valuesGeneral", valuesGeneral);
            request.setAttribute("hostnames", clientsHostnames);
        } else {
            request.setAttribute("valuesGeneral", new HashMap<>());
            request.setAttribute("hostnames", new ArrayList<>());
        }
        valuesKMeans = ConfigurationService.retrieveSpecific("kmeans");
        if (!valuesKMeans.isEmpty()) {
            request.setAttribute("valuesKMeans", valuesKMeans);
        } else {
            request.setAttribute("valuesKMeans", new HashMap<>());
        }
        String numberOfClients = request.getParameter("NumberOfClients");
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
                if (numberOfClients != null && numberOfClients.matches("\\d+") && actualClientsHostnames.size() < Integer.parseInt(numberOfClients)) {
                    messages.put("ClientsHostnames", "Please enter at least as many hostnames as the number of clients");
                }
            }
        } else {
            messages.put("ClientsHostnames", "Please enter at least one client");
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