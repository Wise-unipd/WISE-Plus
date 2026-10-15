package it.unipd.dei.webapp.servlet.rentals;

import it.unipd.dei.webapp.database.rentals.GetActiveRentalsByUserAndMovieIdDAO;
import it.unipd.dei.webapp.database.rentals.InsertRentDAO;
import it.unipd.dei.webapp.database.rentals.InsertRentExpirationDAO;
import it.unipd.dei.webapp.database.movie.SearchMovieByIdDAO;
import it.unipd.dei.webapp.database.rentals.InsertTransactionDAO;
import it.unipd.dei.webapp.resource.authentication.AppUser;
import it.unipd.dei.webapp.resource.LogContext;
import it.unipd.dei.webapp.resource.Message;
import it.unipd.dei.webapp.resource.movie.Movie;
import it.unipd.dei.webapp.resource.rentals.Rental;
import it.unipd.dei.webapp.servlet.AbstractDatabaseServlet;
import it.unipd.dei.webapp.utils.EmailSender;
import it.unipd.dei.webapp.utils.PaymentValidator;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Servlet for handling movie rental payments.
 *
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class PaymentServlet extends AbstractDatabaseServlet {

    /**
     * Displays the payment page.
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
            final String movieIdStr = req.getParameter("movieId");
            final String daysStr = req.getParameter("days");

            if (movieIdStr == null || movieIdStr.isBlank()) {
                LOGGER.error("Invalid payment request: missing movieId");
                req.setAttribute("message", new Message("Invalid payment request. Missing movie or days.", "E4A0", null));
                res.sendRedirect(req.getContextPath() + "/auth/catalog");
                return;
            }

            final int movieId = Integer.parseInt(movieIdStr);

            if (daysStr == null || daysStr.isBlank()) {
                LOGGER.error("Invalid payment request: missing days.");
                req.setAttribute("message", new Message("Invalid payment request. Missing movie or days.", "E4A0", null));
                res.sendRedirect(req.getContextPath() + "/auth/movie?id=" + movieId);
                return;
            }

            final int days = Integer.parseInt(daysStr);

            if (days <= 0 || days > 30) {
                LOGGER.error("Invalid payment request: days must be positive.");
                req.setAttribute("message", new Message("Number of days must be greater than zero.", "E4A0", null));
                res.sendRedirect(req.getContextPath() + "/auth/movie?id=" + movieId);
                return;
            }

            final Rental previous_rental = new GetActiveRentalsByUserAndMovieIdDAO(getConnection(), loggedUser.getId(), movieId).access().getOutputParam();
            LOGGER.info("Previous rental for user {} and movie {}: {}", loggedUser.getId(), movieId, previous_rental);

            if (previous_rental != null) {
                res.sendRedirect(req.getContextPath() + "/auth/watch/" + movieId);
                return;
            }

            // fetch the movie to get the actual base price
            final Movie movie = new SearchMovieByIdDAO(getConnection(), movieId).access().getOutputParam();

            if (movie == null) {
                LOGGER.error("Movie with ID %d not found.", movieId);
                req.setAttribute("message", new Message("Movie not found.", "E1A4", null));
                res.sendRedirect(req.getContextPath() + "/auth/catalog");
                return;
            }

            // do a secure, server-side calculation of the price to be payed
            final double realTotal = movie.getBasePriceEur() * days;

            req.setAttribute("movie", movie);
            req.setAttribute("total", realTotal);
            req.setAttribute("days", days);

            LOGGER.info("Payment page prepared for movie %d and %d days.", movieId, days);

            req.getRequestDispatcher("/auth/payment.jsp").forward(req, res);

        } catch (NumberFormatException e) {
            LOGGER.error("Invalid format for movieId or days.", e);
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
     * Processes the movie rental payment and updates the database.
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
                LOGGER.error("Payment validation failed for user %d: Days must be positive.", userId);
                res.sendRedirect(req.getContextPath() + "/auth/movie?id=" + movieId);
                return;
            }

            // Capture payment variables
            final String cardNumber = req.getParameter("cardNumber");
            final String expiryDate = req.getParameter("expiryDate");
            final String cvv = req.getParameter("cvv");

            // Execute Server-side Validation
            Message validationMessage = PaymentValidator.validatePayment(cardNumber, expiryDate, cvv);
            if (validationMessage != null) {
                LOGGER.error("Payment validation failed for user %d: %s", userId, validationMessage.getMessage());

                final Movie movie = new SearchMovieByIdDAO(getConnection(), movieId).access().getOutputParam();
                req.setAttribute("movie", movie);
                req.setAttribute("total", movie.getBasePriceEur() * days);
                req.setAttribute("days", days);

                req.setAttribute("message", validationMessage);
                req.getRequestDispatcher("/auth/payment.jsp").forward(req, res);
                return;
            }

            final Rental previous_rental = new GetActiveRentalsByUserAndMovieIdDAO(getConnection(), userId, movieId).access().getOutputParam();
            LOGGER.info("Previous rental for user {} and movie {}: {}", userId, movieId, previous_rental);

            if (previous_rental != null) {
                res.sendRedirect(req.getContextPath() + "/auth/watch/" + movieId);
                return;
            }

            // compute the real total price server-side (avoid security issues)
            final Movie movie = new SearchMovieByIdDAO(getConnection(), movieId).access().getOutputParam();
            final double serverCalculatedTotal = movie.getBasePriceEur() * days;

            // insert in 'rent', 'rent_expiration' and 'transaction' table
            final int rentId = new InsertRentDAO(getConnection(), userId, movieId).access().getOutputParam();
            new InsertRentExpirationDAO(getConnection(), rentId, days).access().getOutputParam();
            new InsertTransactionDAO(getConnection(), rentId, serverCalculatedTotal, "rent").access();

            LOGGER.info("Payment processed successfully for user %d and movie %d (%d days, total: %.2f EUR).", userId, movieId, days, serverCalculatedTotal);

            // send the email to the user as confirmation of its rent
            EmailSender.sendEmail(loggedUser.getEmail(), "New rental in WISE!", "Thank for your new rent!");

            res.sendRedirect(req.getContextPath() + "/auth/movie?id=" + movieId);

        } catch (Exception e) {
            LOGGER.error("Unable to process payment.", e);
            req.setAttribute("message", new Message("An error occurred while processing payment.", "E0A2", e.getMessage()));
            req.getRequestDispatcher("/auth/payment.jsp").forward(req, res);

        } finally {
            LogContext.removeIPAddress();
            LogContext.removeResource();
        }
    }
}