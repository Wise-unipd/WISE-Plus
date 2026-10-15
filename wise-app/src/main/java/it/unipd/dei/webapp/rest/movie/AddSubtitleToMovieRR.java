package it.unipd.dei.webapp.rest.movie;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import it.unipd.dei.webapp.database.language.AddSubtitleToMovieDAO;
import it.unipd.dei.webapp.resource.Actions;
import it.unipd.dei.webapp.resource.LogContext;
import it.unipd.dei.webapp.resource.Message;
import it.unipd.dei.webapp.resource.authentication.AppUser;
import it.unipd.dei.webapp.rest.AbstractRR;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * A REST resource to add a subtitle language to a movie.
 * Extracts path parameters and delegates database operations to the corresponding DAO.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class AddSubtitleToMovieRR extends AbstractRR {

    /**
     * Creates a new REST resource to add a subtitle to a movie.
     * 
     * @param req the HTTP request.
     * @param res the HTTP response.
     * @param con the database connection.
     */
    public AddSubtitleToMovieRR(final HttpServletRequest req, final HttpServletResponse res, Connection con) {
        super(Actions.ADD_SUBTITLE_TO_MOVIE, req, res, con);
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
            @SuppressWarnings("unchecked")final Map<String, String> restParameters = (Map<String, String>) req.getAttribute("restParameters");
            final int movieId = Integer.parseInt(restParameters.get("param1"));
            final int languageId = Integer.parseInt(restParameters.get("param2"));

            LogContext.setResource("Movie: " + movieId + ", Subtitle: " + languageId);

            final Boolean status = new AddSubtitleToMovieDAO(con, movieId, languageId).access().getOutputParam();

            // checks if the subtitle has been added to the movie correctly
            if (status == Boolean.TRUE) {
                LOGGER.info("Subtitle successfully added to movie.");
                m = new Message("Subtitle added to movie.");
                res.setStatus(HttpServletResponse.SC_CREATED);
                m.toJSON(res.getOutputStream());
            } else {
                LOGGER.error("Fatal error while adding subtitle to movie.");
                m = new Message("Unable to add subtitle to movie.", "E0A1", null);
                res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                m.toJSON(res.getOutputStream());
            }

        } catch (NumberFormatException e) {
            // error while parsing parameters
            LOGGER.error("Unable to add subtitle to movie: wrong format for the movie ID or language ID.", e);
            m = new Message("Unable to add subtitle to movie: wrong format for the movie ID or language ID.", "E1A1", null);
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            m.toJSON(res.getOutputStream());

        } catch (SQLException e) {
            // the audio is already associated with the movie
            if ("23505".equals(e.getSQLState())) {
                LOGGER.warn("The subtitle is already associated with the movie.", e);
                m = new Message("The subtitle is already associated with the movie.", "E1A5", null);
                res.setStatus(HttpServletResponse.SC_CONFLICT);
                m.toJSON(res.getOutputStream());

            } else {
                // generic database error
                LOGGER.error("Unable to add subtitle to movie: unexpected database error.", e);
                m = new Message("Unable to add subtitle to movie: unexpected database error.", "E0A1", null);
                res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                m.toJSON(res.getOutputStream());
            }

        } catch (Exception e) {
            LOGGER.error("Unable to add subtitle to movie: unexpected error.", e);
            m = new Message("Unable to add subtitle to movie: unexpected error.", "E0A2", null);
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            m.toJSON(res.getOutputStream());
        }
    }
}