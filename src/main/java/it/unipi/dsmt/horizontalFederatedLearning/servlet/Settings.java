package it.unipi.dsmt.horizontalFederatedLearning.servlet;
import it.unipi.dsmt.horizontalFederatedLearning.entities.*;
import it.unipi.dsmt.horizontalFederatedLearning.service.db.LevelDB;
import it.unipi.dsmt.horizontalFederatedLearning.service.db.UserService;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "Settings", value = "/Settings")
public class Settings extends HttpServlet {

    private final LevelDB myLevelDb = LevelDB.getInstance();
    private final UserService myUserService = new UserService(myLevelDb);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("login");
        User myUser = myUserService.findUserByUsername(username);
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
        HttpSession session = request.getSession();
        User myUser = myUserService.findUserByUsername((String) session.getAttribute("login"));

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
            myUserService.updateUser(new User(firstName, lastName, username, password));
            myUser = myUserService.findUserByUsername(username);
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
