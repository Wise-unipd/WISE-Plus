package it.unipd.dei.webapp.rest;

import it.unipd.dei.webapp.database.worker.SearchWorkersByTextDAO;
import it.unipd.dei.webapp.resource.Actions;
import it.unipd.dei.webapp.resource.Message;
import it.unipd.dei.webapp.resource.ResourceList;
import it.unipd.dei.webapp.resource.movie.Worker;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

/**
 * A REST resource for searching workers by text (name and surname) via AJAX.
 * 
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class SearchWorkersByTextRR extends AbstractRR {

    /**
     * Creates a new REST resource for searching workers by text.
     *
     * @param req the HTTP request.
     * @param res the HTTP response.
     * @param con the database connection.
     */
    public SearchWorkersByTextRR(final HttpServletRequest req, final HttpServletResponse res, final Connection con) {
        super(Actions.SEARCH_WORKERS_BY_TEXT, req, res, con);
    }

    @Override
    protected void doServe() throws IOException {
        // 1. Extract the search parameter from the query string
        final String query = req.getParameter("q");
        final int limit = Integer.parseInt(req.getParameter("limit"));

        LOGGER.info("Searching for workers using a text query: " + query);

        try {
            // 2. Execute the DAO
            List<Worker> workers = new SearchWorkersByTextDAO(con, query, limit).access().getOutputParam();

            // 3. Send the response
            if (workers != null) {
                LOGGER.info("Found " + workers.size() + " workers.");
                res.setStatus(HttpServletResponse.SC_OK);
                new ResourceList<>(workers).toJSON(res.getOutputStream());
            } else {
                LOGGER.error("Failed to retrieve workers from the database.");
                Message m = new Message("Failed to retrieve workers.", "E0A1", null);
                res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                m.toJSON(res.getOutputStream());
            }
        } catch (Exception ex) {
            LOGGER.error("Unexpected error while searching for workers.", ex);
            Message m = new Message("Unexpected error while searching.", "E0A2", ex.getMessage());
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            m.toJSON(res.getOutputStream());
        }
    }
}