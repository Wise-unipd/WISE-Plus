package it.unipd.dei.webapp.servlet.catalog;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import it.unipd.dei.webapp.servlet.AbstractDatabaseServlet;

import it.unipd.dei.webapp.database.catalog.GetWishlistDAO;
import it.unipd.dei.webapp.resource.Actions;
import it.unipd.dei.webapp.resource.authentication.AppUser;
import it.unipd.dei.webapp.resource.LogContext;
import it.unipd.dei.webapp.resource.Message;
import it.unipd.dei.webapp.resource.movie.Movie;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Retrieves the wishlist.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class WishlistServlet extends AbstractDatabaseServlet {

    /**
     * Retrieves the wishlist.
     */
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        // setup logging
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setAction(Actions.GET_WISHLIST);

        // models
        List<Movie> movies = null;
        Message m = null;

        // business logic and database access
        try {
            // gets the user ID from the request attribute (set by AuthFilter)
            final AppUser user = (AppUser) req.getAttribute("AppUser");
            final int userId = user.getId();

            // perform the operation on the database
            movies = new GetWishlistDAO(getConnection(), userId).access().getOutputParam();

            m = new Message("Wishlist successfully retrieved.");

            LOGGER.info("Wishlist successfully retrieved with %d movies.", movies.size());
        } catch (SQLException e) {
            m = new Message("Cannot retrieve the wishlist: system error.", "E0A1",
                null);

            LOGGER.error("Cannot retrieve the wishlist: unexpected error while accessing the database.", e);
        }

        try {
            req.setAttribute("movies", movies);
            req.setAttribute("message", m);

            req.getRequestDispatcher("/auth/browse/wishlist.jsp").forward(req, res);
        } finally {
            // final cleaning
            LogContext.removeIPAddress();
            LogContext.removeAction();
        }
    }
}
