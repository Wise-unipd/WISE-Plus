package it.unipd.dei.webapp.servlet.language;

import it.unipd.dei.webapp.database.language.GetAllAudiosWithStatusDAO;
import it.unipd.dei.webapp.database.movie.SearchMovieByIdDAO;
import it.unipd.dei.webapp.resource.movie.Language;
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
 * Manages the retrieval and display of all audios, with their selection status relative to a specific movie.
 * Access to this servlet is restricted to staff and administrators.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class StaffAudiosWithStatusServlet extends AbstractDatabaseServlet {

    /**
     * Handles the HTTP GET request to display the page for linking audios to a movie.
     * Fetches the audios and their status from the database and forwards the data to the JSP view.
     *
     * @param req the HTTP request containing the movie ID parameter.
     * @param res the HTTP response used to forward the data or redirect in case of errors.
     * @throws ServletException if a servlet-specific error occurs.
     * @throws IOException if an I/O error occurs during request processing.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setResource(req.getRequestURI());

        LOGGER.info("Showing Audio-Movie Association Page.");

        int movieId = -1;

        try {
            // Retrieve the movie ID from the query string
            movieId = Integer.parseInt(req.getParameter("id"));

            // Database Access: Fetch the movie
            Movie movie = new SearchMovieByIdDAO(getConnection(), movieId).access().getOutputParam();

            if (movie == null) {
                LOGGER.error("Unable to fetch audio languages with status. Movie with ID %d not found.", movieId);
                res.sendRedirect(req.getContextPath() + "/auth/staff/movies?error=Movie+not+found");
                return;
            }

            // Fetch all audios (as Language objects) and check if they are linked to this movie
            List<Selectable<Language>> audios = new GetAllAudiosWithStatusDAO(getConnection(), movieId).access().getOutputParam();

            // Set attributes and forward to the corresponding JSP view
            req.setAttribute("movie", movie);
            req.setAttribute("audios", audios);
            req.getRequestDispatcher("/auth/staff/movies/link-audios-to-movie.jsp").forward(req, res);

        } catch (NumberFormatException e) {
            LOGGER.error("Cannot retrieve audio languages with status. Missing or invalid movie ID.");
            res.sendRedirect(req.getContextPath() + "/auth/staff/movies?error=Missing+or+Invalid+Movie+ID");
        } catch (SQLException e) {
            LOGGER.error("Unable to fetch audio languages with status from the database.", e);
            res.sendRedirect(req.getContextPath() + "/auth/staff/movies?error=Database+error");
        } catch (Exception e) {
            LOGGER.error("Unexpected error occurred while retrieving audio languages with status.", e);
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
        } finally {
            LogContext.removeIPAddress();
            LogContext.removeResource();
        }
    }
}