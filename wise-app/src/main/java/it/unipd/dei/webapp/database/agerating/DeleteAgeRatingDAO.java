package it.unipd.dei.webapp.database.agerating;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.unipd.dei.webapp.database.AbstractDAO;
import it.unipd.dei.webapp.resource.movie.AgeRating;

/**
 * Deletes an age rating from the database and returns the deleted record.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class DeleteAgeRatingDAO extends AbstractDAO<AgeRating> {
    /**
     * The SQL statement to be executed.
     */
    private static final String STATEMENT = "DELETE FROM AgeRating WHERE rating_id = ? RETURNING *";

    /**
     * The ID of the age rating to delete.
     */
    private final int ageRatingId;

    /**
     * Creates a new object to delete an age rating from the database.
     *
     * @param con         the database connection.
     * @param ageRatingId the ID of the age rating to delete.
     */
    public DeleteAgeRatingDAO(final Connection con, final int ageRatingId) {
        super(con);
        this.ageRatingId = ageRatingId;
    }

    @Override
    protected void doAccess() throws SQLException {
        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT)) {
            pstmt.setInt(1, ageRatingId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    outputParam = new AgeRating(
                            rs.getInt("rating_id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getInt("age")
                    );
                    LOGGER.info("Age Rating %s (ID: %d) was successfully deleted.", outputParam.getName(), outputParam.getId());
                } else {
                    LOGGER.warn("Age Rating with ID %d not found for deletion.", ageRatingId);
                    outputParam = null;
                }
            }
        }
    }
}