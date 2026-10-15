package it.unipd.dei.webapp.database.stats;

import it.unipd.dei.webapp.database.AbstractDAO;
import it.unipd.dei.webapp.resource.stats.KPI;
import it.unipd.dei.webapp.resource.stats.RentalTrend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Retrieves the rental trends from the database, grouped by month.
 * 
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class GetRentalTrendsDAO extends AbstractDAO<ArrayList<RentalTrend>> {

    /**
     * The SQL statement to be executed.
     */
    private static final String STATEMENT = "SELECT" + "    DATE_TRUNC('month', checkout) AS month," + "    COUNT(*) AS rents " + "FROM rent " + "GROUP BY month" + " ORDER BY month;";


    /**
     * Creates a new DAO for retrieving rental trends.
     *
     * @param con the connection to the database.
     */
    public GetRentalTrendsDAO(final Connection con) {
        super(con);
    }

    /**
     * Retrieves the rental trends from the database.
     *
     * @throws SQLException if an error occurs while accessing the database.
     */
    @Override
    protected final void doAccess() throws SQLException {
        ArrayList<RentalTrend> trends = new ArrayList<>();

        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT); ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                trends.add(RentalTrend.fromDBRow(rs));
            }
        }
        this.outputParam = trends;
    }
}
