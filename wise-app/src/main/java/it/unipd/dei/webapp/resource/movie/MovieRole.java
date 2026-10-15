package it.unipd.dei.webapp.resource.movie;

import it.unipd.dei.webapp.resource.AbstractResource;

/**
 * Represents a movie role.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class MovieRole extends AbstractResource {
    /**
     * The unique identifier of the role.
     */
    private final Integer id;

    /**
     * The name of the role.
     */
    private final String name;

    /**
     * Creates a role with an associated id.
     *
     * @param id   the unique identifier.
     * @param name the name.
     */
    public MovieRole(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Creates a movie role.
     *
     * @param name the name.
     */
    public MovieRole(String name) {
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
