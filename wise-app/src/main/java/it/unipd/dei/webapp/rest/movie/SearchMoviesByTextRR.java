package it.unipd.dei.webapp.rest.movie;

import it.unipd.dei.webapp.database.movie.SearchMoviesByTextDAO;
import it.unipd.dei.webapp.resource.Actions;
import it.unipd.dei.webapp.resource.Message;
import it.unipd.dei.webapp.resource.movie.PaginatedMovieList;
import it.unipd.dei.webapp.rest.AbstractRR;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;

/**
 * A REST resource for searching movies by text via AJAX.
 * 
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class SearchMoviesByTextRR extends AbstractRR {

    /**
     * Creates a new REST resource for searching movies by text.
     *
     * @param req the HTTP request.
     * @param res the HTTP response.
     * @param con the connection to the database.
     */
    public SearchMoviesByTextRR(final HttpServletRequest req, final HttpServletResponse res, final Connection con) {
        super(Actions.SEARCH_MOVIES_BY_TEXT, req, res, con);
    }

    @Override
    protected void doServe() throws IOException {

        try {
            // 1. Extract the search parameter from the query string
            String query = req.getParameter("q");
            final int limit = Integer.parseInt(req.getParameter("limit"));
            final int offset = Integer.parseInt(req.getParameter("offset"));

            LOGGER.info("Searching for movies using a text query: " + query);

            // 2. Execute the DAO
            PaginatedMovieList paginatedMovieList = new SearchMoviesByTextDAO(con, query, limit, offset, true).access().getOutputParam();

            // 3. Send the response
            if (paginatedMovieList != null) {
                LOGGER.info("Found " + paginatedMovieList.getMovies().size() + " movies.");
                res.setStatus(HttpServletResponse.SC_OK);
                paginatedMovieList.toJSON(res.getOutputStream());
            } else {
                LOGGER.error("Failed to retrieve movies from the database.");
                Message m = new Message("Failed to retrieve movies.", "E0A1", null);
                res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                m.toJSON(res.getOutputStream());
            }
        } catch (Exception e) {
            LOGGER.error("Unexpected error while searching for movies.", e);
            Message m = new Message("Unexpected error while searching.", "E0A2", null);
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            m.toJSON(res.getOutputStream());
        }
    }
}