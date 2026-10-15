package it.unipd.dei.webapp.database.stats;

import it.unipd.dei.webapp.database.AbstractDAO;
import it.unipd.dei.webapp.resource.movie.Movie;
import it.unipd.dei.webapp.resource.movie.MovieFactory;
import org.javatuples.Pair;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Retrieves the most rented films from the last 30 days.
 * 
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class GetMostRentedFilmsLastMonthDAO extends AbstractDAO<ArrayList<Pair<Movie, Integer>>> {

    /**
     * The SQL statement to be executed.
     */
    private static final String STATEMENT = "SELECT m.*, rc.rent_count " +
            "FROM movie_with_rating m " +
            "JOIN (" +
            "    SELECT movie_id, COUNT(*) AS rent_count " +
            "    FROM rent " +
            "    WHERE checkout >= CURRENT_DATE - INTERVAL '30 days' " +
            "    GROUP BY movie_id " +
            ") rc ON m.movie_id = rc.movie_id " +
            "WHERE m.is_active = true " +
            "ORDER BY rc.rent_count DESC " +
            "LIMIT 10;";


    /**
     * Creates a new DAO for retrieving the most rented films.
     *
     * @param con the connection to the database.
     */
    public GetMostRentedFilmsLastMonthDAO(final Connection con) {
        super(con);
    }

    /**
     * Retrieves the most rented films from the database.
     *
     * @throws SQLException if an error occurs while accessing the database.
     */
    @Override
    protected final void doAccess() throws SQLException {
        ArrayList<Pair<Movie, Integer>> trends = new ArrayList<>();

        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT); ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Integer count = rs.getInt("rent_count");
                Movie movie = MovieFactory.fromDBRow(rs);
                trends.add(new Pair<>(movie, count));
            }
        }
        this.outputParam = trends;
    }
}
