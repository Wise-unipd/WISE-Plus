package it.unipd.dei.webapp.database.genre;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.unipd.dei.webapp.database.AbstractDAO;
import it.unipd.dei.webapp.resource.movie.Genre;

/**
 * Deletes a genre from the database and returns the deleted record.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class DeleteGenreDAO extends AbstractDAO<Genre> {

    /**
     * The SQL statement to delete a genre and return its details.
     */
    private static final String STATEMENT = "DELETE FROM Genre WHERE genre_id = ? RETURNING *";

    /**
     * The ID of the genre to delete.
     */
    private final int genreId;

    /**
     * Creates a new object to delete a genre from the database.
     *
     * @param con     the database connection.
     * @param genreId the ID of the genre to delete.
     */
    public DeleteGenreDAO(final Connection con, final int genreId) {
        super(con);
        this.genreId = genreId;
    }

    @Override
    protected void doAccess() throws SQLException {
        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT)) {
            pstmt.setInt(1, genreId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    outputParam = new Genre(
                            rs.getInt("genre_id"),
                            rs.getString("name")
                    );
                    LOGGER.info("Genre '%s' (ID: %d) was successfully deleted.",
                            outputParam.getName(), outputParam.getId());
                } else {
                    LOGGER.warn("Genre with ID %d not found for deletion.", genreId);
                    outputParam = null;
                }
            }
        }
    }
}