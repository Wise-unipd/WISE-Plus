package it.unipd.dei.webapp.servlet.rentals;

import it.unipd.dei.webapp.database.rentals.*;
import it.unipd.dei.webapp.database.movie.SearchMovieByIdDAO;
import it.unipd.dei.webapp.resource.authentication.AppUser;
import it.unipd.dei.webapp.resource.LogContext;
import it.unipd.dei.webapp.resource.Message;
import it.unipd.dei.webapp.resource.movie.Movie;
import it.unipd.dei.webapp.resource.rentals.Rental;
import it.unipd.dei.webapp.servlet.AbstractDatabaseServlet;
import it.unipd.dei.webapp.utils.PaymentValidator;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Servlet for extending an existing rental or creating a new one for a movie.
 * 
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class ExtendRentServlet extends AbstractDatabaseServlet {

    /**
     * Prepares the payment page with the calculated total price for the requested rental duration.
     *
     * @param req the HTTP request.
     * @param res the HTTP response.
     * @throws ServletException if any error occurs while processing the request.
     * @throws IOException      if any error occurs while sending the response.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setResource(req.getRequestURI());

        // check the authentication
        AppUser loggedUser = (AppUser) req.getAttribute("AppUser");
        if (loggedUser == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try {
            final String requestURI = req.getRequestURI();
            final String movieIdStr = requestURI.substring(requestURI.lastIndexOf("/") + 1);

            if (movieIdStr.isBlank()) {
                LOGGER.error("Invalid payment request: missing movie ID in the URI.");
                req.setAttribute("message", new Message("Invalid payment request. Missing movie.", "E4A0", null));
                res.sendRedirect(req.getContextPath() + "/auth/catalog");
                return;
            }

            final int movieId = Integer.parseInt(movieIdStr);

            final Movie movie = new SearchMovieByIdDAO(getConnection(), movieId).access().getOutputParam();

            if (movie == null) {
                LOGGER.error("Movie with ID %d not found.", movieId);
                req.setAttribute("message", new Message("Movie not found.", "E1A4", null));
                res.sendRedirect(req.getContextPath() + "/auth/catalog");
                return;
            }

            // Check if the user actually has an active rental to extend
            final Rental rent = new GetActiveRentalsByUserAndMovieIdDAO(getConnection(), loggedUser.getId(), movieId).access().getOutputParam();

            if (rent == null) {
                LOGGER.error("No active rent found for user %d and movie %d.", loggedUser.getId(), movieId);
                req.setAttribute("message", new Message("No active rental found to extend.", "E4A1", null));
                res.sendRedirect(req.getContextPath() + "/auth/movie?id=" + movieId);
                return;
            }

            req.setAttribute("movie", movie);
            req.setAttribute("basePrice", movie.getBasePriceEur());

            // Pass the current expiration time in milliseconds so JS can calculate the new date
            req.setAttribute("currentExpirationMs", rent.getExpiration().getTime());

            LOGGER.info("Extension page prepared for movie %d.", movieId);

            req.getRequestDispatcher("/auth/extend_movies.jsp").forward(req, res);

        } catch (NumberFormatException e) {
            LOGGER.error("Invalid movie ID format.", e);
            req.setAttribute("message", new Message("Invalid number format provided.", "E1A0", e.getMessage()));
            res.sendRedirect(req.getContextPath() + "/auth/catalog");

        } catch (SQLException e) {
            LOGGER.error("Unable to access the database to retrieve movie details.", e);
            throw new ServletException("Unable to access the database.", e);

        } finally {
            LogContext.removeIPAddress();
            LogContext.removeResource();
        }
    }

        /**
         * Processes the rent extension payment and updates the database.
         * Applies strict validation on the credit card inputs via {@link PaymentValidator}.
         *
         * @param req the HTTP request.
         * @param res the HTTP response.
         * @throws ServletException if any error occurs while processing the request.
         * @throws IOException      if any error occurs while sending the response.
         */
        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
            LogContext.setIPAddress(req.getRemoteAddr());
            LogContext.setResource(req.getRequestURI());

            AppUser loggedUser = (AppUser) req.getAttribute("AppUser");
            if (loggedUser == null) {
                res.sendRedirect(req.getContextPath() + "/login");
                return;
            }

            try {
                final int userId = loggedUser.getId();
                final int movieId = Integer.parseInt(req.getParameter("movieId"));
                final int days = Integer.parseInt(req.getParameter("days"));

                if (days <= 0 || days > 30) {
                    LOGGER.error("Extension validation failed for user %d: Days must be positive.", userId);
                    final Movie movie = new SearchMovieByIdDAO(getConnection(), movieId).access().getOutputParam();
                    req.setAttribute("movie", movie);
                    req.setAttribute("basePrice", movie.getBasePriceEur());
                    req.setAttribute("message", new Message("Number of days must be greater than zero.", "E4A0", null));
                    req.getRequestDispatcher("/auth/extend_movies.jsp").forward(req, res);
                    return;
                }

                // Capture payment variables
                final String cardNumber = req.getParameter("cardNumber");
                final String expiryDate = req.getParameter("expiryDate");
                final String cvv = req.getParameter("cvv");

                // Execute Server-side Validation
                Message validationMessage = PaymentValidator.validatePayment(cardNumber, expiryDate, cvv);
                if (validationMessage != null) {
                    LOGGER.error("Extension validation failed for user %d: %s", userId, validationMessage.getMessage());

                    final Movie movie = new SearchMovieByIdDAO(getConnection(), movieId).access().getOutputParam();
                    req.setAttribute("movie", movie);
                    req.setAttribute("basePrice", movie.getBasePriceEur()); // For extension UI reconstruction

                    req.setAttribute("message", validationMessage);
                    req.getRequestDispatcher("/auth/extend_movies.jsp").forward(req, res);
                    return;
                }

                // compute the real total price server-side (avoid security issues)
                final Movie movie = new SearchMovieByIdDAO(getConnection(), movieId).access().getOutputParam();
                final double serverCalculatedTotal = movie.getBasePriceEur() * days;

                // insert in 'rent', 'rent_expiration' and 'transaction' table
                final Rental rent = new GetActiveRentalsByUserAndMovieIdDAO(getConnection(), userId, movieId).access().getOutputParam();

                if (rent == null) {
                    LOGGER.error("No active rent found for user %d and movie %d.", userId, movieId);
                    req.setAttribute("message", new Message("No active rental found to extend.", "E4A1", null));
                    res.sendRedirect(req.getContextPath() + "/auth/movie?id=" + movieId);
                    return;
                }

                final int rentId = rent.getId();
                new ExtendRentExpirationDAO(getConnection(), rentId, days, rent.getExpiration()).access().getOutputParam();
                new InsertTransactionDAO(getConnection(), rentId, serverCalculatedTotal, "extension").access();

                LOGGER.info("Rent extension processed successfully for user %d and movie %d (%d days, total: %.2f EUR).", userId, movieId, days, serverCalculatedTotal);

                res.sendRedirect(req.getContextPath() + "/auth/movie?id=" + movieId);

            } catch (Exception e) {
                LOGGER.error("Unable to process rent extension.", e);
                req.setAttribute("message", new Message("An error occurred while processing the rent extension.", "E0A2", e.getMessage()));
                res.sendRedirect(req.getContextPath() + "/auth/catalog");

            } finally {
                LogContext.removeIPAddress();
                LogContext.removeResource();
            }
        }
    }