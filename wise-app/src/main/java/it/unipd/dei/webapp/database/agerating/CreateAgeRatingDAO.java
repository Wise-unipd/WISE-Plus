package it.unipd.dei.webapp.database.agerating;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

import it.unipd.dei.webapp.database.AbstractDAO;
import it.unipd.dei.webapp.resource.movie.AgeRating;

/**
 * Creates a new age rating in the database.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class CreateAgeRatingDAO extends AbstractDAO<AgeRating> {
    /**
     * The SQL statement to be executed
     */
    private static final String STATEMENT = "INSERT INTO AgeRating (name, description, age) VALUES (?, ?, ?) RETURNING *";

    /**
     * The age rating to be stored in the database
     */
    private final AgeRating ageRating;

    /**
     * Creates a new object to store an age rating in the database.
     *
     * @param con       the database connection.
     * @param ageRating the age rating to be stored in the database.
     */
    public CreateAgeRatingDAO(final Connection con, final AgeRating ageRating) {
        super(con);

        if (ageRating == null) {
            LOGGER.error("The age rating cannot be null.");
            throw new NullPointerException("The age rating cannot be null.");
        }

        this.ageRating = ageRating;
    }

    @Override
    protected void doAccess() throws SQLException {

        AgeRating returningAgeRating = null;

        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT)) {
            pstmt.setString(1, ageRating.getName());
            pstmt.setString(2, ageRating.getDescription());
            pstmt.setInt(3, ageRating.getAge());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    returningAgeRating = new AgeRating(
                            rs.getInt("rating_id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getInt("age"));

                    LOGGER.info("Age rating %s successfully stored in the database", ageRating.getId());
                }
            }
        }

        outputParam = returningAgeRating;
    }
}
