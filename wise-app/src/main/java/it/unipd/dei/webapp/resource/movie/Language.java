package it.unipd.dei.webapp.resource.movie;

import it.unipd.dei.webapp.resource.AbstractResource;

/**
 * Represents a language.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class Language extends AbstractResource {
    /**
     * The unique identifier of the language.
     */
    private final Integer id;

    /**
     * The name of the language.
     */
    private final String name;

    /**
     * Creates a language with an associated id.
     *
     * @param id   the unique identifier.
     * @param name the name.
     */
    public Language(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Creates a language.
     *
     * @param name the name.
     */
    public Language(String name) {
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
