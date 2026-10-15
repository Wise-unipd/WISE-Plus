package it.unipd.dei.webapp.database.genre;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.unipd.dei.webapp.database.AbstractDAO;
import it.unipd.dei.webapp.resource.movie.Genre;

/**
 * Inserts a new genre into the database and retrieves the auto-generated ID.
 * <p>If a genre with the same name already exists, the database's UNIQUE constraint
 * will trigger an SQLException.</p>
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class CreateGenreDAO extends AbstractDAO<Genre> {

    /**
     * The SQL statement to insert the genre and return the new record.
     */
    private static final String STATEMENT =
            "INSERT INTO Genre (name) VALUES (?) RETURNING *";

    /**
     * The genre data to be stored.
     */
    private final Genre genre;

    /**
     * Creates a new object to store a genre in the database.
     *
     * @param con   the database connection.
     * @param genre the genre to be stored.
     */
    public CreateGenreDAO(final Connection con, final Genre genre) {
        super(con);

        if (genre == null) {
            LOGGER.error("The genre cannot be null.");
            throw new NullPointerException("The genre cannot be null.");
        }

        this.genre = genre;
    }

    /**
     * Inserts the genre into the database.
     *
     * @throws SQLException if an error occurs while accessing the database.
     */
    @Override
    protected final void doAccess() throws SQLException {
        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT)) {
            pstmt.setString(1, genre.getName());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    outputParam = new Genre(
                            rs.getInt("genre_id"),
                            rs.getString("name")
                    );

                    LOGGER.info("Genre '%s' successfully created with ID %d.", outputParam.getName(), outputParam.getId());
                }
            }
        }
    }
}