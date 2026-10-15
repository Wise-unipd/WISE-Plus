package it.unipd.dei.webapp.database.rentals;

import it.unipd.dei.webapp.database.AbstractDAO;
import it.unipd.dei.webapp.resource.rentals.Rental;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Searches for rentals in the database by a text query.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class GetRentalsByTextDAO extends AbstractDAO<List<Rental>> {

    /**
     * The text query to search for.
     */
    private final String query;

    /**
     * Whether to search in the user's email.
     */
    private final boolean searchEmail;

    /**
     * Whether to search in the movie's title.
     */
    private final boolean searchTitle;

    /**
     * Creates a new object to search for rentals.
     *
     * @param con         the database connection.
     * @param query       the text query to search for.
     * @param searchEmail whether to search in the user's email.
     * @param searchTitle whether to search in the movie's title.
     */
    public GetRentalsByTextDAO(final Connection con, String query, boolean searchEmail, boolean searchTitle) {
        super(con);
        this.query = (query == null || query.trim().isEmpty()) ? "%" : "%" + query + "%";
        this.searchEmail = searchEmail;
        this.searchTitle = searchTitle;
    }

    /**
     * Searches for rentals in the database by a text query.
     *
     * @throws Exception if an error occurs while accessing the database.
     */
    @Override
    protected final void doAccess() throws Exception {
        List<Rental> rentals = new ArrayList<>();

        // Query
        StringBuilder sql = new StringBuilder(
                "SELECT r.id, r.user_id, a.email, a.first_name, a.last_name, r.movie_id, m.title, r.checkout, re.expiration " +
                        "FROM rent AS r " +
                        "JOIN movie AS m ON r.movie_id = m.movie_id " +
                        "JOIN appuser AS a ON a.id = r.user_id " +
                        "LEFT JOIN (SELECT rent, MAX(expiration) AS expiration FROM rent_expiration GROUP BY rent) AS re ON r.id = re.rent WHERE "
        );

        // dinamic logic for the 'where' filter
        if (searchEmail && searchTitle) {
            sql.append("(a.email ILIKE ? OR m.title ILIKE ?)");
        } else if (searchEmail) {
            sql.append("a.email ILIKE ?");
        } else if (searchTitle) {
            sql.append("m.title ILIKE ?");
        } else {
            sql.append("TRUE");
        }

        sql.append(" ORDER BY r.checkout DESC");

        try (PreparedStatement pstmt = con.prepareStatement(sql.toString())) {

            if (searchEmail && searchTitle) {
                pstmt.setString(1, query);
                pstmt.setString(2, query);
            } else {
                pstmt.setString(1, query);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
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
                            rs.getTimestamp("expiration")
                    ));
                }
            }
        }

        outputParam = rentals;
        LOGGER.info("Successfully searched for rentals with query '%s'. Found %d results.", query, rentals.size());
    }
}