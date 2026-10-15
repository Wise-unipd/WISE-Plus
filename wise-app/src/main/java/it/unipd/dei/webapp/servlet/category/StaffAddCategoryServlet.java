package it.unipd.dei.webapp.servlet.category;

import java.io.IOException;
import java.sql.SQLException;

import it.unipd.dei.webapp.servlet.AbstractDatabaseServlet;
import org.apache.logging.log4j.message.StringFormattedMessage;

import it.unipd.dei.webapp.database.category.CreateCategoryDAO;
import it.unipd.dei.webapp.resource.Actions;
import it.unipd.dei.webapp.resource.movie.Category;
import it.unipd.dei.webapp.resource.LogContext;
import it.unipd.dei.webapp.resource.Message;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Creates a new category in the database.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class  StaffAddCategoryServlet extends AbstractDatabaseServlet {

    /**
     * Forwards to the category creation view.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setResource(req.getRequestURI());

        LOGGER.info("Showing category add form.");

        try {
            req.getRequestDispatcher("/auth/staff/categories/add-category.jsp").forward(req, res);
        } finally {
            LogContext.removeIPAddress();
            LogContext.removeResource();
        }
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setAction(Actions.CREATE_CATEGORY);

        // request parameters and models
        String name = null;
        String description = null;
        Category cat = null;
        Message m = null;

        try {
            name = req.getParameter("name");
            name = (name != null) ? name.trim() : "";
            description = req.getParameter("description");
            description = (description != null) ? description.trim() : "";

            LogContext.setResource(name);

            cat = new Category(name, description);

            // Server-side validation
            m = validateInput(cat);

            if (m != null) {
                LOGGER.error("Validation failed while creating category: %s.", m.getMessage());
                forwardWithError(req, res, m);
                return;
            }

            // Database insertion
            cat = new CreateCategoryDAO(getConnection(), cat).access().getOutputParam();

            if (cat == null) {
                LOGGER.error("Category object is null after DAO execution for create: %s.", name);
                m = new Message("The category may have been created, but the system was unable to confirm it. Please check the list.", "E0A1", null);
                forwardWithError(req, res, m);
            } else {
                LOGGER.info("Category %s (ID: %d) created successfully in the database.", cat.getName(), cat.getId());
                res.sendRedirect(req.getContextPath() + "/auth/staff/categories?success=true");
            }
        } catch (SQLException e) {
            // "23505" = Unique Violation
            if ("23505".equals(e.getSQLState())) {
                m = new Message(String.format("Cannot create the category: %s already exists", name), "E1A5",
                    null);
                LOGGER.error(new StringFormattedMessage("Cannot create the category: %s already exists ", name), e);
            } else {
                m = new Message("Cannot create the category: unexpected database error.", "E0A1", null);
                LOGGER.error("Cannot create the category: unexpected error while accessing the database.", e);
            }
            forwardWithError(req, res, m);
        } catch (Exception e) {
            LOGGER.error("Cannot create the category: unexpected fatal error.", e);
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
        req.getRequestDispatcher("/auth/staff/categories/add-category.jsp").forward(req, res);
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
