package it.unipd.dei.webapp.resource.movie;

import it.unipd.dei.webapp.resource.AbstractResource;

/**
 * Represents an age classification.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class AgeRating extends AbstractResource {
    /**
     * The unique identifier of the classification.
     */
    private final Integer id;

    /**
     * The name of the classification.
     */
    private final String name;

    /**
     * The description of the classification.
     */
    private final String description;

    /**
     * The minimum age required or recommended for viewing.
     */
    private final int age;

    /**
     * Creates an age classification with an associated id.
     *
     * @param id          the unique identifier.
     * @param name        the name.
     * @param description the description.
     * @param age         the minimum age.
     */
    public AgeRating(Integer id, String name, String description, int age) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.age = age;
    }

    /**
     * Creates an age classification without an associated id.
     *
     * @param name        the name.
     * @param description the description.
     * @param age         the minimum age.
     */
    public AgeRating(String name, String description, int age) {
        this(null, name, description, age);
    }

    /**
     * Creates an age classification with id only.
     * 
     * @param id the unique identifier.
     */
    public AgeRating(Integer id) {
        this(id, null, null, 0);
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
     * Returns the minimum age.
     *
     * @return the minimum age.
     */
    public int getAge() {
        return age;
    }
}
