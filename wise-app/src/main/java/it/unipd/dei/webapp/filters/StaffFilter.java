package it.unipd.dei.webapp.filters;

import it.unipd.dei.webapp.resource.authentication.AppUser;
import it.unipd.dei.webapp.utils.JWTUtils;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.StringFormatterMessageFactory;

/**
 * Filter that ensures the authenticated user has staff permissions.
 * It checks the user's role and allows access only if the role ID is 1 or higher.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class StaffFilter implements Filter {

    /**
     * The logger instance for this filter.
     */
    private static final Logger LOGGER = LogManager.getLogger(StaffFilter.class, StringFormatterMessageFactory.INSTANCE);

    /**
     * Filters the request by checking for staff permissions.
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

        // Try to get user from request (set by AuthFilter) or from cookies
        AppUser loggedUser = (AppUser) req.getAttribute("AppUser");
        if (loggedUser == null) {
            loggedUser = JWTUtils.getUserFromCookies(req);
        }

        if (loggedUser != null) {
            req.setAttribute("AppUser", loggedUser);

            // Check if user has staff permissions (Role ID >= 1)
            if (loggedUser.getUserRole().getId() >= 1) {
                LOGGER.debug("Staff user %s authorized for resource %s.", loggedUser.getEmail(), req.getRequestURI());
                chain.doFilter(request, response);
            } else {
                // Logged in but not staff
                LOGGER.warn("Access denied for user %s: insufficient permissions for resource %s.", loggedUser.getEmail(), req.getRequestURI());
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "You do not have permission to access staff resources.");
            }
        } else {
            // Not logged in
            LOGGER.info("Unauthenticated access attempt to staff resource %s. Redirecting to login.", req.getRequestURI());
            res.sendRedirect(req.getContextPath() + "/login");
        }
    }
}