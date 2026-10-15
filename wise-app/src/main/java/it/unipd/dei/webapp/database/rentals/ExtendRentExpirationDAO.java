package it.unipd.dei.webapp.database.rentals;

import it.unipd.dei.webapp.database.AbstractDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Extends the expiration date of an existing rental by inserting a new expiration record.
 * 
 * <p>
 * This DAO calculates the new expiration date based on the current expiration and the number of days to extend.
 * It then inserts this new record into the {@code rent_expiration} table.
 * </p>
 * 
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class ExtendRentExpirationDAO extends AbstractDAO<Integer> {

    /**
     * The SQL statement to be executed.
     */
    private static final String STATEMENT = "INSERT INTO rent_expiration (rent, expiration, created) VALUES (?, ?, ?) RETURNING id";

    /**
     * The ID of the rental.
     */
    private final int rentId;

    /**
     * The duration of the extension in days.
     */
    private final int days;

    /**
     * The current expiration date of the rental.
     */
    private final Timestamp currentExpiration;

    /**
     * Creates a new DAO to extend a rental expiration.
     *
     * @param con               the database connection.
     * @param rentId            the ID of the rental to extend.
     * @param days              the number of days to extend the rental by.
     * @param currentExpiration the current expiration date of the rental.
     */
    public ExtendRentExpirationDAO(final Connection con, final int rentId, final int days, final Timestamp currentExpiration) {
        super(con);
        this.rentId = rentId;
        this.days = days;
        this.currentExpiration = currentExpiration;
    }

    /**
     * Inserts the new rental expiration record into the database.
     *
     * @throws SQLException if an error occurs while accessing the database.
     */
    @Override
    protected final void doAccess() throws SQLException {
        final Timestamp now = new Timestamp(System.currentTimeMillis());
        final Timestamp expiration = new Timestamp(currentExpiration.getTime() + (long) days * 24 * 60 * 60 * 1000);

        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT)) {
            pstmt.setInt(1, rentId);
            pstmt.setTimestamp(2, expiration);
            pstmt.setTimestamp(3, now);

            try (ResultSet rs = pstmt.executeQuery()) {
                rs.next();
                outputParam = rs.getInt("id");
            }
        }

        LOGGER.info("Rent expiration extended for rent %d, expires in %d days.", rentId, days);
    }
}