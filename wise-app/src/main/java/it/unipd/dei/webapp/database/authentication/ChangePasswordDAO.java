package it.unipd.dei.webapp.database.authentication;

import it.unipd.dei.webapp.database.AbstractDAO;
import it.unipd.dei.webapp.resource.authentication.AppUser;

import java.sql.*;

/**
 * Updates the password of a user in the database.
 * 
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class ChangePasswordDAO extends AbstractDAO<AppUser> {

    /**
     * The SQL statement to be executed.
     */
    private static final String STATEMENT =
            "WITH updated_user AS ( " +
                    "    UPDATE appuser SET password = ?, last_update = ? " +
                    "    WHERE id = ? " +
                    "    RETURNING * " +
                    ") " +
                    "SELECT u.*, r.description " +
                    "FROM updated_user u " +
                    "JOIN userrole r ON u.role = r.id;";

    /**
     * The ID of the user whose password is to be updated.
     */
    private final int id;

    /**
     * The new password for the user.
     */
    private final String password;

    /**
     * Creates a new DAO for updating a user password.
     *
     * @param con      the connection to the database.
     * @param id       the ID of the user.
     * @param password the new password.
     */
    public ChangePasswordDAO(final Connection con, final int id, final String password) {
        super(con);
        this.id = id;
        this.password = password;
    }

    @Override
    protected final void doAccess() throws Exception {
        LOGGER.info("Updating password for user ID: %d", id);

        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT)) {
            int i = 1;
            pstmt.setString(i++, password);
            pstmt.setTimestamp(i++, new Timestamp(System.currentTimeMillis()));
            pstmt.setInt(i++, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    outputParam = AppUser.fromDBRow(rs);
                    outputParam.resetPassword();
                    LOGGER.info("Password for user with ID %d successfully updated.", id);
                } else {
                    LOGGER.warn("User with ID %d not found or password not updated.", id);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Unable to update password for user ID: %d", id, e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("An unexpected error occurred while updating password for user ID: %d", id, e);
            throw e;
        }
    }
}
