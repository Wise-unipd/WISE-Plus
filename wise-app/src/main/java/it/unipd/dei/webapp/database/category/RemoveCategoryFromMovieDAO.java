package it.unipd.dei.webapp.database.category;

import it.unipd.dei.webapp.database.AbstractDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Remove a category from a movie.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class RemoveCategoryFromMovieDAO extends AbstractDAO<Boolean> {

    /**
     * The SQL statement to be executed.
     */
    private static final String STATEMENT = "DELETE FROM HasCategory WHERE movie_id = ? AND category_id = ?";

    /**
     * The unique identifier of the movie.
     */
    private final int movieId;

    /**
     * The unique identifier of the category.
     */
    private final int categoryId;

    /**
     * Creates a new object to remove a category from a movie.
     *
     * @param con        the database connection.
     * @param movieId    the unique identifier of the movie.
     * @param categoryId the unique identifier of the category.
     */
    public RemoveCategoryFromMovieDAO(final Connection con, final int movieId, final int categoryId) {
        super(con);
        this.movieId = movieId;
        this.categoryId = categoryId;
    }

    @Override
    protected final void doAccess() throws SQLException {

        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT)) {
            pstmt.setInt(1, movieId);
            pstmt.setInt(2, categoryId);

            // returns the number of rows changed
            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                outputParam = true;
                LOGGER.info("Category %d successfully removed from movie %d.", categoryId, movieId);
            } else {
                outputParam = false;
                LOGGER.warn("Failed to remove category %d from movie %d.", categoryId, movieId);
            }
        }
    }
}
