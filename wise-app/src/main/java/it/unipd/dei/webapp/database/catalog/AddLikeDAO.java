package it.unipd.dei.webapp.database.catalog;

import it.unipd.dei.webapp.database.AbstractDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Add 'I like' to a movie.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class AddLikeDAO extends AbstractDAO<Boolean> {
    /**
     * The SQL statement to be executed
     */
    private static final String STATEMENT = "INSERT INTO Likes (user_id, movie_id)" +
                                            "SELECT ?, movie_id FROM Movie WHERE movie_id = ? AND is_active = true";

    /**
     * The unique identifier of the user.
     */
    private final int userId;

    /**
     * The unique identifier of the movie.
     */
    private final int movieId;

    /**
     * Creates a new object to add 'I like' to a movie.
     *
     * @param con     the database connection.
     * @param userId  the unique identifier of the user.
     * @param movieId the unique identifier of the movie.
     */
    public AddLikeDAO(final Connection con, final int userId, final int movieId) {
        super(con);
        this.userId = userId;
        this.movieId = movieId;
    }

    /**
     * Adds the 'I like' to the movie in the database.
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
                LOGGER.info("User %d successfully added 'I like' to the movie %d.", userId, movieId);
            } else {
                outputParam = false;
                LOGGER.warn("User %d failed to add 'I like' to the movie %d.", userId, movieId);
            }
        }
    }
}
