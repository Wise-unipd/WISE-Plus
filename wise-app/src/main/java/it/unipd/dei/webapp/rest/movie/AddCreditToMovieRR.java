package it.unipd.dei.webapp.rest.movie;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import it.unipd.dei.webapp.database.worker.AddCreditToMovieDAO;
import it.unipd.dei.webapp.resource.Actions;
import it.unipd.dei.webapp.resource.movie.Credit;
import it.unipd.dei.webapp.resource.LogContext;
import it.unipd.dei.webapp.resource.Message;
import it.unipd.dei.webapp.resource.authentication.AppUser;
import it.unipd.dei.webapp.rest.AbstractRR;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * A REST resource to add a credit to a movie.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class AddCreditToMovieRR extends AbstractRR {

  /**
   * Creates a new REST resource to add a credit to a movie.
   *
   * @param req the HTTP request.
   * @param res the HTTP response.
   * @param con the database connection.
   */
  public AddCreditToMovieRR(final HttpServletRequest req, final HttpServletResponse res, Connection con) {
    super(Actions.ADD_CREDIT_TO_MOVIE, req, res, con);
  }

  @Override
  protected void doServe() throws IOException {
    Message m = null;
    Credit credit = null;

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

      credit = new AddCreditToMovieDAO(con, movieId, workerId, roleId).access().getOutputParam();

      // checks if the credit has been added to the movie correctly
      if (credit != null) {
        LOGGER.info("Credit successfully added to movie.");
        res.setStatus(HttpServletResponse.SC_CREATED);
        credit.toJSON(res.getOutputStream());
      } else {
        LOGGER.error("Fatal error while adding credit to movie.");
        m = new Message("Unable to add credit to movie.", "E0A1", null);
        res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        m.toJSON(res.getOutputStream());
      }
    } catch (NumberFormatException e) {
      // error while parsing parameters
      LOGGER.error("Unable to add credit to movie: wrong format for the movie ID, worker ID or role ID.", e);
      m = new Message("Unable to add credit to movie: wrong format for the movie ID, worker ID or role ID.", "E1A1", null);
      res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      m.toJSON(res.getOutputStream());
    } catch (SQLException e) {
      // the credit is already associated with the movie
      if ("23505".equals(e.getSQLState())) {
        LOGGER.warn("The credit is already associated with the movie.", e);
        m = new Message("The credit is already associated with the movie.", "E1A5", null);
        res.setStatus(HttpServletResponse.SC_CONFLICT);
        m.toJSON(res.getOutputStream());
      } else {
        // generic database error
        LOGGER.error("Unable to add credit to movie: unexpected database error.", e);
        m = new Message("Unable to add credit to movie: unexpected database error.", "E0A1", null);
        res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        m.toJSON(res.getOutputStream());
      }
    } catch (Exception e) {
      LOGGER.error("Unable to add credit to movie: unexpected error.", e);
      m = new Message("Unable to add credit to movie: unexpected error.", "E0A2", null);
      res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      m.toJSON(res.getOutputStream());
    }
  }
}
