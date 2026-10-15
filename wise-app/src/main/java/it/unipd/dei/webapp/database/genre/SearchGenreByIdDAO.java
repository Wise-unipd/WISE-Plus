package it.unipd.dei.webapp.database.genre;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.unipd.dei.webapp.database.AbstractDAO;
import it.unipd.dei.webapp.resource.movie.Genre;

/**
 * Searches for a genre in the database using its unique identifier.
 * <p>If the genre is found, a {@link Genre} object is created and stored as the output parameter.
 * If no genre matches the provided ID, the output parameter is set to {@code null}.</p>
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class SearchGenreByIdDAO extends AbstractDAO<Genre> {

    /**
     * The SQL statement to retrieve the genre's information.
     */
    private static final String STATEMENT =
            "SELECT * FROM Genre WHERE genre_id = ?";

    /**
     * The unique identifier of the genre.
     */
    private final Integer id;

    /**
     * Creates a new object to search for a genre.
     *
     * @param con the database connection.
     * @param id  the ID of the genre.
     */
    public SearchGenreByIdDAO(final Connection con, final Integer id) {
        super(con);
        this.id = id;
    }

    @Override
    protected final void doAccess() throws SQLException {
        Genre genre = null;

        // Execute the search using a try-with-resources block
        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    genre = new Genre(
                            rs.getInt("genre_id"),
                            rs.getString("name")
                    );

                    LOGGER.info("Genre with ID %d successfully found.", genre.getId());
                } else {
                    LOGGER.warn("Genre with ID %d not found in the database.", id);
                }
            }
        }
        outputParam = genre;
    }
}