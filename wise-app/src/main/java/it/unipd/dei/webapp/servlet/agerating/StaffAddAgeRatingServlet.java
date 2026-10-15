package it.unipd.dei.webapp.servlet.agerating;

import java.io.IOException;
import java.sql.SQLException;

import it.unipd.dei.webapp.servlet.AbstractDatabaseServlet;
import org.apache.logging.log4j.message.StringFormattedMessage;

import it.unipd.dei.webapp.database.agerating.CreateAgeRatingDAO;
import it.unipd.dei.webapp.resource.Actions;
import it.unipd.dei.webapp.resource.movie.AgeRating;
import it.unipd.dei.webapp.resource.LogContext;
import it.unipd.dei.webapp.resource.Message;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Creates a new age rating.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class StaffAddAgeRatingServlet extends AbstractDatabaseServlet {

    /**
     * Forwards to the age rating creation view.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setResource(req.getRequestURI());

        LOGGER.info("Showing age rating add form.");

        try {
            req.getRequestDispatcher("/auth/staff/age-ratings/add-age-rating.jsp").forward(req, res);
        } finally {
            LogContext.removeIPAddress();
            LogContext.removeResource();
        }
    }

    /**
     * Submits the new age rating to the database safely.
     */
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setAction(Actions.CREATE_AGE_RATING);

        // request parameters and models
        String name = null;
        String description = null;
        int age;
        AgeRating ar = null;
        Message m = null;

        try {
            name = req.getParameter("name");
            name = (name != null) ? name.trim() : "";
            description = req.getParameter("description");
            description = (description != null) ? description.trim() : "";
            age = Integer.parseInt(req.getParameter("age"));

            LogContext.setResource(name);

            ar = new AgeRating(name, description, age);

            // Server-side validation
            m = validateInput(ar);

            if (m != null) {
                LOGGER.error("Validation failed while creating age rating: %s.", m.getMessage());
                forwardWithError(req, res, m);
                return;
            }

            ar = new CreateAgeRatingDAO(getConnection(), ar).access().getOutputParam();

            if (ar == null) {
                LOGGER.error("AgeRating object is null after DAO execution for create: %s.", name);
                m = new Message("The age rating may have been created, but the system was unable to confirm it. Please check the list.", "E0A1", null);
                forwardWithError(req, res, m);
            } else {
                LOGGER.info("Age rating %s (ID: %d) created successfully in the database.", ar.getName(), ar.getId());
                res.sendRedirect(req.getContextPath() + "/auth/staff/age-ratings?success=true");
            }
        } catch (NumberFormatException e) {
            LOGGER.error("Cannot create the age rating: wrong number format(s).", e);
            m = new Message("Cannot create the age rating: wrong number format(s).", "E1A1", null);
            forwardWithError(req, res, m);
        } catch (SQLException e) {
            // "23505" = Unique Violation
            if ("23505".equals(e.getSQLState())) {
                m = new Message(String.format("Cannot create the age rating: %s already exists.", name), "E1A5",
                    null);
                LOGGER.error(new StringFormattedMessage("Cannot create the age rating: %s already exists.", name), e);
            } else {
                m = new Message("Cannot create the age rating: unexpected database error.", "E0A1", null);
                LOGGER.error("Cannot create the age rating: unexpected error while accessing the database.", e);
            }

            forwardWithError(req, res, m);
        } catch (Exception e) {
            LOGGER.error("Cannot create the age rating: unexpected fatal error.", e);
            res.sendRedirect(req.getContextPath() + "/auth/staff/age-ratings?error=Unexpected+error");
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
        req.getRequestDispatcher("/auth/staff/age-ratings/add-age-rating.jsp").forward(req, res);
    }

    /**
     * Validates the inputs to prevent injection and ensure data integrity.
     *
     * @param ar the object to validate.
     * @return a Message object containing the error, or null if valid.
     */
    private Message validateInput(AgeRating ar) {
        if (ar.getName().isEmpty()) {
            return new Message("Name missing.", "E1A1", null);
        }

        if (ar.getDescription().isEmpty()) {
            return new Message("Description missing.", "E1A1", null);
        }

        if (ar.getAge() < 0 || ar.getAge() > 18) {
            return new Message("Age must be between 0 and 18.", "E1A1", null);
        }

        return null;
    }
}
