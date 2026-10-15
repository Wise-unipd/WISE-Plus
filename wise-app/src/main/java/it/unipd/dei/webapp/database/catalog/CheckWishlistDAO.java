package it.unipd.dei.webapp.database.catalog;

import it.unipd.dei.webapp.database.AbstractDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Check if a movie is in the wishlist.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class CheckWishlistDAO extends AbstractDAO<Boolean> {
    /**
     * The SQL statement to be executed
     */
    private static final String STATEMENT = "SELECT 1 FROM Wishlist WHERE user_id = ? AND movie_id = ?";

    /**
     * The unique identifier of the user.
     */
    private final int userId;

    /**
     * The unique identifier of the movie.
     */
    private final int movieId;

    /**
     * Creates a new object to check if a movie is in the wishlist.
     *
     * @param con     the database connection.
     * @param userId  the unique identifier of the user.
     * @param movieId the unique identifier of the movie.
     */
    public CheckWishlistDAO(final Connection con, final int userId, final int movieId) {
        super(con);
        this.userId = userId;
        this.movieId = movieId;
    }

    /**
     * Checks if the movie is in the wishlist of the user in the database.
     *
     * @throws SQLException if an error occurs while accessing the database.
     */
    @Override
    protected final void doAccess() throws SQLException {

        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, movieId);

            try (ResultSet rs = pstmt.executeQuery()) {
                // checks if there is data
                outputParam = rs.next();

                LOGGER.info("Successfully checked if movie %d is in the wishlist of user %d.", movieId, userId);
            }
        }
    }
}
