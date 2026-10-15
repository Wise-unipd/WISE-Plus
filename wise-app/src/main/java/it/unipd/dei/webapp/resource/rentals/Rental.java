package it.unipd.dei.webapp.resource.rentals;

import it.unipd.dei.webapp.resource.AbstractResource;

import java.sql.Timestamp;

/**
 * Represents a rental for the application.
 * 
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class Rental extends AbstractResource {
    /**
     * The ID of the rent.
     */
    private final int id;
    /**
     * The ID of the user.
     */
    private int userId;
    /**
     * The email of the user.
     */
    private String email;
    /**
     * The first name of the user.
     */
    private String firstName;
    /**
     * The last name of the user.
     */
    private String lastName;
    /**
     * The ID of the movie.
     */
    private final int movieId;
    /**
     * The title of the movie.
     */
    private final String movieTitle;
    /**
     * The date of the purchase.
     */
    private final Timestamp checkout;
    /**
     * The amount of the purchase.
     */
    private double amount;
    /**
     * The date of the expiration of the rent.
     */
    private final Timestamp expiration;


    /**
     * Creates a new rent for the single profile view
     *
     * @param id         the ID of the rent.
     * @param movieId    the id of the movie.
     * @param movieTitle the title of the movie.
     * @param checkout   the date of the purchase.
     * @param amount     the amount of the purchase
     * @param expiration the date of the expiration of the rent.
     */

    public Rental(int id, int movieId, String movieTitle, Timestamp checkout, double amount, Timestamp expiration) {
        this.id = id;
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.checkout = checkout;
        this.amount = amount;
        this.expiration = expiration;
    }

    /**
     * Creates a new rent for the general page view
     *
     * @param id         the ID of the rent.
     * @param userId     the ID of the user.
     * @param email      the user email
     * @param firstName  the user first name.
     * @param lastName   the user last name
     * @param movieId    the id of the movie.
     * @param movieTitle the title of the movie.
     * @param checkout   the date of the purchase.
     * @param expiration the date of the expiration of the rent.
     */

    public Rental(int id, int userId, String email, String firstName, String lastName, int movieId, String movieTitle, Timestamp checkout, Timestamp expiration) {
        this.id = id;
        this.userId = userId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.checkout = checkout;
        this.expiration = expiration;
    }

    /**
     * Returns the ID of the rent.
     *
     * @return the ID of the rent.
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the ID of the user.
     *
     * @return the ID of the user.
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Returns the user email.
     *
     * @return the user email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns the first name of the user.
     *
     * @return the first name of the user.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Returns the last name of the user.
     *
     * @return the last name of the user.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Returns the ID of the movie.
     *
     * @return the ID of the movie.
     */
    public int getMovieId() {
        return movieId;
    }

    /**
     * Returns the movie title.
     *
     * @return the movie title.
     */
    public String getMovieTitle() {
        return movieTitle;
    }

    /**
     * Returns the date of the purchase.
     *
     * @return the date of the purchase.
     */
    public Timestamp getCheckout() {
        return checkout;
    }
    /**
     * Returns the amount of the purchase.
     *
     * @return the amount of the purchase.
     */
    public double getAmount() {
        return amount;
    }
    /**
     * Returns the date of the expiration of the rent.
     *
     * @return the date of the expiration of the rent
     */
    public Timestamp getExpiration() {
        return expiration;
    }
}