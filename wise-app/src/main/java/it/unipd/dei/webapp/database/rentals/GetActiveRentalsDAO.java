package it.unipd.dei.webapp.database.rentals;

import it.unipd.dei.webapp.database.AbstractDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

/**
 * DAO used to verify if a user has a currently active and valid rental for a specific movie.
 * It queries the database to find the latest expiration date for a given user and movie,
 * ensuring the rental has not been manually deactivated and the expiration date is still in the future.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class GetActiveRentalsDAO extends AbstractDAO<Timestamp> {

    /**
     * The SQL statement to be executed.
     */
    private static final String STATEMENT =
            "SELECT re.expiration " +
                    "FROM rent r " +
                    "JOIN rent_expiration re ON r.id = re.rent " +
                    "WHERE r.user_id = ? AND r.movie_id = ? AND r.is_active = true AND re.expiration > ? " +
                    "ORDER BY re.expiration DESC LIMIT 1";

    /**
     * The ID of the user whose rental status is being checked.
     */
    private final int userId;

    /**
     * The ID of the movie being checked for an active rental.
     */
    private final int movieId;

    /**
     * Creates a new DAO to check for an active rental.
     *
     * @param conn    the database connection.
     * @param userId  the ID of the user.
     * @param movieId the ID of the movie.
     */
    public GetActiveRentalsDAO(final Connection conn, final int userId, final int movieId) {
        super(conn);
        this.userId = userId;
        this.movieId = movieId;
    }

    /**
     * Executes the database query to retrieve the active rental's expiration date.
     *
     * @throws Exception if an error occurs during database access.
     */
    @Override
    protected final void doAccess() throws Exception {
        // capture the exact current time to compare against the expiration column
        Timestamp now = new Timestamp(System.currentTimeMillis());

        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT)) {
            // bind the parameters to the SQL query
            pstmt.setInt(1, userId);
            pstmt.setInt(2, movieId);
            pstmt.setTimestamp(3, now);

            // execute the query and process the results
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // an active, future-dated rental was found; extract the expiration date
                    outputParam = rs.getTimestamp("expiration");
                } else {
                    // no valid rental found
                    outputParam = null;
                }
            }
        }

        // log the result of the search
        if (outputParam != null) {
            LOGGER.info("Active rent found for user %d, movie %d. Expires at: %s.", userId, movieId, outputParam.toString());
        } else {
            LOGGER.info("No active rent found for user %d and movie %d.", userId, movieId);
        }
    }
}