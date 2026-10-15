package it.unipd.dei.webapp.rest;

import it.unipd.dei.webapp.database.authentication.SearchUsersByTextDAO;
import it.unipd.dei.webapp.resource.*;
import it.unipd.dei.webapp.resource.authentication.AppUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

/**
 * A REST resource for searching users by text via AJAX.
 * 
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class SearchUsersByTextRR extends AbstractRR {

    /**
     * Creates a new REST resource for searching users by text.
     *
     * @param req the HTTP request.
     * @param res the HTTP response.
     * @param con the connection to the database.
     */
    public SearchUsersByTextRR(final HttpServletRequest req, final HttpServletResponse res, final Connection con) {
        super(Actions.SEARCH_USERS_BY_TEXT, req, res, con);
    }

    @Override
    protected void doServe() throws IOException {
        // 1. Extract the search parameter from the query string
        String query = req.getParameter("q");

        LOGGER.info("Searching for users with text: " + query);

        // If query is null, default to an empty string to show the whole catalog
        if (query == null) {
            query = "";
        }

        try {
            // 2. Execute the DAO
            List<AppUser> users = new SearchUsersByTextDAO(con, query).access().getOutputParam(); //


            // 3. Send the response
            if (users != null) {
                LOGGER.info("Found " + users.size() + " users.");

                res.setStatus(HttpServletResponse.SC_OK);
                new ResourceList<>(users).toJSON(res.getOutputStream());
            } else {
                LOGGER.error("Failed to retrieve users from the database.");
                Message m = new Message("Failed to retrieve users.", "E0A1", null);
                res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                m.toJSON(res.getOutputStream());
            }
        } catch (Exception ex) {
            LOGGER.error("Unexpected error while searching users.", ex);
            Message m = new Message("Unexpected error while searching.", "E0A2", ex.getMessage());
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            m.toJSON(res.getOutputStream());
        }
    }
}