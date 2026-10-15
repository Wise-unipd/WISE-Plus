package it.unipd.dei.webapp.servlet.users;

import it.unipd.dei.webapp.database.authentication.UpdateProfileDAO;
import it.unipd.dei.webapp.resource.authentication.AppUser;
import it.unipd.dei.webapp.resource.LogContext;
import it.unipd.dei.webapp.resource.Message;
import it.unipd.dei.webapp.servlet.AbstractDatabaseServlet;
import it.unipd.dei.webapp.utils.JWTUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;

/**
 * Updates the profile of a user.
 * 
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class UpdateProfileServlet extends AbstractDatabaseServlet {

    /**
     * Processes the profile update request.
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

        LOGGER.info("Starting profile update.");

        try {
            AppUser currentUser = (AppUser) req.getAttribute("AppUser");
            if (currentUser == null) {
                LOGGER.warn("User not logged in, but tried to update profile.");
                res.sendRedirect(req.getContextPath() + "/login");
                return;
            }

            String idString = req.getParameter("id");
            if (idString == null || idString.trim().isEmpty()) {
                LOGGER.warn("Missing ID for profile update.");
                res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing user ID.");
                return;
            }

            int id = Integer.parseInt(idString);

            // standard users can only update their own profile
            if (currentUser.getId() != id && currentUser.getUserRole().getId() < 1) {
                LOGGER.warn("User %s with ID %d (role %d) unauthorized to update user ID %d.",
                        currentUser.getEmail(), currentUser.getId(), currentUser.getUserRole().getId(), id);
                res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "You are not authorized to update this profile.");
                return;
            }

            // Parse First Name
            String firstName = req.getParameter("firstName");
            if (firstName != null) {
                firstName = firstName.trim();
                // If JavaScript sent the literal string "undefined", convert it back to actual null
                if (firstName.equals("undefined")) {
                    firstName = null;
                }
            }

            // Parse Last Name
            String lastName = req.getParameter("lastName");
            if (lastName != null) {
                lastName = lastName.trim();
                if (lastName.equals("undefined")) {
                    lastName = null;
                }
            }

            // Determine active status
            Boolean active = null;
            if (currentUser.getUserRole().getId() == 2 && currentUser.getId() != id) {
                String activeStr = req.getParameter("active");
                if (activeStr != null && !activeStr.equals("undefined")) {
                    active = activeStr.equalsIgnoreCase("true") || activeStr.equalsIgnoreCase("on");
                }
            }

            try (Connection con = getConnection()) {
                // Pass variables into the DAO
                AppUser updatedUser = new UpdateProfileDAO(con, id, firstName, lastName, active).access().getOutputParam();

                if (updatedUser != null) {
                    LOGGER.info("Profile for user with ID %d successfully updated.", id);

                    // Redirect back to profile view (the specific profile if it's not the current user)
                    String redirectUrl = req.getContextPath() + "/auth/profile";
                    if (currentUser.getId() != id) {
                        redirectUrl += "/" + id;
                    }
                    redirectUrl += "?success=profile";

                    // If the user updated themselves, we issue a new JWT and redirect
                    if (currentUser.getId() == id) {
                        try {
                            JWTUtils.setJWTCookieAndRedirect(updatedUser, req, res, redirectUrl);
                            return;
                        } catch (Exception e) {
                            LOGGER.error("Unable to generate JWT after profile update.", e);
                            // If JWT generation fails, we attempt standard redirect anyway
                        }
                    }

                    res.sendRedirect(redirectUrl);
                } else {
                    LOGGER.warn("Failed to update profile for user ID %d.", id);
                    req.setAttribute("message", new Message("Update failed.", "E0A1", "User not found or database error."));
                    String redirectUrl = "/auth/profile";
                    if (currentUser.getId() != id) {
                        redirectUrl += "/" + id;
                    }
                    req.getRequestDispatcher(redirectUrl).forward(req, res);
                }
            }

        } catch (NumberFormatException e) {
            LOGGER.warn("Invalid ID format for profile update: %s", req.getParameter("id"));
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid user ID.");

        } catch (Exception e) {
            LOGGER.error("Unable to update user profile: unexpected error occurred.", e);
            req.setAttribute("message", new Message("An error occurred while updating the profile.", "E0A2", e.getMessage()));
            req.getRequestDispatcher("/auth/profile").forward(req, res);

        } finally {
            LogContext.removeIPAddress();
            LogContext.removeResource();

        }
    }
}