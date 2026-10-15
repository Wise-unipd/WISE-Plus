package it.unipd.dei.webapp.database.catalog;

import it.unipd.dei.webapp.database.AbstractDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Remove 'I like' from a movie.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class RemoveLikeDAO extends AbstractDAO<Boolean> {
    /**
     * The SQL statement to be executed
     */
    private static final String STATEMENT = "DELETE FROM Likes " +
                                            "  WHERE user_id = ? " +
                                            "  AND movie_id = ? " +
                                            "  AND EXISTS (" +
                                            "    SELECT 1 FROM Movie " +
                                            "    WHERE movie_id = Likes.movie_id " +
                                            "    AND is_active = true" +
                                            "  )";

    /**
     * The unique identifier of the user.
     */
    private final int userId;

    /**
     * The unique identifier of the movie.
     */
    private final int movieId;

    /**
     * Creates a new object to remove 'I like' from a movie.
     *
     * @param con     the database connection.
     * @param userId  the unique identifier of the user.
     * @param movieId the unique identifier of the movie.
     */
    public RemoveLikeDAO(final Connection con, final int userId, final int movieId) {
        super(con);
        this.userId = userId;
        this.movieId = movieId;
    }

    /**
     * Removes the 'I like' from the movie in the database.
     *
     * @throws SQLException if an error occurs while accessing the database.
     */
    @Override
    protected final void doAccess() throws SQLException {

        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, movieId);

            // returns the number of rows changed
            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                outputParam = true;
                LOGGER.info("User %d successfully removed 'I like' from the movie %d.", userId, movieId);
            } else {
                outputParam = false;
                LOGGER.warn("User %d failed to remove 'I like' from movie %d.", userId, movieId);
            }
        }
    }
}
