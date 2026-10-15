package it.unipd.dei.webapp.servlet.category;

import it.unipd.dei.webapp.database.category.GetAllCategoriesWithStatusDAO;
import it.unipd.dei.webapp.database.movie.SearchMovieByIdDAO;
import it.unipd.dei.webapp.resource.movie.Category;
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
 * Manages the retrieval and display of all categories, with their selection status.
 * Access is restricted to staff and administrators.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class StaffCategoriesWithStatusServlet extends AbstractDatabaseServlet {

    /**
     * Handles the HTTP GET request to display the page for linking categories to a movie.
     * Fetches the categories and their status from the database and forwards the data to the JSP view.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setResource(req.getRequestURI());

        LOGGER.info("Showing Category-Movie Association Page.");

        int movieId = -1;

        try {
            // Retrieve the movie ID from the query string
            movieId = Integer.parseInt(req.getParameter("id"));

            // Database Access: Fetch the movie
            Movie movie = new SearchMovieByIdDAO(getConnection(), movieId).access().getOutputParam();

            if (movie == null) {
                LOGGER.error("Unable to fetch categories with status. Movie with ID %d not found.", movieId);
                res.sendRedirect(req.getContextPath() + "/auth/staff/movies?error=Movie+not+found");
                return;
            }

            // Database Access: Fetch the categories
            List<Selectable<Category>> categories = new GetAllCategoriesWithStatusDAO(getConnection(), movieId).access().getOutputParam();

            // Success: set attributes and forward to the view
            req.setAttribute("movie", movie);
            req.setAttribute("categories", categories);
            req.getRequestDispatcher("/auth/staff/movies/link-categories-to-movie.jsp").forward(req, res);

        } catch (NumberFormatException e) {
            LOGGER.error("Cannot retrieve categories with status. Missing or invalid movie ID.");
            res.sendRedirect(req.getContextPath() + "/auth/staff/movies?error=Missing+or+Invalid+Movie+ID");
        } catch (SQLException e) {
            LOGGER.error("Unable to fetch categories with status from the database.", e);
            res.sendRedirect(req.getContextPath() + "/auth/staff/movies?error=Database+error");
        } catch (Exception e) {
            LOGGER.error("Unexpected error occurred while retrieving categories with status.", e);
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
        } finally {
            LogContext.removeIPAddress();
            LogContext.removeResource();
        }
    }
}