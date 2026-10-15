package it.unipd.dei.webapp.servlet.genre;

import it.unipd.dei.webapp.database.genre.GetAllGenresDAO;
import it.unipd.dei.webapp.resource.movie.Genre;
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
 * Manages the retrieval and display of genres.
 * Access is restricted to staff and administrators.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class StaffGenresServlet extends AbstractDatabaseServlet {

    /**
     * Handles the HTTP GET request to list all genres.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setResource(req.getRequestURI());

        LOGGER.info("Showing genres administration page.");

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

            // Database Access: Fetch the genres
            List<Genre> genres = new GetAllGenresDAO(getConnection()).access().getOutputParam();

            // Success: set attributes and forward to the view
            req.setAttribute("genres", genres);
            req.getRequestDispatcher("/auth/staff/genres/genres.jsp").forward(req, res);

        } catch (SQLException e) {
            LOGGER.error("Unable to fetch genres from the database.", e);
            req.setAttribute("message", new Message("Unexpected database error occurred while retrieving genres.", "E0A1", null));
            req.getRequestDispatcher("/auth/staff/genres/genres.jsp").forward(req, res);
        } catch (Exception e) {
            LOGGER.error("Unexpected error occurred while retrieving genres.", e);
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
        } finally {
            LogContext.removeIPAddress();
            LogContext.removeResource();
        }
    }
}