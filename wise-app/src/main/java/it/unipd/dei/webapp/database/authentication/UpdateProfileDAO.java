package it.unipd.dei.webapp.database.authentication;

import it.unipd.dei.webapp.database.AbstractDAO;
import it.unipd.dei.webapp.resource.authentication.AppUser;

import java.sql.*;

/**
 * Updates the profile of a user in the database.
 * 
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class UpdateProfileDAO extends AbstractDAO<AppUser> {

    /**
     * The SQL statement to be executed.
     * Uses COALESCE to keep the old values if the parameters are null.
     */
    private static final String STATEMENT =
            "WITH updated_user AS ( " +
                    "    UPDATE appuser SET first_name = COALESCE(?, first_name), last_name = COALESCE(?, last_name), is_active = COALESCE(?, is_active), last_update = ? " +
                    "    WHERE id = ? " +
                    "    RETURNING * " +
                    ") " +
                    "SELECT u.*, r.description " +
                    "FROM updated_user u " +
                    "JOIN userrole r ON u.role = r.id;";

    /**
     * The ID of the user to be updated.
     */
    private final int id;

    /**
     * The new first name of the user.
     */
    private final String firstName;

    /**
     * The new last name of the user.
     */
    private final String lastName;

    /**
     * The active status of the user (can be null if not updated).
     */
    private final Boolean active;

    /**
     * Creates a new DAO for updating a user profile.
     *
     * @param con       the connection to the database.
     * @param id        the ID of the user.
     * @param firstName the new first name.
     * @param lastName  the new last name.
     * @param active    the new active status (null to leave unchanged).
     */
    public UpdateProfileDAO(final Connection con, final int id, final String firstName, final String lastName, final Boolean active) {
        super(con);
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.active = active;
    }

    @Override
    protected final void doAccess() throws Exception {
        LOGGER.info("Updating profile for user ID: %d", id);

        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT)) {
            int i = 1;

            // Handle First Name
            if (firstName != null) {
                pstmt.setString(i++, firstName);
            } else {
                pstmt.setNull(i++, Types.VARCHAR); // Passing null triggers COALESCE to keep the old value
            }

            // Handle Last Name
            if (lastName != null) {
                pstmt.setString(i++, lastName);
            } else {
                pstmt.setNull(i++, Types.VARCHAR); // Passing null triggers COALESCE to keep the old value
            }

            // Handle Active state
            if (active != null) {
                pstmt.setBoolean(i++, active);
            } else {
                pstmt.setNull(i++, Types.BOOLEAN);
            }

            pstmt.setTimestamp(i++, new Timestamp(System.currentTimeMillis()));
            pstmt.setInt(i++, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    outputParam = AppUser.fromDBRow(rs);
                    outputParam.resetPassword();
                    LOGGER.info("User with ID %d successfully updated.", id);

                } else {
                    LOGGER.warn("User with ID %d not found or not updated.", id);
                    
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Unable to update profile for user ID: %d", id, e);
            throw e;

        } catch (Exception e) {
            LOGGER.error("An unexpected error occurred while updating profile for user ID: %d", id, e);
            throw e;

        }
    }
}
