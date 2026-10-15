package it.unipd.dei.webapp.database.agerating;

import it.unipd.dei.webapp.database.AbstractDAO;
import it.unipd.dei.webapp.resource.movie.AgeRating;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Lists all the age ratings available in the database.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class GetAllAgeRatingsDAO extends AbstractDAO<List<AgeRating>> {

    /**
     * The SQL statement to be executed
     */
    private static final String STATEMENT = "SELECT * FROM AgeRating ORDER BY rating_id";

    /**
     * Creates a new object for listing all age ratings.
     *
     * @param con the connection to the database.
     */
    public GetAllAgeRatingsDAO(final Connection con) {
        super(con);
    }

    /**
     * Accesses the database to fetch the list of age ratings.
     */
    @Override
    protected final void doAccess() throws SQLException {

        List<AgeRating> ratings = new ArrayList<>();

        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                ratings.add(new AgeRating(
                        rs.getInt("rating_id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getInt("age")
                ));
            }

            LOGGER.info("%d age ratings have been successfully retrieved.", ratings.size());
        }

        outputParam = ratings;
    }
}