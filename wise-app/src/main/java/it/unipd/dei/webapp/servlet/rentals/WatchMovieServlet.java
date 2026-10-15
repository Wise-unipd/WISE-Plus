package it.unipd.dei.webapp.servlet.rentals;

import it.unipd.dei.webapp.database.rentals.GetActiveRentalsDAO;
import it.unipd.dei.webapp.database.movie.GetMovieDetailsDAO;
import it.unipd.dei.webapp.resource.authentication.AppUser;
import it.unipd.dei.webapp.resource.LogContext;
import it.unipd.dei.webapp.resource.Message;
import it.unipd.dei.webapp.resource.movie.MovieDetails;
import it.unipd.dei.webapp.servlet.AbstractDatabaseServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Timestamp;

/**
 * Servlet responsible for serving the movie watching interface.
 * It enforces security by verifying user authentication and active rental authorization
 * before allowing access to the video stream.
 * This servlet is mapped to /auth/watch/* via web.xml
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class WatchMovieServlet extends AbstractDatabaseServlet {

    /**
     * Handles GET requests to load the movie player.
     * Extracts the movie ID from the URL path, validates the rental status,
     * and forwards the user to the player JSP.
     *
     * @param req the HTTP request from the client.
     * @param res the HTTP response from the server.
     * @throws ServletException if a server-side error occurs.
     * @throws IOException      if any error occurs during client/server communication.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setResource(req.getRequestURI());

        // ensure the user is authenticated via JWT
        AppUser loggedUser = (AppUser) req.getAttribute("AppUser");
        if (loggedUser == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try {
            int userId = loggedUser.getId();

            // extract the movie ID from the RESTful path
            String pathInfo = req.getPathInfo();

            // validate that a path parameter was actually provided
            if (pathInfo == null || pathInfo.equals("/")) {
                res.sendRedirect(req.getContextPath() + "/catalog");
                return;
            }

            // strip the leading slash to isolate the numeric ID string
            String movieIdStr = pathInfo.substring(1);
            int movieId = Integer.parseInt(movieIdStr);

            // query the database to verify an active rental
            // prevents users from bypassing payment by typing the URL directly
            Timestamp activeRentExpiration = new GetActiveRentalsDAO(getConnection(), userId, movieId).access().getOutputParam();

            if (activeRentExpiration == null) {
                // access denied: no active rent found, redirect back to the movie details page.
                LOGGER.warn("User %d attempted to watch movie %d without an active rent.", userId, movieId);
                res.sendRedirect(req.getContextPath() + "/auth/movie?id=" + movieId);
                return;
            }

            // fetch movie details
            MovieDetails movieDetails = new GetMovieDetailsDAO(getConnection(), movieId).access().getOutputParam();

            if (movieDetails == null || movieDetails.getMovie().getTrailer() == null || movieDetails.getMovie().getTrailer().isBlank()) {
                // the movie exists and was rented, but the media link is missing
                req.setAttribute("message", new Message("Sorry, the video stream for this movie is currently unavailable.", "E1A4", null));
                req.getRequestDispatcher("/auth/browse/movie-details.jsp").forward(req, res);
                return;
            }

            // pass the validated data to the viewing page
            req.setAttribute("movieDetails", movieDetails);
            req.setAttribute("expiration", activeRentExpiration);

            LOGGER.info("User %d started watching movie %d (rent expires at %s).",
                    userId, movieId, activeRentExpiration.toString());

            req.getRequestDispatcher("/auth/watch-movie.jsp").forward(req, res);

        } catch (NumberFormatException e) {
            // handles cases where a user types a non-numeric ID in the URL
            LOGGER.error("Invalid movie ID format provided in URL path.", e);
            res.sendRedirect(req.getContextPath() + "/catalog");
        } catch (Exception e) {
            // catches database failures or unexpected runtime errors
            LOGGER.error("Unexpected error occurred while processing WatchMovieServlet.", e);
            throw new RuntimeException(e);
        } finally {
            // clean up the logging context to prevent memory leaks or misattributed logs
            LogContext.removeIPAddress();
            LogContext.removeResource();
        }
    }
}