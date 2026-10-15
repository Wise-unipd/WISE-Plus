package it.unipd.dei.webapp.servlet;

import it.unipd.dei.webapp.database.stats.GetKPIDAO;
import it.unipd.dei.webapp.database.stats.GetMostRentedFilmsLastMonthDAO;
import it.unipd.dei.webapp.database.stats.GetRentalTrendsDAO;
import it.unipd.dei.webapp.resource.Actions;
import it.unipd.dei.webapp.resource.authentication.AppUser;
import it.unipd.dei.webapp.resource.LogContext;
import it.unipd.dei.webapp.resource.Message;
import it.unipd.dei.webapp.resource.movie.Movie;
import it.unipd.dei.webapp.resource.stats.KPI;
import it.unipd.dei.webapp.resource.stats.RentalTrend;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.javatuples.Pair;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Servlet for displaying application statistics.
 * Accessible only to administrators.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
@WebServlet(name = "Stats", value = "/auth/staff/stats")
public class StatsServlet extends AbstractDatabaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        AppUser loggedUser = (AppUser) req.getAttribute("AppUser");

        // Strictly check for Admin role (ID = 2)
        if (loggedUser == null || loggedUser.getUserRole().getId() < 2) {
            LOGGER.warn("Access denied for user %s: administrator permissions required.",
                    loggedUser != null ? loggedUser.getEmail() : "unknown");
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "You do not have permission to access this resource.");
            return;
        }

        // setup logging
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setAction(Actions.GET_STATISTICS);

        KPI kpi = null;
        List<RentalTrend> rentalTrends = null;
        List<Pair<Movie, Integer>> mostRentedFilms = null;
        Message m = null;

        try {
            kpi = new GetKPIDAO(getConnection()).access().getOutputParam();
            rentalTrends = new GetRentalTrendsDAO(getConnection()).access().getOutputParam();
            mostRentedFilms = new GetMostRentedFilmsLastMonthDAO(getConnection()).access().getOutputParam();

            m = new Message("Statistics successfully retrieved.");
            LOGGER.info("Statistics successfully retrieved.");

        } catch (SQLException e) {
            m = new Message("Cannot retrieve statistics: unexpected error while accessing the database", "E0A1", e.getMessage());
            LOGGER.error("Cannot retrieve statistics: unexpected error while accessing the database", e);
        } finally {
            LogContext.removeIPAddress();
            LogContext.removeAction();
        }

        req.setAttribute("kpi", kpi);
        req.setAttribute("rentalTrends", rentalTrends);
        req.setAttribute("mostRentedFilms", mostRentedFilms);
        req.setAttribute("message", m);

        req.getRequestDispatcher("/auth/stats.jsp").forward(req, resp);
    }
}
