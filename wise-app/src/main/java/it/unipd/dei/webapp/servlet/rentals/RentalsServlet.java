package it.unipd.dei.webapp.servlet.rentals;

import it.unipd.dei.webapp.database.rentals.GetAllRentalsDAO;
import it.unipd.dei.webapp.resource.authentication.AppUser;
import it.unipd.dei.webapp.resource.LogContext;
import it.unipd.dei.webapp.resource.Message;
import it.unipd.dei.webapp.resource.rentals.Rental;
import it.unipd.dei.webapp.servlet.AbstractDatabaseServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Servlet for managing and displaying all rentals (staff/admin only).
 * 
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class RentalsServlet extends AbstractDatabaseServlet {
    /**
     * Displays the rentals page.
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

        try {
            // authorization check, block standard users from accessing this endpoint
            AppUser loggedUser = (AppUser) req.getAttribute("AppUser");
            if (loggedUser.getUserRole().getId() < 1) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "You don't have permission to access this page.");
                return;
            }

            List<Rental> rentals = new GetAllRentalsDAO(getConnection()).access().getOutputParam();
            req.setAttribute("rentals", rentals);

            LOGGER.info("Successfully fetched %d rentals for staff/admin user %d.", rentals.size(), loggedUser.getId());

            req.getRequestDispatcher("/auth/rentals.jsp").forward(req, res);

        } catch (SQLException e) {
            LOGGER.error("Unable to fetch rentals.", e);
            req.setAttribute("message", new Message("An error occurred while accessing the database.", "E0A1", e.getMessage()));
            req.getRequestDispatcher("/auth/rentals.jsp").forward(req, res);

        } finally {
            LogContext.removeIPAddress();
            LogContext.removeResource();
        }
    }
}