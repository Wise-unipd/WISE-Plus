package it.unipd.dei.webapp.servlet.catalog;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import it.unipd.dei.webapp.servlet.AbstractDatabaseServlet;
import it.unipd.dei.webapp.database.movie.GetCategoriesWithMoviesDAO;
import it.unipd.dei.webapp.resource.Actions;
import it.unipd.dei.webapp.resource.movie.CategoryWithMovies;
import it.unipd.dei.webapp.resource.LogContext;
import it.unipd.dei.webapp.resource.Message;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Retrieves a movie catalog.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
@WebServlet(name = "MovieCatalog", value = "/catalog")
public final class MovieCatalogServlet extends AbstractDatabaseServlet {

    /**
     * Retrieves a movie catalog.
     */
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        // setup logging
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setAction(Actions.GET_MOVIE_CATALOG);

        // models
        List<CategoryWithMovies> collections = null;
        Message m = null;

        // business logic and database access
        try {
            // perform the operation on the database
            collections = new GetCategoriesWithMoviesDAO(getConnection(), 20, 30).access().getOutputParam();

            m = new Message("Movie catalog successfully retrieved.");

            LOGGER.info("Movie catalog successfully retrieved.");

            req.setAttribute("collections", collections);
            req.setAttribute("message", m);

            req.getRequestDispatcher("/auth/browse/movie-catalog-result.jsp").forward(req, res);
        } catch (SQLException e) {
            LOGGER.error("Cannot retrieve a movie catalog: unexpected error while accessing the database.", e);

            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Cannot retrieve a movie catalog: system error.");
        } finally {
            // final cleaning
            LogContext.removeIPAddress();
            LogContext.removeAction();
        }
    }
}
