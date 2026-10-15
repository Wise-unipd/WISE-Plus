package it.unipd.dei.webapp.servlet.users;

import it.unipd.dei.webapp.database.authentication.ChangePasswordDAO;
import it.unipd.dei.webapp.resource.authentication.AppUser;
import it.unipd.dei.webapp.resource.LogContext;
import it.unipd.dei.webapp.resource.Message;
import it.unipd.dei.webapp.servlet.AbstractDatabaseServlet;
import it.unipd.dei.webapp.utils.JWTUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.Connection;

/**
 * Changes the password of a user.
 * 
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class ChangePasswordServlet extends AbstractDatabaseServlet {

    /**
     * Processes the password change request.
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

        LOGGER.info("Starting password change.");

        try {
            AppUser currentUser = (AppUser) req.getAttribute("AppUser");
            if (currentUser == null) {
                LOGGER.warn("User not logged in, but tried to change password.");
                res.sendRedirect(req.getContextPath() + "/login");
                return;
            }

            String idString = req.getParameter("id");
            String password = req.getParameter("password");
            String confirmPassword = req.getParameter("confirmPassword");

            if (idString == null || password == null || confirmPassword == null || password.trim().isEmpty() || confirmPassword.trim().isEmpty()) {
                LOGGER.warn("Missing required fields for password change: id=%s", idString);
                req.setAttribute("message", new Message("Password and confirmation are required.", "E1A0", "Missing fields."));

                String redirectUrl = "/auth/profile";
                if (idString != null && !idString.equals(String.valueOf(currentUser.getId()))) {
                    redirectUrl += "/" + idString;
                }
                req.getRequestDispatcher(redirectUrl).forward(req, res);
                return;
            }

            if (!password.equals(confirmPassword)) {
                LOGGER.warn("Passwords do not match for user ID %s", idString);
                req.setAttribute("message", new Message("Passwords do not match.", "E2A5", "Passwords must be identical."));

                String redirectUrl = "/auth/profile";
                if (idString != null && !idString.equals(String.valueOf(currentUser.getId()))) {
                    redirectUrl += "/" + idString;
                }
                req.getRequestDispatcher(redirectUrl).forward(req, res);
                return;
            }

            int id = Integer.parseInt(idString);

            // Security check: user can only change their own password, unless they are staff/admin (but currently logic restricts to self)
            if (currentUser.getId() != id && currentUser.getUserRole().getId() < 1) {
                LOGGER.warn("User %s with ID %d (role %d) unauthorized to change password for user ID %d.",
                        currentUser.getEmail(), currentUser.getId(), currentUser.getUserRole().getId(), id);
                res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "You are not authorized to change this password.");
                return;
            }

            try (Connection con = getConnection()) {
                String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
                AppUser updatedUser = new ChangePasswordDAO(con, id, hashedPassword).access().getOutputParam();

                if (updatedUser != null) {
                    LOGGER.info("Password for user with ID %d successfully changed.", id);

                    // If user updated his password, create a new JWT
                    if (currentUser.getId() == id) {
                        try {
                            JWTUtils.setJWTCookie(updatedUser, req, res);
                            req.setAttribute("AppUser", updatedUser);
                        } catch (Exception e) {
                            LOGGER.error("Unable to generate JWT after password change.", e);
                        }
                    }

                    // Redirect back with success parameter
                    String redirectUrl = req.getContextPath() + "/auth/profile";
                    if (currentUser.getId() != id) {
                        redirectUrl += "/" + id;
                    }
                    redirectUrl += "?success=password";

                    res.sendRedirect(redirectUrl);
                } else {
                    LOGGER.warn("Failed to change password for user ID %d.", id);
                    req.setAttribute("message", new Message("Password change failed.", "E0A2", "User not found or database error."));

                    String redirectUrl = "/auth/profile";
                    if (currentUser.getId() != id) {
                        redirectUrl += "/" + id;
                    }
                    req.getRequestDispatcher(redirectUrl).forward(req, res);
                }
            }

        } catch (NumberFormatException e) {
            LOGGER.warn("Invalid ID format for password change: %s", req.getParameter("id"));
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid user ID.");
        } catch (Exception e) {
            LOGGER.error("Unable to change user password: unexpected error occurred.", e);
            req.setAttribute("message", new Message("An error occurred while changing the password.", "E0A2", e.getMessage()));
            req.getRequestDispatcher("/auth/profile").forward(req, res);
        } finally {
            LogContext.removeIPAddress();
            LogContext.removeResource();
        }
    }
}
