package it.unipd.dei.webapp.servlet.language;

import java.io.IOException;
import java.sql.SQLException;

import it.unipd.dei.webapp.servlet.AbstractDatabaseServlet;
import org.apache.logging.log4j.message.StringFormattedMessage;

import it.unipd.dei.webapp.database.language.DeleteLanguageDAO;
import it.unipd.dei.webapp.database.language.SearchLanguageByIdDAO;
import it.unipd.dei.webapp.resource.Actions;
import it.unipd.dei.webapp.resource.movie.Language;
import it.unipd.dei.webapp.resource.LogContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Handles the removal of a language from the system.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
@WebServlet(name = "DeleteLanguage", value = "/languages/delete-language")
public final class StaffDeleteLanguageServlet extends AbstractDatabaseServlet {

    /**
     * Processes the deletion request.
     */
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setAction(Actions.DELETE_LANGUAGE);

        int langId = -1;
        Language deletedLang = null;

        try {
            String idParam = req.getParameter("id");
            if (idParam == null || idParam.trim().isEmpty()) {
                LOGGER.error("Language deletion failed: ID is missing.");
                res.sendRedirect(req.getContextPath() + "/auth/staff/languages?error=Missing+Language+ID");
                return;
            }

            langId = Integer.parseInt(idParam);
            LogContext.setResource("Language ID: " + langId);

            // Perform the deletion
            deletedLang = new DeleteLanguageDAO(getConnection(), langId).access().getOutputParam();

            if (deletedLang == null) {
                LOGGER.error("Deletion failed: language with ID %d not found.", langId);
                res.sendRedirect(req.getContextPath() + "/auth/staff/languages?error=Language+not+found+or+already+deleted");
            } else {
                LOGGER.info("Successfully deleted language %s (ID: %d)", deletedLang.getName(), deletedLang.getId());
                res.sendRedirect(req.getContextPath() + "/auth/staff/languages?success=Language+deleted");
            }

        } catch (NumberFormatException e) {
            LOGGER.error("Language deletion failed: invalid ID format.", e);
            res.sendRedirect(req.getContextPath() + "/auth/staff/languages?error=Invalid+Language+ID");
        } catch (SQLException e) {
            // "23503" = Foreign Key Violation (Language is mapped to Audio or Subtitle)
            if ("23503".equals(e.getSQLState())) {
                LOGGER.error(new StringFormattedMessage("Foreign key violation: cannot delete language with ID %d.", langId), e);
                res.sendRedirect(req.getContextPath() + "/auth/staff/languages?error=Cannot+delete+language+assigned+as+audio+or+subtitle+to+movies");
            } else {
                LOGGER.error("Unexpected database error while deleting language.", e);
                res.sendRedirect(req.getContextPath() + "/auth/staff/languages?error=Unexpected+database+error");
            }
        } catch (Exception e) {
            LOGGER.error("Cannot delete the language: unexpected fatal error.", e);
            res.sendRedirect(req.getContextPath() + "/auth/staff/languages?error=Unexpected+error");
        } finally {
            LogContext.removeIPAddress();
            LogContext.removeAction();
            LogContext.removeResource();
        }
    }
}