package it.unipd.dei.webapp.servlet.worker;

import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Pattern;

import it.unipd.dei.webapp.servlet.AbstractDatabaseServlet;
import org.apache.logging.log4j.message.StringFormattedMessage;

import it.unipd.dei.webapp.database.worker.CreateWorkerDAO;
import it.unipd.dei.webapp.resource.Actions;
import it.unipd.dei.webapp.resource.LogContext;
import it.unipd.dei.webapp.resource.Message;
import it.unipd.dei.webapp.resource.movie.Worker;
import it.unipd.dei.webapp.utils.NameFormatter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Creates a new worker (actor/director) in the database.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
@WebServlet(name = "AddWorker", value = "/workers/add-worker")
public final class StaffAddWorkerServlet extends AbstractDatabaseServlet {

    /**
     * Regular expression to strictly validate names and surnames.
     * Allows letters (including Unicode), spaces, hyphens, and apostrophes.
     */
    private static final String NAME_REGEX = "^[\\p{L}\\s\\-']{1,50}$";
    private static final Pattern NAME_PATTERN = Pattern.compile(NAME_REGEX);

    /**
     * Forwards to the worker creation view.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setResource(req.getRequestURI());
        // LogContext.setAction(Actions.VIEW_CREATE_WORKER); // Optional: to track view actions

        LOGGER.info("Showing worker add form.");

        try {
            // Forward to the JSP containing the form
            req.getRequestDispatcher("/auth/staff/workers/add-worker.jsp").forward(req, res);
        } finally {
            LogContext.removeIPAddress();
            LogContext.removeResource();
        }
    }

    /**
     * Submits the new worker to the database safely.
     */
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setAction(Actions.CREATE_WORKER);

        String name = null;
        String surname = null;
        Worker worker = null;
        Message m = null;

        try {
            name = req.getParameter("name");
            name = (name != null) ? NameFormatter.formatName(name) : "";

            surname = req.getParameter("surname");
            surname = (surname != null) ? NameFormatter.formatName(surname) : "";

            // Track the resource being acted upon in the logs
            LogContext.setResource(name + " " + surname);

            worker = new Worker(name, surname);

            // Strict server-side validation
            m = validateInput(worker);

            if (m != null) {
                // Validation failed, send back to form with error message
                LOGGER.error("Validation failed while creating the worker: %s.", m.getMessage());
                forwardWithError(req, res, m);
                return;
            }

            // Attempt database insertion. Will throw SQLException state if already exists.
            worker = new CreateWorkerDAO(getConnection(), worker).access().getOutputParam();

            if (worker == null) {
                LOGGER.error("Worker object is null after DAO execution for create: %s %s.", name, surname);
                m = new Message("The worker may have been created, but the system was unable to confirm it. Please check the list.", "E0A1", null);
                forwardWithError(req, res, m);
            } else {
                LOGGER.info("Worker %s %s (ID: %d) successfully created in the database.",
                    worker.getName(), worker.getSurname(), worker.getId());

                // Success: Redirect to the workers management page
                res.sendRedirect(req.getContextPath() + "/auth/staff/workers?success=true");
            }

        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                m = new Message(String.format("Cannot create the worker: %s %s already exists.", name, surname), "E1A5", null);
                LOGGER.error(new StringFormattedMessage("Cannot create the worker: %s %s already exists.", name, surname), e);
            } else {
                m = new Message("Cannot create the worker: unexpected database error.", "E0A1", null);
                LOGGER.error("Cannot create the worker: unexpected error while accessing the database.", e);
            }

            forwardWithError(req, res, m);
        } catch (Exception e) {
            LOGGER.error("Cannot create the worker: unexpected fatal error.", e);
            res.sendRedirect(req.getContextPath() + "/auth/staff/workers?error=Unexpected+error");
        } finally {
            // Clean up context to prevent memory leaks in thread pool
            LogContext.removeIPAddress();
            LogContext.removeAction();
            LogContext.removeResource();
        }
    }

    /**
     * Utility method for forwarding errors back to the form.
     *
     * @param req the HTTP request.
     * @param res the HTTP response.
     * @param m   the message Object.
     * @throws ServletException if a server-side error occurs.
     * @throws IOException      if any error occurs during client/server communication.
     */
    private void forwardWithError(HttpServletRequest req, HttpServletResponse res, Message m) throws ServletException, IOException {
        req.setAttribute("message", m);
        req.getRequestDispatcher("/auth/staff/workers/add-worker.jsp").forward(req, res);
    }

    /**
     * Validates the inputs to prevent injection and ensure data integrity.
     * 
     * @param worker the worker object to validate.
     * @return a Message object containing the error, or null if valid.
     */
    private Message validateInput(Worker worker) {
        if (worker.getName().isEmpty()) {
            return new Message("Name missing.", "E1A1", null);
        }

        if (worker.getSurname().isEmpty()) {
            return new Message("Surname missing.", "E1A1", null);
        }

        if (!NAME_PATTERN.matcher(worker.getName()).matches()) {
            return new Message("Invalid name format. Only letters, spaces, hyphens, and apostrophes are allowed.", "E1A1", null);
        }

        if (!NAME_PATTERN.matcher(worker.getSurname()).matches()) {
            return new Message("Invalid surname format. Only letters, spaces, hyphens, and apostrophes are allowed.", "E1A1", null);
        }

        return null;
    }
}