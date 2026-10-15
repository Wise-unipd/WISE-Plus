package it.unipd.dei.webapp.database.movie;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.unipd.dei.webapp.database.AbstractDAO;
import it.unipd.dei.webapp.resource.movie.Movie;
import it.unipd.dei.webapp.resource.movie.MovieFactory;

/**
 * Search a movie by its unique identifier.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class SearchMovieByIdDAO extends AbstractDAO<Movie> {

    /**
     * The SQL statement to be executed
     */
    private static final String STATEMENT =
            "SELECT m.* FROM movie_with_rating AS m WHERE m.movie_id = ?";

    /**
     * The unique identifier of the movie
     */
    private final Integer id;

    /**
     * Creates a new object to search for a movie by ID.
     *
     * @param con the database connection.
     * @param id  the unique identifier of the movie.
     */
    public SearchMovieByIdDAO(final Connection con, final Integer id) {
        super(con);
        this.id = id;
    }

    @Override
    protected final void doAccess() throws SQLException {

        Movie movie = null;

        // try-with-resources
        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT)) {
            pstmt.setInt(1, id);

            // try-with-resources
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    movie = MovieFactory.fromDBRow(rs);

                    LOGGER.info("Movie with ID %d successfully retrieved", movie.getId());
                }
            }
        }

        outputParam = movie;
    }
}
