package it.unipd.dei.webapp.resource.movie;

import it.unipd.dei.webapp.resource.AbstractResource;

import java.util.List;

/**
 * Represents a paginated list of movies.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class PaginatedMovieList extends AbstractResource {
    /**
     * The list of movies belonging to this page.
     */
    private final List<Movie> movies;

    /**
     * The total number of movies in the whole context.
     */
    private final int totalCount;

    /**
     * Creates a new paginated list of movies.
     *
     * @param movies     the list of movies for the current page.
     * @param totalCount the total number of movies in the whole context.
     */
    public PaginatedMovieList(final List<Movie> movies, final int totalCount) {
        this.movies = movies;
        this.totalCount = totalCount;
    }

    /**
     * Returns the list of movies.
     *
     * @return the list of movies
     */
    public List<Movie> getMovies() {
        return movies;
    }

    /**
     * Returns the absolute total count.
     *
     * @return the absolute total count.
     */
    public int getTotalCount() {
        return totalCount;
    }
}
