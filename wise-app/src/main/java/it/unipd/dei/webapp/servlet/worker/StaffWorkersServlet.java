package it.unipd.dei.webapp.servlet.worker;

import it.unipd.dei.webapp.database.worker.GetAllWorkersDAO;
import it.unipd.dei.webapp.resource.movie.Worker;
import it.unipd.dei.webapp.resource.LogContext;
import it.unipd.dei.webapp.resource.Message;
import it.unipd.dei.webapp.servlet.AbstractDatabaseServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Manages the retrieval and display of workers (actors, directors).
 * Access is restricted to staff and administrators to manage the movie industry personnel database.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class StaffWorkersServlet extends AbstractDatabaseServlet {

    /**
     * Handles the HTTP GET request to list all workers.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        // Set context for logging purposes
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setResource(req.getRequestURI());

        LOGGER.info("Showing workers administration page.");

        try {
            // Check for PRG redirects containing alerts
            String errorParam = req.getParameter("error");
            if (errorParam != null && !errorParam.trim().isEmpty()) {
                req.setAttribute("message", new Message(errorParam, "E2A2", null));
            }

            String successParam = req.getParameter("success");
            if (successParam != null && !successParam.trim().isEmpty()) {
                if ("true".equals(successParam)) {
                    successParam = "Operation completed successfully.";
                }
                req.setAttribute("message", new Message(successParam));
            }

            // Database Access: Fetch the list of all workers using the DAO
            List<Worker> workers = new GetAllWorkersDAO(getConnection()).access().getOutputParam();

            // Forward to the JSP view
            req.setAttribute("workers", workers);
            req.getRequestDispatcher("/auth/staff/workers/workers.jsp").forward(req, res);

        } catch (SQLException e) {
            LOGGER.error("Unable to fetch workers from the database.", e);
            req.setAttribute("message", new Message("Unexpected database error occurred while retrieving workers.", "E0A1", null));
            // Forwarding to the same JSP so the user still sees the UI skeleton instead of a blank 500 page
            req.getRequestDispatcher("/auth/staff/workers/workers.jsp").forward(req, res);
        } catch (Exception e) {
            LOGGER.error("Unexpected error occurred while retrieving workers.", e);
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
        } finally {
            LogContext.removeIPAddress();
            LogContext.removeResource();
        }
    }
}