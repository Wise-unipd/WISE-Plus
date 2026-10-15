package it.unipd.dei.webapp.servlet.worker;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import it.unipd.dei.webapp.servlet.AbstractDatabaseServlet;
import it.unipd.dei.webapp.database.movie.SearchMoviesByWorkerIdDAO;
import it.unipd.dei.webapp.database.worker.SearchWorkerByIdDAO;
import it.unipd.dei.webapp.resource.Actions;
import it.unipd.dei.webapp.resource.LogContext;
import it.unipd.dei.webapp.resource.Message;
import it.unipd.dei.webapp.resource.movie.Movie;
import it.unipd.dei.webapp.resource.movie.Worker;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Searches for a worker and their associated filmography by the worker's ID.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
@WebServlet(name = "PersonServlet", value = "/person")
public final class PersonServlet extends AbstractDatabaseServlet {

    /**
     * Searches for a worker and their associated filmography by the worker's ID.
     */
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        // setup logging
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setAction(Actions.SEARCH_MOVIES_BY_WORKER_ID);

        // request parameters and models
        int workerId = -1;
        Worker worker = null;
        List<Movie> movies = null;
        Message m = null;

        // business logic and database access
        try {
            // retrieves the request parameter
            workerId = Integer.parseInt(req.getParameter("id"));

            // perform the operation on the database
            worker = new SearchWorkerByIdDAO(getConnection(), workerId).access().getOutputParam();

            if (worker == null) {
                m = new Message("Nothing retrieved. The ID is not associated with any worker.", "E1A4", null);

                LOGGER.info("Nothing retrieved. There is no worker with ID %d.", workerId);
            } else {
                // if worker exists, fetch the filmography
                movies = new SearchMoviesByWorkerIdDAO(getConnection(), workerId).access().getOutputParam();

                m = new Message("Worker profile and filmography successfully retrieved.");

                LOGGER.info("Worker and movies successfully searched by ID %d.", workerId);
            }
        } catch (NumberFormatException e) {
            m = new Message("Cannot search for a worker. Missing or invalid ID. Please provide a valid integer number.", "E1A0", null);

            LOGGER.error("Cannot search for a worker. Missing or invalid ID. It must be an integer.", e);
        } catch (SQLException e) {
            m = new Message("Cannot search for a worker: system error.", "E0A1",
                null);

            LOGGER.error("Cannot search for a worker: unexpected error while accessing the database.", e);
        }

        try {
            req.setAttribute("worker", worker);
            req.setAttribute("movies", movies);
            req.setAttribute("message", m);

            req.getRequestDispatcher("/auth/browse/person.jsp").forward(req, res);
        } finally {
            // final cleaning
            LogContext.removeIPAddress();
            LogContext.removeAction();
        }
    }
}