package it.unipd.dei.webapp.database.worker;

import it.unipd.dei.webapp.database.AbstractDAO;
import it.unipd.dei.webapp.resource.movie.Credit;
import it.unipd.dei.webapp.resource.movie.MovieRole;
import it.unipd.dei.webapp.resource.movie.Worker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Lists all credits associated with a movie.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class GetAllCreditsDAO extends AbstractDAO<List<Credit>> {

    /**
     * The SQL statement to be executed.
     */
    private static final String STATEMENT =
            "SELECT w.worker_id, w.name AS worker_name, w.surname AS worker_surname, " +
                    "mr.role_id, mr.name AS role " +
                    "FROM Credit AS c " +
                    "INNER JOIN Worker AS w ON c.worker_id = w.worker_id " +
                    "INNER JOIN MovieRole AS mr ON c.role_id = mr.role_id " +
                    "WHERE c.movie_id = ? " +
                    "ORDER BY " +
                    "	CASE " +
                    "		WHEN mr.name = 'Director' THEN 1 " +
                    "		WHEN mr.name = 'Actor' THEN 2 " +
                    "		ELSE 3 " +
                    "	END, " +
                    "	w.surname ASC, " +
                    "	w.name ASC";

    /**
     * The unique identifier of the movie.
     */
    private final int movieId;

    /**
     * Creates a new object for listing all credits associated with a movie.
     *
     * @param con     the database connection.
     * @param movieId the unique identifier of the movie.
     */
    public GetAllCreditsDAO(final Connection con, final int movieId) {
        super(con);
        this.movieId = movieId;
    }

    @Override
    protected final void doAccess() throws SQLException {

        List<Credit> credits = new ArrayList<>();

        // try-with-resources
        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT)) {
            pstmt.setInt(1, movieId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    credits.add(new Credit(
                            new Worker(
                                    rs.getInt("worker_id"),
                                    rs.getString("worker_name"),
                                    rs.getString("worker_surname")
                            ),
                            new MovieRole(rs.getInt("role_id"), rs.getString("role"))
                    ));
                }

                LOGGER.info("%d credits have been successfully retrieved.", credits.size());
            }
        }

        outputParam = credits;
    }
}