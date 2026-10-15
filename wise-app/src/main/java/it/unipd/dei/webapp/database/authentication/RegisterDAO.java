package it.unipd.dei.webapp.database.authentication;

import it.unipd.dei.webapp.database.AbstractDAO;
import it.unipd.dei.webapp.resource.authentication.AppUser;

import java.sql.*;

/**
 * Registers a new user in the database.
 * 
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class RegisterDAO extends AbstractDAO<AppUser> {

    /**
     * The SQL statement to be executed.
     */
    private static final String STATEMENT =
            "WITH inserted_user AS ( " +
                    "    INSERT INTO appuser (email, password, first_name, last_name, created, last_update, role, is_active) " +
                    "    VALUES (?, ?, ?, ?, ?, ?, 0, true) " +
                    "    RETURNING * " +
                    ") " +
                    "SELECT iu.*, r.description AS description " +
                    "FROM inserted_user iu " +
                    "LEFT JOIN userrole r ON iu.role = r.id;";

    /**
     * The new user to be registered.
     */
    private final AppUser newUser;

    /**
     * True if the email is already registered.
     */
    private boolean duplicateEmail = false;

    /**
     * Returns true if the email is already registered.
     *
     * @return true if the email is already registered.
     */
    public boolean isDuplicateEmail() {
        return duplicateEmail;
    }

    /**
     * Creates a new DAO for registering a user.
     *
     * @param con     the connection to the database.
     * @param newUser the new user to be registered.
     */
    public RegisterDAO(final Connection con, final AppUser newUser) {
        super(con);

        if (newUser == null) {
            LOGGER.error("The new user cannot be null!");
            throw new NullPointerException("The new user cannot be null!");
        }

        this.newUser = newUser;
    }

    @Override
    protected void doAccess() throws Exception {
        AppUser registeredUser = null;
        Timestamp now = new Timestamp(System.currentTimeMillis());

        LOGGER.info("Registering a new user: %s", newUser.getEmail());

        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT)) {
            pstmt.setString(1, newUser.getEmail());
            pstmt.setString(2, newUser.getPassword());
            pstmt.setString(3, newUser.getFirstName());
            pstmt.setString(4, newUser.getLastName());
            pstmt.setTimestamp(5, now);  // created
            pstmt.setTimestamp(6, now);  // last_update

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    registeredUser = AppUser.fromDBRow(rs);
                    registeredUser.resetPassword();
                    LOGGER.info("User %s successfully registered.", registeredUser.getEmail());
                    this.outputParam = registeredUser;
                }
            }
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                LOGGER.warn("Registration failed: an account with email %s is already registered.", newUser.getEmail());
                this.duplicateEmail = true;
                this.outputParam = null;
            } else {
                LOGGER.error("Unable to register user: %s", newUser.getEmail(), e);
                throw e;
            }
        } catch (Exception e) {
            LOGGER.error("An unexpected error occurred while registering user: %s", newUser.getEmail(), e);
            throw e;
        }
    }
}
