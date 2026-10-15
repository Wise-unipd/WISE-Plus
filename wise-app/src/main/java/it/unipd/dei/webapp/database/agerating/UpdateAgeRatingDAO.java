package it.unipd.dei.webapp.database.agerating;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.unipd.dei.webapp.database.AbstractDAO;
import it.unipd.dei.webapp.resource.movie.AgeRating;

/**
 * Updates an existing age rating.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class UpdateAgeRatingDAO extends AbstractDAO<AgeRating> {
    /**
     * The SQL statement to be executed.
     */
    private static final String STATEMENT = "UPDATE AgeRating SET name = ?, description = ?, age = ? WHERE rating_id = ? RETURNING *";

    /**
     * The age rating data to be updated.
     */
    private final AgeRating ageRating;

    /**
     * Creates a new object to update an age rating in the database.
     *
     * @param con       the database connection.
     * @param ageRating the ageRating containing the updated data.
     */
    public UpdateAgeRatingDAO(final Connection con, final AgeRating ageRating) {
        super(con);

        if (ageRating == null) {
            LOGGER.error("The age rating cannot be null.");
            throw new NullPointerException("The age rating cannot be null.");
        }

        this.ageRating = ageRating;
    }

    @Override
    protected void doAccess() throws SQLException {
        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT)) {
            int i = 1;
            pstmt.setString(i++, ageRating.getName());
            pstmt.setString(i++, ageRating.getDescription());
            pstmt.setInt(i++, ageRating.getAge());
            pstmt.setInt(i++, ageRating.getId());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    outputParam = new AgeRating(
                            rs.getInt("rating_id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getInt("age")
                    );

                    LOGGER.info("Age Rating %s (ID: %d) successfully updated.",
                        outputParam.getName(), outputParam.getId());
                } else {
                    LOGGER.warn("Age Rating with ID %d not found for update.", ageRating.getId());
                    outputParam = null;
                }
            }
        }
    }
}