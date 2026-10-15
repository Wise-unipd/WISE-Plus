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
 * Gets the trending movies in the database.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class GetTrendingMoviesDAO extends AbstractDAO<List<Movie>> {
    /**
     * The SQL statement to be executed
     */
    private static final String STATEMENT =
            "SELECT m.*, COALESCE(l.likes_count, 0) AS likes_count " +
                    "FROM movie_with_rating AS m " +
                    "LEFT JOIN (" +
                    "   SELECT movie_id, COUNT(*) AS likes_count " +
                    "   FROM Likes " +
                    "   GROUP BY movie_id " +
                    ") AS l ON m.movie_id = l.movie_id " +
                    "WHERE m.is_active = true "+
                    "ORDER BY m.release_year DESC, likes_count DESC, m.title ASC " +
                    "LIMIT ?";

    /**
     * The maximum number of movies to retrieve
     */
    private final int limit;

    /**
     * Creates a new object to get the trending movies in the database.
     *
     * @param con   the database connection.
     * @param limit the number of rows to return.
     */
    public GetTrendingMoviesDAO(final Connection con, int limit) {
        super(con);
        this.limit = limit;
    }

    protected final void doAccess() throws SQLException {

        List<Movie> movies = new ArrayList<>();

        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT)) {
            pstmt.setInt(1, limit);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    movies.add(MovieFactory.fromDBRow(rs));
                }

                LOGGER.info("%d trending movies have been successfully retrieved (requested limit: %d", movies.size(), limit);
            }
        }

        outputParam = movies;
    }
}
