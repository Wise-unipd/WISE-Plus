package it.unipd.dei.webapp.database.language;

import it.unipd.dei.webapp.database.AbstractDAO;
import it.unipd.dei.webapp.resource.movie.Language;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Lists all the languages available in the database.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class GetAllLanguagesDAO extends AbstractDAO<List<Language>> {

    /**
     * The SQL statement to be executed to retrieve all languages, ordered alphabetically by name.
     */
    private static final String STATEMENT =
            "SELECT * FROM Language ORDER BY name";

    /**
     * Creates a new object for listing all languages.
     *
     * @param con the connection to the database.
     */
    public GetAllLanguagesDAO(final Connection con) {
        super(con);
    }

    /**
     * Accesses the database to fetch the list of languages.
     */
    @Override
    protected final void doAccess() throws SQLException {

        // List to store the retrieved languages
        List<Language> languages = new ArrayList<>();

        // Use try-with-resources to ensure the PreparedStatement and ResultSet are properly closed
        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT);
             ResultSet rs = pstmt.executeQuery()) {

            // Iterate through the result set and create a new Language object for each row
            while (rs.next()) {
                languages.add(new Language(
                        rs.getInt("lang_id"),
                        rs.getString("name")
                ));
            }

            // Log the success of the operation
            LOGGER.info("%d languages have been successfully retrieved.", languages.size());
        }

        // Set the output parameter to be returned by the DAO
        outputParam = languages;
    }
}