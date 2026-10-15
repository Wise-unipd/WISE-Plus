package it.unipd.dei.webapp.database.movie;

import it.unipd.dei.webapp.database.AbstractDAO;
import it.unipd.dei.webapp.resource.movie.Movie;
import it.unipd.dei.webapp.resource.movie.MovieFactory;
import it.unipd.dei.webapp.resource.movie.PaginatedMovieList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Searches movies by title, genre, or worker name/surname using a text query.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class SearchMoviesByTextDAO extends AbstractDAO<PaginatedMovieList> {

    /**
     * The template of the SQL statement to be executed.
     */
    private static final String QUERY_TEMPLATE =
            "SELECT m.*, " +
                    "combined.best_rank, " +
                    "COUNT(*) OVER() AS total_count " +
                    "FROM movie_with_rating AS m " +
                    "INNER JOIN (" +
                    "   SELECT movie_id, MIN(rank) as best_rank " +
                    "   FROM (" +
                    "       /* Rank 1: Title matches */ " +
                    "       SELECT movie_id, 1 AS rank FROM Movie WHERE title ILIKE ? %FILTER% " +
                    "       UNION ALL " +
                    "       /* Rank 2: Worker matches (Name, Surname, or Full Name) */ " +
                    "       SELECT m.movie_id, 2 AS rank " +
                    "       FROM Movie m JOIN Credit c ON m.movie_id = c.movie_id " +
                    "       INNER JOIN Worker w ON c.worker_id = w.worker_id " +
                    "       WHERE (" +
                    "           w.name ILIKE ? OR " +
                    "           w.surname ILIKE ? OR " +
                    "           (w.name || ' ' || w.surname) ILIKE ? OR " +
                    "           (w.surname || ' ' || w.name) ILIKE ? " +
                    "       ) %FILTER_M% " +
                    "       UNION ALL " +
                    "       /* Rank 3: Genre matches */ " +
                    "       SELECT m.movie_id, 3 AS rank " +
                    "       FROM Movie m JOIN HasGenre hg ON m.movie_id = hg.movie_id " +
                    "       INNER JOIN Genre g ON hg.genre_id = g.genre_id" +
                    "       WHERE g.name ILIKE ? %FILTER_M% " +
                    "   ) AS all_ranks " +
                    "   GROUP BY movie_id " +
                    "   ) AS combined ON m.movie_id = combined.movie_id " +
                    "WHERE 1 = 1 %FILTER_M% " +
                    "ORDER BY combined.best_rank ASC, m.title ASC " +
                    "LIMIT ? OFFSET ?"; // Sort by rank, then alphabetically

    /**
     * The SQL statement to be executed.
     */
    private final String query;

    /**
     * Search terms used to filter movies.
     */
    private final String searchTerms;

    /**
     * The maximum number of movies to retrieve
     */
    private final int limit;

    /**
     * The number of movies to skip before starting to count towards the limit
     */
    private final int offset;

    /**
     * Creates a new object to search for movies in an advanced way.
     *
     * @param con         the database connection.
     * @param searchTerms the search terms.
     * @param limit       the number of rows to return.
     * @param offset      the number of rows to skip.
     * @param onlyActive  whether to restrict the search.
     */
    public SearchMoviesByTextDAO(final Connection con, final String searchTerms, final int limit, final int offset, final boolean onlyActive) {
        super(con);
        this.searchTerms = (searchTerms == null) ? "%" : "%" + searchTerms + "%";
        this.limit = limit;
        this.offset = offset;
        
        this.query = QUERY_TEMPLATE
            .replace("%FILTER%", 
                onlyActive ? "AND is_active = TRUE" : "")
            .replace("%FILTER_M%", 
                onlyActive ? "AND m.is_active = TRUE" : "");
    }

    @Override
    protected final void doAccess() throws SQLException {
        final List<Movie> movies = new ArrayList<>();
        int totalCount = 0;

        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            int i = 1;
            // 1. Title match
            pstmt.setString(i++, searchTerms);

            // 2. Worker matches (4 placeholders)
            pstmt.setString(i++, searchTerms); // w.name
            pstmt.setString(i++, searchTerms); // w.surname
            pstmt.setString(i++, searchTerms); // w.name || ' ' || w.surname
            pstmt.setString(i++, searchTerms); // w.surname || ' ' || w.name

            // 3. Genre match
            pstmt.setString(i++, searchTerms);

            // Limit and offset
            pstmt.setInt(i++, limit);
            pstmt.setInt(i++, offset);

            // Execute the query
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // Consider the total count only once
                    if (movies.isEmpty()) {
                        totalCount = rs.getInt("total_count");
                    }
                    movies.add(MovieFactory.fromDBRow(rs));
                }
            }
        }
        outputParam = new PaginatedMovieList(movies, totalCount);
    }
}
