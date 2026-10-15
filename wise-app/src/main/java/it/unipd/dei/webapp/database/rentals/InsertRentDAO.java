package it.unipd.dei.webapp.database.rentals;

import it.unipd.dei.webapp.database.AbstractDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

/**
 * Inserts a new rental into the database.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class InsertRentDAO extends AbstractDAO<Integer> {

    /**
     * The SQL statement to be executed.
     */
    private static final String STATEMENT =
            "INSERT INTO rent (user_id, movie_id, checkout, is_active) VALUES (?, ?, ?, ?) RETURNING id";

    /**
     * The ID of the user who is renting the movie.
     */
    private final int userId;

    /**
     * The ID of the movie being rented.
     */
    private final int movieId;

    /**
     * Creates a new object to insert a rental into the database.
     *
     * @param conn    the database connection.
     * @param userId  the ID of the user.
     * @param movieId the ID of the movie.
     */
    public InsertRentDAO(final Connection conn, final int userId, final int movieId) {
        super(conn);
        this.userId = userId;
        this.movieId = movieId;
    }

    /**
     * Inserts the rental into the database.
     *
     * @throws Exception if an error occurs while accessing the database.
     */
    @Override
    protected final void doAccess() throws Exception {
        Timestamp now = new Timestamp(System.currentTimeMillis());

        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, movieId);
            pstmt.setTimestamp(3, now);
            pstmt.setBoolean(4, true);

            try (ResultSet rs = pstmt.executeQuery()) {
                rs.next();
                outputParam = rs.getInt("id");
            }
        }

        LOGGER.info("Rent inserted with id %d for user %d, movie %d.", outputParam, userId, movieId);
    }
}