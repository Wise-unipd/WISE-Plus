package it.unipd.dei.webapp.rest;

import it.unipd.dei.webapp.database.rentals.GetRentalsByTextDAO;
import it.unipd.dei.webapp.resource.*;
import it.unipd.dei.webapp.resource.rentals.Rental;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;


/**
 * A REST resource for searching rentals by text via AJAX.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class SearchRentalsByTextRR extends AbstractRR {

    /**
     * Creates a new REST resource for searching rentals by text.
     *
     * @param req the HTTP request.
     * @param res the HTTP response.
     * @param con the connection to the database.
     */
    public SearchRentalsByTextRR(final HttpServletRequest req, final HttpServletResponse res, final Connection con) {
        super(Actions.SEARCH_RENTS_BY_TEXT, req, res, con);
    }

    @Override
    protected void doServe() throws IOException {
        //Extract the search parameter from the query string
        String query = req.getParameter("q");
        boolean searchEmail = Boolean.parseBoolean(req.getParameter("searchEmail"));
        boolean searchTitle = Boolean.parseBoolean(req.getParameter("searchTitle"));

        // If query is null, default to an empty string to show the whole catalog
        if (query == null) {
            query = "";
        }

        try {
            // Execute the DAO
            List<Rental> rentals = new GetRentalsByTextDAO(con, query, searchEmail, searchTitle).access().getOutputParam();

            // Send the response
            if (rentals != null) {
                res.setStatus(HttpServletResponse.SC_OK);
                new ResourceList<>(rentals).toJSON(res.getOutputStream());
            } else {
                res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                new Message("Failed to retrieve rents.", "E0A1", null).toJSON(res.getOutputStream());
            }
        } catch (Exception ex) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            new Message("Unexpected error.", "E0A2", ex.getMessage()).toJSON(res.getOutputStream());
        }
    }
}