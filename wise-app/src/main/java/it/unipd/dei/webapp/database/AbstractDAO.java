package it.unipd.dei.webapp.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.StringFormatterMessageFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Provides support for the actual implementation of a DAO object.
 * 
 * {@code AbstractDAO} is designed to achieve two primary goals:
 * <ol>
 * <li>a consistent way of accessing the database and managing exceptions is
 * guaranteed for all the classes that need to directly interact with the
 * DBMS.
 * <li>independence from any particular DBMS is achieved, meaning that the
 * DBMS-dependent code is as isolated as possible and that upper layers can
 * transparently change the underlying DBMS, without the need of modifying their
 * code.
 * </ol>
 *
 * 
 * <p>
 * With respect to the first goal, {@code AbstractDAO}
 * provides a basic infrastructure which manages the connection, the commitment
 * of transactions (or the roll-back), if needed, and any exception that may be
 * thrown while accessing the database.
 * </p>
 * 
 * <p>
 * With respect to the second goal,{@code AbstractDAO} is the
 * root of a hierarchy of helper classes, which isolate as much as possible the
 * DBMS-dependent code.
 * </p>
 * 
 * 
 * <p>
 * Concrete subclasses have to implement the abstract method
 * {@link #doAccess()} in order to define the needed
 * logic for accessing the database. This method is, in turn, called by
 * {@link AbstractDAO#access()}, which provides a consistent way of
 * accessing the database and managing exceptions.
 * </p>
 * 
 * <p>
 * Concrete subclasses may also override {@link AbstractDAO#access()}
 * in order to provide a
 * different management of connection and transaction if they have peculiar
 * needs. Nevertheless, the objective of this class is to ease subclasses by
 * providing a pre-packed logic for connection and transaction management, that
 * is what {@link AbstractDAO#access()} does.
 * </p>
 * 
 * @param <T> the type of the output parameter returned by the DAO (e.g., a JavaBean or List).
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public abstract class AbstractDAO<T> implements DataAccessObject<T> {

    /**
     * The logger instance dedicated to this Data Access Object.
     * <p>
     * It is initialized dynamically via {@code getClass()} rather than statically.
     * This ensures that log entries correctly identify the concrete subclass
     * (e.g., {@code LoginDAO}) executing the operation,
     * facilitating accurate tracing and debugging within the database logs.
     * </p>
     */
    protected final Logger LOGGER = LogManager.getLogger(getClass(), StringFormatterMessageFactory.INSTANCE);

    /**
     * The connection to be used to access the database.
     */
    protected final Connection con;

    /**
     * The output parameter, if any
     */
    protected T outputParam = null;

    /**
     * Thread-safe flag to ensure a DAO instance is executed only once.
     * Note: version 1.0 used a lock object and a synchronized block, replaced with an atomic boolean (same result).
     */
    private final AtomicBoolean hasBeenAccessed = new AtomicBoolean(false);

    /**
     * Initializes a new DAO object.
     *
     * @param con the PostgreSQL connection to be used for accessing the database.
     * @throws NullPointerException if the provided connection is null.
     */
    protected AbstractDAO(final Connection con) {
        if (con == null) {
            LOGGER.error("The database connection cannot be null.");
            throw new NullPointerException("The database connection cannot be null.");
        }

        this.con = con;

        try {
            // Ensure auto-commit is enabled by default for simple operations
            con.setAutoCommit(true);
            LOGGER.debug("Connection auto-commit initialized to true.");
        } catch (final SQLException e) {
            LOGGER.warn("Unable to set connection auto-commit to true.", e);
        }
    }

    @Override
    public final DataAccessObject<T> access() throws SQLException {
        // Prevent reuse of the same DAO instance using an atomic check
        if (!hasBeenAccessed.compareAndSet(false, true)) {
            LOGGER.error("Illegal state: Cannot use a DataAccessObject instance more than once.");
            throw new SQLException("Cannot use a DataAccessObject instance more than once.");
        }

        try {
            // Execute the concrete DAO logic
            doAccess();

            // Attempt to cleanly close the connection upon success
            closeConnection();

        } catch (final Throwable t) {
            LOGGER.error("Execution failed: Unable to perform the requested database operation.", t);

            // Attempt to roll back the transaction if auto-commit was disabled
            try {
                if (!con.getAutoCommit()) {
                    con.rollback();
                    LOGGER.info("Transaction successfully rolled back due to an error.");
                }
            } catch (final SQLException e) {
                LOGGER.error("Critical failure: Unable to roll back the transaction.", e);
            } finally {
                // Ensure the connection is always closed, even after a rollback failure
                closeConnectionSafely();
            }

            // Rethrow the exception cleanly
            if (t instanceof SQLException) {
                throw (SQLException) t;
            } else {
                throw new SQLException("Unable to perform the requested database operation.", t);
            }
        }

        return this;
    }

    @Override
    public final T getOutputParam() {
        if (!hasBeenAccessed.get()) {
            LOGGER.error("Illegal access: Cannot retrieve the output parameter before calling access().");
            throw new IllegalStateException("Cannot retrieve the output parameter before accessing the database.");
        }
        return outputParam;
    }

    /**
     * Performs the actual SQL logic needed for accessing the database.
     * <p>
     * Subclasses must implement this method to map `ResultSet` data to JavaBeans,
     * execute updates, or perform complex analytical queries.
     * </p>
     *
     * @throws Exception if there is any issue during statement execution or mapping.
     */
    protected abstract void doAccess() throws Exception;

    /**
     * Closes the database connection.
     * @throws SQLException if a database access error occurs.
     */
    private void closeConnection() throws SQLException {
        if (con != null && !con.isClosed()) {
            con.close();
            LOGGER.debug("Database connection successfully closed.");
        }
    }

    /**
     * Closes the database connection, suppressing any thrown exceptions.
     * Used exclusively within the error-handling finally block.
     */
    private void closeConnectionSafely() {
        try {
            closeConnection();
        } catch (final SQLException e) {
            LOGGER.error("Unable to close the database connection gracefully during error recovery.", e);
        }
    }
}