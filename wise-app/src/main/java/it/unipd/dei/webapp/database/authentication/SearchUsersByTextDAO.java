package it.unipd.dei.webapp.database.authentication;

import it.unipd.dei.webapp.database.AbstractDAO;
import it.unipd.dei.webapp.resource.authentication.AppUser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Searches for users in the database by a text query.
 * 
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class SearchUsersByTextDAO extends AbstractDAO<List<AppUser>> {

    /**
     * The SQL statement to be executed.
     */
    private static final String QUERY = "SELECT u.*, r.description " + "FROM appuser u " + "JOIN userrole r ON u.role = r.id " + "WHERE  u.email ILIKE ? OR u.first_name ILIKE ? OR u.last_name ILIKE ? OR r.description ILIKE ?";

    /**
     * The search terms.
     */
    private final String searchTerms;

    /**
     * Creates a new DAO for searching users by text.
     *
     * @param con         the database connection.
     * @param searchTerms the search terms.
     */
    public SearchUsersByTextDAO(final Connection con, final String searchTerms) {
        super(con);
        this.searchTerms = (searchTerms == null) ? "%%" : "%" + searchTerms + "%";
    }

    @Override
    protected final void doAccess() throws SQLException {
        final List<AppUser> resultList = new ArrayList<>();

        LOGGER.info("Searching for users with terms: %s", searchTerms);

        try (PreparedStatement pstmt = con.prepareStatement(QUERY)) {
            pstmt.setString(1, searchTerms);
            pstmt.setString(2, searchTerms);
            pstmt.setString(3, searchTerms);
            pstmt.setString(4, searchTerms);

            // Execute the query
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    resultList.add(AppUser.fromDBRow(rs));
                }
            }
            LOGGER.info("Found %d users matching the terms.", resultList.size());
        } catch (SQLException e) {
            LOGGER.error("Unable to search for users with terms: %s", searchTerms, e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("An unexpected error occurred while searching for users with terms: %s", searchTerms, e);
            throw new SQLException(e);
        }
        this.outputParam = resultList;
    }
}
