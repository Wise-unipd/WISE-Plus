package it.unipd.dei.webapp.database.genre;

import it.unipd.dei.webapp.database.AbstractDAO;
import it.unipd.dei.webapp.resource.movie.Genre;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Lists all the genres available in the database.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class GetAllGenresDAO extends AbstractDAO<List<Genre>> {

    /**
     * The SQL statement to be executed to retrieve all genres, ordered alphabetically by name.
     */
    private static final String STATEMENT =
            "SELECT * FROM Genre ORDER BY name";

    /**
     * Creates a new object for listing all genres.
     *
     * @param con the connection to the database.
     */
    public GetAllGenresDAO(final Connection con) {
        super(con);
    }

    /**
     * Accesses the database to fetch the list of genres.
     */
    @Override
    protected final void doAccess() throws SQLException {

        // List to store the retrieved genres
        List<Genre> genres = new ArrayList<>();

        // Use try-with-resources to ensure the PreparedStatement and ResultSet are properly closed
        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT);
             ResultSet rs = pstmt.executeQuery()) {

            // Iterate through the result set and create a new Genre object for each row
            while (rs.next()) {
                genres.add(new Genre(
                        rs.getInt("genre_id"),
                        rs.getString("name")
                ));
            }

            // Log the success of the operation
            LOGGER.info("%d genres have been successfully retrieved.", genres.size());
        }

        // Set the output parameter to be returned by the DAO
        this.outputParam = genres;
    }
}