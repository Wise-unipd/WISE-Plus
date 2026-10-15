package it.unipd.dei.webapp.database.category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.unipd.dei.webapp.database.AbstractDAO;
import it.unipd.dei.webapp.resource.movie.Category;

/**
 * Toggle a category in the database.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class ToggleCategoryStatusDAO extends AbstractDAO<Category> {

    /**
     * The SQL statement to be executed.
     */
    private static final String STATEMENT =
            "UPDATE Category SET is_active = ? WHERE category_id = ? RETURNING *";

    /**
     * The unique identifier of the category.
     */
    private final int categoryId;

    /**
     * The new status of the category.
     */
    private final boolean newStatus;

    /**
     * Creates a new object to toggle a category.
     *
     * @param con        the database connection.
     * @param categoryId the unique identifier of the category.
     * @param newStatus  the new status.
     */
    public ToggleCategoryStatusDAO(final Connection con, final int categoryId, final boolean newStatus) {
        super(con);
        this.categoryId = categoryId;
        this.newStatus = newStatus;
    }

    @Override
    protected void doAccess() throws SQLException {

        Category category = null;

        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT)) {
            pstmt.setBoolean(1, newStatus);
            pstmt.setInt(2, categoryId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    category = new Category(
                            rs.getInt("category_id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getBoolean("is_active"));


                    LOGGER.info("Category %d status successfully udpated", category.getId());
                } else {
                    LOGGER.warn("Category %d not found in the database.", categoryId);
                }
            }
        }

        outputParam = category;
    }
}