package it.unipd.dei.webapp.database.language;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.unipd.dei.webapp.database.AbstractDAO;
import it.unipd.dei.webapp.resource.movie.Language;

/**
 * Updates an existing language's information in the database.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class UpdateLanguageDAO extends AbstractDAO<Language> {

    /**
     * The SQL statement to update the language and return the updated record.
     */
    private static final String STATEMENT =
            "UPDATE Language SET name = ? WHERE lang_id = ? RETURNING *";

    /**
     * The language data to be updated.
     */
    private final Language language;

    /**
     * Creates a new object to update a language in the database.
     *
     * @param con      the database connection.
     * @param language the language containing the updated data.
     */
    public UpdateLanguageDAO(final Connection con, final Language language) {
        super(con);

        if (language == null) {
            LOGGER.error("The language cannot be null.");
            throw new NullPointerException("The language cannot be null.");
        }

        this.language = language;
    }

    @Override
    protected void doAccess() throws SQLException {
        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT)) {
            pstmt.setString(1, language.getName());
            pstmt.setInt(2, language.getId());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    outputParam = new Language(
                            rs.getInt("lang_id"),
                            rs.getString("name")
                    );

                    LOGGER.info("Language '%s' (ID: %d) successfully updated.", this.outputParam.getName(), this.outputParam.getId());
                } else {
                    LOGGER.warn("Language with ID %d not found for update.", language.getId());
                    outputParam = null;
                }
            }
        }
    }
}