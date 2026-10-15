package it.unipd.dei.webapp.database.catalog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.unipd.dei.webapp.database.AbstractDAO;
import it.unipd.dei.webapp.resource.movie.Movie;
import it.unipd.dei.webapp.resource.movie.MovieFactory;

/**
 * Gets all the movies in the wishlist.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class GetWishlistDAO extends AbstractDAO<List<Movie>> {
    /**
     * The SQL statement to be executed
     */
    private static final String STATEMENT =
            "SELECT m.* " +
                    "FROM movie_with_rating AS m " +
                    "INNER JOIN Wishlist AS w ON m.movie_id = w.movie_id " +
                    "WHERE w.user_id = ? AND m.is_active = true " +
                    "ORDER BY w.added_at DESC";

    /**
     * The unique identifier of the user.
     */
    private final int userId;

    /**
     * Creates a new object to get all the movies in the wishlist.
     *
     * @param con    the database connection.
     * @param userId the unique identifier of the user.
     */
    public GetWishlistDAO(final Connection con, final int userId) {
        super(con);
        this.userId = userId;
    }

    /**
     * Gets the movies in the wishlist of the user from the database.
     *
     * @throws SQLException if an error occurs while accessing the database.
     */
    @Override
    protected final void doAccess() throws SQLException {

        List<Movie> movies = new ArrayList<>();

        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT)) {
            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    movies.add(MovieFactory.fromDBRow(rs));
                }

                LOGGER.info("Successfully retrieved movies from the wishlist of user %d.", userId);
            }
        }

        outputParam = movies;
    }
}
