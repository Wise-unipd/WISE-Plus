package it.unipd.dei.webapp.servlet.agerating;

import java.io.IOException;
import java.sql.SQLException;

import it.unipd.dei.webapp.servlet.AbstractDatabaseServlet;
import org.apache.logging.log4j.message.StringFormattedMessage;

import it.unipd.dei.webapp.database.agerating.UpdateAgeRatingDAO;
import it.unipd.dei.webapp.database.agerating.SearchAgeRatingByIdDAO;
import it.unipd.dei.webapp.resource.Actions;
import it.unipd.dei.webapp.resource.movie.AgeRating;
import it.unipd.dei.webapp.resource.LogContext;
import it.unipd.dei.webapp.resource.Message;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Handles the modification of an existing age rating.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class StaffEditAgeRatingServlet extends AbstractDatabaseServlet {

    /**
     * Populates and renders the editing form.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setResource(req.getRequestURI());

        LOGGER.info("Showing age rating update form.");

        try {
            // Parse the ID from the URL parameter
            int ageRatingId = Integer.parseInt(req.getParameter("id"));

            // Fetch the age rating from the database
            AgeRating ageRating = new SearchAgeRatingByIdDAO(getConnection(), ageRatingId).access().getOutputParam();

            if (ageRating == null) {
                LOGGER.error("Age rating with ID %d not found for editing.", ageRatingId);
                res.sendRedirect(req.getContextPath() + "/auth/staff/age-ratings?error=Age+Rating+not+found");
                return;
            }

            req.setAttribute("ageRating", ageRating);
            req.getRequestDispatcher("/auth/staff/age-ratings/edit-age-rating.jsp").forward(req, res);

        } catch (NumberFormatException e) {
            LOGGER.error("Cannot retrieve age rating for editing: ID is missing or not valid.", e);
            res.sendRedirect(req.getContextPath() + "/auth/staff/age-ratings?error=Missing+or+Invalid+ID");
        } catch (SQLException e) {
            LOGGER.error("Cannot retrieve age rating for editing: database error.", e);
            res.sendRedirect(req.getContextPath() + "/auth/staff/age-ratings?error=Database+error");
        } finally {
            LogContext.removeIPAddress();
            LogContext.removeResource();
        }
    }

    /**
     * Persists the changes to the database.
     * Relies on state 23505 to prevent renaming to an age rating.
     */
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setAction(Actions.EDIT_AGE_RATING);

        // request parameters and models
        int ageRatingId = -1;
        String name = null;
        String description = null;
        int age;
        AgeRating oldAgeRating = null;
        AgeRating ar = null;
        Message m = null;

        try {
            // check the ID first
            try {
                ageRatingId = Integer.parseInt(req.getParameter("id"));
            } catch (NumberFormatException e) {
                LOGGER.error("Age rating update failed: ID is missing or not valid.", e);
                res.sendRedirect(req.getContextPath() + "/auth/staff/age-ratings?error=Missing+or+Invalid+ID");
                return;
            }

            oldAgeRating = new SearchAgeRatingByIdDAO(getConnection(), ageRatingId).access().getOutputParam();

            if (oldAgeRating == null) {
                LOGGER.error("Update failed: age rating with ID %d not found.", ageRatingId);
                res.sendRedirect(req.getContextPath() + "/auth/staff/age-ratings?error=Age+Rating+not+found");
                return;
            }

            req.setAttribute("ageRating", oldAgeRating);
            LogContext.setResource(oldAgeRating.getName() + " (ID: " + oldAgeRating.getId() + ")");

            // Parse and validate new data
            try {
                name = req.getParameter("name");
                name = (name != null) ? name.trim() : "";
                description = req.getParameter("description");
                description = (description != null) ? description.trim() : "";
                age = Integer.parseInt(req.getParameter("age"));

                ar = new AgeRating(ageRatingId, name, description, age);

                // Server-side validation
                m = validateInput(ar);

                if (m != null) {
                    LOGGER.error("Validation failed while updating age rating: %s.", m.getMessage());
                    forwardWithError(req, res, m);
                    return;
                }

                // Proceed with the update
                ar = new UpdateAgeRatingDAO(getConnection(), ar).access().getOutputParam();

                if (ar == null) {
                    LOGGER.error("AgeRating object is null after DAO execution for update: %s.", name);
                    m = new Message("An unexpected error occurred while updating the age rating. Please try again or check the list.", "E0A1", null);
                    forwardWithError(req, res, m);
                } else {
                    LOGGER.info("Age rating %s (ID: %d) successfully updated.", ar.getName(), ar.getId());
                    res.sendRedirect(req.getContextPath() + "/auth/staff/age-ratings?success=true");
                }
            } catch (NumberFormatException e) {
                LOGGER.error("Cannot update the age rating: wrong number format(s).", e);
                m = new Message("Cannot update the age rating: wrong number format(s).", "E1A1", null);
                forwardWithError(req, res, m);
            } catch (SQLException e) {
                // "23505" = Unique Violation
                if ("23505".equals(e.getSQLState())) {
                    m = new Message(String.format("Cannot update the age rating: %s already exists.", name), "E1A5", null);
                    LOGGER.error(new StringFormattedMessage("Cannot update the age rating: %s already exists.", name), e);
                } else {
                    m = new Message("Cannot update the age rating due to a database error.", "E0A1", null);
                    LOGGER.error("Cannot update the age rating: unexpected database error.", e);
                }

                forwardWithError(req, res, m);
            }
        } catch (Exception e) {
            LOGGER.error("Cannot update the age rating: unexpected fatal error.", e);
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
        req.getRequestDispatcher("/auth/staff/age-ratings/edit-age-rating.jsp").forward(req, res);
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