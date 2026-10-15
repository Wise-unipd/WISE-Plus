package it.unipd.dei.webapp.database.rentals;

import it.unipd.dei.webapp.database.AbstractDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

/**
 * Inserts a new rental expiration into the database.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class InsertRentExpirationDAO extends AbstractDAO<Integer> {

    /**
     * The SQL statement to be executed.
     */
    private static final String STATEMENT =
            "INSERT INTO rent_expiration (rent, expiration, created) VALUES (?, ?, ?) RETURNING id";

    /**
     * The ID of the rental.
     */
    private final int rentId;

    /**
     * The duration of the rental in days.
     */
    private final int days;

    /**
     * Creates a new object to insert a rental expiration into the database.
     *
     * @param con    the database connection.
     * @param rentId the ID of the rental.
     * @param days   the duration of the rental in days.
     */
    public InsertRentExpirationDAO(final Connection con, final int rentId, final int days) {
        super(con);
        this.rentId = rentId;
        this.days = days;
    }

    /**
     * Inserts the rental expiration into the database.
     *
     * @throws Exception if an error occurs while accessing the database.
     */
    @Override
    protected final void doAccess() throws Exception {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        Timestamp expiration = new Timestamp(now.getTime() + (long) days * 24 * 60 * 60 * 1000);

        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT)) {
            pstmt.setInt(1, rentId);
            pstmt.setTimestamp(2, expiration);
            pstmt.setTimestamp(3, now);

            try (ResultSet rs = pstmt.executeQuery()) {
                rs.next();
                outputParam = rs.getInt("id");
            }
        }

        LOGGER.info("Rent expiration inserted for rent %d, expires in %d days.", rentId, days);
    }
}