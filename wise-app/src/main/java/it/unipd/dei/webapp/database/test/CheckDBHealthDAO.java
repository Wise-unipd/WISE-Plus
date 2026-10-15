package it.unipd.dei.webapp.database.test;


import it.unipd.dei.webapp.database.AbstractDAO;
import it.unipd.dei.webapp.resource.test.DBHealthCheck;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
/**
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */


public class CheckDBHealthDAO extends AbstractDAO<DBHealthCheck> {
    /**
     * The SQL statement to be executed
     */
    private static final String STATEMENT_SELECT = "SELECT * FROM DB_CHECKS ORDER BY id DESC LIMIT 1;";
    private static final String STATEMENT_INSERT = "INSERT INTO DB_CHECKS DEFAULT VALUES";


    public CheckDBHealthDAO(final Connection con) {
        super(con);
    }

    @Override
    public final void doAccess() throws SQLException {

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        DBHealthCheck prev_check = null;

        try {
            con.setAutoCommit(false);

            pstmt = con.prepareStatement(STATEMENT_SELECT);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                prev_check = new DBHealthCheck();
                prev_check.setDatetime(rs.getTimestamp("timestamp").toLocalDateTime());
            }

            pstmt = con.prepareStatement(STATEMENT_INSERT);
            pstmt.executeUpdate();
            con.commit();

        } catch (Exception e) {
            LOGGER.error("Unable to perform the requested database access operation.", e);
        } finally {
            if (rs != null) {
                rs.close();
            }

            if (pstmt != null) {
                pstmt.close();
            }

        }

        this.outputParam = prev_check;
    }
}




