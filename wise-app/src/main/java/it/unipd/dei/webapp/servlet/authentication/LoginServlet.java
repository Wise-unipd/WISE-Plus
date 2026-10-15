package it.unipd.dei.webapp.servlet.authentication;

import it.unipd.dei.webapp.database.authentication.LoginDAO;
import it.unipd.dei.webapp.database.stats.GetKPIDAO;
import it.unipd.dei.webapp.resource.authentication.AppUser;
import it.unipd.dei.webapp.resource.LogContext;
import it.unipd.dei.webapp.resource.Message;
import it.unipd.dei.webapp.resource.stats.KPI;
import it.unipd.dei.webapp.servlet.AbstractDatabaseServlet;
import it.unipd.dei.webapp.utils.JWTUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.SQLException;


/**
 * Authenticates a user and generates a token (JWT).
 * 
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class LoginServlet extends AbstractDatabaseServlet {

    /**
     * Shows the login form.
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

        LOGGER.info("Showing login form.");

        try {
            AppUser loggedUser = JWTUtils.getUserFromCookies(request);

            boolean loggedIn = (loggedUser != null);

            if (loggedIn) {
                LOGGER.info("User already logged in. Redirecting to profile.");
                response.sendRedirect(request.getContextPath() + "/auth/profile");
                return;
            }

            // Fetch KPIs for the login page
            try {
                KPI kpi = new GetKPIDAO(getConnection()).access().getOutputParam();
                request.setAttribute("kpi", kpi);
            } catch (SQLException e) {
                LOGGER.error("Unable to fetch KPIs for login page.", e);
            }

            request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
        } finally {
            LogContext.removeIPAddress();
            LogContext.removeResource();
        }
    }

    /**
     * Processes the login request.
     *
     * @param req the HTTP request.
     * @param res the HTTP response.
     * @throws ServletException if any error occurs.
     * @throws IOException      if any error occurs.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setResource(req.getRequestURI());

        LOGGER.info("Login user.");

        try {
            String email = req.getParameter("email");
            String password = req.getParameter("password");

            // Fetch KPIs for the login page in case of error
            try {
                KPI kpi = new GetKPIDAO(getConnection()).access().getOutputParam();
                req.setAttribute("kpi", kpi);
            } catch (SQLException e) {
                LOGGER.error("Unable to fetch KPIs for login page.", e);
            }

            AppUser loggedUser = new LoginDAO(getConnection(), new AppUser(email, password)).access().getOutputParam();
            LOGGER.info("Authentication result for email %s: %s", email, loggedUser != null ? "Success" : "Failure");

            if (loggedUser == null || !BCrypt.checkpw(password, loggedUser.getPassword())) {
                // User not found or wrong password
                req.getRequestDispatcher("/jsp/login.jsp?error=true").forward(req, res);
            } else if (!loggedUser.isActive()) {
                // User is found but suspended
                LOGGER.warn("Login denied: User %s is suspended.", email);
                req.setAttribute("message", new Message("Your account has been suspended. Please contact support.", "E2A4", "Account is inactive"));
                req.getRequestDispatcher("/jsp/login.jsp?suspended=true").forward(req, res);
            } else {
                // User is active and credentials are correct
                try {
                    loggedUser.resetPassword();
                    JWTUtils.setJWTCookieAndRedirect(loggedUser, req, res, req.getContextPath() + "/auth/profile");
                } catch (Exception e) {
                    LOGGER.error("Unable to generate JWT.", e);
                    req.setAttribute("message", new Message("An error occurred while generating the authentication token.", "E3A0", e.getMessage()));
                    req.getRequestDispatcher("/jsp/login.jsp").forward(req, res);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Unable to login user.", e);
            req.setAttribute("message", new Message("An error occurred while accessing the database.", "E0A1", e.getMessage()));
            req.getRequestDispatcher("/jsp/login.jsp").forward(req, res);

        } finally {
            LogContext.removeIPAddress();
            LogContext.removeResource();
        }
    }
}
