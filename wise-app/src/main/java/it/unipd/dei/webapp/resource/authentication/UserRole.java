package it.unipd.dei.webapp.resource.authentication;

import it.unipd.dei.webapp.resource.AbstractResource;

import java.io.Serial;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Represents a user role in the application.
 * 
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class UserRole extends AbstractResource implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

//    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * The ID of the user role.
     */
    private int id;

    /**
     * The description of the user role.
     */
    private String description;

    /**
     * The timestamp when the user role was created.
     */
    private Timestamp created;

    /**
     * The timestamp when the user role was last updated.
     */
    private Timestamp lastUpdate;

    /**
     * Whether the user role is active.
     */
    private boolean active;

    /**
     * Creates a new user role.
     *
     * @param id          the ID of the user role.
     * @param description the description of the user role.
     * @param created     the timestamp when the user role was created.
     * @param lastUpdate  the timestamp when the user role was last updated.
     * @param active      whether the user role is active.
     */
    public UserRole(int id, String description, Timestamp created, Timestamp lastUpdate, boolean active) {
        this.id = id;
        this.description = description;
        this.created = created;
        this.lastUpdate = lastUpdate;
        this.active = active;
    }

    /**
     * Creates an empty user role.
     */
    public UserRole() {
    }

    /**
     * Creates a new user role from a database row.
     *
     * @param rs the result set.
     * @return the user role.
     * @throws SQLException if an error occurs while reading the result set.
     */
    public static UserRole fromDBRow(ResultSet rs) throws SQLException {
        return new RoleBuilder()
                .id(rs.getInt("id"))
                .description(rs.getString("description"))
                .created(rs.getTimestamp("created"))
                .lastUpdate(rs.getTimestamp("last_update"))
                .isActive(rs.getBoolean("is_active"))
                .build();
    }

    @Override
    public String toString() {
        return "Role{" + "id=" + id + ", description='" + description + '\'' + ", created=" + created + ", lastUpdate=" + lastUpdate + ", active=" + active + '}';
    }

    /**
     * Returns the ID of the user role.
     *
     * @return the ID of the user role.
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the description of the user role.
     *
     * @return the description of the user role.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the timestamp when the user role was created.
     *
     * @return the timestamp when the user role was created.
     */
    public Timestamp getCreated() {
        return created;
    }

    /**
     * Returns the timestamp when the user role was last updated.
     *
     * @return the timestamp when the user role was last updated.
     */
    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    /**
     * Returns whether the user role is active.
     *
     * @return {@code true} if the user role is active, {@code false} otherwise.
     */
    public boolean isActive() {
        return active;
    }

//    @Override
//    public void writeJSON(JsonGenerator jg) throws Exception {
//        MAPPER.writeValue(jg, this);
//    }

    /**
     * A builder for creating user roles.
     */
    public static class RoleBuilder {
        /**
         * The ID of the user role.
         */
        private int id;

        /**
         * The description of the user role.
         */
        private String description;

        /**
         * The timestamp when the user role was created.
         */
        private Timestamp created;

        /**
         * The timestamp when the user role was last updated.
         */
        private Timestamp lastUpdate;

        /**
         * Whether the user role is active.
         */
        private boolean active;

        /**
         * Creates a new role builder.
         */
        public RoleBuilder() {
        }

        /**
         * Sets the ID of the user role.
         *
         * @param id the ID of the user role.
         * @return the builder.
         */
        public RoleBuilder id(int id) {
            this.id = id;
            return this;
        }

        /**
         * Sets the description of the user role.
         *
         * @param description the description of the user role.
         * @return the builder.
         */
        public RoleBuilder description(String description) {
            this.description = description;
            return this;
        }

        /**
         * Sets the timestamp when the user role was created.
         *
         * @param created the timestamp when the user role was created.
         * @return the builder.
         */
        public RoleBuilder created(Timestamp created) {
            this.created = created;
            return this;
        }

        /**
         * Sets the timestamp when the user role was last updated.
         *
         * @param lastUpdate the timestamp when the user role was last updated.
         * @return the builder.
         */
        public RoleBuilder lastUpdate(Timestamp lastUpdate) {
            this.lastUpdate = lastUpdate;
            return this;
        }

        /**
         * Sets whether the user role is active.
         *
         * @param active whether the user role is active.
         * @return the builder.
         */
        public RoleBuilder isActive(boolean active) {
            this.active = active;
            return this;
        }

        /**
         * Builds a new user role.
         *
         * @return the user role.
         */
        public UserRole build() {
            return new UserRole(id, description, created, lastUpdate, active);
        }
    }
}