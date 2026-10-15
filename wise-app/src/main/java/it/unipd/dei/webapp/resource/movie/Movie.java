package it.unipd.dei.webapp.resource.movie;

import it.unipd.dei.webapp.resource.AbstractResource;

/**
 * Represents a movie.
 * Since this is a complex object, the Builder pattern helps you construct it (by providing a Fluent API).
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class Movie extends AbstractResource {
    /**
     * The unique identifier of the movie.
     */
    private final Integer id;

    /**
     * The title of the movie (English version).
     */
    private final String title;

    /**
     * The plot of the movie.
     */
    private final String synopsis;

    /**
     * The release year of the movie (in the United States).
     */
    private final Integer releaseYear;

    /**
     * The duration of the movie (in minutes).
     */
    private final Integer runningTime;

    /**
     * The age classification of the movie.
     */
    private final AgeRating ageRating;

    /**
     * The basic price of the movie.
     */
    private final Double basePriceEur;

    /**
     * The path of the poster image.
     */
    private final String posterPic;

    /**
     * The YouTube link of the trailer.
     */
    private final String trailer;

    /**
     * Indicates whether the movie is active.
     */
    private final Boolean isActive;

    /**
     * Creates a movie with the help of {@link MovieBuilder}.
     *
     * @param builder the builder containing the movie configuration.
     */
    private Movie(MovieBuilder builder) {
        this.id = builder.id;
        this.title = builder.title;
        this.synopsis = builder.synopsis;
        this.releaseYear = builder.releaseYear;
        this.runningTime = builder.runningTime;
        this.ageRating = builder.ageRating;
        this.basePriceEur = builder.basePriceEur;
        this.posterPic = builder.posterPic;
        this.trailer = builder.trailer;
        this.isActive = builder.isActive;
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
     * Returns the title.
     *
     * @return the title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the synopsis.
     *
     * @return the synopsis.
     */
    public String getSynopsis() {
        return synopsis;
    }

    /**
     * Returns the release year.
     *
     * @return the release year.
     */
    public Integer getReleaseYear() {
        return releaseYear;
    }

    /**
     * Returns the running time.
     *
     * @return the running time.
     */
    public Integer getRunningTime() {
        return runningTime;
    }

    /**
     * Returns the age rating.
     *
     * @return the age rating.
     */
    public AgeRating getAgeRating() {
        return ageRating;
    }

    /**
     * Returns the base price.
     *
     * @return the base price.
     */
    public Double getBasePriceEur() {
        return basePriceEur;
    }

    /**
     * Returns the poster path.
     *
     * @return the poster path.
     */
    public String getPosterPic() {
        return posterPic;
    }

    /**
     * Returns the trailer link.
     *
     * @return the trailer link.
     */
    public String getTrailer() {
        return trailer;
    }

    /**
     * Checks if the movie is active.
     *
     * @return {@code true} if active, {@code false} otherwise.
     */
    public Boolean getActive() {
        return isActive;
    }

    /**
     * Returns a movie builder to construct a {@link Movie}.
     *
     * @return a new movie builder instance.
     */
    public static MovieBuilder builder() {
        return new MovieBuilder();
    }

    /**
     * Represents a movie builder.
     */
    public static class MovieBuilder {
        private Integer id;
        private String title;
        private String synopsis;
        private Integer releaseYear;
        private Integer runningTime;
        private AgeRating ageRating;
        private Double basePriceEur;
        private String posterPic;
        private String trailer;
        private Boolean isActive;

        /**
         * Creates a movie builder.
         * This constructor is private to enforce the use of {@link Movie#builder()}.
         */
        private MovieBuilder() {
        }

        /**
         * Sets the unique identifier of the movie.
         *
         * @param id the unique identifier of the movie.
         * @return the movie builder itself.
         */
        public MovieBuilder withId(Integer id) {
            this.id = id;
            return this;
        }

        /**
         * Sets the title of the movie.
         *
         * @param title the title.
         * @return the movie builder itself.
         */
        public MovieBuilder withTitle(String title) {
            this.title = title;
            return this;
        }

        /**
         * Sets the plot of the movie.
         *
         * @param synopsis the plot.
         * @return the movie builder itself.
         */
        public MovieBuilder withSynopsis(String synopsis) {
            this.synopsis = synopsis;
            return this;
        }

        /**
         * Sets the release year of the movie.
         *
         * @param releaseYear the release year.
         * @return the movie builder itself.
         */
        public MovieBuilder withReleaseYear(Integer releaseYear) {
            this.releaseYear = releaseYear;
            return this;
        }

        /**
         * Sets the duration of the movie.
         *
         * @param runningTime the duration.
         * @return the movie builder itself.
         */
        public MovieBuilder withRunningTime(Integer runningTime) {
            this.runningTime = runningTime;
            return this;
        }

        /**
         * Sets the age classification of the movie.
         *
         * @param ageRating the age classification.
         * @return the movie builder itself.
         */
        public MovieBuilder withAgeRating(AgeRating ageRating) {
            this.ageRating = ageRating;
            return this;
        }

        /**
         * Sets the base price of the movie.
         *
         * @param basePriceEur the base price.
         * @return the movie builder itself.
         */
        public MovieBuilder withBasePriceEur(Double basePriceEur) {
            this.basePriceEur = basePriceEur;
            return this;
        }

        /**
         * Sets the path of the poster image.
         *
         * @param posterPic the poster path.
         * @return the movie builder itself.
         */
        public MovieBuilder withPosterPic(String posterPic) {
            this.posterPic = posterPic;
            return this;
        }

        /**
         * Sets the link of the trailer.
         *
         * @param trailer the trailer link.
         * @return the movie builder itself.
         */
        public MovieBuilder withTrailer(String trailer) {
            this.trailer = trailer;
            return this;
        }

        /**
         * Sets the active status of the movie.
         *
         * @param isActive the active status.
         * @return the movie builder itself.
         */
        public MovieBuilder withIsActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        /**
         * Performs construction.
         * @return a new {@link Movie} instance.
         */
        public Movie build() {
            return new Movie(this);
        }
    }
}