package it.unipd.dei.webapp.database.worker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.unipd.dei.webapp.database.AbstractDAO;
import it.unipd.dei.webapp.resource.movie.Worker;

/**
 * Searches workers by text (name and surname).
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class SearchWorkersByTextDAO extends AbstractDAO<List<Worker>> {

    /**
     * The SQL statement(s) to be executed.
     * Using ILIKE for case-insensitive partial matching.
     */
    private static final String STATEMENT =
            "SELECT * " +
                    "FROM Worker " +
                    "WHERE name ILIKE ? " +
                    "   OR surname ILIKE ? " +
                    "   OR (name || ' ' || surname) ILIKE ? " +
                    "   OR (surname || ' ' || name) ILIKE ? " +
                    "ORDER BY surname ASC, name ASC " +
                    "LIMIT ?";

    /**
     * The maximum number of workers to retrieve
     */
    private final int limit;

    /**
     * Search term used to filter workers.
     */
    private final String searchTerm;

    /**
     * Creates a new object to search for workers by text (name and surname).
     *
     * @param con        the database connection.
     * @param searchTerm the search term.
     */
    public SearchWorkersByTextDAO(final Connection con, final String searchTerm, final int limit) {
        super(con);
        this.searchTerm = (searchTerm == null) ? "%" : "%" + searchTerm + "%";
        this.limit = limit;
    }

    @Override
    protected final void doAccess() throws SQLException {

        List<Worker> workers = new ArrayList<>();

        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT)) {
            int i = 1;
            pstmt.setString(i++, searchTerm);
            pstmt.setString(i++, searchTerm);
            pstmt.setString(i++, searchTerm);
            pstmt.setString(i++, searchTerm);
            pstmt.setInt(i++, limit);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    workers.add(new Worker(
                            rs.getInt("worker_id"),
                            rs.getString("name"),
                            rs.getString("surname")
                    ));
                }

                LOGGER.info("Successfully retrieved %d workers", workers.size());
            }
        }

        outputParam = workers;
    }
}
