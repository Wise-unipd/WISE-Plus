package it.unipd.dei.webapp.resource.authentication;

import it.unipd.dei.webapp.resource.AbstractResource;

import java.io.Serial;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Represents a user of the application.
 * 
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class AppUser extends AbstractResource implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The ID of the user.
     */
    private int id;

    /**
     * The password of the user.
     */
    private String password;

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
     * The role of the user.
     */
    private UserRole userRole;

    /**
     * The timestamp when the user was created.
     */
    private Timestamp created;

    /**
     * The timestamp when the user was last updated.
     */
    private Timestamp lastUpdate;

    /**
     * Whether the user is active.
     */
    private boolean active;

    /**
     * Creates a new user.
     *
     * @param id         the ID of the user.
     * @param password   the password of the user.
     * @param email      the email of the user.
     * @param firstName  the first name of the user.
     * @param lastName   the last name of the user.
     * @param userRole   the role of the user.
     * @param created    the timestamp when the user was created.
     * @param lastUpdate the timestamp when the user was last updated.
     * @param active     whether the user is active.
     */
    public AppUser(int id, String password, String email, String firstName, String lastName, UserRole userRole, Timestamp created, Timestamp lastUpdate, boolean active) {
        this.id = id;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userRole = userRole;
        this.created = created;
        this.lastUpdate = lastUpdate;
        this.active = active;
    }

    /**
     * Creates an empty user.
     */
    public AppUser() {
    }

    /**
     * Creates a new user with email and password.
     *
     * @param email    the email of the user.
     * @param password the password of the user.
     */
    public AppUser(String email, String password) {
        this.email = email;
        this.password = password;
    }

    /**
     * Creates a new user from a database row.
     *
     * @param rs the result set.
     * @return the user.
     * @throws SQLException if an error occurs while reading the result set.
     */
    public static AppUser fromDBRow(ResultSet rs) throws SQLException {

        UserRole userRole = new UserRole.RoleBuilder()
                .id(rs.getInt("role"))
                .description(rs.getString("description"))
                .build();

        return new UserBuilder()
                .id(rs.getInt("id"))
                .email(rs.getString("email"))
                .password(rs.getString("password"))
                .firstName(rs.getString("first_name"))
                .lastName(rs.getString("last_name"))
                .role(userRole)
                .created(rs.getTimestamp("created"))
                .lastUpdate(rs.getTimestamp("last_update"))
                .isActive(rs.getBoolean("is_active")).build();
    }

    @Override
    public String toString() {
        return "AppUser{" + "id=" + id + ", password='" + (password != null ? "****" : "null") + '\'' + ", email='" + email + '\'' + ", firstName='" + firstName + '\'' + ", lastName='" + lastName + '\'' + ", role=" + userRole + ", created=" + created + ", lastUpdate=" + lastUpdate + ", active=" + active + '}';
    }

    /**
     * Returns the ID of the user.
     *
     * @return the ID of the user.
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the password of the user.
     *
     * @return the password of the user.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Returns the email of the user.
     *
     * @return the email of the user.
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
     * Returns the role of the user.
     *
     * @return the role of the user.
     */
    public UserRole getUserRole() {
        return userRole;
    }

    /**
     * Returns the timestamp when the user was created.
     *
     * @return the timestamp when the user was created.
     */
    public Timestamp getCreated() {
        return created;
    }

    /**
     * Returns the timestamp when the user was last updated.
     *
     * @return the timestamp when the user was last updated.
     */
    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    /**
     * Returns whether the user is active.
     *
     * @return {@code true} if the user is active, {@code false} otherwise.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Resets the user's password.
     */
    public void resetPassword() {
        this.password = null;
    }

//    @Override
//    public void writeJSON(JsonGenerator jg) throws Exception {
//        jg.writeStartObject();
//        jg.writeNumberField("id", id);
//        jg.writeStringField("email", email);
//        jg.writeStringField("firstName", firstName);
//        jg.writeStringField("lastName", lastName);
//        if (userRole != null) {
//            jg.writeFieldName("userRole");
//            userRole.writeJSON(jg);
//        }
//        if (created != null) {
//            jg.writeStringField("created", created.toString());
//        }
//        if (lastUpdate != null) {
//            jg.writeStringField("lastUpdate", lastUpdate.toString());
//        }
//        jg.writeBooleanField("is_active", active);
//        jg.writeEndObject();
//    }

    /**
     * A builder for creating users.
     */
    public static class UserBuilder {
        /**
         * The ID of the user.
         */
        private int id;

        /**
         * The password of the user.
         */
        private String password;

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
         * The role of the user.
         */
        private UserRole userRole;

        /**
         * The timestamp when the user was created.
         */
        private Timestamp created;

        /**
         * The timestamp when the user was last updated.
         */
        private Timestamp lastUpdate;

        /**
         * Whether the user is active.
         */
        private boolean active;

        /**
         * Creates a new user builder.
         */
        public UserBuilder() {
        }

        /**
         * Sets the ID of the user.
         *
         * @param id the ID of the user.
         * @return the builder.
         */
        public UserBuilder id(int id) {
            this.id = id;
            return this;
        }

        /**
         * Sets the password of the user.
         *
         * @param password the password of the user.
         * @return the builder.
         */
        public UserBuilder password(String password) {
            this.password = password;
            return this;
        }

        /**
         * Sets the email of the user.
         *
         * @param email the email of the user.
         * @return the builder.
         */
        public UserBuilder email(String email) {
            this.email = email;
            return this;
        }

        /**
         * Sets the first name of the user.
         *
         * @param firstName the first name of the user.
         * @return the builder.
         */
        public UserBuilder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        /**
         * Sets the last name of the user.
         *
         * @param lastName the last name of the user.
         * @return the builder.
         */
        public UserBuilder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        /**
         * Sets the role of the user.
         *
         * @param userRole the role of the user.
         * @return the builder.
         */
        public UserBuilder role(UserRole userRole) {
            this.userRole = userRole;
            return this;
        }

        /**
         * Sets the timestamp when the user was created.
         *
         * @param created the timestamp when the user was created.
         * @return the builder.
         */
        public UserBuilder created(Timestamp created) {
            this.created = created;
            return this;
        }

        /**
         * Sets the timestamp when the user was last updated.
         *
         * @param lastUpdate the timestamp when the user was last updated.
         * @return the builder.
         */
        public UserBuilder lastUpdate(Timestamp lastUpdate) {
            this.lastUpdate = lastUpdate;
            return this;
        }

        /**
         * Sets whether the user is active.
         *
         * @param active whether the user is active.
         * @return the builder.
         */
        public UserBuilder isActive(boolean active) {
            this.active = active;
            return this;
        }

        /**
         * Builds a new user.
         *
         * @return the user.
         */
        public AppUser build() {
            return new AppUser(id, password, email, firstName, lastName, userRole, created, lastUpdate, active);
        }
    }
}
