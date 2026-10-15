package it.unipd.dei.webapp.database.movie;

import it.unipd.dei.webapp.database.AbstractDAO;
import it.unipd.dei.webapp.resource.movie.Movie;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.unipd.dei.webapp.resource.movie.MovieFactory;

/**
 * Lists all the active movies available in the database.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class GetAllMoviesDAO extends AbstractDAO<List<Movie>> {
    /**
     * The SQL statement to be executed
     */
    private static final String STATEMENT =
            "SELECT m.* " +
                    "FROM movie_with_rating AS m " +
                    "ORDER BY title";

    /**
     * Creates a new object for listing all movies.
     *
     * @param con the connection to the database.
     */
    public GetAllMoviesDAO(final Connection con) {
        super(con);
    }

    @Override
    protected final void doAccess() throws SQLException {
        List<Movie> movies = new ArrayList<>();

        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                movies.add(MovieFactory.fromDBRow(rs));
            }

            LOGGER.info("%d movies have been successfully retrieved.", movies.size());
        }

        outputParam = movies;
    }
}