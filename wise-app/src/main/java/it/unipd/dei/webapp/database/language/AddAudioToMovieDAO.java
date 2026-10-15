package it.unipd.dei.webapp.database.language;

import it.unipd.dei.webapp.database.AbstractDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Add an audio language to a movie.
 * This Data Access Object (DAO) inserts a new record into the Audio linking table,
 * which connects a Movie to a Language.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class AddAudioToMovieDAO extends AbstractDAO<Boolean> {

    /**
     * The SQL statement to be executed.
     */
    private static final String STATEMENT = "INSERT INTO Audio (movie_id, lang) VALUES (?, ?)";

    /**
     * The unique identifier of the movie.
     */
    private final int movieId;

    /**
     * The unique identifier of the audio language (from the Language table).
     */
    private final int languageId;

    /**
     * Creates a new object to add an audio language to a movie.
     *
     * @param con the database connection.
     * @param movieId the unique identifier of the movie.
     * @param languageId the unique identifier of the language.
     */
    public AddAudioToMovieDAO(final Connection con, final int movieId, final int languageId) {
        super(con);
        this.movieId = movieId;
        this.languageId = languageId;
    }

    @Override
    protected final void doAccess() throws SQLException {
        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT)) {
            pstmt.setInt(1, movieId);
            pstmt.setInt(2, languageId);

            // returns the number of rows changed
            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                outputParam = true;
                LOGGER.info("Audio language %d successfully added to movie %d.", languageId, movieId);
            } else {
                outputParam = false;
                LOGGER.warn("Failed to add audio language %d to movie %d.", languageId, movieId);
            }
        }
    }
}