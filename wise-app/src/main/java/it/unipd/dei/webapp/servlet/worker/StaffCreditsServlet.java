package it.unipd.dei.webapp.servlet.worker;

import it.unipd.dei.webapp.database.worker.GetAllCreditsDAO;
import it.unipd.dei.webapp.database.worker.GetAllMovieRolesDAO;
import it.unipd.dei.webapp.database.movie.SearchMovieByIdDAO;
import it.unipd.dei.webapp.resource.movie.Credit;
import it.unipd.dei.webapp.resource.LogContext;
import it.unipd.dei.webapp.resource.movie.Movie;
import it.unipd.dei.webapp.resource.movie.MovieRole;
import it.unipd.dei.webapp.servlet.AbstractDatabaseServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Manages the retrieval and display of the credits associated with a movie.
 * Access is restricted to staff and administrators.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class StaffCreditsServlet extends AbstractDatabaseServlet {

    /**
     * Handles the HTTP GET request to display the page for linking credits to a movie.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setResource(req.getRequestURI());

        LOGGER.info("Showing Credit-Movie Association Page.");

        int movieId = -1;

        try {
            // Retrieve the movie ID from the query string
            movieId = Integer.parseInt(req.getParameter("id"));

            // Database Access: Fetch the movie
            Movie movie = new SearchMovieByIdDAO(getConnection(), movieId).access().getOutputParam();

            if (movie == null) {
                LOGGER.error("Unable to fetch credits. Movie with ID %d not found.", movieId);
                res.sendRedirect(req.getContextPath() + "/auth/staff/movies?error=Movie+not+found");
                return;
            }

            // Database Access: Fetch the movie roles
            List<MovieRole> movieRoles = new GetAllMovieRolesDAO(getConnection()).access().getOutputParam();

            // Database Access: Fetch the movie credits
            List<Credit> credits = new GetAllCreditsDAO(getConnection(), movieId).access().getOutputParam();

            // Success: set attributes and forward to the view
            req.setAttribute("movie", movie);
            req.setAttribute("movieRoles", movieRoles);
            req.setAttribute("credits", credits);
            req.getRequestDispatcher("/auth/staff/movies/link-credits-to-movie.jsp").forward(req, res);

        } catch (NumberFormatException e) {
            LOGGER.error("Unable to fetch credits. Invalid or missing movie ID.");
            res.sendRedirect(req.getContextPath() + "/auth/staff/movies?error=Invalid+Movie+ID");
        } catch (SQLException e) {
            LOGGER.error("Database error: unable to fetch credits.", e);
            res.sendRedirect(req.getContextPath() + "/auth/staff/movies?error=Database+error");
        } catch (Exception e) {
            LOGGER.error("Unexpected error occurred while retrieving credits.", e);
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
        } finally {
            LogContext.removeIPAddress();
            LogContext.removeResource();
        }
    }
}