package it.unipd.dei.webapp.database.rentals;

import it.unipd.dei.webapp.database.AbstractDAO;
import it.unipd.dei.webapp.resource.rentals.Rental;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Retrieves the active rental for a given user and movie from the database.
 * 
 * <p>
 * This DAO searches for an active rental (where {@code is_active} is true and the expiration date is in the future)
 * for a specific user and movie. If multiple active rentals exist (which shouldn't happen by design, but is handled),
 * it returns the one with the furthest expiration date.
 * </p>
 * 
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class GetActiveRentalsByUserAndMovieIdDAO extends AbstractDAO<Rental> {

    /**
     * The SQL statement to be executed.
     */
    private static final String STATEMENT = "SELECT r.id, r.user_id, a.email, a.first_name, a.last_name, r.movie_id, m.title, r.checkout, re.expiration " + "FROM rent r " + "JOIN rent_expiration re ON r.id = re.rent " + "JOIN movie AS m ON r.movie_id = m.movie_id " + "JOIN appuser AS a ON a.id = r.user_id " + "WHERE r.user_id = ? AND r.movie_id = ? AND r.is_active = true AND re.expiration > ? " + "ORDER BY re.expiration DESC LIMIT 1";

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
    public GetActiveRentalsByUserAndMovieIdDAO(final Connection conn, final int userId, final int movieId) {
        super(conn);
        this.userId = userId;
        this.movieId = movieId;
    }

    /**
     * Executes the database query to retrieve the active rental's details.
     *
     * @throws SQLException if an error occurs during database access.
     */
    @Override
    protected final void doAccess() throws SQLException {
        // capture the exact current time to compare against the expiration column
        final Timestamp now = new Timestamp(System.currentTimeMillis());

        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT)) {
            // bind the parameters to the SQL query
            pstmt.setInt(1, userId);
            pstmt.setInt(2, movieId);
            pstmt.setTimestamp(3, now);

            // execute the query and process the results
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    outputParam = new Rental(rs.getInt("id"), rs.getInt("user_id"), rs.getString("email"), rs.getString("first_name"), rs.getString("last_name"), rs.getInt("movie_id"), rs.getString("title"), rs.getTimestamp("checkout"), rs.getTimestamp("expiration")   // expiration
                    );
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