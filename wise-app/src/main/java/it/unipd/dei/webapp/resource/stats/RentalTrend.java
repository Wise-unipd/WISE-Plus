package it.unipd.dei.webapp.resource.stats;

import it.unipd.dei.webapp.resource.AbstractResource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Represents the rental trends of the application, grouped by month.
 * 
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class RentalTrend extends AbstractResource {

    /**
     * The month of the rental trend.
     */
    private final Timestamp month;

    /**
     * The number of rentals in the month.
     */
    private final Integer rents;

    /**
     * Creates a new rental trend resource.
     *
     * @param month the month of the rental trend.
     * @param rents the number of rentals in the month.
     */
    public RentalTrend(Timestamp month, Integer rents) {
        this.month = month;
        this.rents = rents;
    }

    /**
     * Returns the month of the rental trend.
     *
     * @return the month of the rental trend.
     */
    public Timestamp getMonth() {
        return month;
    }

    /**
     * Returns the number of rentals in the month.
     *
     * @return the number of rentals in the month.
     */
    public Integer getRents() {
        return rents;
    }

    @Override
    public String toString() {
        return "RentalTrend{" + "month=" + month + ", rents=" + rents + '}';
    }

    /**
     * Creates a RentalTrend instance by extracting data from the given database result set.
     *
     * @param rs the result set containing the data.
     * @return a RentalTrend object initialized with the data from the result set.
     * @throws SQLException if any error occurs while accessing the result set.
     */
    public static RentalTrend fromDBRow(ResultSet rs) throws SQLException {
        return new RentalTrend(rs.getTimestamp("month"), rs.getInt("rents"));
    }


}
