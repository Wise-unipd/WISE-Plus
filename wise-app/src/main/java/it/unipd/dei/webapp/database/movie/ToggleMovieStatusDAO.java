package it.unipd.dei.webapp.database.movie;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.unipd.dei.webapp.database.AbstractDAO;
import it.unipd.dei.webapp.resource.movie.Movie;
import it.unipd.dei.webapp.resource.movie.MovieFactory;

/**
 * Toggle a movie in the database.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class ToggleMovieStatusDAO extends AbstractDAO<Movie> {

    /**
     * The SQL statement to be executed.
     */
    private static final String STATEMENT = "WITH toggled_movie AS ( " +
                                            "   UPDATE Movie SET is_active = ? WHERE movie_id = ? " +
                                            "   RETURNING * " +
                                            ") " +
                                            "SELECT tm.*, " +
                                            "       ar.name        AS rating_name, " +
                                            "       ar.description AS rating_description, " +
                                            "       ar.age         AS rating_age " +
                                            "FROM toggled_movie AS tm " +
                                            "INNER JOIN Agerating AS ar ON tm.rating = ar.rating_id";

    /**
     * The unique identifier of the movie.
     */
    private final int movieId;

    /**
     * The new status of the movie.
     */
    private final boolean newStatus;

    /**
     * Creates a new object to toggle a movie.
     *
     * @param con       the database connection.
     * @param movieId   the unique identifier of the movie.
     * @param newStatus the new status.
     */
    public ToggleMovieStatusDAO(final Connection con, final int movieId, final boolean newStatus) {
        super(con);
        this.movieId = movieId;
        this.newStatus = newStatus;
    }

    @Override
    protected void doAccess() throws SQLException {

        Movie movie = null;

        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT)) {
            pstmt.setBoolean(1, newStatus);
            pstmt.setInt(2, movieId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    movie = MovieFactory.fromDBRow(rs);

                    LOGGER.info("Movie %d status successfully udpated", movie.getId());
                } else {
                    LOGGER.warn("Movie %d not found in the database.", movieId);
                }
            }
        }

        outputParam = movie;
    }
}