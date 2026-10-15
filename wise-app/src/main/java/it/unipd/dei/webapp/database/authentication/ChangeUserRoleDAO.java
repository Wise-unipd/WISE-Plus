package it.unipd.dei.webapp.database.authentication;

import it.unipd.dei.webapp.database.AbstractDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;


/**
 * Updates the role of a user in the database.
 * 
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class ChangeUserRoleDAO extends AbstractDAO<Integer> {

    /**
     * The SQL statement to be executed.
     */
    private static final String STATEMENT =
            "UPDATE appuser SET role = ?, last_update = CURRENT_TIMESTAMP WHERE id = ?";

    /**
     * The ID of the user whose role is to be changed.
     */
    private final int targetUserId;

    /**
     * The ID of the new role for the user.
     */
    private final int newRoleId;

    /**
     * Creates a new DAO for updating a user role.
     *
     * @param con          the connection to the database.
     * @param targetUserId the ID of the user.
     * @param newRoleId    the ID of the new role.
     */
    public ChangeUserRoleDAO(final Connection con, final int targetUserId, final int newRoleId) {
        super(con);
        this.targetUserId = targetUserId;
        this.newRoleId = newRoleId;
    }

    @Override
    protected void doAccess() throws Exception {
        LOGGER.info("Updating role for user ID: %d to role ID: %d", targetUserId, newRoleId);

        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT)) {
            pstmt.setInt(1, newRoleId);
            pstmt.setInt(2, targetUserId);

            // Execute update and store the number of affected rows as the output parameter
            this.outputParam = pstmt.executeUpdate();

            if (this.outputParam > 0) {
                LOGGER.info("Role successfully updated for user %d to role %d.", targetUserId, newRoleId);
            } else {
                LOGGER.warn("User with ID %d not found or role not updated.", targetUserId);
            }
        } catch (Exception e) {
            LOGGER.error("Unable to update role for user ID: %d to role ID: %d", targetUserId, newRoleId, e);
            throw e;
        }
    }
}