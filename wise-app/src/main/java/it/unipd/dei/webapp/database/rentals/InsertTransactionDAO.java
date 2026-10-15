package it.unipd.dei.webapp.database.rentals;

import it.unipd.dei.webapp.database.AbstractDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

/**
 * Inserts a new transaction into the database.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class InsertTransactionDAO extends AbstractDAO<Void> {

    /**
     * The SQL statement to be executed.
     */
    private static final String STATEMENT =
            "INSERT INTO transactions (rent, amount, type, created) VALUES (?, ?, ?::transaction_type, ?)";

    /**
     * The ID of the rental.
     */
    private final int rentId;

    /**
     * The amount of the transaction.
     */
    private final double amount;

    /**
     * The type of the transaction ('rent', 'extension', 'refund').
     */
    private final String type;

    /**
     * Creates a new object to insert a transaction into the database.
     *
     * @param conn   the database connection.
     * @param rentId the ID of the rental.
     * @param amount the amount of the transaction.
     * @param type   the type of the transaction.
     */
    public InsertTransactionDAO(final Connection conn, final int rentId, final double amount, final String type) {
        super(conn);
        this.rentId = rentId;
        this.amount = amount;
        this.type = type;
    }

    /**
     * Inserts the transaction into the database.
     *
     * @throws Exception if an error occurs while accessing the database.
     */
    @Override
    protected final void doAccess() throws Exception {
        Timestamp now = new Timestamp(System.currentTimeMillis());

        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT)) {
            pstmt.setInt(1, rentId);
            pstmt.setDouble(2, amount);
            pstmt.setString(3, type);
            pstmt.setTimestamp(4, now);

            pstmt.executeUpdate();
        }

        LOGGER.info("Transaction '%s' inserted for rent %d, amount %.2f.", type, rentId, amount);
    }
}