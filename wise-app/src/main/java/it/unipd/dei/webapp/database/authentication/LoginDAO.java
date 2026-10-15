package it.unipd.dei.webapp.database.authentication;


import it.unipd.dei.webapp.database.AbstractDAO;
import it.unipd.dei.webapp.resource.authentication.AppUser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * Authenticates a user.
 * 
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class LoginDAO extends AbstractDAO<AppUser> {

    /**
     * The SQL statement to be executed.
     */
    private static final String STATEMENT =
            "SELECT u.*, r.description " +
                    "FROM appuser u " +
                    "JOIN userrole r ON u.role = r.id " +
                    "WHERE u.email = ?";

    /**
     * The user to be authenticated.
     */
    private final AppUser loginUser;

    /**
     * Creates a new DAO for authenticating a user.
     *
     * @param con       the connection to the database.
     * @param loginUser the user to be authenticated.
     */
    public LoginDAO(final Connection con, final AppUser loginUser) {
        super(con);

        if (loginUser == null) {
            LOGGER.error("The login user cannot be null!");
            throw new NullPointerException("The login user cannot be null!");
        }

        this.loginUser = loginUser;
    }

    @Override
    protected final void doAccess() throws SQLException {
        AppUser loggedUser = null;

        LOGGER.info("Authenticating user: %s", loginUser.getEmail());

        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT)) {
            pstmt.setString(1, loginUser.getEmail());
//            pstmt.setString(2, loginUser.getPassword());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    loggedUser = AppUser.fromDBRow(rs);
//                    loggedUser.resetPassword();
                    LOGGER.info("User %s successfully authenticated.", loggedUser.getEmail());
                } else {
                    LOGGER.warn("Authentication failed for user %s: wrong credentials.", loginUser.getEmail());
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Unable to authenticate user: %s", loginUser.getEmail(), e);
            throw e;
        }

        this.outputParam = loggedUser;
    }
}
