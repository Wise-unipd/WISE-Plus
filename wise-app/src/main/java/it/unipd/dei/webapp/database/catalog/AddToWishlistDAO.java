package it.unipd.dei.webapp.database.catalog;

import it.unipd.dei.webapp.database.AbstractDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Add a movie to the wishlist.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class AddToWishlistDAO extends AbstractDAO<Boolean> {
    /**
     * The SQL statement to be executed
     */
    private static final String STATEMENT = "INSERT INTO Wishlist (user_id, movie_id) " +
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
     * Creates a new object to add a movie to the wishlist.
     *
     * @param con     the database connection.
     * @param userId  the unique identifier of the user.
     * @param movieId the unique identifier of the movie.
     */
    public AddToWishlistDAO(final Connection con, final int userId, final int movieId) {
        super(con);
        this.userId = userId;
        this.movieId = movieId;
    }

    /**
     * Adds the movie to the wishlist in the database.
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
                LOGGER.info("Movie %d successfully added to wishlist for user %d.", movieId, userId);
            } else {
                outputParam = false;
                LOGGER.warn("User %d failed to add to wishlist the movie %d.", userId, movieId);
            }
        }
    }
}
