package it.unipd.dei.webapp.database.authentication;


import it.unipd.dei.webapp.database.AbstractDAO;
import it.unipd.dei.webapp.resource.authentication.AppUser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * Retrieves a user profile from the database by their ID.
 * 
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class GetProfileByIDDAO extends AbstractDAO<AppUser> {

    /**
     * The SQL statement to be executed.
     */
    private static final String STATEMENT = "SELECT u.*, r.description " + "FROM appuser u " + "JOIN userrole r ON u.role = r.id " + "WHERE u.id = ?";

    /**
     * The ID of the user to retrieve.
     */
    private final Integer ID;

    /**
     * Creates a new DAO for retrieving a user profile by ID.
     *
     * @param con the connection to the database.
     * @param ID  the ID of the user.
     */
    public GetProfileByIDDAO(final Connection con, final Integer ID) {
        super(con);

        if (ID == null) {
            LOGGER.error("The profile ID cannot be null!");
            throw new NullPointerException("The profile ID cannot be null!");
        }

        this.ID = ID;
    }

    @Override
    protected final void doAccess() throws SQLException {
        AppUser returningUser = null;

        LOGGER.info("Retrieving user profile for ID: %d", ID);

        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT)) {
            pstmt.setInt(1, ID);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    returningUser = AppUser.fromDBRow(rs);
                    returningUser.resetPassword();
                    LOGGER.info("User %s (ID %d) successfully retrieved from the database.", returningUser.getEmail(), ID);
                } else {
                    LOGGER.warn("User with ID %d not found in the database.", ID);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Unable to retrieve user profile for ID: %d", ID, e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("An unexpected error occurred while retrieving user profile for ID: %d", ID, e);
            throw new SQLException(e);
        }

        this.outputParam = returningUser;
    }
}
