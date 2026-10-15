package it.unipd.dei.webapp.database.stats;

import it.unipd.dei.webapp.database.AbstractDAO;
import it.unipd.dei.webapp.resource.stats.KPI;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Retrieves Key Performance Indicators (KPIs) from the database.
 * 
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class GetKPIDAO extends AbstractDAO<KPI> {

    /**
     * The SQL statement to be executed.
     */
    private static final String STATEMENT = "SELECT" +
            "  (SELECT COUNT(*) FROM movie WHERE is_active = true) AS total_movies,\n" +
            "  (SELECT COUNT(*) FROM appuser WHERE role = 0 AND is_active = true) AS active_users,\n" +
            "  (SELECT COUNT(*) FROM appuser WHERE role IN (1, 2) AND is_active = true) AS total_editors,\n" +
            "  (SELECT COUNT(*) FROM rent WHERE is_active = true) AS active_rents,\n" +
            "  (SELECT COUNT(*) FROM rent) AS total_rents,\n" +
            "  (SELECT COALESCE(SUM(amount),0) FROM transactions) AS total_revenue;";


    /**
     * Creates a new DAO for retrieving KPIs.
     *
     * @param con the connection to the database.
     */
    public GetKPIDAO(final Connection con) {
        super(con);
    }

    /**
     * Retrieves the KPIs from the database.
     *
     * @throws SQLException if an error occurs while accessing the database.
     */
    @Override
    protected final void doAccess() throws SQLException {
        KPI kpis = null;

        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT); ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                kpis = KPI.fromDBRow(rs);
            }
        }
        this.outputParam = kpis;
    }
}
