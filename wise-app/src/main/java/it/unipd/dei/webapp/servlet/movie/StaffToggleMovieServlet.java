package it.unipd.dei.webapp.servlet.movie;

import java.io.IOException;
import java.sql.SQLException;

import it.unipd.dei.webapp.database.movie.SearchMovieByIdDAO;
import it.unipd.dei.webapp.database.movie.ToggleMovieStatusDAO;
import it.unipd.dei.webapp.resource.Actions;
import it.unipd.dei.webapp.resource.movie.Movie;
import it.unipd.dei.webapp.resource.LogContext;
import it.unipd.dei.webapp.servlet.AbstractDatabaseServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Toggle a movie in the system.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class StaffToggleMovieServlet extends AbstractDatabaseServlet {

    /**
     * Processes the activation/deactivation request.
     */
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setAction(Actions.TOGGLE_MOVIE);

        int movieId = -1;
        boolean newStatus;
        Movie movie = null;

        try {
            String idParam = req.getParameter("id");
            if (idParam == null || idParam.trim().isEmpty()) {
                LOGGER.error("Movie activation/deactivation failed: ID is missing.");
                res.sendRedirect(req.getContextPath() + "/auth/staff/movies?error=Missing+Movie+ID");
                return;
            }

            movieId = Integer.parseInt(idParam);
            newStatus = Boolean.parseBoolean(req.getParameter("active"));
            
            LogContext.setResource("Movie ID: " + movieId);

            movie = new ToggleMovieStatusDAO(getConnection(), movieId, newStatus).access().getOutputParam();

            if (movie == null) {
                LOGGER.error("Activation/deactivation failed: movie with ID %d not found.", movieId);
                res.sendRedirect(req.getContextPath() + "/auth/staff/movies?error=Movie+not+found");
            } else {
                String statusMsg = movie.getActive() ? "enabled" : "disabled";
                LOGGER.info("Successfully %s movie %s (ID: %d)", statusMsg, movie.getTitle(), movie.getId());
                res.sendRedirect(req.getContextPath() + "/auth/staff/movies?success=Movie+status+updated");
            }

        } catch (NumberFormatException e) {
            LOGGER.error("Movie activation/deactivation failed: invalid ID format.", e);
            res.sendRedirect(req.getContextPath() + "/auth/staff/movies?error=Invalid+Movie+ID");
        } catch (SQLException e) {
            LOGGER.error("Unexpected database error during movie activation/deactivation.", e);
            res.sendRedirect(req.getContextPath() + "/auth/staff/movies?error=Unexpected+database+error");
        } catch (Exception e) {
            LOGGER.error("Cannot activate/deactivate the movie: unexpected fatal error.", e);
            res.sendRedirect(req.getContextPath() + "/auth/staff/movies?error=Unexpected+error");
        } finally {
            LogContext.removeIPAddress();
            LogContext.removeAction();
            LogContext.removeResource();
        }
    }
}