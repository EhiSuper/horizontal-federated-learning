package it.unipi.dsmt.horizontalFederatedLearning.servlet;

import it.unipi.dsmt.horizontalFederatedLearning.entities.Experiment;
import it.unipi.dsmt.horizontalFederatedLearning.entities.ExperimentRound;
import it.unipi.dsmt.horizontalFederatedLearning.entities.KMeansAlgorithm;
import it.unipi.dsmt.horizontalFederatedLearning.entities.User;
import it.unipi.dsmt.horizontalFederatedLearning.service.db.LevelDB;
import it.unipi.dsmt.horizontalFederatedLearning.service.db.UserService;
import it.unipi.dsmt.horizontalFederatedLearning.service.erlang.Communication;
import it.unipi.dsmt.horizontalFederatedLearning.service.exceptions.ErlangErrorException;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
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
        String targetJSP = "/pages/jsp/home.jsp";
        RequestDispatcher requestDispatcher = request.getRequestDispatcher(targetJSP);
        requestDispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //Parametri che non so dove andare a prendere: mode, numRounds, numCrashes, numClients
        //numMinClients, randomCliens, randomClientsSeed, timeout, MaxAttemptsClientCrash, MaxAttemptsOverallCrash
        //MaxAttemptsServerCrash, clients, algorithm(tipo), SeedCenters,
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("login");
        User myUser = myUserService.findUserByUsername(username);

        String name = request.getParameter("name");
        String dataset = request.getParameter("dataset");
        int numFeatures = Integer.parseInt(request.getParameter("numFeatures"));
        int maxNumberRounds = Integer.parseInt(request.getParameter("maxNumberRounds"));
        String distance = request.getParameter("distance");
        double epsilon = Double.parseDouble(request.getParameter("epsilon"));
        String normFn = request.getParameter(request.getParameter("normFn"));
        int numClusters = Integer.parseInt(request.getParameter("numClusters"));

        Experiment experiment = new Experiment();
        experiment.setName(name);
        experiment.setDataset(dataset);
        experiment.setLastUpdateDate(LocalDate.now());
        experiment.setCreationDate(LocalDate.now());
        experiment.setNumFeatures(numFeatures);
        experiment.setMode(1);
        experiment.setUser(myUser);
        experiment.setNumRounds(4);
        experiment.setMaxNumRounds(maxNumberRounds);
        experiment.setNumCrashes(5);
        experiment.setNumClients(3);
        experiment.setNumMinClients(3);
        experiment.setRandomClients(false);
        experiment.setRandomClientsSeed(0);
        experiment.setTimeout(25000);
        experiment.setMaxAttemptsClientCrash(3);
        experiment.setMaxAttemptsOverallCrash(20);
        experiment.setMaxAttemptsServerCrash(2);
        List<String> clients = new ArrayList<>();
        clients.add("x@localhost");
        clients.add("y@localhost");
        clients.add("z@localhost");
        clients.add("h@localhost");
        experiment.setClientsHostnames(clients);
        KMeansAlgorithm algorithm = new KMeansAlgorithm();
        algorithm.setDistance(distance);
        algorithm.setEpsilon(epsilon);
        algorithm.setNormFn(normFn);
        algorithm.setNumClusters(numClusters);
        algorithm.setSeedCenters(100);
        experiment.setAlgorithm(algorithm);
        Communication.startExperiment(experiment);
        ExperimentRound round = null;
        while (true) {
            try {
                round = Communication.receiveRound();
            } catch (ErlangErrorException ex) {
                System.out.println("Error during erlang computations: " + ex.getMessage());
                continue;
            }
            if (round == null) {
                System.out.println("finished experiment");
                break;
            }
            //round contiene le info di quel round
            //printo il round in maniera momentanea nella home page
            request.setAttribute("round", round);
            String targetJSP = "/pages/jsp/home.jsp";
            RequestDispatcher requestDispatcher = request.getRequestDispatcher(targetJSP);
            requestDispatcher.forward(request, response);
        }
        //creato oggetto esperimento e richiesta esecuzione va aggiornato oggetto esperimento
        // vedere come chiedere esecuzione esperimento
    }

}
