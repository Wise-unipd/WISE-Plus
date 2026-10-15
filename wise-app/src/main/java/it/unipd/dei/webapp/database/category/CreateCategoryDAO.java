package it.unipd.dei.webapp.database.category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

import it.unipd.dei.webapp.database.AbstractDAO;
import it.unipd.dei.webapp.resource.movie.Category;

/**
 * Creates a new category in the database.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class CreateCategoryDAO extends AbstractDAO<Category> {
    /**
     * The SQL statement to be executed
     */
    private static final String STATEMENT = "INSERT INTO Category (name, description, is_active) VALUES (?, ?, FALSE) RETURNING *";

    /**
     * The category to be stored in the database
     */
    private final Category category;

    /**
     * Creates a new object to store a category in the database.
     *
     * @param con      the database connection.
     * @param category the category to be stored in the database.
     */
    public CreateCategoryDAO(final Connection con, final Category category) {
        super(con);

        if (category == null) {
            LOGGER.error("The category cannot be null.");
            throw new NullPointerException("The category cannot be null.");
        }

        this.category = category;
    }

    @Override
    protected void doAccess() throws SQLException {

        Category returningCategory = null;

        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT)) {
            pstmt.setString(1, category.getName());
            pstmt.setString(2, category.getDescription());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    returningCategory = new Category(
                            rs.getInt("category_id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getBoolean("is_active"));

                    LOGGER.info("Category %d successfully stored in the database", category.getId());
                }
            }
        }

        outputParam = returningCategory;
    }
}
