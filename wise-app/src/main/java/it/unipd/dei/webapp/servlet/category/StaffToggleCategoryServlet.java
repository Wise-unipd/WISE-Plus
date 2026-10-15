package it.unipd.dei.webapp.servlet.category;

import java.io.IOException;
import java.sql.SQLException;

import it.unipd.dei.webapp.database.category.SearchCategoryByIdDAO;
import it.unipd.dei.webapp.database.category.ToggleCategoryStatusDAO;
import it.unipd.dei.webapp.resource.Actions;
import it.unipd.dei.webapp.resource.movie.Category;
import it.unipd.dei.webapp.resource.LogContext;
import it.unipd.dei.webapp.servlet.AbstractDatabaseServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Toggle a category in the system.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class StaffToggleCategoryServlet extends AbstractDatabaseServlet {

    /**
     * Processes the activation/deactivation request.
     */
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setAction(Actions.TOGGLE_CATEGORY);

        int categoryId = -1;
        boolean newStatus;
        Category category = null;

        try {
            String idParam = req.getParameter("id");
            if (idParam == null || idParam.trim().isEmpty()) {
                LOGGER.error("Category activation/deactivation failed: ID is missing.");
                res.sendRedirect(req.getContextPath() + "/auth/staff/categories?error=Missing+Category+ID");
                return;
            }

            categoryId = Integer.parseInt(idParam);
            newStatus = Boolean.parseBoolean(req.getParameter("active"));

            LogContext.setResource("Category ID: " + categoryId);

            category = new ToggleCategoryStatusDAO(getConnection(), categoryId, newStatus).access().getOutputParam();

            if (category == null) {
                LOGGER.error("Activation/deactivation failed: category with ID %d not found.", categoryId);
                res.sendRedirect(req.getContextPath() + "/auth/staff/categories?error=Category+not+found");
            } else {
                String statusMsg = category.getActive() ? "enabled" : "disabled";
                LOGGER.info("Successfully %s category %s (ID: %d).", statusMsg, category.getName(), category.getId());
                res.sendRedirect(req.getContextPath() + "/auth/staff/categories?success=Category+status+updated");
            }
        } catch (NumberFormatException e) {
            LOGGER.error("Category activation/deactivation failed: invalid ID format.", e);
            res.sendRedirect(req.getContextPath() + "/auth/staff/categories?error=Invalid+Category+ID");
        } catch (SQLException e) {
            LOGGER.error("Unexpected database error during category activation/deactivation.", e);
            res.sendRedirect(req.getContextPath() + "/auth/staff/categories?error=Unexpected+database+error");
        } catch (Exception e) {
            LOGGER.error("Cannot activate/deactivate the category: unexpected fatal error.", e);
            res.sendRedirect(req.getContextPath() + "/auth/staff/categories?error=Unexpected+error");
        } finally {
            LogContext.removeIPAddress();
            LogContext.removeAction();
            LogContext.removeResource();
        }
    }
}