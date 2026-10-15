package it.unipd.dei.webapp.database.authentication;

import it.unipd.dei.webapp.database.AbstractDAO;
import it.unipd.dei.webapp.resource.authentication.AppUser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Searches for a user in the database by their email address.
 * 
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class SearchUserByEmailDAO extends AbstractDAO<AppUser> {
    /**
     * The SQL statement to be executed.
     */
    private static final String STATEMENT = "SELECT u.*, r.description FROM Appuser u JOIN userrole r ON u.role = r.id WHERE email = ?";

    /**
     * The email of the user to search for.
     */
    private final String email;

    /**
     * Creates a new DAO for searching a user by email.
     *
     * @param con   the database connection.
     * @param email the email of the user.
     */
    public SearchUserByEmailDAO(final Connection con, final String email) {
        super(con);
        if (email == null) {
            LOGGER.error("The email cannot be null!");
            throw new NullPointerException("The email cannot be null!");
        }

        this.email = email;
    }

    @Override
    protected final void doAccess() throws SQLException {
        AppUser retrievedUser = null;

        LOGGER.info("Searching for user with email: %s", email);

        // try-with-resources
        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT)) {
            pstmt.setString(1, email);

            // try-with-resources
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    retrievedUser = AppUser.fromDBRow(rs);
                    retrievedUser.resetPassword();
                    LOGGER.info("User with email %s successfully retrieved (ID: %d)", email, retrievedUser.getId());
                } else {
                    LOGGER.info("No user found with email: %s", email);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Unable to search for user with email: %s", email, e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("An unexpected error occurred while searching for user with email: %s", email, e);
            throw new SQLException(e);
        }

        this.outputParam = retrievedUser;
    }
}
