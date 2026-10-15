package it.unipd.dei.webapp.database.rentals;

import it.unipd.dei.webapp.database.AbstractDAO;
import it.unipd.dei.webapp.resource.rentals.Rental;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Gets all the rentals for a specific user.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class GetRentalsByUserDAO extends AbstractDAO<List<Rental>> {

    /**
     * The SQL statement used to retrieve all unique rentals for a specific user.
     * This query relies on pre-aggregated subqueries for both expirations and transactions.
     * By isolating the MAX(expiration) and the SUM(amount) before joining
     * them to the main Rent table, this query avoids the Cartesian Product trap.
     * The total amount calculated here explicitly ignores refund transactions.
     */
    private static final String STATEMENT =
            "SELECT r.id, r.movie_id, m.title, r.checkout, " +
                    "re.latest_expiration AS expiration, " +
                    "COALESCE(t.total_amount, 0) AS total_amount " +
                    "FROM rent r " +
                    "JOIN movie m ON r.movie_id = m.movie_id " +
                    "LEFT JOIN (" +
                    "    SELECT rent, MAX(expiration) AS latest_expiration " +
                    "    FROM rent_expiration " +
                    "    GROUP BY rent" +
                    ") re ON re.rent = r.id " +
                    "LEFT JOIN (" +
                    "    SELECT rent, SUM(CASE WHEN type IN ('rent', 'extension') THEN amount ELSE 0 END) AS total_amount " +
                    "    FROM transactions " +
                    "    GROUP BY rent" +
                    ") t ON t.rent = r.id " +
                    "WHERE r.user_id = ? " +
                    "ORDER BY r.checkout DESC";

    /**
     * The ID of the user whose rentals are being retrieved.
     */
    private final int userId;

    /**
     * Creates a new object to get all the rentals for a specific user.
     *
     * @param conn   the database connection.
     * @param userId the ID of the user.
     */
    public GetRentalsByUserDAO(final Connection conn, final int userId) {
        super(conn);
        this.userId = userId;
    }

    /**
     * Gets all the rentals for a specific user from the database.
     *
     * @throws Exception if an error occurs while accessing the database.
     */
    @Override
    protected final void doAccess() throws Exception {
        List<Rental> rentals = new ArrayList<>();

        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT)) {
            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    rentals.add(new Rental(
                            rs.getInt("id"),
                            rs.getInt("movie_id"),
                            rs.getString("title"),
                            rs.getTimestamp("checkout"),
                            rs.getDouble("total_amount"),
                            rs.getTimestamp("expiration")
                    ));
                }
            }
        }

        LOGGER.info("Loaded %d unique rents for user %d.", rentals.size(), userId);
        outputParam = rentals;
    }
}