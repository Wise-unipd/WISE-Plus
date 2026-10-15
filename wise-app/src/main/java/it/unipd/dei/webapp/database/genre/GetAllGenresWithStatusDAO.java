package it.unipd.dei.webapp.database.genre;

import it.unipd.dei.webapp.database.AbstractDAO;
import it.unipd.dei.webapp.resource.movie.Genre;
import it.unipd.dei.webapp.resource.movie.Selectable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Lists all genres available in the database, with their selection status.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class GetAllGenresWithStatusDAO extends AbstractDAO<List<Selectable<Genre>>> {

    /**
     * The SQL statement to be executed.
     */
    private static final String STATEMENT =
            "SELECT g.*, " +
                    "(hg.movie_id IS NOT NULL) AS is_selected " +
                    "FROM Genre AS g " +
                    "LEFT JOIN HasGenre AS hg ON g.genre_id = hg.genre_id " +
                    "AND hg.movie_id = ? " +
                    "ORDER BY is_selected DESC, g.name ASC";

    /**
     * The unique identifier of the movie.
     */
    private final int movieId;

    /**
     * Creates a new object for listing all genres with status.
     *
     * @param con     the database connection.
     * @param movieId the unique identifier of the movie.
     */
    public GetAllGenresWithStatusDAO(final Connection con, final int movieId) {
        super(con);
        this.movieId = movieId;
    }

    @Override
    protected final void doAccess() throws SQLException {

        List<Selectable<Genre>> genres = new ArrayList<>();

        // try-with-resources
        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT)) {
            pstmt.setInt(1, movieId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    genres.add(new Selectable<>(
                            new Genre(rs.getInt("genre_id"), rs.getString("name")),
                            rs.getBoolean("is_selected")
                    ));
                }

                LOGGER.info("%d genres have been successfully retrieved.", genres.size());
            }
        }

        outputParam = genres;
    }
}