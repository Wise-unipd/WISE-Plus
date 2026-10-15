package it.unipd.dei.webapp.database.language;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.unipd.dei.webapp.database.AbstractDAO;
import it.unipd.dei.webapp.resource.movie.Language;

/**
 * Deletes a language from the database and returns the deleted record.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class DeleteLanguageDAO extends AbstractDAO<Language> {

    /**
     * The SQL statement to delete a language and return its details.
     */
    private static final String STATEMENT = "DELETE FROM Language WHERE lang_id = ? RETURNING *";

    /**
     * The ID of the language to delete.
     */
    private final int languageId;

    /**
     * Creates a new object to delete a language from the database.
     *
     * @param con        the database connection.
     * @param languageId the ID of the language to delete.
     */
    public DeleteLanguageDAO(final Connection con, final int languageId) {
        super(con);
        this.languageId = languageId;
    }

    @Override
    protected void doAccess() throws SQLException {
        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT)) {
            pstmt.setInt(1, languageId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    outputParam = new Language(
                            rs.getInt("lang_id"),
                            rs.getString("name")
                    );
                    LOGGER.info("Language '%s' (ID: %d) was successfully deleted.",
                            outputParam.getName(), outputParam.getId());
                } else {
                    LOGGER.warn("Language with ID %d not found for deletion.", languageId);
                    outputParam = null;
                }
            }
        }
    }
}