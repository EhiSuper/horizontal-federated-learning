package it.unipi.dsmt.horizontalFederatedLearning.servlet;

import it.unipi.dsmt.horizontalFederatedLearning.entities.Algorithm;
import it.unipi.dsmt.horizontalFederatedLearning.entities.Experiment;
import it.unipi.dsmt.horizontalFederatedLearning.entities.KMeansAlgorithm;
import it.unipi.dsmt.horizontalFederatedLearning.entities.User;
import it.unipi.dsmt.horizontalFederatedLearning.service.db.ExperimentService;
import it.unipi.dsmt.horizontalFederatedLearning.service.db.LevelDB;
import it.unipi.dsmt.horizontalFederatedLearning.service.db.UserService;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@WebServlet(name = "ExperimentInfo", value = "/ExperimentInfo")
public class ExperimentInfo extends HttpServlet {

    private final LevelDB myLevelDb = LevelDB.getInstance();
    private final ExperimentService myExperimentService = new ExperimentService(myLevelDb);
    private final UserService myUserService = new UserService(myLevelDb);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String targetJSP = "/pages/jsp/experimentInfo.jsp";
        String id = request.getParameter("id");
        System.out.println(id);
        Experiment experiment = myExperimentService.findExperimentById(Integer.parseInt(id));
        System.out.println(experiment);
        request.setAttribute("experiment", experiment);
        RequestDispatcher requestDispatcher = request.getRequestDispatcher(targetJSP);
        requestDispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        int experimentId = Integer.parseInt(request.getParameter("id"));
        if(action.equals("back")) {
            response.sendRedirect(request.getContextPath() + "/History");
        } else if(action.equals("delete")){
            myExperimentService.deleteExperimentById(experimentId);
            response.sendRedirect(request.getContextPath() + "/History");
        } else if(action.equals("update")){
            String name = request.getParameter("name");
            String dataset = request.getParameter("dataset");
            int numFeatures = Integer.parseInt(request.getParameter("numFeatures"));
            int mode = Integer.parseInt(request.getParameter("mode"));
            LocalDate creationDate = LocalDate.parse(request.getParameter("creationDate"));
            // aggiorno
            LocalDate lastUpdateDate = LocalDate.now();
            int numRounds = Integer.parseInt(request.getParameter("numRounds"));
            int maxNumRounds = Integer.parseInt(request.getParameter("maxNumRounds"));
            int numCrashes = Integer.parseInt(request.getParameter("numCrashes"));
            int numClients = Integer.parseInt(request.getParameter("numClients"));
            int numMinClients = Integer.parseInt(request.getParameter("numMinClients"));
            List<String> clientsHostnames = Arrays.asList(request.getParameter("clientsHostnames").split(","));
            boolean randomClients = Boolean.parseBoolean(request.getParameter("randomClients"));
            double randomClientsSeed = Double.parseDouble(request.getParameter("randomClientsSeed"));
            double timeout = Double.parseDouble(request.getParameter("timeout"));
            int maxAttemptsClientCrash = Integer.parseInt(request.getParameter("maxAttemptsClientCrash"));
            int maxAttemptsServerCrash = Integer.parseInt(request.getParameter("maxAttemptsServerCrash"));
            int maxAttemptsOverallCrash = Integer.parseInt(request.getParameter("maxAttemptsOverallCrash"));
            String username = request.getParameter("username");
            User user = myUserService.findUserByUsername(username);
            String algorithmName = request.getParameter("algorithmName");
            Algorithm algorithm = null;
            if(algorithmName.equals("KMeans")){
                int numClusters = Integer.parseInt(request.getParameter("numClusters"));
                double epsilon = Double.parseDouble(request.getParameter("epsilon"));
                String distance = request.getParameter("distance");
                double seedCenters = Double.parseDouble(request.getParameter("seedCenters"));
                String normFn = request.getParameter("normFn");
                algorithm = new KMeansAlgorithm(numClusters, epsilon, distance,  seedCenters, normFn);
            }
            algorithm.setName(algorithmName);
            Experiment experiment = new Experiment(experimentId, name, algorithm, dataset, numFeatures, mode, user, creationDate, lastUpdateDate,numRounds,maxNumRounds,numCrashes,numClients, numMinClients,
            clientsHostnames,  randomClients, randomClientsSeed, (int) timeout,maxAttemptsClientCrash, maxAttemptsServerCrash, maxAttemptsOverallCrash);
            myExperimentService.editExperiment(experiment);
            // non ho controllato se campi diversi
            // reindirizza su pagina opportuna per rieseguire algoritmo
            String targetJSP = "/pages/jsp/experimentInfo.jsp";
            System.out.println(experiment.getId());
            request.setAttribute("experiment", experiment);
            RequestDispatcher requestDispatcher = request.getRequestDispatcher(targetJSP);
            requestDispatcher.forward(request, response);
        }
    }
}
