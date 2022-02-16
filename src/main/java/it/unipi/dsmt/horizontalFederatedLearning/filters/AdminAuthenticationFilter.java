package it.unipi.dsmt.horizontalFederatedLearning.filters;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class AdminAuthenticationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws ServletException, IOException {
        System.out.println("Admin auth filter");
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        HttpSession session = request.getSession(false);
        boolean adminLoggedIn = (session != null && session.getAttribute("user") != null && session.getAttribute("isAdmin") != null);
        System.out.println(session != null);
        System.out.println(session.getAttribute("user") != null);
        System.out.println(session.getAttribute("isAdmin") != null);
        if (adminLoggedIn) {
            System.out.println("Satisfied");
            chain.doFilter(req, res);
        } else {
            System.out.println("Not Satisfied");
            response.sendRedirect(request.getContextPath() + "/Login");
        }
    }

    @Override
    public void destroy() {
    }
}