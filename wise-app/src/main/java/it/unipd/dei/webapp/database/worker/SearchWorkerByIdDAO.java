package it.unipd.dei.webapp.database.worker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.unipd.dei.webapp.database.AbstractDAO;
import it.unipd.dei.webapp.resource.movie.Worker;

/**
 * Searches for a worker in the database using their unique identifier.
 * <p>If the worker is found, a {@link Worker} object is created and stored as the output parameter.
 * If no worker matches the provided ID, the output parameter is set to {@code null}.</p>
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class SearchWorkerByIdDAO extends AbstractDAO<Worker> {

    /**
     * The SQL statement to retrieve the worker's personal information.
     */
    private static final String STATEMENT =
            "SELECT * FROM Worker WHERE worker_id = ?";

    /**
     * The unique identifier of the worker.
     */
    private final Integer id;

    /**
     * Creates a new object to search for a worker's info.
     *
     * @param con the database connection.
     * @param id  the ID of the worker.
     */
    public SearchWorkerByIdDAO(final Connection con, final Integer id) {
        super(con);
        this.id = id;
    }

    /**
     * Accesses the database to find the worker details.
     */
    @Override
    protected final void doAccess() throws SQLException {
        Worker worker = null; // Result of the search

        // try-with-resources
        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    worker = new Worker(
                            rs.getInt("worker_id"),
                            rs.getString("name"),
                            rs.getString("surname")
                    );

                    LOGGER.info("Worker with ID %d successfully found.", worker.getId());
                } else {
                    LOGGER.warn("Worker with ID %d not found in the database.", id);
                }
            }
        }
        outputParam = worker;
    }
}