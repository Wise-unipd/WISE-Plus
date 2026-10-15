package it.unipd.dei.webapp.rest.movie;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import it.unipd.dei.webapp.database.worker.RemoveCreditFromMovieDAO;
import it.unipd.dei.webapp.resource.Actions;
import it.unipd.dei.webapp.resource.LogContext;
import it.unipd.dei.webapp.resource.Message;
import it.unipd.dei.webapp.resource.authentication.AppUser;
import it.unipd.dei.webapp.rest.AbstractRR;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * A REST resource to remove a credit from a movie.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class RemoveCreditFromMovieRR extends AbstractRR {

  /**
   * Creates a new REST resource to remove a credit from a movie.
   * 
   * @param req the HTTP request.
   * @param res the HTTP response. 
   * @param con the database connection.
   */
  public RemoveCreditFromMovieRR(final HttpServletRequest req, final HttpServletResponse res, Connection con) {
    super(Actions.REMOVE_CREDIT_FROM_MOVIE, req, res, con);
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
      @SuppressWarnings("unchecked")
      final Map<String, String> restParameters = (Map<String, String>) req.getAttribute("restParameters");
      final int movieId = Integer.parseInt(restParameters.get("param1"));
      final int workerId = Integer.parseInt(restParameters.get("param2"));
      final int roleId = Integer.parseInt(restParameters.get("param3"));

      LogContext.setResource("Movie: " + movieId + ", Worker: " + workerId + ", Role: " + roleId);

      final Boolean status = new RemoveCreditFromMovieDAO(con, movieId, workerId, roleId).access().getOutputParam();

      // checks if the credit has been removed from the movie correctly
      if (status == Boolean.TRUE) {
        LOGGER.info("Credit successfully removed from movie.");
        m = new Message("Credit removed from movie.");
        res.setStatus(HttpServletResponse.SC_OK);
        m.toJSON(res.getOutputStream());
      } else {
        // it is not possible to remove a credit from a movie it is not associated with
        LOGGER.warn("Credit not associated with the movie. Cannot remove.");
        m = new Message(String.format("Credit not associated with the movie %d. Cannot remove.", movieId), "E1A4", null);
        res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        m.toJSON(res.getOutputStream());
      }
    } catch (NumberFormatException e) {
      // error while parsing parameters
      LOGGER.error("Unable to remove credit from movie: wrong format for the movie ID, worker ID or role ID.", e);
      m = new Message("Unable to remove credit from movie: wrong format for the movie ID, worker ID or role ID.", "E1A2", null);
      res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      m.toJSON(res.getOutputStream());
    } catch (SQLException e) {
      // generic database error
      LOGGER.error("Unable to remove credit from movie: unexpected database error.", e);
      m = new Message("Unable to remove credit from movie: unexpected database error.", "E0A1", null);
      res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      m.toJSON(res.getOutputStream());
    } catch (Exception e) {
      LOGGER.error("Unable to remove credit from movie: unexpected error.", e);
      m = new Message("Unable to remove credit from movie: unexpected error.", "E0A2", null);
      res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      m.toJSON(res.getOutputStream());
    }
  } 
}
