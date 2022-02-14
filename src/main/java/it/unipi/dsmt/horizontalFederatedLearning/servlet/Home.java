package it.unipi.dsmt.horizontalFederatedLearning.servlet;

import it.unipi.dsmt.horizontalFederatedLearning.entities.*;
import it.unipi.dsmt.horizontalFederatedLearning.service.db.ConfigurationService;
import it.unipi.dsmt.horizontalFederatedLearning.service.db.ExperimentService;
import it.unipi.dsmt.horizontalFederatedLearning.service.db.LevelDB;
import it.unipi.dsmt.horizontalFederatedLearning.service.db.UserService;
import it.unipi.dsmt.horizontalFederatedLearning.service.erlang.Communication;
import it.unipi.dsmt.horizontalFederatedLearning.service.exceptions.ErlangErrorException;
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
        if (request.getSession().getAttribute("login") == null) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }
        String targetJSP = "/pages/jsp/home.jsp";
        Map<String, String> defaultValues = myConfigurationService.retrieveGeneral();
        request.setAttribute("numClients", Integer.parseInt(defaultValues.get("NumberOfClients")));
        RequestDispatcher requestDispatcher = request.getRequestDispatcher(targetJSP);
        requestDispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("login");
        User myUser = myUserService.findUserByUsername(username);

        if (request.getParameter("run") != null) {
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
                case "KMeans":
                    int numClusters = Integer.parseInt(request.getParameter("numClusters"));
                    String distance = request.getParameter("distance");
                    double epsilon = Double.parseDouble(request.getParameter("epsilon"));
                    String normFn = request.getParameter("normFn");
                    int seedCenters = Integer.parseInt(request.getParameter("seedCenters"));
                    //Map<String, String> kmeansDefaultValues = myConfigurationService.retrieveSpecific("kmeans");
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

            Map<String, String> defaultValues = myConfigurationService.retrieveGeneral();
            experiment.setMode(Integer.parseInt(defaultValues.get("Mode")));
            experiment.setMaxNumRounds(Integer.parseInt(defaultValues.get("MaxNumberRound")));
            experiment.setNumClients(Integer.parseInt(defaultValues.get("NumberOfClients")));
            experiment.setRandomClientsSeed(Integer.parseInt(defaultValues.get("RandomClientsSeed")));
            experiment.setMaxAttemptsClientCrash(Integer.parseInt(defaultValues.get("MaxAttemptsClientCrash")));
            experiment.setMaxAttemptsOverallCrash(Integer.parseInt(defaultValues.get("MaxAttemptsOverallCrash")));
            experiment.setMaxAttemptsServerCrash(Integer.parseInt(defaultValues.get("MaxAttemptsServerCrash")));

            String[] clients = defaultValues.remove("ClientsHostnames").split(",");
            List<String> clientsHostnames = Arrays.asList(clients);
            experiment.setClientsHostnames(clientsHostnames);
            myExperimentService.insert(experiment);
            myLevelDb.printContent();
            Communication.startExperiment(experiment);
            List<ExperimentRound> rounds = new ArrayList<>();
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
            // save in the db
            int numCrashes = 0;
            for (int i = 0; i < rounds.size(); ++i) {
                ExperimentRound singleRound = rounds.get(i);
                if (singleRound != null && !singleRound.getLast())
                    numCrashes += singleRound.getNumCrashes();
                else if(singleRound != null && singleRound.getLast()){
                    switch (experiment.getAlgorithm().getName()) {
                        case "KMeans":
                            KMeansAlgorithmRound kmround = (KMeansAlgorithmRound)rounds.get(i-1).getAlgorithmRound();
                            KMeansAlgorithm kMeansAlgorithm = (KMeansAlgorithm) experiment.getAlgorithm();
                            kMeansAlgorithm.setfNorm(kmround.getfNorm());
                            kMeansAlgorithm.setCenters(kmround.getCenters());
                            algorithm = kMeansAlgorithm;
                            break;
                    }
                }
            }
            experiment.setNumCrashes(numCrashes);
            experiment.setNumRounds(rounds.size()-2);
            experiment.setAlgorithm(algorithm);
            myExperimentService.editExperiment(experiment);
            myLevelDb.printContent();
            List<String> logExecution = Log.getLogExperimentText(experiment);
            for(int i = 0; i < logExecution.size(); ++i)
                logExecution.set(i, "'"+logExecution.get(i)+"'");
            request.setAttribute("rounds", rounds);
            request.setAttribute("algorithm", selectedAlgorithm);
            request.setAttribute("logExecution", logExecution);
            request.setAttribute("firstFeature", firstFeature);
            request.setAttribute("secondFeature", secondFeature);
            request.setAttribute("numClients", experiment.getNumClients());
            request.setAttribute("experimentId", experiment.getId());
            request.setAttribute("numMinClients", experiment.getNumMinClients());
            String targetJSP = "/pages/jsp/home.jsp";
            RequestDispatcher requestDispatcher = request.getRequestDispatcher(targetJSP);
            requestDispatcher.forward(request, response);
        } else if(request.getParameter("export") != null) {
            int id = Integer.parseInt(request.getParameter("id"));
            List<String> logs = Log.getLogExperimentText(myExperimentService.findExperimentById(id));
            String result = String.join( "\n", logs);
            response.setContentType("plain/text");
            response.addHeader("Content-Disposition", "attachment:filename=\"log.txt\"");
            PrintWriter writer = response.getWriter();
            writer.write(result);
            writer.close();
        }
    }
}
