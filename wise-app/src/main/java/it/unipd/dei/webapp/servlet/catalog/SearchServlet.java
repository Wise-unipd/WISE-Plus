package it.unipd.dei.webapp.servlet.catalog;

import it.unipd.dei.webapp.resource.LogContext;
import it.unipd.dei.webapp.resource.Actions;
import it.unipd.dei.webapp.servlet.AbstractDatabaseServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Servlet that handles the display of the movie search page.
 * <p>
 * This servlet is expected to be mapped to {@code /auth/search} in the {@code web.xml}
 * deployment descriptor. It simply forwards the user to the search view, which then
 * handles the actual dynamic searching via AJAX and the REST API.
 * </p>
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class SearchServlet extends AbstractDatabaseServlet {

    /**
     * Handles the HTTP {@code GET} request.
     * <p>
     * Forwards the request to the search JSP view. No initial database operations
     * are required here because the frontend JavaScript will immediately trigger
     * an AJAX call to load the catalog when the page loads.
     * </p>
     *
     * @param req the {@code HttpServletRequest} object that contains the request the client made to the servlet.
     * @param res the {@code HttpServletResponse} object that contains the response the servlet returns to the client.
     * @throws ServletException if the request for the GET could not be handled.
     * @throws IOException      if an input or output error occurs while the servlet is handling the GET request.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        // Set up the logging context with the specific action
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setResource(req.getRequestURI());
        LogContext.setAction(Actions.SEARCH_MOVIES_BY_TEXT);

        LOGGER.info("Accessing the dynamic search catalog page.");

        try {
            // Forward the request to your search JSP page.
            req.getRequestDispatcher("/auth/browse/search.jsp").forward(req, res);

        } catch (Exception e) {
            LOGGER.error("Error forwarding to the search page.", e);
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error loading the search page.");
        } finally {
            // Clean up the logging context to prevent memory leaks or incorrect logs in thread pooling
            LogContext.removeIPAddress();
            LogContext.removeResource();
            LogContext.removeAction();
        }
    }
}