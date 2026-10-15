package it.unipd.dei.webapp.database.rentals;

import it.unipd.dei.webapp.database.AbstractDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

/**
 * DAO responsible for calculating financial data for rentals
 * Returns a Map where the key is the rent ID and the value is a Double array:
 * Index [0] = max_refundable  (the total remaining amount that can still be refunded)
 * Index [1] = total_refunded (the absolute magnitude of money that has been refunded)
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class GetRefundableAmountsByUserDAO extends AbstractDAO<Map<Integer, Double[]>> {

    /**
     * The SQL statement used to calculate the financial status and refund limits
     * for every rental associated with a specific user.
     * It uses ROUND(CAST(... AS numeric), 2) to guarantee exact currency values
     * and prevent floating-point precision errors during limit checks.

     */
    private static final String STATEMENT =
            "SELECT r.id AS rent_id, " +
                    "       ROUND(CAST(" +
                    "           COALESCE(SUM(CASE WHEN t.type IN ('rent', 'extension') THEN t.amount ELSE 0 END), 0) + " +
                    "           COALESCE(SUM(CASE WHEN t.type = 'refund' THEN t.amount ELSE 0 END), 0) " +
                    "       AS numeric), 2) AS max_refundable, " +
                    "       ROUND(CAST(" +
                    "           COALESCE(SUM(CASE WHEN t.type = 'refund' THEN ABS(t.amount) ELSE 0 END), 0) " +
                    "       AS numeric), 2) AS total_refunded " +
                    "FROM Rent r " +
                    "LEFT JOIN Transactions t ON r.id = t.rent " +
                    "WHERE r.user_id = ? " +
                    "GROUP BY r.id";

    /**
     * The ID of the user whose rentals we are checking.
     */
    private final int userId;

    /**
     * Creates a new object to calculate financial data for rentals of a specific user.
     *
     * @param conn   the connection to the database.
     * @param userId the ID of the user whose rentals we are checking.
     */
    public GetRefundableAmountsByUserDAO(final Connection conn, final int userId) {
        super(conn);
        this.userId = userId;
    }

    /**
     * Executes the database query and processes the ResultSet.
     *
     * @throws Exception if a database error occurs during execution.
     */
    @Override
    protected final void doAccess() throws Exception {
        Map<Integer, Double[]> map = new HashMap<>();

        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT)) {

            // bind the target user ID to the '?' in the SQL query
            pstmt.setInt(1, userId);

            // execute the query and process the results
            try (ResultSet rs = pstmt.executeQuery()) {

                // iterate through every rental returned by the database
                while (rs.next()) {
                    // extract the rent ID and compute the results
                    int rentId = rs.getInt("rent_id");
                    double maxRefundable = rs.getDouble("max_refundable");
                    double totalRefunded = rs.getDouble("total_refunded");

                    // store the values in the array: [0] is max limit, [1] is total refunded
                    map.put(rentId, new Double[]{maxRefundable, totalRefunded});
                }
            }
        }

        outputParam = map;
        LOGGER.info("Successfully retrieved refundable amounts for %d rentals of user %d.", map.size(), userId);
    }
}
