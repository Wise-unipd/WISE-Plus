package it.unipd.dei.webapp.database.movie;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.unipd.dei.webapp.database.AbstractDAO;
import it.unipd.dei.webapp.resource.movie.Credit;
import it.unipd.dei.webapp.resource.movie.Genre;
import it.unipd.dei.webapp.resource.movie.Language;
import it.unipd.dei.webapp.resource.movie.Movie;
import it.unipd.dei.webapp.resource.movie.MovieDetails;
import it.unipd.dei.webapp.resource.movie.MovieFactory;
import it.unipd.dei.webapp.resource.movie.MovieRole;
import it.unipd.dei.webapp.resource.movie.Worker;

/**
 * Get the details of a movie.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class GetMovieDetailsDAO extends AbstractDAO<MovieDetails> {

    /**
     * The SQL statement(s) to be executed
     */
    private static final String STATEMENT = "SELECT m.* FROM movie_with_rating AS m WHERE m.movie_id = ? AND m.is_active = TRUE";
    private static final String STATEMENT_GENRES = "SELECT Genre.* FROM Genre INNER JOIN HasGenre ON Genre.genre_id = HasGenre.genre_id WHERE HasGenre.movie_id = ?";
    private static final String STATEMENT_AUDIOS = "SELECT Language.* FROM Language INNER JOIN Audio ON Language.lang_id = Audio.lang WHERE Audio.movie_id = ?";
    private static final String STATEMENT_SUBTITLES = "SELECT Language.* FROM Language INNER JOIN Subtitle ON Language.lang_id = Subtitle.lang WHERE Subtitle.movie_id = ?";
    private static final String STATEMENT_CREDITS = "SELECT Worker.worker_id, Worker.name AS worker_name, Worker.surname AS worker_surname, MovieRole.role_id, MovieRole.name AS role FROM Worker INNER JOIN Credit ON Worker.worker_id = Credit.worker_id INNER JOIN MovieRole ON Credit.role_id = MovieRole.role_id WHERE Credit.movie_id = ?";

    /**
     * The unique identifier of the movie
     */
    private final Integer id;

    /**
     * Creates a new object to get movie details.
     *
     * @param con the database connection.
     * @param id  the unique identifier of the movie.
     */
    public GetMovieDetailsDAO(final Connection con, final Integer id) {
        super(con);
        this.id = id;
    }

    @Override
    protected final void doAccess() throws SQLException {

        MovieDetails movieDetails = null;

        // try-with-resources
        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT)) {
            pstmt.setInt(1, id);

            // try-with-resources
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Movie movie = MovieFactory.fromDBRow(rs);

                    movieDetails = MovieDetails.builder()
                            .withMovie(movie)
                            .withGenres(getGenres())
                            .withAudios(getAudios())
                            .withSubtitles(getSubtitles())
                            .withCredits(getCredits())
                            .build();

                    LOGGER.info("The details of the movie %d has been successfully retrieved", movieDetails.getMovie().getId());
                }
            }
        }

        outputParam = movieDetails;
    }

    /**
     * Retrieves the genres associated with the movie.
     *
     * @return the list of genres.
     * @throws SQLException the exception to throw.
     */
    private final List<Genre> getGenres() throws SQLException {
        List<Genre> genres = new ArrayList<>();
        // try-with-resources
        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT_GENRES)) {
            pstmt.setInt(1, id);

            // try-with-resources
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    genres.add(new Genre(rs.getInt("genre_id"), rs.getString("name")));
                }
            }
        }

        return genres;
    }

    /**
     * Retrieves the audio languages associated with the movie.
     *
     * @return the list of audio languages.
     * @throws SQLException the exception to throw.
     */
    private final List<Language> getAudios() throws SQLException {
        List<Language> audios = new ArrayList<>();

        // try-with-resources
        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT_AUDIOS)) {
            pstmt.setInt(1, id);

            // try-with-resources
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    audios.add(new Language(rs.getInt("lang_id"), rs.getString("name")));
                }
            }
        }

        return audios;
    }

    /**
     * Retrieves the subtitle languages associated with the movie.
     *
     * @return the list of subtitle languages.
     * @throws SQLException the exception to throw.
     */
    private final List<Language> getSubtitles() throws SQLException {
        List<Language> subtitles = new ArrayList<>();

        // try-with-resources
        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT_SUBTITLES)) {
            pstmt.setInt(1, id);

            // try-with-resources
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    subtitles.add(new Language(rs.getInt("lang_id"), rs.getString("name")));
                }
            }
        }

        return subtitles;
    }

    /**
     * Retrieves the credits associated with the movie.
     *
     * @return the list of credits.
     * @throws SQLException the exception to throw.
     */
    private final List<Credit> getCredits() throws SQLException {
        List<Credit> credits = new ArrayList<>();

        // try-with-resources
        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT_CREDITS)) {
            pstmt.setInt(1, id);

            // try-with-resources
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    credits.add(new Credit(
                            new Worker(rs.getInt("worker_id"), rs.getString("worker_name"), rs.getString("worker_surname")),
                            new MovieRole(rs.getInt("role_id"), rs.getString("role"))
                    ));
                }
            }
        }

        return credits;
    }
}
