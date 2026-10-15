package it.unipd.dei.webapp.servlet.authentication;

import it.unipd.dei.webapp.database.authentication.GetMagicLinkByToken;
import it.unipd.dei.webapp.database.authentication.GetProfileByIDDAO;
import it.unipd.dei.webapp.resource.authentication.AppUser;
import it.unipd.dei.webapp.resource.LogContext;
import it.unipd.dei.webapp.resource.authentication.MagicLink;
import it.unipd.dei.webapp.resource.Message;
import it.unipd.dei.webapp.servlet.AbstractDatabaseServlet;
import it.unipd.dei.webapp.utils.JWTUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;


/**
 * Validates a magic link and logs the user in if the link is valid and not expired.
 * 
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class MagicLinkValidationServlet extends AbstractDatabaseServlet {

    /**
     * Validates the magic link token from the URL.
     *
     * @param req the HTTP request.
     * @param res the HTTP response.
     * @throws ServletException if any error occurs.
     * @throws IOException      if any error occurs.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setResource(req.getRequestURI());

        LOGGER.info("Validating magic link.");

        try {
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.length() <= 1) {
                LOGGER.warn("Missing token in magic link validation request.");
                req.setAttribute("message", new Message("Missing token.", "E3A1", null));
                req.getRequestDispatcher("/jsp/passwordless-login.jsp").forward(req, res);
                return;
            }

            String token = pathInfo.substring(1);

            MagicLink magicLink = new GetMagicLinkByToken(getConnection(), token).access().getOutputParam();

            if (magicLink == null) {
                LOGGER.warn("Invalid magic link token: %s", token);
                req.setAttribute("message", new Message("Token not valid.", "E3A2", null));
                req.getRequestDispatcher("/jsp/passwordless-login.jsp").forward(req, res);
            } else if (magicLink.getExp().before(new java.sql.Timestamp(System.currentTimeMillis()))) {
                LOGGER.warn("Expired magic link token: %s for user ID: %d", token, magicLink.getUser_id());
                req.setAttribute("message", new Message("The login link has expired. Request a new one.", "E3A3", null));
                req.getRequestDispatcher("/jsp/passwordless-login.jsp").forward(req, res);
            } else {
                LOGGER.info("Magic link valid for user ID: %d. Logging in.", magicLink.getUser_id());
                AppUser loggedUser = new GetProfileByIDDAO(getConnection(), magicLink.getUser_id()).access().getOutputParam();

                LOGGER.info("User %s successfully logged in via magic link.", loggedUser.getEmail());
                JWTUtils.setJWTCookieAndRedirect(loggedUser, req, res, req.getContextPath() + "/auth/profile");
            }
        } catch (SQLException e) {
            LOGGER.error("Database error during magic link validation.", e);
            req.setAttribute("message", new Message("An error occurred while accessing the database.", "E0A1", e.getMessage()));
            req.getRequestDispatcher("/jsp/login.jsp").forward(req, res);
        } catch (Exception e) {
            LOGGER.error("Unexpected error during magic link validation.", e);
            req.setAttribute("message", new Message("An unexpected error occurred.", "E0A2", e.getMessage()));
            req.getRequestDispatcher("/jsp/login.jsp").forward(req, res);
        } finally {
            LogContext.removeIPAddress();
            LogContext.removeResource();
        }
    }
}
