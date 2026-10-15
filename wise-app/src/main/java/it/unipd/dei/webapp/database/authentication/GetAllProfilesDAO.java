package it.unipd.dei.webapp.database.authentication;

import it.unipd.dei.webapp.database.AbstractDAO;
import it.unipd.dei.webapp.resource.authentication.AppUser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Retrieves all user profiles from the database.
 * 
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class GetAllProfilesDAO extends AbstractDAO<List<AppUser>> {

    /**
     * The SQL statement to be executed.
     */
    private static final String STATEMENT =
            "SELECT a.*, r.description FROM appuser a " +
                    "LEFT JOIN userrole r ON a.role = r.id " +
                    "ORDER BY a.id";

    /**
     * Creates a new DAO for retrieving all user profiles.
     *
     * @param con the connection to the database.
     */
    public GetAllProfilesDAO(final Connection con) {
        super(con);
    }

    @Override
    protected void doAccess() throws Exception {
        List<AppUser> users = new ArrayList<>();

        LOGGER.info("Retrieving all user profiles from the database.");

        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                AppUser user = AppUser.fromDBRow(rs);
                user.resetPassword(); // do not show the password
                users.add(user);
            }
        } catch (Exception e) {
            LOGGER.error("Unable to retrieve all user profiles.", e);
            throw e;
        }

        LOGGER.info("Successfully loaded %d users.", users.size());
        this.outputParam = users;
    }
}