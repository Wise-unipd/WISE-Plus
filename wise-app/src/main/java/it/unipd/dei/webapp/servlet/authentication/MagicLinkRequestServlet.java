package it.unipd.dei.webapp.servlet.authentication;

import it.unipd.dei.webapp.database.authentication.CreateMagiclinkDao;
import it.unipd.dei.webapp.database.authentication.SearchUserByEmailDAO;
import it.unipd.dei.webapp.database.stats.GetKPIDAO;
import it.unipd.dei.webapp.resource.authentication.AppUser;
import it.unipd.dei.webapp.resource.LogContext;
import it.unipd.dei.webapp.resource.Message;
import it.unipd.dei.webapp.resource.stats.KPI;
import it.unipd.dei.webapp.servlet.AbstractDatabaseServlet;
import it.unipd.dei.webapp.utils.EmailSender;
import it.unipd.dei.webapp.utils.JWTUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;


/**
 * Requests a magic link for passwordless login.
 * 
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class MagicLinkRequestServlet extends AbstractDatabaseServlet {

    /**
     * Shows the passwordless login form.
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

        LOGGER.info("Showing passwordless login form.");

        try {
            AppUser loggedUser = JWTUtils.getUserFromCookies(request);

            boolean loggedIn = (loggedUser != null);

            if (loggedIn) {
                response.sendRedirect(request.getContextPath() + "/auth/profile");
                return;
            }

            // Fetch KPIs for the passwordless login page
            try {
                KPI kpi = new GetKPIDAO(getConnection()).access().getOutputParam();
                request.setAttribute("kpi", kpi);
            } catch (SQLException e) {
                LOGGER.error("Unable to fetch KPIs for passwordless login page.", e);
            }

            request.getRequestDispatcher("/jsp/passwordless-login.jsp").forward(request, response);
        } finally {
            LogContext.removeIPAddress();
            LogContext.removeResource();
        }
    }

    /**
     * Processes the request for a magic link and sends an email if the user exists.
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

        LOGGER.info("Requesting magic link.");

        try {
            String email = req.getParameter("email");

            // Fetch KPIs for the passwordless login page in case of error/message
            try {
                KPI kpi = new GetKPIDAO(getConnection()).access().getOutputParam();
                req.setAttribute("kpi", kpi);
            } catch (SQLException e) {
                LOGGER.error("Unable to fetch KPIs for passwordless login page.", e);
            }

            AppUser retrievedUser = new SearchUserByEmailDAO(getConnection(), email).access().getOutputParam();

            if (retrievedUser != null) {
                LOGGER.info("User found for email: %s. Generating magic link.", email);
                String token = new CreateMagiclinkDao(getConnection(), retrievedUser.getId()).access().getOutputParam();
                String link = "http://localhost:8080/login-help-validation/" + token;
                EmailSender.sendEmail(retrievedUser.getEmail(), "Your Magic Login Link", "Click here to login: %s".formatted(link));
                LOGGER.info("Magic link sent to: %s.", email);
            } else {
                LOGGER.info("No user found for email: %s. No magic link sent for security reasons.", email);
            }

            req.setAttribute("message", new Message("If there is an account linked to the email address you entered, you will receive a link to log in without a password."));
            req.getRequestDispatcher("/jsp/passwordless-login.jsp").forward(req, res);
        } catch (SQLException e) {
            LOGGER.error("Unable to request magic link.", e);
            req.setAttribute("message", new Message("An error occurred while accessing the database.", "E0A1", e.getMessage()));
            req.getRequestDispatcher("/jsp/passwordless-login.jsp").forward(req, res);
        } catch (Exception e) {
            LOGGER.error("An unexpected error occurred while requesting magic link.", e);
            req.setAttribute("message", new Message("An unexpected error occurred.", "E0A2", e.getMessage()));
            req.getRequestDispatcher("/jsp/passwordless-login.jsp").forward(req, res);
        } finally {
            LogContext.removeIPAddress();
            LogContext.removeResource();
        }
    }
}
