package it.unipd.dei.webapp.database.rentals;

import it.unipd.dei.webapp.database.AbstractDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * DAO responsible for securely processing a refund.
 * It calculates the remaining available refund balance and processes the
 * refund in a single, secure database operation.
 * * @returns A Boolean: {@code true} if the refund was successfully inserted,
 * {@code false} if it was rejected.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class ProcessRefundDAO extends AbstractDAO<Boolean> {

    /**
     * The atomic SQL statement used to insert a new refund transaction.
     * Processes the refund and checks the remaining balance in a single, atomic database operation,
     * preventing race conditions if multiple refund requests are made simultaneously.
     * This query assumes refunds are inserted as negative values.
     */
    private static final String STATEMENT =
            "INSERT INTO Transactions (rent, amount, type, created) " +
                    "SELECT ?, ?, 'refund', CURRENT_TIMESTAMP " +
                    "WHERE ? <= (" +
                    "    SELECT ROUND(CAST(" +
                    "        COALESCE(SUM(CASE WHEN t.type IN ('rent', 'extension') THEN t.amount ELSE 0 END), 0) + " +
                    "        COALESCE(SUM(CASE WHEN t.type = 'refund' THEN t.amount ELSE 0 END), 0) " +
                    "    AS numeric), 2) " +
                    "    FROM Transactions t WHERE t.rent = ?" +
                    ")";

    /**
     * The ID of the rental to be refunded.
     */
    private final int rentId;

    /**
     * The exact amount to refund.
     */
    private final double amount;

    /**
     * Creates a new object to securely process a refund.
     *
     * @param con    the connection to the database.
     * @param rentId the ID of the rental to be refunded.
     * @param amount the exact amount to refund.
     */
    public ProcessRefundDAO(final Connection con, final int rentId, final double amount) {
        super(con);
        this.rentId = rentId;
        this.amount = amount;
    }

    /**
     * Executes the secure refund insertion.
     *
     * @throws Exception if a database execution error occurs.
     */
    @Override
    protected final void doAccess() throws Exception {
        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT)) {

            // ensure the input is treated as a positive magnitude for the limit check
            double absoluteAmount = Math.abs(amount);

            // bind the parameters for the INSERT
            pstmt.setInt(1, rentId);
            pstmt.setDouble(2, -absoluteAmount);

            // bind the exact same parameters for the WHERE clause
            pstmt.setDouble(3, absoluteAmount);
            pstmt.setInt(4, rentId);

            // execute the query
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected == 1) {
                // the amount was valid, the database successfully recorded the refund
                LOGGER.info("Refund of %f processed for rent ID %d.", amount, rentId);
                outputParam = true;
            } else {
                // the query executed but inserted 0 rows, the requested amount
                //  was higher than the currently available refundable limit
                LOGGER.warn("Refund failed for rent ID %d. Requested amount %f exceeded limits.", rentId, amount);
                outputParam = false;
            }
        }
    }
}