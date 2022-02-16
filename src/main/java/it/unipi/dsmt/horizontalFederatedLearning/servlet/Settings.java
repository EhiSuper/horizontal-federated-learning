package it.unipi.dsmt.horizontalFederatedLearning.servlet;

import it.unipi.dsmt.horizontalFederatedLearning.entities.*;
import it.unipi.dsmt.horizontalFederatedLearning.service.db.LevelDB;
import it.unipi.dsmt.horizontalFederatedLearning.service.db.UserService;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.logging.Level;

@WebServlet(name = "Settings", value = "/Settings")
public class Settings extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = (int) request.getSession().getAttribute("user");
        User myUser = UserService.findUserById(id);
        request.setAttribute("username", myUser.getUsername());
        request.setAttribute("firstName", myUser.getFirstName());
        request.setAttribute("lastName", myUser.getLastName());
        request.setAttribute("password", myUser.getPassword());
        request.setAttribute("confirmPassword", myUser.getPassword());
        String targetJSP = "/pages/jsp/settings.jsp";
        RequestDispatcher requestDispatcher = request.getRequestDispatcher(targetJSP);
        requestDispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        User myUser = UserService.findUserById((int) request.getSession().getAttribute("user"));

        if (!password.equals(confirmPassword)) {
            request.setAttribute("error", "The two passwords are not equal");

            request.setAttribute("username", myUser.getUsername());
            request.setAttribute("firstName", myUser.getFirstName());
            request.setAttribute("lastName", myUser.getLastName());
            request.setAttribute("password", myUser.getPassword());
            request.setAttribute("confirmPassword", myUser.getPassword());
            String targetJSP = "/pages/jsp/settings.jsp";
            RequestDispatcher requestDispatcher = request.getRequestDispatcher(targetJSP);
            requestDispatcher.forward(request, response);
            return;
        }

        try {
            myUser = UserService.findUserByUsername(username);
            myUser.setFirstName(firstName);
            myUser.setLastName(lastName);
            myUser.setUsername(username);
            myUser.setPassword(password);
            UserService.updateUser(myUser);
            request.setAttribute("username", myUser.getUsername());
            request.setAttribute("firstName", myUser.getFirstName());
            request.setAttribute("lastName", myUser.getLastName());
            request.setAttribute("password", myUser.getPassword());
            request.setAttribute("confirmPassword", myUser.getPassword());
            String targetJSP = "/pages/jsp/settings.jsp";
            RequestDispatcher requestDispatcher = request.getRequestDispatcher(targetJSP);
            requestDispatcher.forward(request, response);
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            String targetJSP = "/pages/jsp/settings.jsp";
            RequestDispatcher requestDispatcher = request.getRequestDispatcher(targetJSP);
            requestDispatcher.forward(request, response);
        }
    }
}
