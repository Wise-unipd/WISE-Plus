package it.unipd.dei.webapp.servlet.authentication;

import it.unipd.dei.webapp.resource.LogContext;
import it.unipd.dei.webapp.servlet.AbstractDatabaseServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;


/**
 * Handles user logout by clearing the JWT cookie.
 * 
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class LogoutServlet extends AbstractDatabaseServlet {

    /**
     * Clears the JWT cookie and redirects to the login page.
     *
     * @param request  the HTTP request.
     * @param response the HTTP response.
     * @throws ServletException if any error occurs.
     * @throws IOException      if any error occurs.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        LogContext.setIPAddress(request.getRemoteAddr());
        LogContext.setResource(request.getRequestURI());

        LOGGER.info("Logging out user.");

        try {
            Cookie jwtCookie = new Cookie("JWT_TOKEN", "");
            jwtCookie.setHttpOnly(true);
            jwtCookie.setPath(request.getContextPath().isEmpty() ? "/" : request.getContextPath());
            jwtCookie.setMaxAge(0);
            response.addCookie(jwtCookie);

            LOGGER.info("JWT cookie cleared. Redirecting to login.");
            response.sendRedirect(request.getContextPath() + "/");
        } finally {
            LogContext.removeIPAddress();
            LogContext.removeResource();
        }
    }
}
