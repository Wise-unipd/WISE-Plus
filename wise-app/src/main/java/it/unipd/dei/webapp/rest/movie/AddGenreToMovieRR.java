package it.unipd.dei.webapp.rest.movie;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import it.unipd.dei.webapp.database.genre.AddGenreToMovieDAO;
import it.unipd.dei.webapp.resource.Actions;
import it.unipd.dei.webapp.resource.LogContext;
import it.unipd.dei.webapp.resource.Message;
import it.unipd.dei.webapp.resource.authentication.AppUser;
import it.unipd.dei.webapp.rest.AbstractRR;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * A REST resource to add a genre to a movie.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class AddGenreToMovieRR extends AbstractRR {

    /**
     * Creates a new REST resource to add a genre to a movie.
     *
     * @param req the HTTP request.
     * @param res the HTTP response.
     * @param con the database connection.
     */
    public AddGenreToMovieRR(final HttpServletRequest req, final HttpServletResponse res, Connection con) {
        super(Actions.ADD_GENRE_TO_MOVIE, req, res, con);
    }

    @Override
    protected void doServe() throws IOException {
        Message m = null;

        try {
            AppUser loggedUser = (AppUser) req.getAttribute("AppUser");

            // Check if user does not have staff permissions (Role ID < 1)
            if (loggedUser.getUserRole().getId() < 1) {
                LOGGER.warn("Access denied for user %s: insufficient permissions for resource %s.", loggedUser.getEmail(), req.getRequestURI());
                m = new Message("Access denied: insufficient permissions.", "E2A1", null); 
                res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                m.toJSON(res.getOutputStream());
                return;
            } 

            // extracts "REST parameters" (e.g., {id} in movie/{id})
            @SuppressWarnings("unchecked") final Map<String, String> restParameters = (Map<String, String>) req.getAttribute("restParameters");
            final int movieId = Integer.parseInt(restParameters.get("param1"));
            final int genreId = Integer.parseInt(restParameters.get("param2"));

            LogContext.setResource("Movie: " + movieId + ", Genre: " + genreId);

            final Boolean status = new AddGenreToMovieDAO(con, movieId, genreId).access().getOutputParam();

            // checks if the genre has been added to the movie correctly
            if (status == Boolean.TRUE) {
                LOGGER.info("Genre successfully added to movie.");
                m = new Message("Genre added to movie.");
                res.setStatus(HttpServletResponse.SC_CREATED);
                m.toJSON(res.getOutputStream());
            } else {
                LOGGER.error("Fatal error while adding genre to movie.");
                m = new Message("Unable to add genre to movie.", "E0A1", null);
                res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                m.toJSON(res.getOutputStream());
            }
        } catch (NumberFormatException e) {
            // error while parsing parameters
            LOGGER.error("Unable to add genre to movie: wrong format for the movie ID or genre ID.", e);
            m = new Message("Unable to add genre to movie: wrong format for the movie ID or genre ID.", "E1A1", null);
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            m.toJSON(res.getOutputStream());
        } catch (SQLException e) {
            // the genre is already associated with the movie
            if ("23505".equals(e.getSQLState())) {
                LOGGER.warn("The genre is already associated with the movie.", e);
                m = new Message("The genre is already associated with the movie.", "E1A5", null);
                res.setStatus(HttpServletResponse.SC_CONFLICT);
                m.toJSON(res.getOutputStream());
            } else {
                // generic database error
                LOGGER.error("Unable to add genre to movie: unexpected database error.", e);
                m = new Message("Unable to add genre to movie: unexpected database error.", "E0A1", null);
                res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                m.toJSON(res.getOutputStream());
            }
        } catch (Exception e) {
            LOGGER.error("Unable to add genre to movie: unexpected error.", e);
            m = new Message("Unable to add genre to movie: unexpected error.", "E0A2", null);
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            m.toJSON(res.getOutputStream());
        }
    }
}
