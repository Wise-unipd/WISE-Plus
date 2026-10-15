package it.unipd.dei.webapp.database.authentication;

import it.unipd.dei.webapp.database.AbstractDAO;
import it.unipd.dei.webapp.resource.authentication.MagicLink;

import java.sql.*;

/**
 * Retrieves a magic link from the database by its token.
 * 
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class GetMagicLinkByToken extends AbstractDAO<MagicLink> {
    /**
     * The SQL statement to be executed.
     */
    private static final String STATEMENT = "SELECT * FROM magiclinks WHERE token = ?";

    /**
     * The token of the magic link to retrieve.
     */
    private final String token;

    /**
     * Creates a new DAO for retrieving a magic link by token.
     *
     * @param con   the connection to the database.
     * @param token the token of the magic link.
     */
    public GetMagicLinkByToken(final Connection con, String token) {
        super(con);

        if (token == null) {
            LOGGER.error("The token cannot be null!");
            throw new NullPointerException("The token cannot be null!");
        }

        this.token = token;
    }

    @Override
    protected void doAccess() throws Exception {
        MagicLink returning = null;

        LOGGER.info("Retrieving magic link for token: %s", token);

        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT)) {
            pstmt.setString(1, token);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    returning = MagicLink.fromDBRow(rs);
                    LOGGER.info("Magic link found for user ID: %d", returning.getUser_id());
                } else {
                    LOGGER.info("No magic link found for token: %s", token);
                }
            }

            this.outputParam = returning;
        } catch (SQLException e) {
            LOGGER.error("Unable to retrieve magic link for token: %s", token, e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("An unexpected error occurred while retrieving magic link for token: %s", token, e);
            throw e;
        }
    }
}
