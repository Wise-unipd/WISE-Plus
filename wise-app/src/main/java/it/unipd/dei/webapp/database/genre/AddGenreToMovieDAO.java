package it.unipd.dei.webapp.database.genre;

import it.unipd.dei.webapp.database.AbstractDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Add a genre to a movie.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class AddGenreToMovieDAO extends AbstractDAO<Boolean> {

    /**
     * The SQL statement to be executed.
     */
    private static final String STATEMENT = "INSERT INTO HasGenre (movie_id, genre_id) VALUES (?, ?)";

    /**
     * The unique identifier of the movie.
     */
    private final int movieId;

    /**
     * The unique identifier of the genre.
     */
    private final int genreId;

    /**
     * Creates a new object to add a genre to a movie.
     *
     * @param con     the database connection.
     * @param movieId the unique identifier of the movie.
     * @param genreId the unique identifier of the genre.
     */
    public AddGenreToMovieDAO(final Connection con, final int movieId, final int genreId) {
        super(con);
        this.movieId = movieId;
        this.genreId = genreId;
    }

    @Override
    protected final void doAccess() throws SQLException {

        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT)) {
            pstmt.setInt(1, movieId);
            pstmt.setInt(2, genreId);

            // returns the number of rows changed
            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                outputParam = true;
                LOGGER.info("Genre %d successfully add to movie %d.", genreId, movieId);
            } else {
                outputParam = false;
                LOGGER.warn("Failed to add genre %d to movie %d.", genreId, movieId);
            }
        }
    }
}