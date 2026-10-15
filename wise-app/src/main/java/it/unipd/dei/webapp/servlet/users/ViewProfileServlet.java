package it.unipd.dei.webapp.servlet.users;

import it.unipd.dei.webapp.database.rentals.GetRefundableAmountsByUserDAO;
import it.unipd.dei.webapp.database.rentals.GetRentalsByUserDAO;
import it.unipd.dei.webapp.database.authentication.GetProfileByIDDAO;
import it.unipd.dei.webapp.resource.authentication.AppUser;

import it.unipd.dei.webapp.resource.LogContext;
import it.unipd.dei.webapp.resource.rentals.Rental;
import it.unipd.dei.webapp.servlet.AbstractDatabaseServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Date; // <-- NEW IMPORT
import java.util.List;
import java.util.Map;

/**
 * Servlet responsible for rendering the user profile page.
 * This servlet handles two distinct paths:
 * 1. "/auth/profile" -> Renders the logged-in user's personal profile.
 * 2. "/auth/profile/{id}" -> Renders the profile of the specified user ID
 * (Restricted to Staff and Admins only).
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class ViewProfileServlet extends AbstractDatabaseServlet {

    /**
     * Processes the HTTP GET requests to retrieve and display user profiles.
     *
     * @param req the HTTP request containing attributes and potential path parameters.
     * @param res the HTTP response used to forward to the JSP or return HTTP error codes.
     * @throws ServletException if a servlet-specific error occurs during JSP forwarding.
     * @throws IOException      if an I/O error occurs during request handling.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setResource(req.getRequestURI());

        LOGGER.info("Accessing profile view.");

        try {
            AppUser currentUser = (AppUser) req.getAttribute("AppUser");

            if (currentUser == null) {
                LOGGER.warn("Unauthorized access attempt to profile view.");
                res.sendRedirect(req.getContextPath() + "/login");
                return;
            }

            // set the accessing user (the logged-in user) for the JSP
            req.setAttribute("AccessingUser", currentUser);

            // current time of the request:  to check if the movie is still watchable, or it is expired
            req.setAttribute("now", new java.sql.Timestamp(System.currentTimeMillis()));

            // determine if the user is trying to view their own profile or someone else's
            String pathInfo = req.getPathInfo();

            // check if there is extra path information (e.g., "/123")
            if (pathInfo != null && !pathInfo.equals("/")) {

                // block standard users from viewing other people's profiles.
                if (currentUser.getUserRole().getId() < 1) {
                    LOGGER.warn("Unauthorized access attempt to another user's profile by user %s (role ID %d).",
                            currentUser.getEmail(), currentUser.getUserRole().getId());
                    res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "UNAUTHORIZED");
                    return;
                }

                // parse the requested user ID from the URL path
                String[] parts = pathInfo.split("/");
                if (parts.length != 2) {
                    LOGGER.warn("Malformed URL for profile view: %s. Expected /auth/profile/{id}.", pathInfo);
                    res.sendError(HttpServletResponse.SC_BAD_REQUEST, "MALFORMED URL");
                    return;
                }

                String id = parts[1];

                try {
                    int value = Integer.parseInt(id);
                    LOGGER.info("Loading profile for user ID %d.", value);

                    // fetch the target user's basic profile details
                    AppUser profile = new GetProfileByIDDAO(getConnection(), value).access().getOutputParam();
                    if (profile == null) {
                        LOGGER.warn("User profile with ID %d not found.", value);
                        res.sendError(HttpServletResponse.SC_NOT_FOUND, "USER NOT FOUND");
                        return;
                    }

                    LOGGER.info("Profile for user %s (ID %d) successfully loaded.", profile.getEmail(), value);

                    // fetch the target user's rental history
                    List<Rental> rentals = new GetRentalsByUserDAO(getConnection(), value).access().getOutputParam();

                    // Sort the rentals before sending to JSP
                    sortRentals(rentals);

                    // fetch the refund statuses for the target user's rentals
                    Map<Integer, Double[]> refundableAmounts = new GetRefundableAmountsByUserDAO(getConnection(), value).access().getOutputParam();

                    // attach all retrieved data to the request to be rendered by the JSP
                    req.setAttribute("refundableAmounts", refundableAmounts);
                    req.setAttribute("AppUser", profile);
                    req.setAttribute("rentals", rentals);
                    req.getRequestDispatcher("/auth/profile.jsp").forward(req, res);

                    return;

                } catch (NumberFormatException e) {
                    LOGGER.warn("Malformed URL for profile view: %s. ID is not a valid integer.", pathInfo);
                    res.sendError(HttpServletResponse.SC_BAD_REQUEST, "MALFORMED URL");
                    return;
                }
            }

            // Default: show personal profile
            LOGGER.info("Loading personal profile for user %s (ID %d).", currentUser.getEmail(), currentUser.getId());

            // fetch personal rental history
            List<Rental> rentals = new GetRentalsByUserDAO(getConnection(), currentUser.getId()).access().getOutputParam();

            // Sort the rentals before sending to JSP
            sortRentals(rentals);

            // fetch personal refund statuses
            Map<Integer, Double[]> refundableAmounts = new GetRefundableAmountsByUserDAO(getConnection(), currentUser.getId()).access().getOutputParam();
            req.setAttribute("refundableAmounts", refundableAmounts);

            // attach personal data to the request and forward to the JSP
            req.setAttribute("AppUser", currentUser);
            req.setAttribute("rentals", rentals);
            req.getRequestDispatcher("/auth/profile.jsp").forward(req, res);

        } catch (Exception e) {
            LOGGER.error("Unable to load user profile: unexpected error occurred.", e);
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while loading the profile.");

        } finally {
            LogContext.removeIPAddress();
            LogContext.removeResource();
        }
    }

    /**
     * Handles HTTP POST requests by delegating them to the doGet method.
     * This allows forms that submit via POST to safely reload the profile view.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        doGet(req, res);
    }

    /**
     * Helper method to sort a list of rentals.
     * Sorts first by Active Status (Expiration in the future), then Alphabetically by Movie Title.
     * * @param rentals The list of rentals to sort.
     */
    private void sortRentals(List<Rental> rentals) {
        if (rentals == null || rentals.isEmpty()) {
            return;
        }

        Date now = new Date();

        rentals.sort((a, b) -> {
            // Sort by Active Status
            boolean aActive = a.getExpiration() != null && a.getExpiration().after(now);
            boolean bActive = b.getExpiration() != null && b.getExpiration().after(now);

            if (aActive != bActive) {
                return aActive ? -1 : 1; // Active comes before Inactive
            }

            // Sort by Movie Title (Alphabetically A-Z)
            String aTitle = a.getMovieTitle() != null ? a.getMovieTitle().toLowerCase() : "";
            String bTitle = b.getMovieTitle() != null ? b.getMovieTitle().toLowerCase() : "";

            return aTitle.compareTo(bTitle);
        });
    }
}