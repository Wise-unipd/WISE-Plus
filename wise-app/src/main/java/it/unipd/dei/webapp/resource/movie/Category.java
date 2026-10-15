package it.unipd.dei.webapp.resource.movie;

import it.unipd.dei.webapp.resource.AbstractResource;

/**
 * Represents a Category.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class Category extends AbstractResource {
    /**
     * The unique identifier of the category.
     */
    private final Integer id;

    /**
     * The name of the category.
     */
    private final String name;

    /**
     * The description of the category.
     */
    private final String description;

    /**
     * Whether the category is active.
     */
    private final Boolean isActive;

    /**
     * Creates a category with an associated id.
     *
     * @param id          the unique identifier.
     * @param name        the name.
     * @param description the description.
     * @param isActive    whether the category is active.
     */
    public Category(Integer id, String name, String description, Boolean isActive) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isActive = isActive;
    }

    /**
     * Creates a category without an associated id.
     *
     * @param name        the name.
     * @param description the description.
     */
    public Category(String name, String description) {
        this(null, name, description, null);
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
     * Returns the description.
     *
     * @return the description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether the category is active.
     *
     * @return {@code true} if the category is active, {@code false} otherwise.
     */
    public Boolean getActive() {
        return isActive;
    }
}
