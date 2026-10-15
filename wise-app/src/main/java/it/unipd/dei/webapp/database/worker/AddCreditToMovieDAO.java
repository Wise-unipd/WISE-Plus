package it.unipd.dei.webapp.database.worker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.unipd.dei.webapp.database.AbstractDAO;
import it.unipd.dei.webapp.resource.movie.Credit;
import it.unipd.dei.webapp.resource.movie.MovieRole;
import it.unipd.dei.webapp.resource.movie.Worker;

/**
 * Add a credit to a movie.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class AddCreditToMovieDAO extends AbstractDAO<Credit> {

    /**
     * The SQL statement to be executed.
     */
    private static final String STATEMENT =
            "WITH inserted_credit AS ( " +
                    "   INSERT INTO Credit (movie_id, worker_id, role_id) " +
                    "   VALUES (?, ?, ?) " +
                    "   RETURNING * " +
                    ") " +
                    "SELECT w.worker_id, w.name AS worker_name, w.surname AS worker_surname, " +
                    "mr.role_id, mr.name AS role " +
                    "FROM inserted_credit AS ic " +
                    "INNER JOIN Worker AS w ON ic.worker_id = w.worker_id " +
                    "INNER JOIN MovieRole AS mr ON ic.role_id = mr.role_id";

    /**
     * The unique identifier of the movie.
     */
    private final int movieId;

    /**
     * The unique identifier of the worker.
     */
    private final int workerId;

    /**
     * The unique identifier of the role.
     */
    private final int roleId;

    /**
     * Creates a new object to add a credit to a movie.
     *
     * @param con      the database connection.
     * @param movieId  the unique identifier of the movie.
     * @param workerId the unique identifier of the worker.
     * @param roleId   the unique identifier of the role.
     */
    public AddCreditToMovieDAO(final Connection con, final int movieId, final int workerId, final int roleId) {
        super(con);
        this.movieId = movieId;
        this.workerId = workerId;
        this.roleId = roleId;
    }

    @Override
    protected final void doAccess() throws SQLException {

        Credit credit = null;

        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT)) {
            pstmt.setInt(1, movieId);
            pstmt.setInt(2, workerId);
            pstmt.setInt(3, roleId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    credit = new Credit(
                            new Worker(
                                    rs.getInt("worker_id"),
                                    rs.getString("worker_name"),
                                    rs.getString("worker_surname")
                            ),
                            new MovieRole(rs.getInt("role_id"), rs.getString("role"))
                    );

                    LOGGER.info("Worker %d successfully add to movie %d with role %d", credit.getWorker().getId(), movieId, credit.getRole().getId());
                } else {
                    LOGGER.warn("Failed to add worker %d to movie %d with role %d.", workerId, movieId, roleId);
                }
            }
        }

        outputParam = credit;
    }
}