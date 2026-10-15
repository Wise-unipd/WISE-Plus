package it.unipd.dei.webapp.resource.movie;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.unipd.dei.webapp.resource.AbstractResource;
import it.unipd.dei.webapp.resource.Resource;

/**
 * Represents a resource with a selection status.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class Selectable<T extends Resource> extends AbstractResource {

    /**
     * The selectable resource.
     */
    @JsonUnwrapped
    private final T resource;

    /**
     * The selection status of the resource.
     */
    protected final boolean isSelected;

    /**
     * Creates a selectable resource.
     *
     * @param resource   the resource.
     * @param isSelected the selection status.
     */
    public Selectable(final T resource, final boolean isSelected) {
        this.resource = resource;
        this.isSelected = isSelected;
    }

    /**
     * Returns the resource.
     *
     * @return the resource.
     */
    public final T getResource() {
        return resource;
    }

    /**
     * Checks if the resource is selected.
     *
     * @return {@code true} if selected, {@code false} otherwise.
     */
    public final boolean isSelected() {
        return isSelected;
    }
}
