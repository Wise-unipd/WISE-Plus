package it.unipd.dei.webapp.servlet.genre;

import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Pattern;

import it.unipd.dei.webapp.servlet.AbstractDatabaseServlet;
import it.unipd.dei.webapp.utils.NameFormatter;
import org.apache.logging.log4j.message.StringFormattedMessage;

import it.unipd.dei.webapp.database.genre.UpdateGenreDAO;
import it.unipd.dei.webapp.database.genre.SearchGenreByIdDAO;
import it.unipd.dei.webapp.resource.Actions;
import it.unipd.dei.webapp.resource.movie.Genre;
import it.unipd.dei.webapp.resource.LogContext;
import it.unipd.dei.webapp.resource.Message;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Handles the modification of an existing genre with strict sanitization.
 * Catching SQL state 23505 protects against renaming a genre to one that already exists.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
@WebServlet(name = "EditGenre", value = "/genres/edit-genre")
public final class StaffEditGenreServlet extends AbstractDatabaseServlet {

    /**
     * Regular expression to strictly validate genre names.
     */
    private static final String NAME_REGEX = "^[\\p{L}\\s\\-]{1,50}$";
    private static final Pattern NAME_PATTERN = Pattern.compile(NAME_REGEX);

    /**
     * Handles the HTTP GET request to display the edit form with pre-filled data.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setResource(req.getRequestURI());

        LOGGER.info("Showing genre update form.");

        try {
            String idParam = req.getParameter("id");
            if (idParam == null || idParam.trim().isEmpty()) {
                LOGGER.error("Cannot retrieve genre for editing: ID is missing.");
                res.sendRedirect(req.getContextPath() + "/auth/staff/genres?error=Missing+Genre+ID");
                return;
            }

            int genreId = Integer.parseInt(idParam);
            Genre genre = new SearchGenreByIdDAO(getConnection(), genreId).access().getOutputParam();

            if (genre == null) {
                LOGGER.error("Genre with ID %d not found for editing.", genreId);
                res.sendRedirect(req.getContextPath() + "/auth/staff/genres?error=Genre+not+found");
                return;
            }

            req.setAttribute("genre", genre);
            req.getRequestDispatcher("/auth/staff/genres/edit-genre.jsp").forward(req, res);

        } catch (NumberFormatException e) {
            LOGGER.error("Cannot retrieve genre for editing: ID is not valid.", e);
            res.sendRedirect(req.getContextPath() + "/auth/staff/genres?error=Invalid+Genre+ID");
        } catch (SQLException e) {
            LOGGER.error("Cannot retrieve genre for editing: database error.", e);
            res.sendRedirect(req.getContextPath() + "/auth/staff/genres?error=Database+error");
        } finally {
            LogContext.removeIPAddress();
            LogContext.removeResource();
        }
    }

    /**
     * Handles the HTTP POST request to apply updates to a genre.
     */
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setAction(Actions.EDIT_GENRE);

        int genreId = -1;
        String name = null;
        Genre oldGenre = null;
        Genre genre = null;
        Message m = null;

        try {
            // check the ID first
            try {
                genreId = Integer.parseInt(req.getParameter("id"));
            } catch (NumberFormatException e) {
                LOGGER.error("Genre update failed: ID is missing or not valid.", e);
                res.sendRedirect(req.getContextPath() + "/auth/staff/genres?error=Missing+or+Invalid+ID");
                return;
            }

            oldGenre = new SearchGenreByIdDAO(getConnection(), genreId).access().getOutputParam();

            if (oldGenre == null) {
                LOGGER.error("Update failed: genre with ID %d not found.", genreId);
                res.sendRedirect(req.getContextPath() + "/auth/staff/genres?error=Genre+not+found");
                return;
            }

            req.setAttribute("genre", oldGenre);
            LogContext.setResource(oldGenre.getName() + " (ID: " + oldGenre.getId() + ")");

            // Parse and validate new data
            try {
                name = req.getParameter("name");
                name = (name != null) ? NameFormatter.formatName(name) : "";

                genre = new Genre(genreId, name);

                // Strict Server-side validation
                m = validateInput(genre);
                if (m != null) {
                    LOGGER.error("Validation failed while updating genre: %s.", m.getMessage());
                    forwardWithError(req, res, m);
                    return;
                }

                // Attempt update. Throws SQL 23505 if name already exists for another ID.
                genre = new UpdateGenreDAO(getConnection(), genre).access().getOutputParam();

                if (genre == null) {
                    LOGGER.error("Genre object is null after DAO execution for update: %s.", name);
                    m = new Message("An unexpected error occurred while updating the genre. Please try again or check the list.", "E0A1", null);
                    forwardWithError(req, res, m);
                } else {
                    LOGGER.info("Genre %s (ID: %d) successfully updated.", genre.getName(), genre.getId());
                    res.sendRedirect(req.getContextPath() + "/auth/staff/genres?success=true");
                }
            } catch (SQLException e) {
                // "23505" = Unique Violation
                if ("23505".equals(e.getSQLState())) {
                    m = new Message(String.format("Cannot update the genre: %s already exists.", name), "E1A5", null);
                    LOGGER.error(new StringFormattedMessage("Cannot update the genre: %s already exists ", name), e);
                } else {
                    m = new Message("Cannot update the genre due to a database error.", "E0A1", null);
                    LOGGER.error("Cannot update the genre: unexpected error while accessing the database.", e);
                }
                forwardWithError(req, res, m);
            }
        } catch (Exception e) {
            LOGGER.error("Cannot update the genre: unexpected fatal error.", e);
            res.sendRedirect(req.getContextPath() + "/auth/staff/genres?error=Unexpected+error");
        } finally {
            LogContext.removeIPAddress();
            LogContext.removeAction();
            LogContext.removeResource();
        }
    }

    /**
     * Utility method for forwarding errors back to the edit form.
     *
     * @param req   the HTTP request.
     * @param res   the HTTP response.
     * @param m     the message Object.
     * @throws ServletException if a server-side error occurs.
     * @throws IOException      if any error occurs during client/server communication.
     */
    private void forwardWithError(HttpServletRequest req, HttpServletResponse res, Message m) throws ServletException, IOException {
        req.setAttribute("message", m);
        req.getRequestDispatcher("/auth/staff/genres/edit-genre.jsp").forward(req, res);
    }

    /**
     * Validates input fields to prevent injection.
     *
     * @param genre the object to validate.
     * @return a Message object containing the error, or null if valid.
     */
    private Message validateInput(Genre genre) {
        if (genre.getName() == null || genre.getName().isEmpty()) {
            return new Message("Name missing.", "E1A1", null);
        }

        if (!NAME_PATTERN.matcher(genre.getName()).matches()) {
            return new Message("Invalid genre name format. Only letters, spaces, and hyphens are allowed (max 50 characters).", "E1A1", null);
        }

        return null;
    }
}