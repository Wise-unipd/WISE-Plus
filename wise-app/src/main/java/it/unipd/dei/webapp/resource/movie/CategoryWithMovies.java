package it.unipd.dei.webapp.resource.movie;

import it.unipd.dei.webapp.resource.AbstractResource;

import java.util.List;
import java.util.ArrayList;

/**
 * Represents a category and its associated list of movies. Allows to provide cleaner JSON responses.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class CategoryWithMovies extends AbstractResource {
    /**
     * The category details.
     */
    private final Category category;

    /**
     * The list of movies belonging to this category.
     */
    private final List<Movie> movies;

    /**
     * Creates a new CategoryWithMovies object.
     *
     * @param category the category details.
     * @param movies   the list of movies in this category.
     */
    public CategoryWithMovies(Category category, List<Movie> movies) {
        this.category = category;
        this.movies = (movies != null) ? movies : new ArrayList<>();
    }

    /**
     * Returns the category details.
     * 
     * @return the category.
     */
    public Category getCategory() {
        return category;
    }

    /**
     * Returns the list of movies.
     * 
     * @return the list of movies.
     */
    public List<Movie> getMovies() {
        return movies;
    }
}
