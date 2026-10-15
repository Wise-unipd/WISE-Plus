package it.unipd.dei.webapp.servlet.category;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import it.unipd.dei.webapp.servlet.AbstractDatabaseServlet;
import it.unipd.dei.webapp.database.movie.SearchMoviesByCategoryIdDAO;
import it.unipd.dei.webapp.database.category.SearchCategoryByIdDAO;
import it.unipd.dei.webapp.resource.Actions;
import it.unipd.dei.webapp.resource.LogContext;
import it.unipd.dei.webapp.resource.Message;
import it.unipd.dei.webapp.resource.movie.Movie;
import it.unipd.dei.webapp.resource.movie.Category;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Searches for a category and associated movies by the category ID.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
@WebServlet(name = "CategoryServlet", value = "/category")
public final class CategoryServlet extends AbstractDatabaseServlet {

    /**
     * Searches for movies and category details by the category's ID.
     */
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        // setup logging
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setAction(Actions.SEARCH_MOVIES_BY_CATEGORY_ID);

        // request parameters and models
        int categoryId = -1;
        Category category = null;
        List<Movie> movies = null;
        Message m = null;

        // business logic and database access
        try {
            // retrieves the request parameter
            categoryId = Integer.parseInt(req.getParameter("id"));

            // perform the operation on the database
            category = new SearchCategoryByIdDAO(getConnection(), categoryId, true).access().getOutputParam();

            if (category == null) {
                m = new Message("The requested category is not available.", "E1A4", null);
                LOGGER.error("The category with ID %d is not available.", categoryId);
            } else {
                // if category exists, fetch the filmography
                movies = new SearchMoviesByCategoryIdDAO(getConnection(), categoryId).access().getOutputParam();

                m = new Message("Category profile and filmography successfully retrieved.");

                LOGGER.info("Category and movies successfully searched by ID %d.", categoryId);
            }
        } catch (NumberFormatException e) {
            m = new Message("Cannot search for a category. Missing or invalid ID. Please provide a valid integer number.", "E1A0", null);

            LOGGER.error("Cannot search for a category. Missing or invalid ID. It must be an integer.", e);
        } catch (SQLException e) {
            m = new Message("Cannot search for a category: system error.", "E0A1", null);

            LOGGER.error("Cannot search for a category: unexpected error while accessing the database.", e);
        }

        try {
            req.setAttribute("category", category);
            req.setAttribute("movies", movies);
            req.setAttribute("message", m);

            req.getRequestDispatcher("/auth/browse/category.jsp").forward(req, res);
        } finally {
            // final cleaning
            LogContext.removeIPAddress();
            LogContext.removeAction();
        }
    }
}