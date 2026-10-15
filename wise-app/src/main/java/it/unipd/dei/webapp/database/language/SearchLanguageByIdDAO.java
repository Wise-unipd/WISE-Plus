package it.unipd.dei.webapp.database.language;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.unipd.dei.webapp.database.AbstractDAO;
import it.unipd.dei.webapp.resource.movie.Language;

/**
 * Searches for a language in the database using its unique identifier.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class SearchLanguageByIdDAO extends AbstractDAO<Language> {

    /**
     * The SQL statement to retrieve the language information.
     */
    private static final String STATEMENT =
            "SELECT * FROM Language WHERE lang_id = ?";

    /**
     * The unique identifier of the language.
     */
    private final Integer id;

    /**
     * Creates a new object to search for a language.
     *
     * @param con the database connection.
     * @param id  the ID of the language.
     */
    public SearchLanguageByIdDAO(final Connection con, final Integer id) {
        super(con);
        this.id = id;
    }

    @Override
    protected final void doAccess() throws SQLException {
        Language language = null;

        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    language = new Language(
                            rs.getInt("lang_id"),
                            rs.getString("name")
                    );

                    LOGGER.info("Language with ID %d successfully found.", language.getId());
                } else {
                    LOGGER.warn("Language with ID %d not found in the database.", id);
                }
            }
        }
        outputParam = language;
    }
}