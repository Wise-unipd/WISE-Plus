package it.unipd.dei.webapp.resource;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A generic container for a collection of resources.
 * It serializes the collection as a JSON object containing a "resource-list" array.
 * 
 * @param <T> the type of resource contained in the list, must extend {@link AbstractResource}.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class ResourceList<T extends AbstractResource> extends AbstractResource {
    /**
     * The list of resources to be serialized.
     */
    @JsonProperty("resource-list")
    private final Iterable<T> list;

    /**
     * Creates a new resource list.
     *
     * @param list the iterable collection of resources.
     */
    public ResourceList(final Iterable<T> list) {
        if (list == null) {
            LOGGER.error("Resource list cannot be null.");
            throw new NullPointerException("Resource list cannot be null.");
        }
        this.list = list;
    }

    public Iterable<T> getList() {
        return list;
    }
}
