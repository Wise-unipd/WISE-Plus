package it.unipd.dei.webapp.resource.movie;

import it.unipd.dei.webapp.resource.AbstractResource;

/**
 * Represents a movie credit.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class Credit extends AbstractResource {
    /**
     * The accredited  worker.
     */
    private final Worker worker;

    /**
     * The role for which the worker is accredited.
     */
    private final MovieRole role;

    /**
     * Creates a credit.
     *
     * @param worker the worker.
     * @param role   the role.
     */
    public Credit(Worker worker, MovieRole role) {
        this.worker = worker;
        this.role = role;
    }

    /**
     * Returns the worker.
     *
     * @return the worker.
     */
    public Worker getWorker() {
        return worker;
    }

    /**
     * Returns the role.
     *
     * @return the role.
     */
    public MovieRole getRole() {
        return role;
    }
}
