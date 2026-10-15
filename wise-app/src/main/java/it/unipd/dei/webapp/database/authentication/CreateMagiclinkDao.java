package it.unipd.dei.webapp.database.authentication;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.sql.Timestamp;

import it.unipd.dei.webapp.database.AbstractDAO;
import it.unipd.dei.webapp.utils.TokenGenerator;

import java.sql.*;

/**
 * Creates a new magic link in the database.
 * 
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class CreateMagiclinkDao extends AbstractDAO<String> {
    /**
     * The SQL statement to be executed.
     */
    private static final String STATEMENT = "INSERT INTO magiclinks (user_id, token, created, exp) VALUES (?, ?, ?, ?) RETURNING *";

    /**
     * The user ID to create the magic link for.
     */
    private final Integer userid;

    /**
     * Creates a new magic link DAO.
     *
     * @param con    the connection to the database.
     * @param userid the user ID.
     */
    public CreateMagiclinkDao(final Connection con, Integer userid) {
        super(con);

        if (userid == null) {
            LOGGER.error("The user ID cannot be null!");
            throw new NullPointerException("The user ID cannot be null!");
        }

        this.userid = userid;
    }

    @Override
    protected void doAccess() throws Exception {
        String token = TokenGenerator.generateToken();

        LOGGER.info("Creating a new magic link for user ID: %d.", userid);

        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT)) {
            pstmt.setInt(1, userid);
            pstmt.setString(2, token);
            pstmt.setTimestamp(3, Timestamp.from(Instant.now()));
            pstmt.setTimestamp(4, Timestamp.from(Instant.now().plus(20, ChronoUnit.MINUTES)));

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    LOGGER.info("Magic link successfully created for user ID: %d.", userid);
                    this.outputParam = token;
                } else {
                    LOGGER.error("Unable to create magic link for user ID: %d: no row returned.", userid);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Unable to create magic link for user ID: %d.", userid, e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("An unexpected error occurred while creating magic link for user ID: %d.", userid, e);
            throw e;
        }
    }
}
