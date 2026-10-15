package it.unipd.dei.webapp.database.language;

import it.unipd.dei.webapp.database.AbstractDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Remove a subtitle language from a movie.
 * This Data Access Object (DAO) deletes a specific record from the Subtitle linking table.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class RemoveSubtitleFromMovieDAO extends AbstractDAO<Boolean> {

    /**
     * The SQL statement to be executed.
     */
    private static final String STATEMENT = "DELETE FROM Subtitle WHERE movie_id = ? AND lang = ?";

    /**
     * The unique identifier of the movie.
     */
    private final int movieId;

    /**
     * The unique identifier of the subtitle language (from the Language table).
     */
    private final int languageId;

    /**
     * Creates a new object to remove a subtitle language from a movie.
     *
     * @param con the database connection.
     * @param movieId the unique identifier of the movie.
     * @param languageId the unique identifier of the subtitle language.
     */
    public RemoveSubtitleFromMovieDAO(final Connection con, final int movieId, final int languageId) {
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
                LOGGER.info("Subtitle language %d successfully removed from movie %d.", languageId, movieId);
            } else {
                outputParam = false;
                LOGGER.warn("Failed to remove subtitle language %d from movie %d.", languageId, movieId);
            }
        }
    }
}