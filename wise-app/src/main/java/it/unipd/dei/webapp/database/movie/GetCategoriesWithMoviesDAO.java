package it.unipd.dei.webapp.database.movie;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.unipd.dei.webapp.database.AbstractDAO;
import it.unipd.dei.webapp.resource.movie.Category;
import it.unipd.dei.webapp.resource.movie.CategoryWithMovies;
import it.unipd.dei.webapp.resource.movie.Movie;
import it.unipd.dei.webapp.resource.movie.MovieFactory;

/**
 * Retrieves a movie catalog from the database.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class GetCategoriesWithMoviesDAO extends AbstractDAO<List<CategoryWithMovies>> {
    /**
     * The SQL statement(s) to be executed
     */
    private static final String STATEMENT_CATEGORIES =
            "SELECT c.category_id, c.name, c.description, c.is_active " +
                    "FROM Category AS c INNER JOIN HasCategory AS hc ON c.category_id = hc.category_id " +
                    "WHERE c.is_active = true " +
                    "GROUP BY c.category_id, c.name, c.description, c.is_active " +
                    "ORDER BY COUNT(*) DESC, c.name ASC " +
                    "LIMIT ?";
    private static final String STATEMENT_MOVIES =
            "SELECT m.* " +
                    "FROM movie_with_rating AS m " +
                    "INNER JOIN HasCategory AS hc ON m.movie_id = hc.movie_id " +
                    "WHERE hc.category_id = ? AND m.is_active = true " +
                    "ORDER BY m.title ASC " +
                    "LIMIT ?";

    /**
     * The maximum number of categories to retrieve
     */
    private final int category_limit;

    /**
     * The maximum number of movies to retrieve for each category
     */
    private final int movie_limit;

    /**
     * Creates a new object to get a movie catalog from the database.
     *
     * @param con            the database connection.
     * @param category_limit the number of category rows to return.
     * @param movie_limit    the number of movie rows to return.
     */
    public GetCategoriesWithMoviesDAO(final Connection con, int category_limit, int movie_limit) {
        super(con);
        this.category_limit = category_limit;
        this.movie_limit = movie_limit;
    }

    @Override
    protected final void doAccess() throws SQLException {
        List<CategoryWithMovies> collections = new ArrayList<>();

        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT_CATEGORIES)) {
            pstmt.setInt(1, category_limit);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Category category = new Category(
                            rs.getInt("category_id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getBoolean("is_active")
                    );

                    List<Movie> movies = getMovies(category);

                    collections.add(new CategoryWithMovies(category, movies));
                }

                LOGGER.info("%d categories have been successfully retrieved (requested limit: %d", collections.size(), category_limit);
            }
        }

        outputParam = collections;
    }

    /**
     * Retrieves the movies associated with the category.
     *
     * @param category the category.
     * @return the list of movies.
     * @throws SQLException the exception to throw.
     */
    private final List<Movie> getMovies(Category category) throws SQLException {
        List<Movie> movies = new ArrayList<>();

        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT_MOVIES)) {
            pstmt.setInt(1, category.getId());
            pstmt.setInt(2, movie_limit);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    movies.add(MovieFactory.fromDBRow(rs));
                }
            }
        }

        return movies;
    }
}
