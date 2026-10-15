package it.unipd.dei.webapp.resource;

/**
 * Central dictionary for all application actions
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class Actions {
    /**
     * The creation of an age rating
     */
    public static final String CREATE_AGE_RATING = "CREATE_AGE_RATING";

    /**
     * Get the details of a movie
     */
    public static final String GET_MOVIE_DETAILS = "GET_MOVIE_DETAILS";

    /**
     * The extraction of a movie catalog.
     */
    public static final String GET_MOVIE_CATALOG = "GET_MOVIE_CATALOG";

    /**
     * The extraction of trending movies.
     */
    public static final String GET_TRENDING_MOVIES = "GET_TRENDING_MOVIES";

    /**
     * The extraction of application statistics.
     */
    public static final String GET_STATISTICS = "GET_STATISTICS";

    /**
     * Searching for movies by a worker ID
     */
    public static final String SEARCH_MOVIES_BY_WORKER_ID = "SEARCH_MOVIES_BY_WORKER_ID";

    /**
     * Searching for movies by a category ID
     */
    public static final String SEARCH_MOVIES_BY_CATEGORY_ID = "SEARCH_MOVIES_BY_CATEGORY_ID";

    /**
     * Searching for movies by a text query
     */
    public static final String SEARCH_MOVIES_BY_TEXT = "SEARCH_MOVIES_BY_TEXT";

    /**
     * Searching for workers by a text query
     */
    public static final String SEARCH_WORKERS_BY_TEXT = "SEARCH_WORKERS_BY_TEXT";

    /**
     * The extraction of the wishlist
     */
    public static final String GET_WISHLIST = "GET_WISHLIST";

    /**
     * Add a movie to the wishlist
     */
    public static final String ADD_MOVIE_TO_WISHLIST = "ADD_MOVIE_TO_WISHLIST";

    /**
     * Remove a movie from the wishlist
     */
    public static final String REMOVE_MOVIE_FROM_WISHLIST = "REMOVE_MOVIE_FROM_WISHLIST";

    /**
     * Add 'I like' to a movie
     */
    public static final String ADD_LIKE_TO_MOVIE = "ADD_LIKE_TO_MOVIE";

    /**
     * Remove 'I like' from a movie
     */
    public static final String REMOVE_LIKE_FROM_MOVIE = "REMOVE_LIKE_FROM_MOVIE";

    /**
     * Add a genre to a movie
     */
    public static final String ADD_GENRE_TO_MOVIE = "ADD_GENRE_TO_MOVIE";

    /**
     * Add a category to a movie
     */
    public static final String ADD_CATEGORY_TO_MOVIE = "ADD_CATEGORY_TO_MOVIE";

    /**
     * Add a credit to a movie
     */
    public static final String ADD_CREDIT_TO_MOVIE = "ADD_CREDIT_TO_MOVIE";

    /**
     * Remove a genre from a movie
     */
    public static final String REMOVE_GENRE_FROM_MOVIE = "REMOVE_GENRE_FROM_MOVIE";

    /**
     * Remove a category from a movie
     */
    public static final String REMOVE_CATEGORY_FROM_MOVIE = "REMOVE_CATEGORY_FROM_MOVIE";

    /**
     * Remove an audio from a movie
     */
    public static final String REMOVE_AUDIO_FROM_MOVIE = "REMOVE_AUDIO_FROM_MOVIE";

    /**
     * Remove a subtitle from a movie
     */
    public static final String REMOVE_SUBTITLE_FROM_MOVIE = "REMOVE_AUDIO_FROM_MOVIE";

    /**
    * Add an audio to a movie
    */
    public static final String ADD_AUDIO_TO_MOVIE = "ADD_AUDIO_TO_MOVIE";

    /**
     * Add a subtitle to a movie
     */
    public static final String ADD_SUBTITLE_TO_MOVIE = "ADD_AUDIO_TO_MOVIE";

    /**
     * Remove a credit from a movie
     */
    public static final String REMOVE_CREDIT_FROM_MOVIE = "REMOVE_CREDIT_FROM_MOVIE";

    /**
     * Creation of a new worker (actor/director)
     */
    public static final String CREATE_WORKER = "CREATE_WORKER";

    /**
     * Modification of an existing worker
     */
    public static final String EDIT_WORKER = "EDIT_WORKER";

    /**
     * Modification of an existing age rating
     */
    public static final String EDIT_AGE_RATING = "EDIT_AGE_RATING";

    /**
     * The deletion of an existing worker
     */
    public static final String DELETE_WORKER = "DELETE_WORKER";

    /**
     * The deletion of an existing age rating
     */
    public static final String DELETE_AGE_RATING = "DELETE_AGE_RATING";

    /**
     * Action for creating a new genre.
     */
    public static final String CREATE_GENRE = "CREATE_GENRE";

    /**
     * Action for updating an existing genre.
     */
    public static final String EDIT_GENRE = "EDIT_GENRE";

    /**
     * Action for deleting a genre.
     */
    public static final String DELETE_GENRE = "DELETE_GENRE";

    /**
     * Action for creating a new language.
     */
    public static final String CREATE_LANGUAGE = "CREATE_LANGUAGE";

    /**
     * Action for updating an existing language.
     */
    public static final String EDIT_LANGUAGE = "EDIT_LANGUAGE";

    /**
     * Action for deleting a language.
     */
    public static final String DELETE_LANGUAGE = "DELETE_LANGUAGE";

    /**
     * Action for creating a new movie.
     */
    public static final String CREATE_MOVIE = "CREATE_MOVIE";

    /**
     * Action for updating an existing movie.
     */
    public static final String EDIT_MOVIE = "EDIT_MOVIE";

    /**
     * Toggle a movie.
     */
    public static final String TOGGLE_MOVIE = "TOGGLE_MOVIE";

    /**
     * Toggle a category.
     */
    public static final String TOGGLE_CATEGORY = "TOGGLE_CATEGORY";

    /*
     * Requesting a magic login link
     */
    public static final String REQUEST_MAGIC_LINK = "REQUEST_MAGIC_LINK";

    /**
     * Validating a magic login link
     */
    public static final String VALIDATE_MAGIC_LINK = "VALIDATE_MAGIC_LINK";

    /**
     * Searching for users by a text query
     */
    public static final String SEARCH_USERS_BY_TEXT = "SEARCH_USERS_BY_TEXT";

    /**
     * Updating the user profile
     */
    public static final String UPDATE_PROFILE = "UPDATE_PROFILE";

    /**
     * Changing the user password
     */
    public static final String CHANGE_PASSWORD = "CHANGE_PASSWORD";

    /**
     * Action for creating a category.
     */
    public static final String CREATE_CATEGORY = "CREATE_CATEGORY";

    /**
     * Action for updating an existing category.
     */
    public static final String EDIT_CATEGORY = "EDIT_CATEGORY";

    /**
     * Searching for rentals by a text
     */
    public static final String SEARCH_RENTS_BY_TEXT = "SEARCH_RENTS_BY_TEXT";

    /**
     * This class can be neither instantiated nor sub-classed.
     */
    private Actions() {
        throw new AssertionError(String.format("No instances of %s allowed.", Actions.class.getName()));
    }
}
