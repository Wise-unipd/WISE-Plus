package it.unipd.dei.webapp.rest.catalog;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import it.unipd.dei.webapp.database.catalog.RemoveFromWishlistDAO;
import it.unipd.dei.webapp.resource.Actions;
import it.unipd.dei.webapp.resource.authentication.AppUser;
import it.unipd.dei.webapp.resource.LogContext;
import it.unipd.dei.webapp.resource.Message;
import it.unipd.dei.webapp.rest.AbstractRR;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * A REST resource to remove a movie from the wishlist.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class RemoveFromWishlistRR extends AbstractRR {
    /**
     * Creates a new REST resource to remove a movie from the wishlist.
     *
     * @param req the HTTP request.
     * @param res the HTTP response.
     * @param con the database connection.
     */
    public RemoveFromWishlistRR(final HttpServletRequest req, final HttpServletResponse res, Connection con) {
        super(Actions.REMOVE_MOVIE_FROM_WISHLIST, req, res, con);
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

            final Boolean status = new RemoveFromWishlistDAO(con, userId, movieId).access().getOutputParam();

            // checks if the movie has been removed from the wishlist correctly
            if (status == Boolean.TRUE) {
                LOGGER.info("Movie successfully removed from wishlist.");
                m = new Message("Movie removed from wishlist.");
                res.setStatus(HttpServletResponse.SC_OK);
                m.toJSON(res.getOutputStream());
            } else {
                // it is not possible to delete a movie that is not in the user wishlist
                LOGGER.warn("Movie not found in wishlist. Cannot delete.");
                m = new Message(String.format("Movie %d not found in the wishlist. Cannot delete.", movieId), "E1A4", null);
                res.setStatus(HttpServletResponse.SC_NOT_FOUND);
                m.toJSON(res.getOutputStream());
            }
        } catch (NumberFormatException e) {
            // error while parsing parameters
            LOGGER.error("Unable to remove movie from wishlist: wrong format for the movie ID.", e);
            m = new Message("Unable to remove movie from wishlist: wrong format for the movie ID.", "E1A2", null);
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            m.toJSON(res.getOutputStream());
        } catch (SQLException e) {
            // generic database error
            LOGGER.error("Unable to remove movie from wishlist: unexpected database error.", e);
            m = new Message("Unable to remove movie from wishlist: unexpected database error.", "E0A1", null);
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            m.toJSON(res.getOutputStream());
        } catch (Exception e) {
            LOGGER.error("Unable to remove movie from wishlist: unexpected error.", e);
            m = new Message("Unable to remove movie from wishlist: unexpected error.", "E0A2", null);
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            m.toJSON(res.getOutputStream());
        }
    }
}
