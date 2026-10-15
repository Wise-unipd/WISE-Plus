package it.unipd.dei.webapp.rest.catalog;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import it.unipd.dei.webapp.database.catalog.AddToWishlistDAO;
import it.unipd.dei.webapp.resource.Actions;
import it.unipd.dei.webapp.resource.authentication.AppUser;
import it.unipd.dei.webapp.resource.LogContext;
import it.unipd.dei.webapp.resource.Message;
import it.unipd.dei.webapp.rest.AbstractRR;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * A REST resource to add a movie to the wishlist.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class AddToWishlistRR extends AbstractRR {
    /**
     * Creates a new REST resource to add a movie to the wishlist.
     *
     * @param req the HTTP request.
     * @param res the HTTP response.
     * @param con the database connection.
     */
    public AddToWishlistRR(final HttpServletRequest req, final HttpServletResponse res, Connection con) {
        super(Actions.ADD_MOVIE_TO_WISHLIST, req, res, con);
    }

    @Override
    protected void doServe() throws IOException {
        Message m = null;

        try {
            // gets the user ID from the request attribute (set by RestDispatcherServlet)
            final AppUser user = (AppUser) req.getAttribute("AppUser");
            final int userId = user.getId();

            // extracts "REST parameters" (e.g., {id} in movie/{id})
            @SuppressWarnings("unchecked") final Map<String, String> restParameters = (Map<String, String>) req.getAttribute("restParameters");
            final int movieId = Integer.parseInt(restParameters.get("param1"));

            LogContext.setResource("Movie: " + movieId);

            final Boolean status = new AddToWishlistDAO(con, userId, movieId).access().getOutputParam();

            // checks if the movie has been added to the wishlist correctly
            if (status == Boolean.TRUE) {
                LOGGER.info("Movie successfully added to wishlist.");
                m = new Message("Movie added to wishlist.");
                res.setStatus(HttpServletResponse.SC_CREATED);
                m.toJSON(res.getOutputStream());
            } else {
                LOGGER.error("Fatal error while adding movie to wishlist.");
                m = new Message("Unable to add movie to wishlist.", "E0A1", null);
                res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                m.toJSON(res.getOutputStream());
            }
        } catch (NumberFormatException e) {
            // error while parsing parameters
            LOGGER.error("Unable to add movie to wishlist: wrong format for the movie ID.", e);
            m = new Message("Unable to add movie to wishlist: wrong format for the movie ID.", "E1A1", null);
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            m.toJSON(res.getOutputStream());
        } catch (SQLException e) {
            // the movie is already in the user wishlist
            if ("23505".equals(e.getSQLState())) {
                LOGGER.warn("The movie is already in the wishlist.", e);
                m = new Message("The movie is already in the wishlist.", "E1A5", null);
                res.setStatus(HttpServletResponse.SC_CONFLICT);
                m.toJSON(res.getOutputStream());
            } else {
                // generic database error
                LOGGER.error("Unable to add movie to wishlist: unexpected database error.", e);
                m = new Message("Unable to add movie to wishlist: unexpected database error.", "E0A1", null);
                res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                m.toJSON(res.getOutputStream());
            }
        } catch (Exception e) {
            LOGGER.error("Unable to add movie to wishlist: unexpected error.", e);
            m = new Message("Unable to add movie to wishlist: unexpected error.", "E0A2", null);
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            m.toJSON(res.getOutputStream());
        }
    }
}
