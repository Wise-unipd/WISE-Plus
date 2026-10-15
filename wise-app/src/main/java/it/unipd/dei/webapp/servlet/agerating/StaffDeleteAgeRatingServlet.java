package it.unipd.dei.webapp.servlet.agerating;

import java.io.IOException;
import java.sql.SQLException;

import it.unipd.dei.webapp.servlet.AbstractDatabaseServlet;
import org.apache.logging.log4j.message.StringFormattedMessage;

import it.unipd.dei.webapp.database.agerating.DeleteAgeRatingDAO;
import it.unipd.dei.webapp.database.agerating.SearchAgeRatingByIdDAO;
import it.unipd.dei.webapp.resource.Actions;
import it.unipd.dei.webapp.resource.movie.AgeRating;
import it.unipd.dei.webapp.resource.LogContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Handles the deletion of an age rating.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class StaffDeleteAgeRatingServlet extends AbstractDatabaseServlet {

    /**
     * Processes the deletion request.
     */
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setAction(Actions.DELETE_AGE_RATING);

        int ageRatingId = -1;
        AgeRating deletedAgeRating = null;

        try {
            String idParam = req.getParameter("id");
            if (idParam == null || idParam.trim().isEmpty()) {
                LOGGER.error("Age rating deletion failed: ID is missing.");
                res.sendRedirect(req.getContextPath() + "/auth/staff/age-ratings?error=Missing+Age+Rating+ID");
                return;
            }

            ageRatingId = Integer.parseInt(idParam);

            LogContext.setResource("Age Rating ID: " + ageRatingId);

            // Perform the deletion
            deletedAgeRating = new DeleteAgeRatingDAO(getConnection(), ageRatingId).access().getOutputParam();

            if (deletedAgeRating == null) {
                LOGGER.error("Deletion failed: age rating with ID %d not found.", ageRatingId);
                res.sendRedirect(req.getContextPath() + "/auth/staff/age-ratings?error=Age+Rating+not+found+or+already+deleted");
            } else {
                LOGGER.info("Successfully deleted age rating %s (ID: %d)", deletedAgeRating.getName(), deletedAgeRating.getId());
                res.sendRedirect(req.getContextPath() + "/auth/staff/age-ratings?success=Age+Rating+deleted");
            }

        } catch (NumberFormatException e) {
            LOGGER.error("Age rating deletion failed: invalid ID format.", e);
            res.sendRedirect(req.getContextPath() + "/auth/staff/age-ratings?error=Invalid+Age+Rating+ID");
        } catch (SQLException e) {
            // "23503" = Foreign Key Violation
            if ("23503".equals(e.getSQLState())) {
                LOGGER.error(new StringFormattedMessage("Foreign key violation: cannot delete age rating with ID %d.", ageRatingId), e);
                res.sendRedirect(req.getContextPath() + "/auth/staff/age-ratings?error=Cannot+delete+age+rating+assigned+to+movies");
            } else {
                LOGGER.error("Unexpected database error while deleting age rating.", e);
                res.sendRedirect(req.getContextPath() + "/auth/staff/age-ratings?error=Unexpected+database+error");
            }
        } catch (Exception e) {
            LOGGER.error("Cannot delete the age rating: unexpected fatal error.", e);
            res.sendRedirect(req.getContextPath() + "/auth/staff/age-ratings?error=Unexpected+error");
        } finally {
            LogContext.removeIPAddress();
            LogContext.removeAction();
            LogContext.removeResource();
        }
    }
}