package it.unipd.dei.webapp.rest;

import java.io.IOException;

/**
 * Represents a generic REST resource.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public interface RestResource {
    /**
     * Serves the REST request.
     *
     * @throws IOException if any error occurs during the client/server communication.
     */
    void serve() throws IOException;
}
