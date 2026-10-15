package it.unipd.dei.webapp.servlet.genre;

import it.unipd.dei.webapp.database.genre.GetAllGenresWithStatusDAO;
import it.unipd.dei.webapp.database.movie.SearchMovieByIdDAO;
import it.unipd.dei.webapp.resource.movie.Genre;
import it.unipd.dei.webapp.resource.movie.Movie;
import it.unipd.dei.webapp.resource.LogContext;
import it.unipd.dei.webapp.resource.movie.Selectable;
import it.unipd.dei.webapp.servlet.AbstractDatabaseServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Manages the retrieval and display of all genres, with their selection status.
 * Access is restricted to staff and administrators.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class StaffGenresWithStatusServlet extends AbstractDatabaseServlet {

    /**
     * Handles the HTTP GET request to display the page for linking genres to a movie.
     * Fetches the genres and their status from the database and forwards the data to the JSP view.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setResource(req.getRequestURI());

        LOGGER.info("Showing Genre-Movie Association Page.");

        int movieId = -1;

        try {
            // Retrieve the movie ID from the query string
            movieId = Integer.parseInt(req.getParameter("id"));

            // Database Access: Fetch the movie
            Movie movie = new SearchMovieByIdDAO(getConnection(), movieId).access().getOutputParam();

            if (movie == null) {
                LOGGER.error("Unable to fetch genres with status. Movie with ID %d not found.", movieId);
                res.sendRedirect(req.getContextPath() + "/auth/staff/movies?error=Movie+not+found");
                return;
            }

            // Database Access: Fetch the genres
            List<Selectable<Genre>> genres = new GetAllGenresWithStatusDAO(getConnection(), movieId).access().getOutputParam();

            // Success: set attributes and forward to the view
            req.setAttribute("movie", movie);
            req.setAttribute("genres", genres);
            req.getRequestDispatcher("/auth/staff/movies/link-genres-to-movie.jsp").forward(req, res);

        } catch (NumberFormatException e) {
            LOGGER.error("Cannot retrieve the genres with status. Missing or invalid movie ID.");
            res.sendRedirect(req.getContextPath() + "/auth/staff/movies?error=Missing+or+Invalid+Movie+ID");
        } catch (SQLException e) {
            LOGGER.error("Unable to fetch genres with status from the database.", e);
            res.sendRedirect(req.getContextPath() + "/auth/staff/movies?error=Database+error");
        } catch (Exception e) {
            LOGGER.error("Unexpected error occurred while retrieving genres with status.", e);
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
        } finally {
            LogContext.removeIPAddress();
            LogContext.removeResource();
        }
    }
}