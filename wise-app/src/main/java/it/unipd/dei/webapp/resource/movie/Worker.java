package it.unipd.dei.webapp.resource.movie;

import it.unipd.dei.webapp.resource.AbstractResource;

/**
 * Represents a worker.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class Worker extends AbstractResource {
    /**
     * The unique identifier of the worker.
     */
    private final Integer id;

    /**
     * The name of the worker.
     */
    private final String name;

    /**
     * The surname of the worker.
     */
    private final String surname;

    /**
     * Creates a worker with an associated id.
     *
     * @param id      the unique identifier.
     * @param name    the name.
     * @param surname the surname.
     */
    public Worker(Integer id, String name, String surname) {
        this.id = id;
        this.name = name;
        this.surname = surname;
    }

    /**
     * Creates a worker.
     *
     * @param name    the name.
     * @param surname the surname.
     */
    public Worker(String name, String surname) {
        this(null, name, surname);
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

    /**
     * Returns the surname.
     *
     * @return the surname.
     */
    public String getSurname() {
        return surname;
    }
}
