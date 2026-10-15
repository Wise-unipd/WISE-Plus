package it.unipd.dei.webapp.servlet.movie;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;

import it.unipd.dei.webapp.database.rentals.GetActiveRentalsDAO;
import it.unipd.dei.webapp.servlet.AbstractDatabaseServlet;
import it.unipd.dei.webapp.database.catalog.CheckLikeDAO;
import it.unipd.dei.webapp.database.catalog.CheckWishlistDAO;
import it.unipd.dei.webapp.database.movie.GetMovieDetailsDAO;
import it.unipd.dei.webapp.resource.Actions;
import it.unipd.dei.webapp.resource.authentication.AppUser;
import it.unipd.dei.webapp.resource.LogContext;
import it.unipd.dei.webapp.resource.Message;
import it.unipd.dei.webapp.resource.movie.Movie;
import it.unipd.dei.webapp.resource.movie.MovieDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Gets the details of a movie.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class GetMovieDetailsServlet extends AbstractDatabaseServlet {

    /**
     * Gets the details of a movie.
     */
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        // setup logging
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setAction(Actions.GET_MOVIE_DETAILS);

        // request parameters and models
        int movieId = -1;
        MovieDetails movieDetails = null;
        Boolean isLiked = false;
        Boolean isInWishlist = false;
        Message m = null;

        // business logic and database access
        try {
            // retrieves the request parameter
            movieId = Integer.parseInt(req.getParameter("id"));

            // gets the user ID from the request attribute (set by AuthFilter)
            final AppUser user = (AppUser) req.getAttribute("AppUser");
            final int userId = user.getId();

            // perform the operation on the database
            movieDetails = new GetMovieDetailsDAO(getConnection(), movieId).access().getOutputParam();

            if (movieDetails == null) {
                m = new Message("The requested movie is not available.", "E1A4", null);
                LOGGER.error("The movie with ID %d is not available.", movieId);
            } else {
                Movie movie = movieDetails.getMovie();
                isLiked = new CheckLikeDAO(getConnection(), userId, movie.getId()).access().getOutputParam();
                isInWishlist = new CheckWishlistDAO(getConnection(), userId, movie.getId()).access().getOutputParam();

                // check for active rental
                Timestamp activeRentExpiration = new GetActiveRentalsDAO(getConnection(), userId, movie.getId()).access().getOutputParam();
                req.setAttribute("activeRentExpiration", activeRentExpiration);

                m = new Message("Movie details successfully retrieved.");

                LOGGER.info("Movie details successfully retrieved by ID %d. Is movie liked: %b. Is movie in wishlist: %b. Has movie an active rent: %b.", movieId, isLiked, isInWishlist, (activeRentExpiration != null));
            }
        } catch (NumberFormatException e) {
            m = new Message("Cannot search for movie details. Missing or invalid ID. Please provide a valid integer number.", "E1A0", null);

            LOGGER.error("Cannot search for movie details. Missing or invalid ID. It must be an integer.", e);
        } catch (SQLException e) {
            m = new Message("Cannot search for movie details: system error.", "E0A1", null);

            LOGGER.error("Cannot search for movie details: unexpected error while accessing the database.", e);
        }

        try {
            req.setAttribute("movieDetails", movieDetails);
            req.setAttribute("isLiked", isLiked);
            req.setAttribute("isInWishlist", isInWishlist);
            req.setAttribute("message", m);

            req.getRequestDispatcher("/auth/browse/movie-details.jsp").forward(req, res);
        } finally {
            // final cleaning
            LogContext.removeIPAddress();
            LogContext.removeAction();
        }
    }
}
