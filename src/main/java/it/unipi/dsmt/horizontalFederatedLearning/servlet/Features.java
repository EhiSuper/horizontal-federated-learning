package it.unipi.dsmt.horizontalFederatedLearning.servlet;

import com.google.gson.Gson;
import it.unipi.dsmt.horizontalFederatedLearning.entities.Client;
import it.unipi.dsmt.horizontalFederatedLearning.entities.ExperimentRound;
import it.unipi.dsmt.horizontalFederatedLearning.entities.KMeansAlgorithmRound;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        HttpSession session = request.getSession();
        session.setAttribute("firstFeature", firstFeature);
        session.setAttribute("secondFeature", secondFeature);
        ArrayList<String> dataPoints = setDataPoint(request, firstFeature, secondFeature);
        session.setAttribute("dataPoints", dataPoints);
        String targetJSP = "/pages/jsp/run.jsp";
        RequestDispatcher requestDispatcher = request.getRequestDispatcher(targetJSP);
        requestDispatcher.forward(request, response);
    }

    protected ArrayList<String> setDataPoint(HttpServletRequest request, String firstFeature, String secondFeature) {
        HttpSession session = request.getSession();
        String reason = "";
        List<ExperimentRound> rounds = (List<ExperimentRound>) session.getAttribute("rounds");
        ArrayList<String> dataPoints = new ArrayList<>();
        Gson gsonObj = new Gson();
        Map<Object, Object> map;
        List<Map<Object, Object>> normList = new ArrayList<>();
        for (int i = 0; i < rounds.size(); i++) {
            List<Map<Object, Object>> pointList = new ArrayList<>();
            if (!rounds.get(i).getLast()) {
                KMeansAlgorithmRound kmRound = (KMeansAlgorithmRound) rounds.get(i).getAlgorithmRound();
                List<List<Double>> centers = kmRound.getCenters();

                for (Client client : rounds.get(i).getClientsState()) {
                    List<List<Double>> chunk = client.getChunk();
                    for (List<Double> point : chunk) {
                        map = new HashMap<>();
                        map.put("x", point.get(Integer.parseInt(firstFeature)));
                        map.put("y", point.get(Integer.parseInt(secondFeature)));
                        map.put("color", "grey");
                        map.put("markerSize", 3);
                        map.put("fillOpacity", ".3");
                        pointList.add(map);
                    }
                }
                for (List<Double> center : centers) {
                    map = new HashMap<>();
                    map.put("x", center.get(Integer.parseInt(firstFeature)));
                    map.put("y", center.get(Integer.parseInt(secondFeature)));
                    map.put("color", "black");
                    map.put("markerSize", 20);
                    map.put("markerBorderColor", "red");
                    pointList.add(map);
                }
                dataPoints.add(gsonObj.toJson(pointList));
            } else {
                reason = rounds.get(i).getReason();
            }
        }
        return dataPoints;
    }
}