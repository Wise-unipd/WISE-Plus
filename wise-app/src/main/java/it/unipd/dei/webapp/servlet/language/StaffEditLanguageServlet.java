package it.unipd.dei.webapp.servlet.language;

import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Pattern;

import it.unipd.dei.webapp.servlet.AbstractDatabaseServlet;
import it.unipd.dei.webapp.utils.NameFormatter;
import org.apache.logging.log4j.message.StringFormattedMessage;

import it.unipd.dei.webapp.database.language.UpdateLanguageDAO;
import it.unipd.dei.webapp.database.language.SearchLanguageByIdDAO;
import it.unipd.dei.webapp.resource.Actions;
import it.unipd.dei.webapp.resource.movie.Language;
import it.unipd.dei.webapp.resource.LogContext;
import it.unipd.dei.webapp.resource.Message;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Handles the modification of a language with strict input validation.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
@WebServlet(name = "EditLanguage", value = "/languages/edit-language")
public final class StaffEditLanguageServlet extends AbstractDatabaseServlet {

    /**
     * Regular expression to strictly validate language names.
     */
    private static final String NAME_REGEX = "^[\\p{L}\\s\\-]{1,50}$";
    private static final Pattern NAME_PATTERN = Pattern.compile(NAME_REGEX);

    /**
     * Populates and renders the editing form.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setResource(req.getRequestURI());

        LOGGER.info("Showing language update form.");

        try {
            String idParam = req.getParameter("id");
            if (idParam == null || idParam.trim().isEmpty()) {
                LOGGER.error("Cannot retrieve the language for editing: ID is missing.");
                res.sendRedirect(req.getContextPath() + "/auth/staff/languages?error=Missing+Language+ID");
                return;
            }

            int langId = Integer.parseInt(idParam);
            Language language = new SearchLanguageByIdDAO(getConnection(), langId).access().getOutputParam();

            if (language == null) {
                LOGGER.error("Language with ID %d not found for editing.", langId);
                res.sendRedirect(req.getContextPath() + "/auth/staff/languages?error=Language+not+found");
                return;
            }

            req.setAttribute("language", language);
            req.getRequestDispatcher("/auth/staff/languages/edit-language.jsp").forward(req, res);

        } catch (NumberFormatException e) {
            LOGGER.error("Cannot retrieve the language for editing: ID is not valid.", e);
            res.sendRedirect(req.getContextPath() + "/auth/staff/languages?error=Invalid+Language+ID");
        } catch (SQLException e) {
            LOGGER.error("Cannot retrieve the language for editing: database error.", e);
            res.sendRedirect(req.getContextPath() + "/auth/staff/languages?error=Database+error");
        } finally {
            LogContext.removeIPAddress();
            LogContext.removeResource();
        }
    }

    /**
     * Persists the changes to the database.
     * Relies on state 23505 to prevent renaming to an existing language.
     */
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setAction(Actions.EDIT_LANGUAGE);

        int langId = -1;
        String name = null;
        Language oldLanguage = null;
        Language language = null;
        Message m = null;

        try {
            // check the ID first
            try {
                langId = Integer.parseInt(req.getParameter("id"));
            } catch (NumberFormatException e) {
                LOGGER.error("Language update failed: ID is missing or not valid.", e);
                res.sendRedirect(req.getContextPath() + "/auth/staff/languages?error=Missing+or+Invalid+ID");
                return;
            }

            oldLanguage = new SearchLanguageByIdDAO(getConnection(), langId).access().getOutputParam();

            if (oldLanguage == null) {
                LOGGER.error("Update failed: language with ID %d not found.", langId);
                res.sendRedirect(req.getContextPath() + "/auth/staff/languages?error=Language+not+found");
                return;
            }

            req.setAttribute("language", oldLanguage);
            LogContext.setResource(oldLanguage.getName() + " (ID: " + oldLanguage.getId() + ")");

            // Parse and validate new data
            try {
                name = req.getParameter("name");
                name = (name != null) ? NameFormatter.formatName(name) : "";

                language = new Language(langId, name);

                // Strict Server-side validation
                m = validateInput(language);
                if (m != null) {
                    LOGGER.error("Validation failed while updating the language: %s.", m.getMessage());
                    forwardWithError(req, res, m);
                    return;
                }

                language = new UpdateLanguageDAO(getConnection(), language).access().getOutputParam();

                if (language == null) {
                    LOGGER.error("Language object is null after DAO execution for update: %s.", name);
                    m = new Message("An unexpected error occurred while updating the language. Please try again or check the list.", "E0A1", null);
                    forwardWithError(req, res, m);
                } else {
                    LOGGER.info("Language %s (ID: %d) successfully updated.", language.getName(), language.getId());
                    res.sendRedirect(req.getContextPath() + "/auth/staff/languages?success=true");
                }
            } catch (SQLException e) {
                // Unique Violation ensures we don't accidentally create duplicate names
                if ("23505".equals(e.getSQLState())) {
                    m = new Message(String.format("Cannot update the language: %s already exists.", name), "E1A5", null);
                    LOGGER.error(new StringFormattedMessage("Cannot update the language: %s already exists.", name), e);
                } else {
                    m = new Message("Cannot update the language due to a database error.", "E0A1", null);
                    LOGGER.error("Cannot update the language: unexpected error while accessing the database.", e);
                }
                forwardWithError(req, res, m);
            }
        } catch (Exception e) {
            LOGGER.error("Cannot update the language: unexpected fatal error.", e);
            res.sendRedirect(req.getContextPath() + "/auth/staff/languages?error=Unexpected+error");
        } finally {
            LogContext.removeIPAddress();
            LogContext.removeAction();
            LogContext.removeResource();
        }
    }

    /**
     * Utility method for forwarding errors back to the edit form.
     *
     * @param req      the HTTP request.
     * @param res      the HTTP response.
     * @param m        the message Object.
     * @throws ServletException if a server-side error occurs.
     * @throws IOException      if any error occurs during client/server communication.
     */
    private void forwardWithError(HttpServletRequest req, HttpServletResponse res, Message m) throws ServletException, IOException {
        req.setAttribute("message", m);
        req.getRequestDispatcher("/auth/staff/languages/edit-language.jsp").forward(req, res);
    }

    /**
     * Validates input fields to prevent injection.
     *
     * @param language the object to validate.
     * @return a Message object containing the error, or null if valid.
     */
    private Message validateInput(Language language) {
        if (language.getName() == null || language.getName().isEmpty()) {
            return new Message("Language name cannot be empty.", "E1A1", null);
        }

        if (!NAME_PATTERN.matcher(language.getName()).matches()) {
            return new Message("Invalid language name format. Only letters, spaces, and hyphens are allowed (max 50 characters).", "E1A1", null);
        }

        return null;
    }
}