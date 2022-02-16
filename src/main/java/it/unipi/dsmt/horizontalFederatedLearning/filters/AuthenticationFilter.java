package it.unipi.dsmt.horizontalFederatedLearning.filters;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@WebFilter("/*")
public class AuthenticationFilter implements Filter {
    private static final Set<String> ALLOWED_PATHS = Collections.unmodifiableSet(new HashSet<>(
            Arrays.asList("", "/Login", "/Logout", "/Signup")));
    private static final Set<String> ADMIN_PATHS = Collections.unmodifiableSet(new HashSet<>(
            Arrays.asList("/AdminPage")));

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws ServletException, IOException {
        System.out.println("Auth filter");
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        HttpSession session = request.getSession(false);
        String path = request.getRequestURI().substring(request.getContextPath().length()).replaceAll("[/]+$", "");
        boolean loggedIn = (session != null && session.getAttribute("user") != null);
        boolean adminLoggedIn = (session != null && session.getAttribute("user") != null && session.getAttribute("isAdmin") != null);
        boolean allowedPath = ALLOWED_PATHS.contains(path);

        if(ADMIN_PATHS.contains(path)) {
            if (adminLoggedIn) {
                chain.doFilter(req, res);
            } else response.sendRedirect(request.getContextPath() + "/Home");
        } else {
            if (loggedIn || allowedPath) {
                chain.doFilter(req, res);
            } else {
                response.sendRedirect(request.getContextPath() + "/Login");
            }
        }
    }

    @Override
    public void destroy() {

    }

    // ...
}