package it.unipd.dei.webapp.servlet.catalog;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import it.unipd.dei.webapp.servlet.AbstractDatabaseServlet;

import it.unipd.dei.webapp.database.movie.GetTrendingMoviesDAO;
import it.unipd.dei.webapp.resource.Actions;
import it.unipd.dei.webapp.resource.authentication.AppUser;
import it.unipd.dei.webapp.resource.LogContext;
import it.unipd.dei.webapp.resource.Message;
import it.unipd.dei.webapp.resource.movie.Movie;
import it.unipd.dei.webapp.utils.JWTUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Retrieves trending movies.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
@WebServlet(name = "Home", value = "/home")
public final class HomeServlet extends AbstractDatabaseServlet {

    /**
     * Retrieves trending movies.
     */
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        // check if the user is already logged in: if so, I don't want him to see the welcome page
        AppUser loggedUser = JWTUtils.getUserFromCookies(req);

        if (loggedUser != null) {
            res.sendRedirect(req.getContextPath() + "/auth/catalog");
            return;
        }

        // setup logging
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setAction(Actions.GET_TRENDING_MOVIES);

        // models
        List<Movie> movies = null;
        Message m = null;

        // business logic and database access
        try {
            // perform the operation on the database
            movies = new GetTrendingMoviesDAO(getConnection(), 10).access().getOutputParam();

            m = new Message("Trending movies successfully retrieved.");

            LOGGER.info("%d trending movies successfully retrieved.", movies.size());
        } catch (SQLException e) {
            m = new Message("Cannot extract trending movies: system error.", "E0A1", null);
            LOGGER.error("Cannot extract trending movies: unexpected error while accessing the database.", e);
        }

        try {
            req.setAttribute("movies", movies);
            req.setAttribute("message", m);

            req.getRequestDispatcher("/jsp/index.jsp").forward(req, res);
        } finally {
            // final cleaning
            LogContext.removeIPAddress();
            LogContext.removeAction();
        }
    }
}
