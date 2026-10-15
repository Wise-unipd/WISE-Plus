package it.unipd.dei.webapp.servlet.category;

import java.io.IOException;
import java.sql.SQLException;

import it.unipd.dei.webapp.servlet.AbstractDatabaseServlet;
import org.apache.logging.log4j.message.StringFormattedMessage;

import it.unipd.dei.webapp.database.category.UpdateCategoryDAO;
import it.unipd.dei.webapp.database.category.SearchCategoryByIdDAO;
import it.unipd.dei.webapp.resource.Actions;
import it.unipd.dei.webapp.resource.movie.Category;
import it.unipd.dei.webapp.resource.LogContext;
import it.unipd.dei.webapp.resource.Message;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Handles the modification of an existing category.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class StaffEditCategoryServlet extends AbstractDatabaseServlet {

    /**
     * Populates and renders the editing form.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setResource(req.getRequestURI());

        LOGGER.info("Showing category update form.");

        try {
            String idParam = req.getParameter("id");
            if (idParam == null || idParam.trim().isEmpty()) {
                LOGGER.error("Cannot retrieve category for editing: ID is missing.");
                res.sendRedirect(req.getContextPath() + "/auth/staff/categories?error=Missing+Category+ID");
                return;
            }

            // Parse the ID from the URL parameter
            int categoryId = Integer.parseInt(idParam);

            // Fetch the category from the database
            Category category = new SearchCategoryByIdDAO(getConnection(), categoryId, false).access().getOutputParam();

            if (category == null) {
                LOGGER.error("Category with ID %d not found for editing.", categoryId);
                res.sendRedirect(req.getContextPath() + "/auth/staff/categories?error=Category+not+found");
                return;
            }

            req.setAttribute("category", category);
            req.getRequestDispatcher("/auth/staff/categories/edit-category.jsp").forward(req, res);

        } catch (NumberFormatException e) {
            LOGGER.error("Cannot retrieve category for editing: ID is not valid.", e);
            res.sendRedirect(req.getContextPath() + "/auth/staff/categories?error=Invalid+Category+ID");
        } catch (SQLException e) {
            LOGGER.error("Cannot retrieve category for editing: database error.", e);
            res.sendRedirect(req.getContextPath() + "/auth/staff/categories?error=Database+error");
        } finally {
            LogContext.removeIPAddress();
            LogContext.removeResource();
        }
    }

    /**
     * Persists the changes to the database.
     * Relies on state 23505 to prevent renaming to an existing category.
     */
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setAction(Actions.EDIT_CATEGORY);

        // request parameters and models
        int categoryId = -1;
        String name = null;
        String description = null;
        Category oldCategory = null;
        Category cat = null;
        Message m = null;

        try {
            // check the ID first
            try {
                categoryId = Integer.parseInt(req.getParameter("id"));
            } catch (NumberFormatException e) {
                LOGGER.error("Category update failed: ID is missing or not valid.", e);
                res.sendRedirect(req.getContextPath() + "/auth/staff/categories?error=Missing+or+Invalid+ID");
                return;
            }

            oldCategory = new SearchCategoryByIdDAO(getConnection(), categoryId, false).access().getOutputParam();

            if (oldCategory == null) {
                LOGGER.error("Update failed: category with ID %d not found.", categoryId);
                res.sendRedirect(req.getContextPath() + "/auth/staff/categories?error=Category+not+found");
                return;
            }

            req.setAttribute("category", oldCategory);
            LogContext.setResource(oldCategory.getName() + " (ID: " + oldCategory.getId() + ")");

            // Parse and validate new data
            try {
                name = req.getParameter("name");
                name = (name != null) ? name.trim() : "";
                description = req.getParameter("description");
                description = (description != null) ? description.trim() : "";

                cat = new Category(categoryId, name, description, null);

                // Server-side validation
                m = validateInput(cat);

                if (m != null) {
                    LOGGER.error("Validation failed while updating category: %s.", m.getMessage());
                    forwardWithError(req, res, m);
                    return;
                }

                // Proceed with the update
                cat = new UpdateCategoryDAO(getConnection(), cat).access().getOutputParam();

                if (cat == null) {
                    LOGGER.info("Category object is null after DAO execution for update: %s.", name);
                    m = new Message("An unexpected error occurred while updating the category. Please try again or check the list.", "E0A1", null);
                    forwardWithError(req, res, m);
                } else {
                    LOGGER.info("Category %s (ID: %d) successfully updated.", cat.getName(), cat.getId());
                    res.sendRedirect(req.getContextPath() + "/auth/staff/categories?success=true");
                }
            } catch (SQLException e) {
                // "23505" = Unique Violation
                if ("23505".equals(e.getSQLState())) {
                    m = new Message(String.format("Cannot update the category: %s already exists", name), "E1A5", null);
                    LOGGER.error(new StringFormattedMessage("Cannot update the category: %s already exists ", name), e);
                } else {
                    m = new Message("Cannot update the category due to a database error.", "E0A1", null);
                    LOGGER.error("Cannot update the category: unexpected error while accessing the database.", e);
                }

                forwardWithError(req, res, m);
            }
        } catch (Exception e) {
            LOGGER.error("Cannot update the category: unexpected fatal error.", e);
            res.sendRedirect(req.getContextPath() + "/auth/staff/categories?error=Unexpected+error");
        } finally {
            LogContext.removeIPAddress();
            LogContext.removeAction();
            LogContext.removeResource();
        }
    }

    /**
     * Utility method for forwarding errors.
     *
     * @param req the HTTP request.
     * @param res the HTTP response.
     * @param m   the message Object.
     * @throws ServletException if a server-side error occurs.
     * @throws IOException      if any error occurs during client/server communication.
     */
    private void forwardWithError(HttpServletRequest req, HttpServletResponse res, Message m) throws ServletException, IOException {
        req.setAttribute("message", m);
        req.getRequestDispatcher("/auth/staff/categories/edit-category.jsp").forward(req, res);
    }

    /**
     * Validates the inputs to prevent injection and ensure data integrity.
     *
     * @param cat the object to validate.
     * @return a Message object containing the error, or null if valid.
     */
    private Message validateInput(Category cat) {
        if (cat.getName().isEmpty()) {
            return new Message("Name missing.", "E1A1", null);
        }

        if (cat.getDescription().isEmpty()) {
            return new Message("Description missing.", "E1A1", null);
        }

        return null;
    }
}