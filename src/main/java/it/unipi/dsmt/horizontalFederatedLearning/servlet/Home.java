package it.unipi.dsmt.horizontalFederatedLearning.servlet;

import it.unipi.dsmt.horizontalFederatedLearning.entities.*;
import it.unipi.dsmt.horizontalFederatedLearning.service.db.ConfigurationService;
import it.unipi.dsmt.horizontalFederatedLearning.service.db.ExperimentService;
import it.unipi.dsmt.horizontalFederatedLearning.service.db.LevelDB;
import it.unipi.dsmt.horizontalFederatedLearning.service.db.UserService;
import it.unipi.dsmt.horizontalFederatedLearning.service.erlang.Communication;
import it.unipi.dsmt.horizontalFederatedLearning.service.exceptions.ErlangErrorException;
import it.unipi.dsmt.horizontalFederatedLearning.service.exceptions.LoginException;
import it.unipi.dsmt.horizontalFederatedLearning.service.exceptions.RegistrationException;
import it.unipi.dsmt.horizontalFederatedLearning.util.Log;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@WebServlet(name = "Home", value = "/Home")
public class Home extends HttpServlet {

    private final LevelDB myLevelDb = LevelDB.getInstance();
    private final UserService myUserService = new UserService(myLevelDb);
    private final ExperimentService myExperimentService = new ExperimentService(myLevelDb);
    private final ConfigurationService myConfigurationService = new ConfigurationService(myLevelDb);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String targetJSP = "/pages/jsp/home.jsp";
        Map<String, String> defaultValues = myConfigurationService.retrieveGeneral();
        request.setAttribute("numClients", Integer.parseInt(defaultValues.get("NumberOfClients")));
        RequestDispatcher requestDispatcher = request.getRequestDispatcher(targetJSP);
        requestDispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int userId = (int) request.getSession().getAttribute("user");
        User myUser = myUserService.findUserById(userId);
        

        if (request.getParameter("run") != null) {
            String name = request.getParameter("name");
            String dataset = request.getParameter("dataset");
            int numFeatures = Integer.parseInt(request.getParameter("numFeatures"));
            int numMinClients = Integer.parseInt(request.getParameter("numMinClients"));
            boolean randomClients = Boolean.parseBoolean(request.getParameter("randomClients"));
            int timeout = Integer.parseInt(request.getParameter("timeout"));
            request.setAttribute("firstFeature", 0);
            request.setAttribute("secondFeature", 1);
            String selectedAlgorithm = request.getParameter("algorithm");
            Algorithm algorithm = null;
            switch (selectedAlgorithm) {
                case "KMeans":
                    int numClusters = Integer.parseInt(request.getParameter("numClusters"));
                    String distance = request.getParameter("distance");
                    double epsilon = Double.parseDouble(request.getParameter("epsilon"));
                    String normFn = request.getParameter("normFn");
                    int seedCenters = Integer.parseInt(request.getParameter("seedCenters"));
                    KMeansAlgorithm kMeansAlgorithm = new KMeansAlgorithm();
                    kMeansAlgorithm.setDistance(distance);
                    kMeansAlgorithm.setEpsilon(epsilon);
                    kMeansAlgorithm.setNormFn(normFn);
                    kMeansAlgorithm.setNumClusters(numClusters);
                    kMeansAlgorithm.setSeedCenters(seedCenters);
                    algorithm = kMeansAlgorithm;
                    algorithm.setName("KMeans");
            }
            Experiment experiment = new Experiment();
            experiment.setAlgorithm(algorithm);
            experiment.setUser(myUser);
            experiment.setName(name);
            experiment.setDataset(dataset);
            experiment.setLastUpdateDate(LocalDate.now());
            experiment.setCreationDate(LocalDate.now());
            experiment.setNumFeatures(numFeatures);
            experiment.setNumMinClients(numMinClients);
            experiment.setRandomClients(randomClients);
            experiment.setTimeout(timeout);
            Map<String, String> defaultValues = myConfigurationService.retrieveGeneral();
            experiment.setMode(Integer.parseInt(defaultValues.get("Mode")));
            experiment.setMaxNumRounds(Integer.parseInt(defaultValues.get("MaxNumberRound")));
            experiment.setNumClients(Integer.parseInt(defaultValues.get("NumberOfClients")));
            System.out.println("NUMClients" + experiment.getNumClients());
            System.out.println("NumMinClients" + experiment.getNumMinClients());
            experiment.setRandomClientsSeed(Integer.parseInt(defaultValues.get("RandomClientsSeed")));
            experiment.setMaxAttemptsClientCrash(Integer.parseInt(defaultValues.get("MaxAttemptsClientCrash")));
            experiment.setMaxAttemptsOverallCrash(Integer.parseInt(defaultValues.get("MaxAttemptsOverallCrash")));
            experiment.setMaxAttemptsServerCrash(Integer.parseInt(defaultValues.get("MaxAttemptsServerCrash")));
            String[] clients = defaultValues.remove("ClientsHostnames").split(",");
            List<String> clientsHostnames = Arrays.asList(clients);
            for(String x : clientsHostnames)
                System.out.println(x);
            experiment.setClientsHostnames(clientsHostnames);
            try {
                myExperimentService.insert(experiment); //da vedere
            } catch (RegistrationException e) {
                request.setAttribute("error", e.getMessage());
                String targetJSP = "/pages/jsp/home.jsp";
                RequestDispatcher requestDispatcher = request.getRequestDispatcher(targetJSP);
                requestDispatcher.forward(request, response);
                return;
            }
            request.setAttribute("ExperimentId", experiment.getId());
            RequestDispatcher requestDispatcher = request.getRequestDispatcher("Run");
            requestDispatcher.forward(request, response);
        }
    }
}
