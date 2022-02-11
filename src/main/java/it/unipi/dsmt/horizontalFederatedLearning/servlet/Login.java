package it.unipi.dsmt.horizontalFederatedLearning.servlet;

import it.unipi.dsmt.horizontalFederatedLearning.entities.User;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import it.unipi.dsmt.horizontalFederatedLearning.service.db.*;
import it.unipi.dsmt.horizontalFederatedLearning.service.exceptions.LoginException;

@WebServlet(name = "Login", value = "/Login")
public class Login extends HttpServlet {

    private final LevelDB myLevelDb = LevelDB.getInstance();
    private final UserService myUserService = new UserService(myLevelDb);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String targetJSP = "/pages/jsp/login.jsp";
        RequestDispatcher requestDispatcher = request.getRequestDispatcher(targetJSP);
        requestDispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        request.setAttribute("error", " ");

        try{
            myUserService.login(username, password);
            response.sendRedirect(request.getContextPath() + "/MainPage");
        }catch(LoginException e){
            request.setAttribute("error", e.getMessage());
            String targetJSP = "/pages/jsp/login.jsp";
            RequestDispatcher requestDispatcher = request.getRequestDispatcher(targetJSP);
            requestDispatcher.forward(request, response);
        }
    }
}
