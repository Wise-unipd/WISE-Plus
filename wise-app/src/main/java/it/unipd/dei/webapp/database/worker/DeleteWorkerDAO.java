package it.unipd.dei.webapp.database.worker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.unipd.dei.webapp.database.AbstractDAO;
import it.unipd.dei.webapp.resource.movie.Worker;

/**
 * Deletes a worker from the database and returns the deleted record.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class DeleteWorkerDAO extends AbstractDAO<Worker> {

    /**
     * The SQL statement to delete a worker and return their details.
     */
    private static final String STATEMENT = "DELETE FROM Worker WHERE worker_id = ? RETURNING *";

    /**
     * The ID of the worker to delete.
     */
    private final int workerId;

    /**
     * Creates a new object to delete a worker from the database.
     *
     * @param con      the database connection.
     * @param workerId the ID of the worker to delete.
     */
    public DeleteWorkerDAO(final Connection con, final int workerId) {
        super(con);
        this.workerId = workerId;
    }

    @Override
    protected void doAccess() throws SQLException {
        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT)) {
            pstmt.setInt(1, workerId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Populate the output parameter with the deleted worker's data for logging
                    outputParam = new Worker(
                            rs.getInt("worker_id"),
                            rs.getString("name"),
                            rs.getString("surname")
                    );
                    LOGGER.info("Worker %s %s (ID: %d) was successfully deleted.", outputParam.getName(), outputParam.getSurname(), outputParam.getId());
                } else {
                    LOGGER.warn("Worker with ID %d not found for deletion.", workerId);
                    outputParam = null;
                }
            }
        }
    }
}