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
 * Searches for all movies in which a specific worker participated.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class SearchMoviesByWorkerIdDAO extends AbstractDAO<List<Movie>> {

    /**
     * The SQL statement to retrieve the movies.
     * Joins Movie with Credit to filter by the worker's unique identifier.
     */
    private static final String STATEMENT =
            "SELECT DISTINCT m.* " +
                    "FROM movie_with_rating AS m " +
                    "INNER JOIN Credit AS c ON m.movie_id = c.movie_id " +
                    "WHERE c.worker_id = ? AND m.is_active = TRUE";

    /**
     * The unique identifier of the worker.
     */
    private final Integer workerId;

    /**
     * Creates a new object to search for movies associated with a worker.
     * 
     * @param con the database connection.
     * @param workerId the ID of the worker.
     */
    public SearchMoviesByWorkerIdDAO(final Connection con, final Integer workerId) {
        super(con);
        this.workerId = workerId;
    }

    @Override
    protected final void doAccess() throws SQLException {

        final List<Movie> movies = new ArrayList<>();

        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT)) {

            pstmt.setInt(1, workerId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    movies.add(MovieFactory.fromDBRow(rs));
                }
            }

            LOGGER.info("Successfully retrieved %d movies for worker with ID %d", movies.size(), workerId);

        } catch (SQLException e) {
            LOGGER.error("Unable to search movies for worker ID %d.", workerId, e);
            throw e;
        }

        // Sets the output parameter that can be retrieved by getOutputParam()
        outputParam = movies;
    }
}