package it.unipd.dei.webapp.resource.movie;

import it.unipd.dei.webapp.resource.AbstractResource;

/**
 * Represents a genre.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class Genre extends AbstractResource {
    /**
     * The unique identifier of the genre.
     */
    private final Integer id;

    /**
     * The name of the genre.
     */
    private final String name;

    /**
     * Creates a genre with an associated id.
     *
     * @param id   the unique identifier.
     * @param name the name.
     */
    public Genre(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Creates a genre.
     *
     * @param name the name.
     */
    public Genre(String name) {
        this(null, name);
    }

    /**
     * Returns the id.
     *
     * @return the id.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Returns the name.
     *
     * @return the name.
     */
    public String getName() {
        return name;
    }
}
