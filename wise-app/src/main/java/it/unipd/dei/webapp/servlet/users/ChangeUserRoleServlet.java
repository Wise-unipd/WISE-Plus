package it.unipd.dei.webapp.servlet.users;

import it.unipd.dei.webapp.database.authentication.ChangeUserRoleDAO;
import it.unipd.dei.webapp.resource.authentication.AppUser;
import it.unipd.dei.webapp.resource.LogContext;
import it.unipd.dei.webapp.resource.Message;
import it.unipd.dei.webapp.servlet.AbstractDatabaseServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Servlet responsible for managing user role modifications.
 * This servlet handles both displaying the role change form (GET) and processing
 * the role update submission (POST).
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class ChangeUserRoleServlet extends AbstractDatabaseServlet {

    /**
     * Handles the HTTP GET request to display the role change form.
     *
     * @param req the HTTP request containing the target user ID in the URI.
     * @param res the HTTP response used to forward to the JSP or return HTTP errors.
     * @throws ServletException if a servlet-specific error occurs.
     * @throws IOException      if an I/O error occurs.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setResource(req.getRequestURI());

        try {
            // check if the user is an admin
            if (!verifyAdminAccess(req, res)) return;

            // extract the target user ID from the URL path
            int targetUserId = extractUserId(req.getRequestURI());
            AppUser loggedUser = (AppUser) req.getAttribute("AppUser");

            // prevent admins from accidentally modifying their role
            if (targetUserId == loggedUser.getId()) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "Action denied: You cannot change your own role.");
                return;
            }

            // attach the target user ID to the request and forward to the form view
            req.setAttribute("targetUserId", targetUserId);
            req.getRequestDispatcher("/auth/change-user-role.jsp").forward(req, res);

        } catch (Exception e) {
            LOGGER.error("Error displaying change role page.", e);
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request format.");

        } finally {
            LogContext.removeIPAddress();
            LogContext.removeResource();
        }
    }

    /**
     * Handles the HTTP POST request to process the actual role update in the database.
     *
     * @param req the HTTP request containing the new role ID and the target user ID.
     * @param res the HTTP response used to redirect on success.
     * @throws ServletException if a servlet-specific error occurs.
     * @throws IOException      if an I/O error occurs.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setResource(req.getRequestURI());

        try {
            // check if the user is an admin
            if (!verifyAdminAccess(req, res)) return;

            // extract the target user ID from the URL path
            int targetUserId = extractUserId(req.getRequestURI());
            AppUser loggedUser = (AppUser) req.getAttribute("AppUser");

            // prevent admins from accidentally modifying their role
            if (targetUserId == loggedUser.getId()) {
                LOGGER.warn("Admin %s tried to change their own role (denied).", loggedUser.getEmail());
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "Action denied: You cannot change your own role.");
                return;
            }

            // parse the new role ID submitted via the HTML form
            int newRoleId = Integer.parseInt(req.getParameter("newRoleId"));

            LOGGER.info("Admin %s changing role for user ID %d to %d.", loggedUser.getEmail(), targetUserId, newRoleId);

            // execute the database update via the DAO
            new ChangeUserRoleDAO(getConnection(), targetUserId, newRoleId).access();

            LOGGER.info("Role for user ID %d successfully updated to %d by admin %s.", targetUserId, newRoleId, loggedUser.getEmail());

            // redirect back to the profiles list on success
            res.sendRedirect(req.getContextPath() + "/auth/staff/profiles");

        } catch (SQLException | NumberFormatException e) {
            LOGGER.error("Unable to change user role.", e);
            req.setAttribute("message", new Message("An error occurred while updating the role.", "E0A1", e.getMessage()));

            try {
                req.getRequestDispatcher("/auth/change-user-role.jsp").forward(req, res);

            } catch (ServletException | IOException ex) {
                LOGGER.error("Failed to forward to JSP.", ex);

            }
        } catch (Exception e) {
            LOGGER.error("Unexpected error occurred.", e);
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid request.");

        } finally {
            // 2. Cleanup Logging Context
            LogContext.removeIPAddress();
            LogContext.removeResource();

        }
    }

    /**
     * Utility method to enforce strict Authentication and Authorization.
     *
     * @param req the HTTP request.
     * @param res the HTTP response.
     * @return {@code true} if the user is logged in AND is an Admin (Role ID = 2);
     * {@code false} otherwise.
     * @throws IOException if a redirect or error transmission fails.
     */
    private boolean verifyAdminAccess(HttpServletRequest req, HttpServletResponse res) throws IOException {
        AppUser loggedUser = (AppUser) req.getAttribute("AppUser");
        if (loggedUser == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return false;
        }

        if (loggedUser.getUserRole().getId() != 2) { // 2 = Admin
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "You don't have permission to access this page.");
            return false;
        }

        return true;
    }

    /**
     * Utility method to dynamically extract the target user ID from the request URI.
     *
     * @param uri the raw request URI.
     * @return the extracted user ID as an integer.
     * @throws IllegalArgumentException if no valid integer can be parsed from the URI.
     */
    private int extractUserId(String uri) {
        String[] parts = uri.split("/");

        // iterate through the URI segments to find the first valid integer
        for (String part : parts) {
            try {
                return Integer.parseInt(part);

            } catch (NumberFormatException ignored) {
                // ignore segments that aren't numbers (e.g., "auth", "change-role")
            }
        }
        throw new IllegalArgumentException("User ID not found in URI.");
    }
}