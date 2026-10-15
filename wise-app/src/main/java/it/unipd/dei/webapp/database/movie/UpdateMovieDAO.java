package it.unipd.dei.webapp.database.movie;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.unipd.dei.webapp.database.AbstractDAO;
import it.unipd.dei.webapp.resource.movie.Movie;
import it.unipd.dei.webapp.resource.movie.MovieFactory;

/**
 * Updates an existing movie's core information in the database.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class UpdateMovieDAO extends AbstractDAO<Movie> {

    /**
     * SQL statement to update a movie.
     * Note: 'rating' is a foreign key to AgeRating(rating_id).
     */
    private static final String STATEMENT = "WITH updated_movie AS ( " +
                                            "   UPDATE Movie SET title = ?, synopsis = ?, release_year = ?, running_time = ?, " +
                                            "   rating = ?, base_price_eur = ?, poster_pic = ?, trailer = ? " +
                                            "   WHERE movie_id = ? " +
                                            "   RETURNING * " +
                                            ") " +
                                            "SELECT um.*, " +
                                            "       ar.name        AS rating_name, " +
                                            "       ar.description AS rating_description, " +
                                            "       ar.age         AS rating_age " +
                                            "FROM updated_movie AS um " +
                                            "INNER JOIN Agerating AS ar ON um.rating = ar.rating_id";

    /**
     * The movie to update.
     */
    private final Movie movie;

    /**
     * Creates a new DAO for movie update.
     * 
     * @param con the database connection.
     * @param movie the movie object containing data to be stored.
     */
    public UpdateMovieDAO(final Connection con, final Movie movie) {
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
            int i = 1;
            pstmt.setString(i++, movie.getTitle());
            pstmt.setString(i++, movie.getSynopsis());
            pstmt.setInt(i++, movie.getReleaseYear());
            pstmt.setInt(i++, movie.getRunningTime());
            pstmt.setInt(i++, movie.getAgeRating().getId());
            pstmt.setDouble(i++, movie.getBasePriceEur());
            pstmt.setString(i++, movie.getPosterPic());
            pstmt.setString(i++, movie.getTrailer());
            pstmt.setInt(i++, movie.getId());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    returningMovie = MovieFactory.fromDBRow(rs);
                    LOGGER.info("Movie '%s' (ID: %d) successfully updated.", returningMovie.getTitle(), returningMovie.getId());
                } else {
                    LOGGER.warn("Movie with ID %d not found for update.", movie.getId());
                }
            }
        }

        outputParam = returningMovie;
    }
}