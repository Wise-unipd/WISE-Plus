package it.unipd.dei.webapp.database.worker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.unipd.dei.webapp.database.AbstractDAO;
import it.unipd.dei.webapp.resource.movie.Worker;

/**
 * Updates an existing worker's information in the database.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class UpdateWorkerDAO extends AbstractDAO<Worker> {

    /**
     * The SQL statement to update the worker and return the updated record.
     */
    private static final String STATEMENT = "UPDATE Worker SET name = ?, surname = ? WHERE worker_id = ? RETURNING *";

    /**
     * The worker data to be updated.
     */
    private final Worker worker;

    /**
     * Creates a new object to update a worker in the database.
     *
     * @param con    the database connection.
     * @param worker the worker containing the updated data.
     */
    public UpdateWorkerDAO(final Connection con, final Worker worker) {
        super(con);

        if (worker == null) {
            LOGGER.error("The worker cannot be null.");
            throw new NullPointerException("The worker cannot be null.");
        }

        this.worker = worker;
    }

    @Override
    protected void doAccess() throws SQLException {
        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT)) {
            pstmt.setString(1, worker.getName());
            pstmt.setString(2, worker.getSurname());
            pstmt.setInt(3, worker.getId());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    outputParam = new Worker(
                            rs.getInt("worker_id"),
                            rs.getString("name"),
                            rs.getString("surname")
                    );

                    LOGGER.info("Worker %s %s (ID: %d) successfully updated.",
                            outputParam.getName(), outputParam.getSurname(), outputParam.getId());
                } else {
                    LOGGER.warn("Worker with ID %d not found for update.", worker.getId());
                    outputParam = null;
                }
            }
        }
    }
}