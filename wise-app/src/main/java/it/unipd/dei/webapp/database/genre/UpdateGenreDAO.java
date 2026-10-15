package it.unipd.dei.webapp.database.genre;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.unipd.dei.webapp.database.AbstractDAO;
import it.unipd.dei.webapp.resource.movie.Genre;

/**
 * Updates an existing genre's information in the database.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class UpdateGenreDAO extends AbstractDAO<Genre> {

    /**
     * The SQL statement to update the genre and return the updated record.
     */
    private static final String STATEMENT =
            "UPDATE Genre SET name = ? WHERE genre_id = ? RETURNING *";

    /**
     * The genre data to be updated.
     */
    private final Genre genre;

    /**
     * Creates a new object to update a genre in the database.
     *
     * @param con   the database connection.
     * @param genre the genre containing the updated data.
     */
    public UpdateGenreDAO(final Connection con, final Genre genre) {
        super(con);

        if (genre == null) {
            LOGGER.error("The genre cannot be null.");
            throw new NullPointerException("The genre cannot be null.");
        }

        this.genre = genre;
    }

    @Override
    protected void doAccess() throws SQLException {
        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT)) {
            pstmt.setString(1, genre.getName());
            pstmt.setInt(2, genre.getId());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    outputParam = new Genre(
                            rs.getInt("genre_id"),
                            rs.getString("name")
                    );

                    LOGGER.info("Genre '%s' (ID: %d) successfully updated.",
                            outputParam.getName(), outputParam.getId());
                } else {
                    LOGGER.warn("Genre with ID %d not found for update.", genre.getId());
                    outputParam = null;
                }
            }
        }
    }
}