package it.unipd.dei.webapp.resource.authentication;

import it.unipd.dei.webapp.resource.AbstractResource;

import java.io.Serial;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Represents a magic link for passwordless login.
 * 
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class MagicLink extends AbstractResource implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;


    /**
     * The ID of the magic link.
     */
    private int id;

    /**
     * The ID of the user associated with the magic link.
     */
    private int user_id;

    /**
     * The unique token for the magic link.
     */
    private String token;

    /**
     * The timestamp when the magic link was created.
     */
    private Timestamp created;

    /**
     * The timestamp when the magic link expires.
     */
    private Timestamp exp;

    /**
     * Creates a new magic link.
     *
     * @param id      the ID of the magic link.
     * @param user_id the ID of the user associated with the magic link.
     * @param token   the unique token for the magic link.
     * @param created the timestamp when the magic link was created.
     * @param exp     the timestamp when the magic link expires.
     */
    public MagicLink(int id, int user_id, String token, Timestamp created, Timestamp exp) {
        this.id = id;
        this.user_id = user_id;
        this.token = token;
        this.created = created;
        this.exp = exp;
    }

    /**
     * Creates an empty magic link.
     */
    public MagicLink() {
    }

    /**
     * Creates a new magic link from the builder.
     *
     * @param builder the builder.
     */
    private MagicLink(Builder builder) {
        this.id = builder.id;
        this.user_id = builder.user_id;
        this.token = builder.token;
        this.created = builder.created;
        this.exp = builder.exp;
    }

    /**
     * Returns the ID of the magic link.
     *
     * @return the ID of the magic link.
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the ID of the user associated with the magic link.
     *
     * @return the ID of the user associated with the magic link.
     */
    public int getUser_id() {
        return user_id;
    }

    /**
     * Returns the unique token for the magic link.
     *
     * @return the unique token for the magic link.
     */
    public String getToken() {
        return token;
    }

    /**
     * Returns the timestamp when the magic link was created.
     *
     * @return the timestamp when the magic link was created.
     */
    public Timestamp getCreated() {
        return created;
    }

    /**
     * Returns the timestamp when the magic link expires.
     *
     * @return the timestamp when the magic link expires.
     */
    public Timestamp getExp() {
        return exp;
    }


    /**
     * Creates a new magic link from a database row.
     *
     * @param rs the result set.
     * @return the magic link.
     * @throws SQLException if an error occurs while reading the result set.
     */
    public static MagicLink fromDBRow(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int user_id = rs.getInt("user_id");
        String token = rs.getString("token");
        Timestamp created = rs.getTimestamp("created");
        Timestamp exp = rs.getTimestamp("exp");

        return new MagicLink(id, user_id, token, created, exp);
    }

    /**
     * A builder for creating magic links.
     */
    public static class Builder {
        /**
         * The ID of the magic link.
         */
        private int id;

        /**
         * The ID of the user associated with the magic link.
         */
        private int user_id;

        /**
         * The unique token for the magic link.
         */
        private String token;

        /**
         * The timestamp when the magic link was created.
         */
        private Timestamp created;

        /**
         * The timestamp when the magic link expires.
         */
        private Timestamp exp;

        /**
         * Sets the ID of the magic link.
         *
         * @param id the ID of the magic link.
         * @return the builder.
         */
        public Builder id(int id) {
            this.id = id;
            return this;
        }

        /**
         * Sets the ID of the user associated with the magic link.
         *
         * @param user_id the ID of the user associated with the magic link.
         * @return the builder.
         */
        public Builder user_id(int user_id) {
            this.user_id = user_id;
            return this;
        }

        /**
         * Sets the unique token for the magic link.
         *
         * @param token the unique token for the magic link.
         * @return the builder.
         */
        public Builder token(String token) {
            this.token = token;
            return this;
        }

        /**
         * Sets the timestamp when the magic link was created.
         *
         * @param created the timestamp when the magic link was created.
         * @return the builder.
         */
        public Builder created(Timestamp created) {
            this.created = created;
            return this;
        }

        /**
         * Sets the timestamp when the magic link expires.
         *
         * @param exp the timestamp when the magic link expires.
         * @return the builder.
         */
        public Builder exp(Timestamp exp) {
            this.exp = exp;
            return this;
        }

        /**
         * Builds a new magic link.
         *
         * @return the magic link.
         */
        public MagicLink build() {
            return new MagicLink(this);
        }
    }


}