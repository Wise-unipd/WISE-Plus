package it.unipd.dei.webapp.resource.movie;

import it.unipd.dei.webapp.resource.AbstractResource;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the details of a movie.
 * Since this is a complex object, the Builder pattern helps you construct it (by providing a Fluent API).
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class MovieDetails extends AbstractResource {
    /**
     * The movie object.
     */
    private final Movie movie;

    /**
     * The list of genres.
     */
    private final List<Genre> genres;

    /**
     * The list of audio languages.
     */
    private final List<Language> audios;

    /**
     * The list of subtitle languages.
     */
    private final List<Language> subtitles;

    /**
     * The list of credits.
     */
    private final List<Credit> credits;

    /**
     * Creates a movie with the help of {@link MovieDetailsBuilder}.
     *
     * @param builder the builder containing the movie configuration.
     */
    private MovieDetails(MovieDetailsBuilder builder) {
        this.movie = builder.movie;
        this.genres = List.copyOf(builder.genres);
        this.audios = List.copyOf(builder.audios);
        this.subtitles = List.copyOf(builder.subtitles);
        this.credits = List.copyOf(builder.credits);
    }

    /**
     * Returns the movie object.
     *
     * @return the movie object.
     */
    public Movie getMovie() {
        return movie;
    }

    /**
     * Returns the list of genres.
     *
     * @return the list of genres.
     */
    public List<Genre> getGenres() {
        return genres;
    }

    /**
     * Returns the list of audio languages.
     *
     * @return the list of audio languages.
     */
    public List<Language> getAudios() {
        return audios;
    }

    /**
     * Returns the list of subtitle languages.
     *
     * @return the list of subtitle languages.
     */
    public List<Language> getSubtitles() {
        return subtitles;
    }

    /**
     * Returns the list of credits.
     *
     * @return the list of credits.
     */
    public List<Credit> getCredits() {
        return credits;
    }

    /**
     * Returns a movie details builder to construct {@link MovieDetails}.
     *
     * @return a new movie details builder instance.
     */
    public static MovieDetailsBuilder builder() {
        return new MovieDetailsBuilder();
    }

    /**
     * Represents a movie details builder.
     */
    public static class MovieDetailsBuilder {
        private Movie movie;
        private List<Genre> genres = new ArrayList<>();
        private List<Language> audios = new ArrayList<>();
        private List<Language> subtitles = new ArrayList<>();
        private List<Credit> credits = new ArrayList<>();

        /**
         * Creates a movie details builder.
         * This constructor is private to enforce the use of {@link MovieDetails#builder()}.
         */
        private MovieDetailsBuilder() {
        }

        /**
         * Sets the movie object.
         *
         * @param movie the movie object.
         * @return the movie details builder itself.
         */
        public MovieDetailsBuilder withMovie(Movie movie) {
            this.movie = movie;
            return this;
        }

        /**
         * Sets the list of genres.
         *
         * @param genres list of genres.
         * @return the movie details builder itself.
         */
        public MovieDetailsBuilder withGenres(List<Genre> genres) {
            this.genres = genres;
            return this;
        }

        /**
         * Sets the list of audio languages.
         *
         * @param audios the list of audio languages.
         * @return the movie details builder itself.
         */
        public MovieDetailsBuilder withAudios(List<Language> audios) {
            this.audios = audios;
            return this;
        }

        /**
         * Sets the list of subtitle languages.
         *
         * @param subtitles the list of subtitle languages.
         * @return the movie details builder itself.
         */
        public MovieDetailsBuilder withSubtitles(List<Language> subtitles) {
            this.subtitles = subtitles;
            return this;
        }

        /**
         * Sets the list of credits.
         *
         * @param credits the list of credits.
         * @return the movie details builder itself.
         */
        public MovieDetailsBuilder withCredits(List<Credit> credits) {
            this.credits = credits;
            return this;
        }

        /**
         * Performs field validation and construction.
         *
         * @return a new {@link MovieDetails} instance.
         */
        public MovieDetails build() {
            if (this.movie == null) {
                throw new IllegalStateException("MovieDetails must be associated with a Movie object.");
            }
            return new MovieDetails(this);
        }
    }
}
