package it.unipd.dei.webapp.database.agerating;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.unipd.dei.webapp.database.AbstractDAO;
import it.unipd.dei.webapp.resource.movie.AgeRating;

/**
 * Search an age rating by its unique identifier.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class SearchAgeRatingByIdDAO extends AbstractDAO<AgeRating> {
    /**
     * The SQL statement to be executed.
     */
    private static final String STATEMENT = "SELECT * FROM AgeRating WHERE rating_id = ?";

    /**
     * The unique identifier of the age rating.
     */
    private final Integer id;

    /**
     * Creates a new object to search for an age rating.
     *
     * @param con the database connection.
     * @param id  the unique identifier of the age rating.
     */
    public SearchAgeRatingByIdDAO(final Connection con, final Integer id) {
        super(con);
        this.id = id;
    }

    @Override
    protected final void doAccess() throws SQLException {
        AgeRating ageRating = null;

        // try-with-resources
        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    ageRating = new AgeRating(
                            rs.getInt("rating_id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getInt("age")
                    );

                    LOGGER.info("Age Rating with ID %d successfully found.", ageRating.getId());
                } else {
                    LOGGER.warn("Age Rating with ID %d not found in the database.", id);
                }
            }
        }
        outputParam = ageRating;
    }
}