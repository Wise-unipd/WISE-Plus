package it.unipd.dei.webapp.database.language;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.unipd.dei.webapp.database.AbstractDAO;
import it.unipd.dei.webapp.resource.movie.Language;

/**
 * Inserts a new language into the database and retrieves the auto-generated ID.
 * <p>If a language with the same name already exists, the database's UNIQUE constraint
 * will trigger an SQLException.</p>
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class CreateLanguageDAO extends AbstractDAO<Language> {

    /**
     * The SQL statement to insert the language and return the new record.
     */
    private static final String STATEMENT =
            "INSERT INTO Language (name) VALUES (?) RETURNING *";

    /**
     * The language data to be stored.
     */
    private final Language language;

    /**
     * Creates a new object to store a language in the database.
     *
     * @param con      the database connection.
     * @param language the language to be stored.
     */
    public CreateLanguageDAO(final Connection con, final Language language) {
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

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    outputParam = new Language(
                            rs.getInt("lang_id"),
                            rs.getString("name")
                    );

                    LOGGER.info("Language '%s' successfully created with ID %d.",
                            outputParam.getName(), outputParam.getId());
                }
            }
        }
    }
}