package it.unipd.dei.webapp.database.movie;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.unipd.dei.webapp.database.AbstractDAO;
import it.unipd.dei.webapp.resource.movie.Movie;
import it.unipd.dei.webapp.resource.movie.MovieFactory;

/**
 * Inserts a new movie into the database, handling the age rating and initial active status.
 * Retrieves the auto-generated ID and the full record upon successful insertion.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class CreateMovieDAO extends AbstractDAO<Movie> {

    /**
     * SQL statement to insert a movie.
     * Note: 'rating' is a foreign key to AgeRating(rating_id).
     */
    private static final String STATEMENT = "WITH inserted_movie AS ( " +
                                            "   INSERT INTO Movie (title, synopsis, release_year, running_time, rating, base_price_eur, poster_pic, trailer, is_active) " +
                                            "   VALUES (?, ?, ?, ?, ?, ?, ?, ?, FALSE) " +
                                            "   RETURNING * " +
                                            ") " +
                                            "SELECT im.*, " +
                                            "       ar.name        AS rating_name, " +
                                            "       ar.description AS rating_description, " +
                                            "       ar.age         AS rating_age " +
                                            "FROM inserted_movie AS im " +
                                            "INNER JOIN Agerating AS ar ON im.rating = ar.rating_id";

    /**
     * The movie to insert.
     */
    private final Movie movie;

    /**
     * Creates a new DAO for movie insertion.
     * 
     * @param con the database connection.
     * @param movie the movie object containing data to be stored.
     */
    public CreateMovieDAO(final Connection con, final Movie movie) {
        super(con);

        if (movie == null) {
            LOGGER.error("The movie cannot be null.");
            throw new NullPointerException("The movie cannot be null.");
        }

        this.movie = movie;
    }

    @Override
    protected void doAccess() throws SQLException {

        Movie returningMovie = null;

        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT)) {
            // Mapping Movie object fields to SQL parameters
            int i = 1;
            pstmt.setString(i++, movie.getTitle());
            pstmt.setString(i++, movie.getSynopsis());
            pstmt.setInt(i++, movie.getReleaseYear());
            pstmt.setInt(i++, movie.getRunningTime());
            pstmt.setInt(i++, movie.getAgeRating().getId()); // Foreign key to AgeRating
            pstmt.setDouble(i++, movie.getBasePriceEur());
            pstmt.setString(i++, movie.getPosterPic());
            pstmt.setString(i++, movie.getTrailer());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    returningMovie = MovieFactory.fromDBRow(rs);

                    LOGGER.info("Movie '%s' successfully created with ID %d.",
                        returningMovie.getTitle(), returningMovie.getId());
                } else {
                    LOGGER.warn("Movie inserted but not found.");
                }
            }
        }

        outputParam = returningMovie;
    }
}