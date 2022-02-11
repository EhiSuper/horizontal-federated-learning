package it.unipi.dsmt.horizontalFederatedLearning.servlet;

import it.unipi.dsmt.horizontalFederatedLearning.entities.Experiment;
import it.unipi.dsmt.horizontalFederatedLearning.entities.User;
import it.unipi.dsmt.horizontalFederatedLearning.service.db.ExperimentService;
import it.unipi.dsmt.horizontalFederatedLearning.service.db.LevelDB;
import it.unipi.dsmt.horizontalFederatedLearning.service.db.UserService;
import it.unipi.dsmt.horizontalFederatedLearning.service.exceptions.LoginException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "History", value = "/History")
public class History extends HttpServlet {

    private final LevelDB myLevelDb = LevelDB.getInstance();
    private final ExperimentService myExperimentService = new ExperimentService(myLevelDb);
    private final UserService myUserService = new UserService(myLevelDb);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String targetJSP = "/pages/jsp/history.jsp";
        List<Experiment> listExperiment = myExperimentService.readAllExperiments();
        request.setAttribute("listExperiment", listExperiment);
        RequestDispatcher requestDispatcher = request.getRequestDispatcher(targetJSP);
        requestDispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String targetJSP = "/pages/jsp/history.jsp";
        String filter = request.getParameter("filter");
        String value = request.getParameter("value");
        String user = request.getParameter("user");
        // agisci su user
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("login");
        User myUser = myUserService.findUserByUsername(username);
        if(!user.equals("all"))
            user = ""+myUser.getId();
        List<Experiment> listExperiments = myExperimentService.findExperimentsByFilter(user,filter, value);
        System.out.println(listExperiments.size());
        request.setAttribute("listExperiment", listExperiments);
        RequestDispatcher requestDispatcher = request.getRequestDispatcher(targetJSP);
        requestDispatcher.forward(request, response);
    }
}