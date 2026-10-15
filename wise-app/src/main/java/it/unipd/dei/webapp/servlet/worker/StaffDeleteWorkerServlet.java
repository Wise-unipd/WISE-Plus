package it.unipd.dei.webapp.servlet.worker;

import java.io.IOException;
import java.sql.SQLException;

import it.unipd.dei.webapp.servlet.AbstractDatabaseServlet;
import org.apache.logging.log4j.message.StringFormattedMessage;

import it.unipd.dei.webapp.database.worker.DeleteWorkerDAO;
import it.unipd.dei.webapp.database.worker.SearchWorkerByIdDAO;
import it.unipd.dei.webapp.resource.Actions;
import it.unipd.dei.webapp.resource.LogContext;
import it.unipd.dei.webapp.resource.Message;
import it.unipd.dei.webapp.resource.movie.Worker;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Handles the deletion of a worker, ensuring proper confirmation and referential integrity checks.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
@WebServlet(name = "DeleteWorker", value = "/workers/delete-worker")
public final class StaffDeleteWorkerServlet extends AbstractDatabaseServlet {

    /**
     * Processes the deletion request.
     */
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setAction(Actions.DELETE_WORKER);

        int workerId = -1;
        Worker deletedWorker = null;
        Message m = null;

        try {
            String idParam = req.getParameter("id");
            if (idParam == null || idParam.trim().isEmpty()) {
                LOGGER.error("Worker deletion failed: ID is missing.");
                res.sendRedirect(req.getContextPath() + "/auth/staff/workers?error=Missing+Worker+ID");
                return;
            }

            workerId = Integer.parseInt(idParam);

            LogContext.setResource("Worker ID: " + workerId);

            // Perform the deletion
            deletedWorker = new DeleteWorkerDAO(getConnection(), workerId).access().getOutputParam();

            if (deletedWorker == null) {
                LOGGER.error("Deletion failed: worker with ID %d not found.", workerId);
                res.sendRedirect(req.getContextPath() + "/auth/staff/workers?error=Worker+not+found+or+already+deleted");
            } else {
                LOGGER.info("Successfully deleted worker %s %s (ID: %d)", deletedWorker.getName(), deletedWorker.getSurname(), deletedWorker.getId());
                res.sendRedirect(req.getContextPath() + "/auth/staff/workers?success=Worker+deleted");
            }
        } catch (NumberFormatException e) {
            LOGGER.error("Worker deletion failed: invalid ID format.", e);
            res.sendRedirect(req.getContextPath() + "/auth/staff/workers?error=Invalid+Worker+ID");
        } catch (SQLException e) {
            // "23503" = Foreign Key Violation
            if ("23503".equals(e.getSQLState())) {
                LOGGER.error(new StringFormattedMessage("Foreign key violation: cannot delete worker with ID %d.", workerId), e);
                res.sendRedirect(req.getContextPath() + "/auth/staff/workers?error=Cannot+delete+worker+assigned+to+movies");
            } else {
                LOGGER.error("Unexpected database error while deleting worker.", e);
                res.sendRedirect(req.getContextPath() + "/auth/staff/workers?error=Unexpected+database+error");
            }
        } catch (Exception e) {
            LOGGER.error("Cannot delete the worker: unexpected fatal error.", e);
            res.sendRedirect(req.getContextPath() + "/auth/staff/workers?error=Unexpected+error");
        } finally {
            LogContext.removeIPAddress();
            LogContext.removeAction();
            LogContext.removeResource();
        }
    }
}