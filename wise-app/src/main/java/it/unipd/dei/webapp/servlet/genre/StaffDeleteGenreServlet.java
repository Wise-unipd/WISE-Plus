package it.unipd.dei.webapp.servlet.genre;

import java.io.IOException;
import java.sql.SQLException;

import it.unipd.dei.webapp.servlet.AbstractDatabaseServlet;
import org.apache.logging.log4j.message.StringFormattedMessage;

import it.unipd.dei.webapp.database.genre.DeleteGenreDAO;
import it.unipd.dei.webapp.database.genre.SearchGenreByIdDAO;
import it.unipd.dei.webapp.resource.Actions;
import it.unipd.dei.webapp.resource.movie.Genre;
import it.unipd.dei.webapp.resource.LogContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Handles the deletion of a genre.
 * If the genre is assigned to movies, the foreign key constraint (23503) will block it.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
@WebServlet(name = "DeleteGenre", value = "/genres/delete-genre")
public final class StaffDeleteGenreServlet extends AbstractDatabaseServlet {

    /**
     * Handles the HTTP POST request to execute the deletion.
     */
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setAction(Actions.DELETE_GENRE);

        int genreId = -1;
        Genre deletedGenre = null;

        try {
            String idParam = req.getParameter("id");
            if (idParam == null || idParam.trim().isEmpty()) {
                LOGGER.error("Genre deletion failed: ID is missing.");
                res.sendRedirect(req.getContextPath() + "/auth/staff/genres?error=Missing+Genre+ID");
                return;
            }

            genreId = Integer.parseInt(idParam);
            LogContext.setResource("Genre ID: " + genreId);

            // Perform the deletion
            deletedGenre = new DeleteGenreDAO(getConnection(), genreId).access().getOutputParam();

            if (deletedGenre == null) {
                LOGGER.error("Deletion failed: genre with ID %d not found.", genreId);
                res.sendRedirect(req.getContextPath() + "/auth/staff/genres?error=Genre+not+found+or+already+deleted");
            } else {
                LOGGER.info("Successfully deleted genre %s (ID: %d)", deletedGenre.getName(), deletedGenre.getId());
                res.sendRedirect(req.getContextPath() + "/auth/staff/genres?success=Genre+deleted");
            }

        } catch (NumberFormatException e) {
            LOGGER.error("Genre deletion failed: invalid ID format.", e);
            res.sendRedirect(req.getContextPath() + "/auth/staff/genres?error=Invalid+ID");
        } catch (SQLException e) {
            // "23503" = Foreign Key Violation
            // Triggered if the genre is currently linked to any movie via HasGenre
            if ("23503".equals(e.getSQLState())) {
                LOGGER.error(new StringFormattedMessage("Foreign key violation: cannot delete genre with ID %d.", genreId), e);
                res.sendRedirect(req.getContextPath() + "/auth/staff/genres?error=Cannot+delete+genre+assigned+to+movies");
            } else {
                LOGGER.error("Unexpected database error while deleting genre.", e);
                res.sendRedirect(req.getContextPath() + "/auth/staff/genres?error=Unexpected+database+error");
            }
        } catch (Exception e) {
            LOGGER.error("Cannot delete the genre: unexpected fatal error.", e);
            res.sendRedirect(req.getContextPath() + "/auth/staff/genres?error=Unexpected+error");
        } finally {
            LogContext.removeIPAddress();
            LogContext.removeAction();
            LogContext.removeResource();
        }
    }
}