package it.unipd.dei.webapp.database.rentals;

import it.unipd.dei.webapp.database.AbstractDAO;
import it.unipd.dei.webapp.resource.rentals.Rental;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Gets all the rentals from the database.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class GetAllRentalsDAO extends AbstractDAO<List<Rental>> {

    /**
     * The SQL statement to be executed.
     */
    private static final String STATEMENT =
            "SELECT r.id, r.user_id, a.email, a.first_name, a.last_name, r.movie_id, m.title, r.checkout " +
                    "FROM rent AS r " +
                    "JOIN movie AS m ON r.movie_id = m.movie_id " +
                    "JOIN appuser AS a ON a.id = r.user_id " +
                    "ORDER BY r.checkout DESC";

    /**
     * Creates a new object to get all the rentals.
     *
     * @param con the database connection.
     */
    public GetAllRentalsDAO(final Connection con) {
        super(con);
    }

    /**
     * Gets all the rentals from the database.
     *
     * @throws Exception if an error occurs while accessing the database.
     */
    @Override
    protected final void doAccess() throws Exception {
        List<Rental> rentals = new ArrayList<>();

        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                rentals.add(new Rental(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("email"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getInt("movie_id"),
                        rs.getString("title"),
                        rs.getTimestamp("checkout"),
                        null   // expiration
                ));
            }
        }

        outputParam = rentals;
        LOGGER.info("Successfully retrieved %d rentals.", rentals.size());
    }
}