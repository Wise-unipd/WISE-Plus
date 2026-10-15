package it.unipd.dei.webapp.database.category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.unipd.dei.webapp.database.AbstractDAO;
import it.unipd.dei.webapp.resource.movie.Category;

/**
 * Searches for a category in the database using its unique identifier.
 * <p>If the category is found, a {@link Category} object is created and stored as the output parameter.
 * If no category matches the provided ID, the output parameter is set to {@code null}.</p>
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class SearchCategoryByIdDAO extends AbstractDAO<Category> {

    /**
     * The SQL statement(s) to retrieve the category information.
     */
    private static final String STATEMENT = "SELECT * FROM Category WHERE category_id = ?";
    private static final String STATEMENT_ACTIVE = "SELECT * FROM Category WHERE category_id = ? AND is_active = true";

    /**
     * The unique identifier of the category.
     */
    private final Integer categoryId;

    /**
     * Whether to restrict the search to active categories only.
     */
    private final boolean onlyActive;

    /**
     * Creates a new object to search for a category's info.
     *
     * @param con        the database connection.
     * @param categoryId the ID of the category.
     * @param onlyActive whether to restrict the search.
     */
    public SearchCategoryByIdDAO(final Connection con, final Integer categoryId, final boolean onlyActive) {
        super(con);
        this.categoryId = categoryId;
        this.onlyActive = onlyActive;
    }

    /**
     * Accesses the database to find the category details.
     */
    @Override
    protected final void doAccess() throws SQLException {

        String query = onlyActive ? STATEMENT_ACTIVE : STATEMENT;
        Category category = null; // Result of the search

        // try-with-resources
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, categoryId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    category = new Category(
                            rs.getInt("category_id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getBoolean("is_active")
                    );

                    LOGGER.info("Category with ID %d successfully found.", categoryId);
                } else {
                    LOGGER.warn("Category with ID %d not found.", categoryId);
                }
            }
        }

        outputParam = category;
    }
}