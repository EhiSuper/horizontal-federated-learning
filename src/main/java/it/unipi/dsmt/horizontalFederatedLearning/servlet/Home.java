package it.unipi.dsmt.horizontalFederatedLearning.servlet;

import it.unipi.dsmt.horizontalFederatedLearning.entities.*;
import it.unipi.dsmt.horizontalFederatedLearning.service.db.LevelDB;
import it.unipi.dsmt.horizontalFederatedLearning.service.db.UserService;
import it.unipi.dsmt.horizontalFederatedLearning.service.erlang.Communication;
import it.unipi.dsmt.horizontalFederatedLearning.service.exceptions.ErlangErrorException;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.beans.IntrospectionException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "Home", value = "/Home")
public class Home extends HttpServlet {

    private final LevelDB myLevelDb = LevelDB.getInstance();
    private final UserService myUserService = new UserService(myLevelDb);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getSession().getAttribute("login") == null) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }
        String targetJSP = "/pages/jsp/home.jsp";
        RequestDispatcher requestDispatcher = request.getRequestDispatcher(targetJSP);
        requestDispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("login");
        User myUser = myUserService.findUserByUsername(username);

        String name = request.getParameter("name");
        String dataset = request.getParameter("dataset");
        int numFeatures = Integer.parseInt(request.getParameter("numFeatures"));
        int numMinClients = Integer.parseInt(request.getParameter("numMinClients"));
        boolean randomClients = Boolean.parseBoolean(request.getParameter("randomClients"));
        int timeout = Integer.parseInt(request.getParameter("timeout"));

        String firstFeature = request.getParameter("firstFeature");
        String secondFeature = request.getParameter("secondFeature");

        String selectedAlgorithm = request.getParameter("algorithm");
        Algorithm algorithm = null;
        switch (selectedAlgorithm) {
            case "kmeans":
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

        experiment.setMode(1);
        experiment.setMaxNumRounds(10);
        experiment.setNumClients(3);
        experiment.setRandomClientsSeed(0);
        experiment.setMaxAttemptsClientCrash(3);
        experiment.setMaxAttemptsOverallCrash(20);
        experiment.setMaxAttemptsServerCrash(2);
        List<String> clients = new ArrayList<>();
        clients.add("x@127.0.0.1");
        clients.add("y@127.0.0.1");
        clients.add("z@127.0.0.1");
        clients.add("h@127.0.0.1");
        experiment.setClientsHostnames(clients);

        Communication.startExperiment(experiment);
        List<ExperimentRound> rounds = null;
        ExperimentRound round = null;
        while (true) {
            try {
                round = Communication.receiveRound();
                rounds.add(round);
            } catch (ErlangErrorException ex) {
                System.out.println("Error during erlang computations: " + ex.getMessage());
                continue;
            }
            if (round == null) {
                System.out.println("finished experiment");
                break;
            }
        }

        request.setAttribute("rounds", rounds);

        request.setAttribute("firstFeature", firstFeature);
        request.setAttribute("secondFeature", secondFeature);

        String targetJSP = "/pages/jsp/home.jsp";
        RequestDispatcher requestDispatcher = request.getRequestDispatcher(targetJSP);
        requestDispatcher.forward(request, response);


        int numCrashes = 0;
        for (ExperimentRound singleRound : rounds) {
            numCrashes += singleRound.getNumCrashes();
        }
        experiment.setNumCrashes(numCrashes);
        experiment.setNumRounds(rounds.size());


        //salvare il round nel database
    }
}
