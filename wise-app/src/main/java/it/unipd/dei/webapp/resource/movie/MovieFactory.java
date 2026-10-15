package it.unipd.dei.webapp.resource.movie;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Manages the construction of a complex movie object from external resources.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class MovieFactory {
    /**
     * Creates a new movie from a database row.
     *
     * @param rs the result set.
     * @return the movie.
     * @throws SQLException if an error occurs while reading the result set.
     */
    public static Movie fromDBRow(ResultSet rs) throws SQLException {

        AgeRating ageRating = new AgeRating(
                rs.getInt("rating"),
                rs.getString("rating_name"),
                rs.getString("rating_description"),
                rs.getInt("rating_age")
        );

        return Movie.builder()
                .withId(rs.getInt("movie_id"))
                .withTitle(rs.getString("title"))
                .withSynopsis(rs.getString("synopsis"))
                .withReleaseYear(rs.getInt("release_year"))
                .withRunningTime(rs.getInt("running_time"))
                .withAgeRating(ageRating)
                .withBasePriceEur(rs.getDouble("base_price_eur"))
                .withPosterPic(rs.getString("poster_pic"))
                .withTrailer(rs.getString("trailer"))
                .withIsActive(rs.getBoolean("is_active"))
                .build();
    }
}
