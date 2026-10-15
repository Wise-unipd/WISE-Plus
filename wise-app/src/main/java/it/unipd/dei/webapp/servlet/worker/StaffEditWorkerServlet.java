package it.unipd.dei.webapp.servlet.worker;

import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Pattern;

import it.unipd.dei.webapp.servlet.AbstractDatabaseServlet;
import org.apache.logging.log4j.message.StringFormattedMessage;

import it.unipd.dei.webapp.database.worker.SearchWorkerByIdDAO;
import it.unipd.dei.webapp.database.worker.UpdateWorkerDAO;
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
 * Handles the modification of an existing worker.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
@WebServlet(name = "EditWorker", value = "/workers/edit-worker")
public final class StaffEditWorkerServlet extends AbstractDatabaseServlet {

    private static final String NAME_REGEX = "^[\\p{L}\\s\\-']{1,50}$";
    private static final Pattern NAME_PATTERN = Pattern.compile(NAME_REGEX);

    /**
     * Populates and renders the editing form.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setResource(req.getRequestURI());

        LOGGER.info("Showing worker update form.");

        try {
            // Parse the ID from the URL parameter
            String idParam = req.getParameter("id");
            if (idParam == null || idParam.trim().isEmpty()) {
                LOGGER.error("Cannot retrieve the worker for editing: ID is missing.");
                res.sendRedirect(req.getContextPath() + "/auth/staff/workers?error=Missing+Worker+ID");
                return;
            }

            int workerId = Integer.parseInt(idParam);

            // Fetch the worker from the database
            Worker worker = new SearchWorkerByIdDAO(getConnection(), workerId).access().getOutputParam();

            if (worker == null) {
                LOGGER.error("Worker with ID %d not found for editing.", workerId);
                res.sendRedirect(req.getContextPath() + "/auth/staff/workers?error=Worker+not+found");
                return;
            }

            req.setAttribute("worker", worker);
            req.getRequestDispatcher("/auth/staff/workers/edit-worker.jsp").forward(req, res);

        } catch (NumberFormatException e) {
            LOGGER.error("Cannot retrieve the worker for editing: ID is not valid.", e);
            res.sendRedirect(req.getContextPath() + "/auth/staff/workers?error=Invalid+Worker+ID");
        } catch (SQLException e) {
            LOGGER.error("Cannot retrieve the worker for editing: database error.", e);
            res.sendRedirect(req.getContextPath() + "/auth/staff/workers?error=Database+error");
        } finally {
            LogContext.removeIPAddress();
            LogContext.removeResource();
        }
    }

    /**
     * Persists the changes to the database.
     * Relies on state 23505 to prevent renaming to an existing worker.
     */
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setAction(Actions.EDIT_WORKER);

        int workerId = -1;
        String name = null;
        String surname = null;
        Worker oldWorker = null;
        Worker worker = null;
        Message m = null;

        try {
            // check the ID first
            try {
                workerId = Integer.parseInt(req.getParameter("id"));
            } catch (NumberFormatException e) {
                LOGGER.error("Worker update failed: ID is missing or not valid.", e);
                res.sendRedirect(req.getContextPath() + "/auth/staff/workers?error=Missing+or+Invalid+ID");
                return;
            }

            oldWorker = new SearchWorkerByIdDAO(getConnection(), workerId).access().getOutputParam();

            if (oldWorker == null) {
                LOGGER.error("Update failed: worker with ID %d not found.", workerId);
                res.sendRedirect(req.getContextPath() + "/auth/staff/workers?error=Worker+not+found");
                return;
            }

            req.setAttribute("worker", oldWorker);
            LogContext.setResource(oldWorker.getName() + " " + oldWorker.getSurname() + " (ID: " + oldWorker.getId() + ")");

            // Parse and validate new data
            try {
                name = req.getParameter("name");
                name = (name != null) ? NameFormatter.formatName(name) : "";

                surname = req.getParameter("surname");
                surname = (surname != null) ? NameFormatter.formatName(surname) : "";

                worker = new Worker(workerId, name, surname);

                // Strict server-side validation
                m = validateInput(worker);

                if (m != null) {
                    LOGGER.error("Validation failed while updating the worker: %s.", m.getMessage());
                    forwardWithError(req, res, m);
                    return;
                }

                // Attempt update. Throws SQL 23505 if already exists.
                worker = new UpdateWorkerDAO(getConnection(), worker).access().getOutputParam();

                if (worker == null) {
                    LOGGER.error("Worker object is null after DAO execution for update: %s %s.", name, surname);
                    m = new Message("An unexpected error occurred while updating the worker. Please try again or check the list.", "E0A1", null);
                    forwardWithError(req, res, m);
                } else {
                    LOGGER.info("Worker %s %s (ID: %d) successfully updated.", worker.getName(), worker.getSurname(), worker.getId());
                    res.sendRedirect(req.getContextPath() + "/auth/staff/workers?success=true");
                }

            } catch (SQLException e) {
                if ("23505".equals(e.getSQLState())) {
                    m = new Message(String.format("Cannot update the worker: %s %s already exists.", name, surname), "E1A5", null);
                    LOGGER.error(new StringFormattedMessage("Cannot update the worker: %s %s already exists.", name, surname), e);
                } else {
                    m = new Message("Cannot update the worker due to a database error.", "E0A1", null);
                    LOGGER.error("Cannot update the worker: unexpected error while accessing the database.", e);
                }

                forwardWithError(req, res, m);
            }
        } catch (Exception e) {
            LOGGER.error("Cannot update the worker: unexpected fatal error.", e);
            res.sendRedirect(req.getContextPath() + "/auth/staff/workers?error=Unexpected+error");
        } finally {
            LogContext.removeIPAddress();
            LogContext.removeAction();
            LogContext.removeResource();
        }
    }

    /**
     * Utility method for forwarding errors back to the edit form.
     *
     * @param req      the HTTP request.
     * @param res      the HTTP response.
     * @param m        the message Object.
     * @throws ServletException if a server-side error occurs.
     * @throws IOException      if any error occurs during client/server communication.
     */
    private void forwardWithError(HttpServletRequest req, HttpServletResponse res, Message m) throws ServletException, IOException {
        req.setAttribute("message", m);
        req.getRequestDispatcher("/auth/staff/workers/edit-worker.jsp").forward(req, res);
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