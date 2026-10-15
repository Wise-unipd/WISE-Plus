package it.unipd.dei.webapp.database.worker;

import it.unipd.dei.webapp.database.AbstractDAO;
import it.unipd.dei.webapp.resource.movie.Worker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Lists all the workers (actors and directors) available in the database.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class GetAllWorkersDAO extends AbstractDAO<List<Worker>> {

    /**
     * The SQL statement to be executed to retrieve all workers, ordered by surname and name.
     */
    private static final String STATEMENT =
            "SELECT * FROM Worker ORDER BY surname, name";

    /**
     * Creates a new object for listing all workers.
     *
     * @param con the connection to the database.
     */
    public GetAllWorkersDAO(final Connection con) {
        super(con);
    }

    /**
     * Accesses the database to fetch the list of workers.
     */
    @Override
    protected final void doAccess() throws SQLException {

        // List to store the retrieved workers
        List<Worker> workers = new ArrayList<>();

        // Use try-with-resources to ensure the PreparedStatement and ResultSet are closed
        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT);
             ResultSet rs = pstmt.executeQuery()) {

            // Iterate through the result set and create a new Worker object for each row
            while (rs.next()) {
                workers.add(new Worker(
                        rs.getInt("worker_id"),
                        rs.getString("name"),
                        rs.getString("surname")
                ));
            }

            // Log the success of the operation
            LOGGER.info("%d workers have been successfully retrieved.", workers.size());
        }

        // Set the output parameter to be returned by the DAO
        outputParam = workers;
    }
}