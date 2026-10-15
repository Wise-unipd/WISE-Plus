package it.unipd.dei.webapp.filters;

import it.unipd.dei.webapp.resource.authentication.AppUser;
import it.unipd.dei.webapp.utils.JWTUtils;

import java.io.IOException;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.StringFormatterMessageFactory;

/**
 * Filter that ensures a user is authenticated.
 * It checks for the presence of a valid JWT token in the cookies and, if present,
 * sets the corresponding user as a request attribute.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class AuthFilter implements Filter {

    /**
     * The logger instance for this filter.
     */
    private static final Logger LOGGER = LogManager.getLogger(AuthFilter.class, StringFormatterMessageFactory.INSTANCE);

    /**
     * Filters the request by checking for an authenticated user.
     *
     * @param request  the servlet request.
     * @param response the servlet response.
     * @param chain    the filter chain.
     * @throws IOException      if an I/O error occurs.
     * @throws ServletException if a servlet error occurs.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // Try to retrieve the user from the JWT stored in the cookies
        AppUser loggedUser = JWTUtils.getUserFromCookies(req);

        if (loggedUser != null) {
            // User is authenticated
            req.setAttribute("AppUser", loggedUser);
            LOGGER.debug("User %s successfully authenticated via AuthFilter.", loggedUser.getEmail());
            chain.doFilter(request, response);
        } else {
            // User is not authenticated; redirect to the login page
            LOGGER.info("Unauthenticated access attempt to protected resource %s. Redirecting to login.", req.getRequestURI());
            res.sendRedirect(req.getContextPath() + "/login");
        }
    }
}