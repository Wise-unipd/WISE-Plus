package it.unipd.dei.webapp.servlet.movie;

import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Pattern;

import it.unipd.dei.webapp.database.movie.CreateMovieDAO;
import it.unipd.dei.webapp.database.agerating.GetAllAgeRatingsDAO;
import it.unipd.dei.webapp.database.movie.SearchMovieByIdDAO;
import it.unipd.dei.webapp.database.movie.UpdateMovieDAO;
import it.unipd.dei.webapp.resource.Actions;
import it.unipd.dei.webapp.resource.movie.AgeRating;
import it.unipd.dei.webapp.resource.LogContext;
import it.unipd.dei.webapp.resource.Message;
import it.unipd.dei.webapp.resource.movie.Movie;
import it.unipd.dei.webapp.servlet.AbstractDatabaseServlet;
import it.unipd.dei.webapp.utils.ImageHelper;
import it.unipd.dei.webapp.utils.NameFormatter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

/**
 * Handles both the creation of new movies and the modification of existing ones.
 * <p>
 * <strong>Architecture Note on Input Parsing:</strong><br>
 * Even though the form is submitted as {@code multipart/form-data} (which is required for file uploads and
 * sends the payload as a complex raw byte stream), we do not need to manually read the {@code InputStream}.
 * Because this servlet is annotated with {@code @MultipartConfig}, the Servlet Container (Tomcat)
 * automatically intercepts and parses the multipart byte stream for us. This allows us to conveniently 
 * extract standard text fields using {@code req.getParameter()} and binary file data using 
 * {@code req.getPart()}, abstracting away boundary detection and character encoding complexities.
 * </p>
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 10)
public final class StaffSaveMovieServlet extends AbstractDatabaseServlet {

    /**
     * Regex for basic validation: alphanumeric, spaces, and basic punctuation for titles/synopsis
     */
    // TO-DO: some movies have strange titles (e.g., WALL•E)
    //private static final String TEXT_REGEX = "^[\\p{L}\\p{N}\\s\\-:,.'!?]{1,255}$";
    //private static final Pattern TEXT_PATTERN = Pattern.compile(TEXT_REGEX);

    /**
     * Regex for YouTube ID (Exactly 11 characters, alphanumeric, dashes, and underscores)
     */
    private static final String YOUTUBE_ID_REGEX = "^[a-zA-Z0-9_-]{11}$";
    private static final Pattern YOUTUBE_ID_PATTERN = Pattern.compile(YOUTUBE_ID_REGEX);

    /**
     * Forwards to the movie creation/update view.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setResource(req.getRequestURI());

        LOGGER.info("Showing movie save form.");

        try {
            String idParam = req.getParameter("id");
            if (idParam != null && !idParam.trim().isEmpty()) {
                int movieId = Integer.parseInt(idParam);
                Movie movie = new SearchMovieByIdDAO(getConnection(), movieId).access().getOutputParam();

                if (movie == null) {
                    LOGGER.error("Movie with ID %d not found for editing.", movieId);
                    res.sendRedirect(req.getContextPath() + "/auth/staff/movies?error=Movie+not+found");
                    return;
                }
                req.setAttribute("movie", movie);
            }

            // Always fetch age ratings for the dropdown
            req.setAttribute("ageRatings", new GetAllAgeRatingsDAO(getConnection()).access().getOutputParam());

            // Forward to the unified form
            req.getRequestDispatcher("/auth/staff/movies/movie-form.jsp").forward(req, res);

        } catch (NumberFormatException e) {
            LOGGER.error("Cannot retrieve the movie for editing. Invalid ID.", e);
            res.sendRedirect(req.getContextPath() + "/auth/staff/movies?error=Invalid+Movie+ID");
        } catch (SQLException e) {
            LOGGER.error("Cannot load data for saving movie: database error.", e);
            res.sendRedirect(req.getContextPath() + "/auth/staff/movies?error=Database+error");
        } finally {
            LogContext.removeIPAddress();
            LogContext.removeResource();
        }
    }

    /**
     * Persists the changes to the database.
     */
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        LogContext.setIPAddress(req.getRemoteAddr());

        String idParam = req.getParameter("id");
        boolean isEditMode = (idParam != null && !idParam.trim().isEmpty());
        Integer movieId = null;
        String title = null;
        String oldPosterPath = null;
        String newPosterPath = null;
        Movie oldMovie = null;
        Movie movie = null;
        Message m = null;

        LogContext.setAction(isEditMode ? Actions.EDIT_MOVIE : Actions.CREATE_MOVIE);

        try {
            if (isEditMode) {
                // check the ID first
                try {
                    movieId = Integer.parseInt(idParam);
                } catch (NumberFormatException e) {
                    LOGGER.error("Movie update failed: ID is missing or not valid.", e);
                    res.sendRedirect(req.getContextPath() + "/auth/staff/movies?error=Missing+or+Invalid+ID");
                    return;
                }

                oldMovie = new SearchMovieByIdDAO(getConnection(), movieId).access().getOutputParam();

                if (oldMovie == null) {
                    LOGGER.error("Update failed: movie with ID %d not found.", movieId);
                    res.sendRedirect(req.getContextPath() + "/auth/staff/movies?error=Movie+not+found");
                    return;
                }
                
                oldPosterPath = oldMovie.getPosterPic();
                req.setAttribute("movie", oldMovie);
                LogContext.setResource(oldMovie.getTitle() + " (ID: " + oldMovie.getId() + ")");
            }

            // Parse and validate new data
            try {
                // Build the movie object
                movie = buildMovieFromRequest(req, movieId, oldPosterPath);
                title = movie.getTitle();

                if (!isEditMode) {
                    LogContext.setResource(title);
                }

                // Validate standard text/number inputs
                m = validateInput(movie);
                if (m != null) {
                    LOGGER.error("Validation failed while saving the movie: %s.", m.getMessage());
                    forwardWithError(req, res, m);
                    return;
                }

                // Handle Image Validation
                Part filePart = req.getPart("posterPic");
                boolean isNewFileUploaded = (filePart != null && filePart.getSize() > 0);
                
                // If creating, image is strictly required. If editing, it's optional.
                if (!isEditMode && !isNewFileUploaded) {
                    m = new Message("A poster image is required for new movies.", "E1A1", null);
                    LOGGER.error("Validation failed while creating the movie: %s.", m.getMessage());
                    forwardWithError(req, res, m);
                    return;
                }

                // Validate dimensions if a file was actually uploaded            
                if (isNewFileUploaded) {
                    m = ImageHelper.validatePoster(filePart);
                    if (m != null) {
                        LOGGER.error("Validation failed while saving the movie: %s.", m.getMessage());
                        forwardWithError(req, res, m);
                        return;
                    }

                    newPosterPath = ImageHelper.savePoster(filePart);

                    movie = Movie.builder()
                        .withId(movie.getId())
                        .withTitle(movie.getTitle())
                        .withSynopsis(movie.getSynopsis())
                        .withReleaseYear(movie.getReleaseYear())
                        .withRunningTime(movie.getRunningTime())
                        .withBasePriceEur(movie.getBasePriceEur())
                        .withAgeRating(movie.getAgeRating())
                        .withTrailer(movie.getTrailer())
                        .withPosterPic(newPosterPath)
                        .build();
                }

                // Execute Database Operations
                if (isEditMode) {
                    movie = new UpdateMovieDAO(getConnection(), movie).access().getOutputParam();

                    if (isNewFileUploaded && oldPosterPath != null) {
                        ImageHelper.deletePoster(oldPosterPath);
                    }
                } else {
                    // Handle Create
                    movie = new CreateMovieDAO(getConnection(), movie).access().getOutputParam();
                }

                if (movie == null) {
                    LOGGER.error("Movie object is null after DAO execution for save: %s.", title);
                    forwardWithError(req, res, new Message("The movie data may have been saved, but the system was unable to confirm it. Please check the list.", "E0A1", null));
                } else {
                    LOGGER.info("Movie %s (ID: %d) saved successfully.", movie.getTitle(), movie.getId());
                    res.sendRedirect(req.getContextPath() + "/auth/staff/movies?success=true");
                }

            } catch (NumberFormatException e) {
                LOGGER.error("Cannot save the movie: wrong number format(s).", e);
                forwardWithError(req, res, new Message("Numeric fields (Year, Time, Price) must be valid numbers.", "E1A1", null));
            } catch (SQLException e) {
                if (newPosterPath != null) {
                    ImageHelper.deletePoster(newPosterPath);
                }

                m = new Message("Cannot save the movie due to a database error.", "E0A1", null);
                LOGGER.error("Cannot save the movie: unexpected error while accessing the database.", e);
                forwardWithError(req, res, m);
            }
        } catch (Exception e) {
            LOGGER.error("Cannot save the movie: unexpected fatal error.", e);
            res.sendRedirect(req.getContextPath() + "/auth/staff/movies?error=Unexpected+error");
        } finally {
            LogContext.removeIPAddress();
            LogContext.removeAction();
            LogContext.removeResource();
        }
    }

    /**
     * Re-populates necessary form data and forwards back to the JSP when an error occurs.
     *
     * @param req   The HTTP request.
     * @param res   The HTTP response.
     * @param m     The error message.
     */
    private void forwardWithError(HttpServletRequest req, HttpServletResponse res, Message m) throws ServletException, IOException {
        try {
            req.setAttribute("ageRatings", new GetAllAgeRatingsDAO(getConnection()).access().getOutputParam());
        } catch (SQLException e) {
            LOGGER.error("Could not reload age ratings for error view.", e);
            res.sendRedirect(req.getContextPath() + "/auth/staff/movies?error=Database+error");
        }
        req.setAttribute("message", m);
        req.getRequestDispatcher("/auth/staff/movies/movie-form.jsp").forward(req, res);
    }

    /**
     * Validates the standard inputs required for Movie creation/modification.
     *
     * @param movie The movie payload object.
     * @return null if valid, or a Message describing the error.
     */
    private Message validateInput(Movie movie) {
        if (movie.getTitle() == null || movie.getTitle().isEmpty()) {
            return new Message("Movie title cannot be empty.", "E1A1", null);
        }
        /* if (!TEXT_PATTERN.matcher(movie.getTitle()).matches()) {
            return new Message("Invalid title format. Contains unsupported characters.", "E1A1", null);
        } */
        if (movie.getSynopsis() == null || movie.getSynopsis().isEmpty()) {
            return new Message("Movie synopsis cannot be empty.", "E1A1", null);
        }
        if (movie.getReleaseYear() < 1888 || movie.getReleaseYear() > 2100) {
            return new Message("Invalid release year.", "E1A1", null);
        }
        if (movie.getRunningTime() <= 0 || movie.getRunningTime() > 1000) {
            return new Message("Invalid running time.", "E1A1", null);
        }
        if (movie.getBasePriceEur() < 0) {
            return new Message("Price cannot be negative.", "E1A1", null);
        }
        if (movie.getAgeRating() == null || movie.getAgeRating().getId() == null || movie.getAgeRating().getId() <= 0) {
            return new Message("A valid age rating must be selected.", "E1A1", null);
        }
        if (movie.getTrailer() == null || movie.getTrailer().isEmpty()) {
            return new Message("Trailer missing.", "E1A1", null);
        }
        if (!YOUTUBE_ID_PATTERN.matcher(movie.getTrailer()).matches()) {
            return new Message("Invalid trailer format. Please provide only the 11-character YouTube ID (e.g., dQw4w9WgXcQ).", "E1A1", null);
        }
        return null;
    }

    /**
     * Builds a Movie object from an HTTP request.
     * 
     * @param req           the HTTP request.
     * @param movieId       the unique identifier of the movie.
     * @param oldPosterPath the poster path.
     * @return a Movie object.
     */
    private final Movie buildMovieFromRequest(HttpServletRequest req, Integer movieId, String oldPosterPath) {
        String synopsis = req.getParameter("synopsis");
        synopsis = (synopsis != null) ? synopsis.trim() : "";
        String trailer = req.getParameter("trailer");
        trailer = (trailer != null) ? trailer.trim() : "";

        return Movie.builder()
            .withId(movieId)
            .withTitle(NameFormatter.formatName(req.getParameter("title")))
            .withSynopsis(synopsis)
            .withReleaseYear(Integer.parseInt(req.getParameter("releaseYear")))
            .withRunningTime(Integer.parseInt(req.getParameter("runningTime")))
            .withBasePriceEur(Double.parseDouble(req.getParameter("basePriceEur")))
            .withAgeRating(new AgeRating(Integer.parseInt(req.getParameter("rating"))))
            .withTrailer(trailer)
            .withPosterPic(oldPosterPath)
            .build();
    }
}