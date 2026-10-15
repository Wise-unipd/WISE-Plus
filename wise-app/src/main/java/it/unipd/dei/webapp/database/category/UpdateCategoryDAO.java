package it.unipd.dei.webapp.database.category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.unipd.dei.webapp.database.AbstractDAO;
import it.unipd.dei.webapp.resource.movie.Category;

/**
 * Updates an existing category.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class UpdateCategoryDAO extends AbstractDAO<Category> {
    /**
     * The SQL statement to be executed.
     */
    private static final String STATEMENT = "UPDATE Category SET name = ?, description = ? WHERE category_id = ? RETURNING *";

    /**
     * The category data to be updated.
     */
    private final Category category;

    /**
     * Creates a new object to update a category in the database.
     *
     * @param con      the database connection.
     * @param category the category containing the updated data.
     */
    public UpdateCategoryDAO(final Connection con, final Category category) {
        super(con);

        if (category == null) {
            LOGGER.error("The category cannot be null.");
            throw new NullPointerException("The category cannot be null.");
        }

        this.category = category;
    }

    @Override
    protected void doAccess() throws SQLException {
        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT)) {
            int i = 1;
            pstmt.setString(i++, category.getName());
            pstmt.setString(i++, category.getDescription());
            pstmt.setInt(i++, category.getId());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    outputParam = new Category(
                            rs.getInt("category_id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getBoolean("is_active")
                    );

                    LOGGER.info("Category %s (ID: %d) successfully updated.",
                            outputParam.getName(), outputParam.getId());
                } else {
                    LOGGER.warn("Category with ID %d not found for update.", category.getId());
                    outputParam = null;
                }
            }
        }
    }
}