package it.unipd.dei.webapp.database.language;

import it.unipd.dei.webapp.database.AbstractDAO;
import it.unipd.dei.webapp.resource.movie.Language;
import it.unipd.dei.webapp.resource.movie.Selectable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Lists all audio languages available in the database, with their selection status for a specific movie.
 * It performs a LEFT JOIN between the Language table and the Audio junction table to determine
 * if each language is currently linked to the given movie.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class GetAllAudiosWithStatusDAO extends AbstractDAO<List<Selectable<Language>>> {

    /**
     * The SQL statement to be executed.
     */
    private static final String STATEMENT =
            "SELECT l.lang_id, l.name, " +
                    "(a.movie_id IS NOT NULL) AS is_selected " +
                    "FROM Language AS l " +
                    "LEFT JOIN Audio AS a ON l.lang_id = a.lang " +
                    "AND a.movie_id = ? " +
                    "ORDER BY is_selected DESC, l.name ASC";

    /**
     * The unique identifier of the movie to check associations against.
     */
    private final int movieId;

    /**
     * Creates a new object for listing all audio languages with their status relative to a movie.
     *
     * @param con the database connection.
     * @param movieId the unique identifier of the movie.
     */
    public GetAllAudiosWithStatusDAO(final Connection con, final int movieId) {
        super(con);
        this.movieId = movieId;
    }

    @Override
    protected final void doAccess() throws SQLException {
        List<Selectable<Language>> audios = new ArrayList<>();

        // try-with-resources
        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT)) {
            pstmt.setInt(1, movieId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // Instantiate the Language object instead of Audio
                    audios.add(new Selectable<>(
                            new Language(rs.getInt("lang_id"), rs.getString("name")),
                            rs.getBoolean("is_selected")
                    ));
                }
                LOGGER.info("%d audio languages have been successfully retrieved.", audios.size());
            }
        }
        outputParam = audios;
    }
}