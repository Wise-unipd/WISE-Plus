package it.unipd.dei.webapp.servlet.users;

import it.unipd.dei.webapp.database.authentication.GetAllProfilesDAO;
import it.unipd.dei.webapp.resource.authentication.AppUser;
import it.unipd.dei.webapp.resource.LogContext;
import it.unipd.dei.webapp.resource.Message;
import it.unipd.dei.webapp.servlet.AbstractDatabaseServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Servlet responsible for displaying a list of all user profiles.
 * This view is restricted to Staff and Admins.
 * 
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class ProfilesServlet extends AbstractDatabaseServlet {
    /**
     * Handles the HTTP GET request to display all user profiles.
     *
     * @param req the HTTP request containing the current user attributes.
     * @param res the HTTP response used to forward to the profiles JSP.
     * @throws ServletException if a servlet-specific error occurs.
     * @throws IOException      if an I/O error occurs.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setResource(req.getRequestURI());

        LOGGER.info("Accessing all profiles view.");

        // check that the user is logged in and has appropriate role
        AppUser loggedUser = (AppUser) req.getAttribute("AppUser");
        if (loggedUser == null) {
            LOGGER.warn("Unauthorized access attempt to profiles list (not logged in).");
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        if (loggedUser.getUserRole().getId() < 1) {
            LOGGER.warn("User %s with ID %d (role ID %d) unauthorized to view all profiles.",
                    loggedUser.getEmail(), loggedUser.getId(), loggedUser.getUserRole().getId());
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "You don't have permission to access this page.");
            return;
        }

        try {
            List<AppUser> users = new GetAllProfilesDAO(getConnection()).access().getOutputParam();

            LOGGER.info("Retrieved %d profiles for the list.", users != null ? users.size() : 0);

            req.setAttribute("users", users);
            req.getRequestDispatcher("/auth/profiles.jsp").forward(req, res);

            LOGGER.info("Profiles page successfully served to user %s.", loggedUser.getEmail());

        } catch (SQLException e) {
            LOGGER.error("Unable to fetch users from database.", e);
            req.setAttribute("message", new Message("An error occurred while accessing the database.", "E0A1", e.getMessage()));
            req.getRequestDispatcher("/auth/profiles.jsp").forward(req, res);

        } catch (Exception e) {
            LOGGER.error("Unexpected error occurred while loading profiles.", e);
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred.");
        } finally {
            LogContext.removeIPAddress();
            LogContext.removeResource();
        }
    }
}
