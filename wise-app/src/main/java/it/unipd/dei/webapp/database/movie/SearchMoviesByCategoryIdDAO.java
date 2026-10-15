package it.unipd.dei.webapp.database.movie;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.unipd.dei.webapp.database.AbstractDAO;
import it.unipd.dei.webapp.resource.movie.Movie;
import it.unipd.dei.webapp.resource.movie.MovieFactory;

/**
 * Searches for all movies belonging to a specific category
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class SearchMoviesByCategoryIdDAO extends AbstractDAO<List<Movie>> {

    /**
     * The SQL statement to retrieve the movies.
     * Joins Movie with HasCategory to filter by category_id and joins with AgeRating.
     */
    private static final String STATEMENT =
            "SELECT m.* " +
                    "FROM movie_with_rating AS m " +
                    "INNER JOIN HasCategory AS hc ON m.movie_id = hc.movie_id " +
                    "INNER JOIN Category AS c ON hc.category_id = c.category_id AND c.is_active = TRUE " +
                    "WHERE hc.category_id = ? AND m.is_active = TRUE";

    /**
     * The unique identifier of the category.
     */
    private final Integer categoryId;

    /**
     * Creates a new object to search for movies by category ID.
     * 
     * @param con the database connection.
     * @param categoryId the ID of the category.
     */
    public SearchMoviesByCategoryIdDAO(final Connection con, final Integer categoryId) {
        super(con);
        this.categoryId = categoryId;
    }

    @Override
    protected final void doAccess() throws SQLException {

        final List<Movie> movies = new ArrayList<>();

        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT)) {

            pstmt.setInt(1, categoryId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    movies.add(MovieFactory.fromDBRow(rs));
                }
            }

            LOGGER.info("Successfully retrieved %d movies for category with ID %d", movies.size(), categoryId);

        }

        // Sets the output parameter that can be retrieved by getOutputParam()
        outputParam = movies;
    }
}