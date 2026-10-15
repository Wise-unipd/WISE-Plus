package it.unipd.dei.webapp.rest.catalog;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import it.unipd.dei.webapp.database.catalog.RemoveLikeDAO;
import it.unipd.dei.webapp.resource.Actions;
import it.unipd.dei.webapp.resource.authentication.AppUser;
import it.unipd.dei.webapp.resource.LogContext;
import it.unipd.dei.webapp.resource.Message;
import it.unipd.dei.webapp.rest.AbstractRR;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * A REST resource to remove 'I like' from a movie.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class RemoveLikeRR extends AbstractRR {
    /**
     * Creates a new REST resource to remove 'I like' from a movie.
     *
     * @param req the HTTP request.
     * @param res the HTTP response.
     * @param con the database connection.
     */
    public RemoveLikeRR(final HttpServletRequest req, final HttpServletResponse res, Connection con) {
        super(Actions.REMOVE_LIKE_FROM_MOVIE, req, res, con);
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

            final Boolean status = new RemoveLikeDAO(con, userId, movieId).access().getOutputParam();

            // checks if 'I like' has been removed from the movie correctly
            if (status == Boolean.TRUE) {
                LOGGER.info("'I like' successfully removed from the movie.");
                m = new Message("'I like' removed from the movie.");
                res.setStatus(HttpServletResponse.SC_OK);
                m.toJSON(res.getOutputStream());
            } else {
                // it is not possible to unlike a movie that is not liked
                LOGGER.warn("'I like' not found attached to the movie. Cannot remove.");
                m = new Message(String.format("'I like' not found attached to the movie %d. Cannot remove.", movieId), "E1A4", null);
                res.setStatus(HttpServletResponse.SC_NOT_FOUND);
                m.toJSON(res.getOutputStream());
            }
        } catch (NumberFormatException e) {
            // error while parsing parameters
            LOGGER.error("Unable to remove 'I like' from the movie: wrong format for the movie ID.", e);
            m = new Message("Unable to remove 'I like' from the movie: wrong format for the movie ID.", "E1A2", null);
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            m.toJSON(res.getOutputStream());
        } catch (SQLException e) {
            // generic database error
            LOGGER.error("Unable to remove 'I like' from the movie: unexpected database error.", e);
            m = new Message("Unable to remove 'I like' from the movie: unexpected database error.", "E0A1", null);
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            m.toJSON(res.getOutputStream());
        } catch (Exception e) {
            LOGGER.error("Unable to remove 'I like' from the movie: unexpected error.", e);
            m = new Message("Unable to remove 'I like' from the movie: unexpected error.", "E0A2", null);
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            m.toJSON(res.getOutputStream());
        }
    }
}
