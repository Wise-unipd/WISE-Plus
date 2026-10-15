package it.unipd.dei.webapp.servlet.genre;

import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Pattern;

import it.unipd.dei.webapp.servlet.AbstractDatabaseServlet;
import it.unipd.dei.webapp.utils.NameFormatter;
import org.apache.logging.log4j.message.StringFormattedMessage;

import it.unipd.dei.webapp.database.genre.CreateGenreDAO;
import it.unipd.dei.webapp.resource.Actions;
import it.unipd.dei.webapp.resource.movie.Genre;
import it.unipd.dei.webapp.resource.LogContext;
import it.unipd.dei.webapp.resource.Message;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Creates a new genre in the database with strict input sanitization.
 * Relies on database constraints to prevent duplicate entries.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
@WebServlet(name = "AddGenre", value = "/genres/add-genre")
public final class StaffAddGenreServlet extends AbstractDatabaseServlet {

    /**
     * Regular expression to strictly validate genre names.
     * Allows letters (including Unicode), spaces, and hyphens up to 50 characters.
     * Prevents script injection and invalid characters.
     */
    private static final String NAME_REGEX = "^[\\p{L}\\s\\-]{1,50}$";
    private static final Pattern NAME_PATTERN = Pattern.compile(NAME_REGEX);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setResource(req.getRequestURI());

        LOGGER.info("Showing genre add form.");

        try {
            req.getRequestDispatcher("/auth/staff/genres/add-genre.jsp").forward(req, res);
        } finally {
            LogContext.removeIPAddress();
            LogContext.removeResource();
        }
    }

    /**
     * Submits the new genre to the database safely.
     */
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setAction(Actions.CREATE_GENRE);

        String name = null;
        Genre genre = null;
        Message m = null;

        try {
            name = req.getParameter("name");
            name = (name != null) ? NameFormatter.formatName(name) : "";

            LogContext.setResource(name);
            genre = new Genre(name);

            // Strict Server-side validation
            m = validateInput(genre);

            if (m != null) {
                LOGGER.error("Validation failed while creating genre: %s.", m.getMessage());
                forwardWithError(req, res, m);
                return;
            }

            // Attempt database insertion. Will throw SQLException state 23505 if name exists.
            genre = new CreateGenreDAO(getConnection(), genre).access().getOutputParam();

            if (genre == null) {
                LOGGER.error("Genre object is null after DAO execution for create: %s.", name);
                m = new Message("The genre may have been created, but the system was unable to confirm it. Please check the list.", "E0A1", null);
                forwardWithError(req, res, m);
            } else {
                LOGGER.info("Genre %s (ID: %d) created successfully in the database.", genre.getName(), genre.getId());
                res.sendRedirect(req.getContextPath() + "/auth/staff/genres?success=true");
            }
        } catch (SQLException e) {
            // "23505" = Unique Violation (Duplicate name)
            if ("23505".equals(e.getSQLState())) {
                m = new Message(String.format("Cannot create the genre: %s already exists.", name), "E1A5", null);
                LOGGER.error(new StringFormattedMessage("Cannot create the genre: %s already exists.", name), e);
            } else {
                m = new Message("Cannot create the genre: unexpected database error.", "E0A1", null);
                LOGGER.error("Cannot create the genre: unexpected error while accessing the database.", e);
            }
            forwardWithError(req, res, m);
        } catch (Exception e) {
            LOGGER.error("Cannot create the genre: unexpected fatal error.", e);
            res.sendRedirect(req.getContextPath() + "/auth/staff/genres?error=Unexpected+error");
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
        req.getRequestDispatcher("/auth/staff/genres/add-genre.jsp").forward(req, res);
    }

    /**
     * Validates the genre inputs to prevent injection and ensure data integrity.
     * 
     * @param genre the object to validate.
     * @return a Message object containing the error, or null if valid.
     */
    private Message validateInput(Genre genre) {
        if (genre.getName() == null || genre.getName().isEmpty()) {
            return new Message("Genre name cannot be empty.", "E1A1", null);
        }

        if (!NAME_PATTERN.matcher(genre.getName()).matches()) {
            return new Message("Invalid genre name format. Only letters, spaces, and hyphens are allowed (max 50 characters).", "E1A1", null);
        }

        return null;
    }
}