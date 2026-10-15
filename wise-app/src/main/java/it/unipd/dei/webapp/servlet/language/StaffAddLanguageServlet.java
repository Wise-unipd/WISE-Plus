package it.unipd.dei.webapp.servlet.language;

import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Pattern;

import it.unipd.dei.webapp.servlet.AbstractDatabaseServlet;
import it.unipd.dei.webapp.utils.NameFormatter;
import org.apache.logging.log4j.message.StringFormattedMessage;

import it.unipd.dei.webapp.database.language.CreateLanguageDAO;
import it.unipd.dei.webapp.resource.Actions;
import it.unipd.dei.webapp.resource.movie.Language;
import it.unipd.dei.webapp.resource.LogContext;
import it.unipd.dei.webapp.resource.Message;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Creates a new language in the database with strict input validation.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
@WebServlet(name = "AddLanguage", value = "/languages/add-language")
public final class StaffAddLanguageServlet extends AbstractDatabaseServlet {

    /**
     * Regular expression to strictly validate language names.
     * Allows letters (including Unicode) and spaces up to 50 characters.
     */
    private static final String NAME_REGEX = "^[\\p{L}\\s\\-]{1,50}$";
    private static final Pattern NAME_PATTERN = Pattern.compile(NAME_REGEX);

    /**
     * Forwards to the language creation view.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setResource(req.getRequestURI());

        LOGGER.info("Showing language add form.");

        try {
            req.getRequestDispatcher("/auth/staff/languages/add-language.jsp").forward(req, res);
        } finally {
            LogContext.removeIPAddress();
            LogContext.removeResource();
        }
    }

    /**
     * Submits the new language to the database safely.
     */
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setAction(Actions.CREATE_LANGUAGE);

        String name = null;
        Language language = null;
        Message m = null;

        try {
            name = req.getParameter("name");
            name = (name != null) ? NameFormatter.formatName(name) : "";

            LogContext.setResource(name);
            language = new Language(name);

            // Strict Server-side validation
            m = validateInput(language);
            if (m != null) {
                LOGGER.error("Validation failed while creating the language: %s.", m.getMessage());
                forwardWithError(req, res, m);
                return;
            }

            // Database insertion
            language = new CreateLanguageDAO(getConnection(), language).access().getOutputParam();

            if (language == null) {
                LOGGER.error("Language object is null after DAO execution for create: %s.", name);
                m = new Message("The language may have been created, but the system was unable to confirm it. Please check the list.", "E0A1", null);
                forwardWithError(req, res, m);
            } else {
                LOGGER.info("Language %s (ID: %d) created successfully.", language.getName(), language.getId());
                res.sendRedirect(req.getContextPath() + "/auth/staff/languages?success=true");
            }
        } catch (SQLException e) {
            // "23505" = Unique Violation
            if ("23505".equals(e.getSQLState())) {
                m = new Message(String.format("Cannot create the language: %s already exists.", name), "E1A5", null);
                LOGGER.error(new StringFormattedMessage("Cannot create the language: %s already exists.", name), e);
            } else {
                m = new Message("Cannot create the language: unexpected database error.", "E0A1", null);
                LOGGER.error("Cannot create the language: unexpected error while accessing the database.", e);
            }
            forwardWithError(req, res, m);
        } catch (Exception e) {
            LOGGER.error("Cannot create the language: unexpected fatal error.", e);
            res.sendRedirect(req.getContextPath() + "/auth/staff/languages?error=Unexpected+error");
        } finally {
            LogContext.removeIPAddress();
            LogContext.removeAction();
            LogContext.removeResource();
        }
    }

    /**
     * Utility method for forwarding errors back to the form.
     *
     * @param req the HTTP request.
     * @param res the HTTP response.
     * @param m   the message Object.
     * @throws ServletException if a server-side error occurs.
     * @throws IOException      if any error occurs during client/server communication.
     */
    private void forwardWithError(HttpServletRequest req, HttpServletResponse res, Message m) throws ServletException, IOException {
        req.setAttribute("message", m);
        req.getRequestDispatcher("/auth/staff/languages/add-language.jsp").forward(req, res);
    }

    /**
     * Validates the inputs to prevent injection and ensure data integrity.
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