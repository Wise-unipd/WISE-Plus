package it.unipd.dei.webapp.database.category;

import it.unipd.dei.webapp.database.AbstractDAO;
import it.unipd.dei.webapp.resource.movie.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Lists all the categories available in the database.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class GetAllCategoriesDAO extends AbstractDAO<List<Category>> {
    /**
     * The SQL statement to be executed
     */
    private static final String STATEMENT = "SELECT * FROM Category ORDER BY category_id";

    /**
     * Creates a new object for listing all categories.
     *
     * @param con the connection to the database.
     */
    public GetAllCategoriesDAO(final Connection con) {
        super(con);
    }

    /**
     * Accesses the database to fetch the list of categories.
     */
    @Override
    protected final void doAccess() throws SQLException {

        List<Category> categories = new ArrayList<>();

        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                categories.add(new Category(
                        rs.getInt("category_id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getBoolean("is_active")
                ));
            }

            LOGGER.info("%d categories have been successfully retrieved.", categories.size());
        }

        outputParam = categories;
    }
}