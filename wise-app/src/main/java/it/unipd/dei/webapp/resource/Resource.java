package it.unipd.dei.webapp.resource;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Represents a resource able to serialize itself to JSON.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public interface Resource {
    /**
     * Writes the JSON representation of the {@code Resource} to the provided {@code OutputStream}.
     *
     * @param out the target output stream.
     * @throws IOException if an error occurs during serialization.
     */
    void toJSON(OutputStream out) throws IOException;
}
