package it.unipd.dei.webapp.resource.stats;

import it.unipd.dei.webapp.resource.AbstractResource;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Represents Key Performance Indicators (KPIs) of the application.
 * 
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class KPI extends AbstractResource {

    /**
     * The total number of active movies.
     */
    private final Integer total_movies;

    /**
     * The total number of active users.
     */
    private final Integer active_users;

    /**
     * The total number of active editors (staff + admins).
     */
    private final Integer total_editors;

    /**
     * The total number of active rentals.
     */
    private final Integer active_rents;

    /**
     * The total number of rentals.
     */
    private final Integer total_rents;

    /**
     * The total revenue from transactions.
     */
    private final Double total_revenue;

    /**
     * Creates a new KPI resource.
     *
     * @param total_movies  the total number of active movies.
     * @param active_users  the total number of active users.
     * @param total_editors the total number of active editors.
     * @param active_rents  the total number of active rentals.
     * @param total_rents   the total number of rentals.
     * @param total_revenue the total revenue from transactions.
     */
    public KPI(Integer total_movies, Integer active_users, Integer total_editors, Integer active_rents, Integer total_rents, Double total_revenue) {
        this.total_movies = total_movies;
        this.active_users = active_users;
        this.total_editors = total_editors;
        this.active_rents = active_rents;
        this.total_rents = total_rents;
        this.total_revenue = total_revenue;
    }

    /**
     * Returns the total number of active movies.
     *
     * @return the total number of active movies.
     */
    public Integer getTotal_movies() {
        return total_movies;
    }

    /**
     * Returns the total number of active users.
     *
     * @return the total number of active users.
     */
    public Integer getActive_users() {
        return active_users;
    }

    /**
     * Returns the total number of active editors.
     *
     * @return the total number of active editors.
     */
    public Integer getTotal_editors() {
        return total_editors;
    }

    /**
     * Returns the total number of active rentals.
     *
     * @return the total number of active rentals.
     */
    public Integer getActive_rents() {
        return active_rents;
    }

    /**
     * Returns the total number of rentals.
     *
     * @return the total number of rentals.
     */
    public Integer getTotal_rents() {
        return total_rents;
    }

    /**
     * Returns the total revenue from transactions.
     *
     * @return the total revenue from transactions.
     */
    public Double getTotal_revenue() {
        return total_revenue;
    }

    @Override
    public String toString() {
        return "KPI{" + "total_movies=" + total_movies + ", active_users=" + active_users + ", total_editors=" + total_editors + ", active_rents=" + active_rents + ", total_rents=" + total_rents + ", total_revenue=" + total_revenue + '}';
    }


    /**
     * Creates a KPI instance by extracting data from the given database result set.
     *
     * @param rs the result set containing the data.
     * @return a KPI object initialized with the data from the result set.
     * @throws SQLException if any error occurs while accessing the result set.
     */
    public static KPI fromDBRow(ResultSet rs) throws SQLException {
        return new KPI(rs.getInt("total_movies"), rs.getInt("active_users"), rs.getInt("total_editors"), rs.getInt("active_rents"), rs.getInt("total_rents"), rs.getDouble("total_revenue"));
    }
}
