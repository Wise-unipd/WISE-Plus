package it.unipd.dei.webapp.database.worker;

import it.unipd.dei.webapp.database.AbstractDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Remove a credit from a movie.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class RemoveCreditFromMovieDAO extends AbstractDAO<Boolean> {
  
  /**
   * The SQL statement to be executed.
   */
  private static final String STATEMENT = 
    "DELETE FROM Credit WHERE movie_id = ? AND worker_id = ? AND role_id = ?";

  /**
   * The unique identifier of the movie.
   */
  private final int movieId;

  /**
   * The unique identifier of the worker.
   */
  private final int workerId;

  /**
   * The unique identifier of the role.
   */
  private final int roleId;

  /**
   * Creates a new object to remove a credit from a movie.
   * 
   * @param con the database connection.
   * @param movieId the unique identifier of the movie.
   * @param workerId the unique identifier of the worker.
   * @param roleId the unique identifier of the role.
   */
  public RemoveCreditFromMovieDAO(final Connection con, final int movieId, final int workerId, final int roleId) {
    super(con);
    this.movieId = movieId;
    this.workerId = workerId;
    this.roleId = roleId;
  }

  @Override
  protected final void doAccess() throws SQLException {

    try (PreparedStatement pstmt = con.prepareStatement(STATEMENT)) {
      pstmt.setInt(1, movieId);
      pstmt.setInt(2, workerId);
      pstmt.setInt(3, roleId);

      // returns the number of rows changed
      int rows = pstmt.executeUpdate();

      if (rows > 0) {
        outputParam = true;
        LOGGER.info("Worker %d with role %d successfully removed from movie %d.", workerId, roleId, movieId);
      } else {
        outputParam = false;
        LOGGER.warn("Failed to remove worker %d with role %d from movie %d.", workerId, roleId, movieId);
      }
    }
  }
}
