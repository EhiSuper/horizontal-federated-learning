package it.unipi.dsmt.horizontalFederatedLearning.servlet;

import com.google.gson.Gson;
import it.unipi.dsmt.horizontalFederatedLearning.entities.*;
import it.unipi.dsmt.horizontalFederatedLearning.service.db.ConfigurationService;
import it.unipi.dsmt.horizontalFederatedLearning.service.db.ExperimentService;
import it.unipi.dsmt.horizontalFederatedLearning.service.db.LevelDB;
import it.unipi.dsmt.horizontalFederatedLearning.service.db.UserService;
import it.unipi.dsmt.horizontalFederatedLearning.service.erlang.Communication;
import it.unipi.dsmt.horizontalFederatedLearning.service.exceptions.ErlangErrorException;
import it.unipi.dsmt.horizontalFederatedLearning.service.exceptions.RegistrationException;
import it.unipi.dsmt.horizontalFederatedLearning.util.Log;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.*;

@WebServlet(name = "Run", value = "/Run")
public class Run extends HttpServlet {

    private final LevelDB myLevelDb = LevelDB.getInstance();
    private final ExperimentService myExperimentService = new ExperimentService(myLevelDb);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String targetJSP = "/pages/jsp/run.jsp";
        RequestDispatcher requestDispatcher = request.getRequestDispatcher(targetJSP);
        requestDispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getAttribute("ExperimentId") != null || request.getParameter("ExperimentId") != null) {
            Experiment experiment = null;
            int firstFeature = 0;
            int secondFeature = 1;
            System.out.println("1st branch");
            if(request.getAttribute("ExperimentId") != null) {
                System.out.println("1st inner branch");
                experiment = myExperimentService.findExperimentById((Integer)request.getAttribute("ExperimentId"));
                firstFeature = (Integer)request.getAttribute("firstFeature");
                secondFeature = (Integer)request.getAttribute("secondFeature");
            } else if (request.getParameter("ExperimentId") != null){
                System.out.println("2nd inner branch");
                experiment = myExperimentService.findExperimentById(Integer.parseInt(request.getParameter("ExperimentId")));
                firstFeature = Integer.parseInt(request.getParameter("firstFeature"));
                secondFeature = Integer.parseInt(request.getParameter("secondFeature"));
            }
            Communication communication = new Communication();
            List<ExperimentRound> rounds = communication.startExperiment(experiment);
            experiment.setPostAlgorithmParameters(rounds);
            myExperimentService.editExperiment(experiment);
            List<String> logExecution = Log.getLogExperiment(experiment);
            for(int i = 0; i < logExecution.size(); ++i)
                logExecution.set(i, "'"+logExecution.get(i)+"'");
            ArrayList<String> dataPoints = new ArrayList<>();
            ArrayList<String> normPoints = new ArrayList<>();
            ArrayList<Integer> crashes = new ArrayList<>();
            ArrayList<String> availableClients = new ArrayList<>();
            ArrayList<String> involvedClients = new ArrayList<>();
            String reason = "";
            Gson gsonObj = new Gson();
            Map<Object, Object> map;
            List<Map<Object, Object>> normList = new ArrayList<>();
            for(int i=0; i<rounds.size(); i++){
                List<Map<Object, Object>> pointList = new ArrayList<>();
                if(!rounds.get(i).getLast()){
                    KMeansAlgorithmRound kmRound = (KMeansAlgorithmRound) rounds.get(i).getAlgorithmRound();
                    List<List<Double>> centers = kmRound.getCenters();
                    List<Client> involvedClientsRound = rounds.get(i).getInvolvedClients();
                    for(Client involvedClient: involvedClientsRound)
                        involvedClients.add(involvedClient.getHostname() + " ");
                    int numcrashesRound = rounds.get(i).getNumCrashes();
                    for (Client client : involvedClientsRound) {
                        List<List<Double>> chunk = client.getChunk();
                        for(List<Double> point: chunk){
                                map = new HashMap<>();
                                map.put("x", point.get(firstFeature));
                                map.put("y", point.get(secondFeature));
                                map.put("color", "grey");
                                map.put("markerSize", 3);
                                map.put("fillOpacity", ".3");
                                pointList.add(map);
                        }
                    }
                    for (List<Double> center : centers) {
                        map = new HashMap<>();
                        map.put("x", center.get(firstFeature));
                        map.put("y", center.get(secondFeature));
                        map.put("color", "black");
                        map.put("markerSize", 20);
                        map.put("markerBorderColor", "red");
                        pointList.add(map);
                    }
                    String selectedAlgorithm = experiment.getAlgorithm().getName();
                    switch(selectedAlgorithm){
                        case "KMeans":
                            KMeansAlgorithmRound kmround = (KMeansAlgorithmRound) rounds.get(i).getAlgorithmRound();
                            double norm = kmround.getfNorm();
                            map = new HashMap<>();
                            map.put("x", i + 1);
                            map.put("y", norm);
                            normList.add(map);
                    }
                    dataPoints.add(gsonObj.toJson(pointList));
                    normPoints.add(gsonObj.toJson(normList));
                    crashes.add(Integer.valueOf(gsonObj.toJson(numcrashesRound)));
                } else {
                    reason = rounds.get(i).getReason();
                }
            }
            request.setAttribute("dataPoints", dataPoints);
            request.setAttribute("normPoints", normPoints);
            request.setAttribute("availableClients", availableClients);
            request.setAttribute("involvedClients", involvedClients);
            request.setAttribute("crashes", crashes);
            request.setAttribute("numRounds", experiment.getNumRounds());
            request.setAttribute("algorithm", experiment.getAlgorithm().getName());
            request.setAttribute("numOverallCrashes", experiment.getNumCrashes());
            request.setAttribute("reason", reason);
            request.setAttribute("time", experiment.getTime());
            request.setAttribute("crashes", crashes);
            request.setAttribute("firstFeature", firstFeature);
            request.setAttribute("secondFeature", secondFeature);
            request.setAttribute("experimentId", experiment.getId());
            request.setAttribute("logExecution", logExecution);
            request.setAttribute("numMinClients", experiment.getNumMinClients());
            request.setAttribute("numFeatures", experiment.getNumFeatures());
            String targetJSP = "/pages/jsp/run.jsp";
            RequestDispatcher requestDispatcher = request.getRequestDispatcher(targetJSP);
            requestDispatcher.forward(request, response);
        } else if(request.getParameter("export") != null) {
            int id = Integer.parseInt(request.getParameter("id"));
            List<String> logs = Log.getLogExperiment(myExperimentService.findExperimentById(id));
            String result = String.join( "\n", logs);
            response.setContentType("plain/text");
            response.addHeader("Content-Disposition", "attachment; filename=\"log.txt\"");
            PrintWriter writer = response.getWriter();
            writer.write(result);
            writer.close();
        } else if(request.getParameter("back") != null) {
            response.sendRedirect(request.getContextPath() + "/Home");
        }
    }
}
