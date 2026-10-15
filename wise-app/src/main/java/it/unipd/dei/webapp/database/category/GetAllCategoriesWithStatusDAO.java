package it.unipd.dei.webapp.database.category;

import it.unipd.dei.webapp.database.AbstractDAO;
import it.unipd.dei.webapp.resource.movie.Category;
import it.unipd.dei.webapp.resource.movie.Selectable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Lists all categories available in the database, with their selection status.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class GetAllCategoriesWithStatusDAO extends AbstractDAO<List<Selectable<Category>>> {

    /**
     * The SQL statement to be executed.
     */
    private static final String STATEMENT =
            "SELECT c.*, " +
                    "(hc.movie_id IS NOT NULL) AS is_selected " +
                    "FROM Category AS c " +
                    "LEFT JOIN HasCategory AS hc ON c.category_id = hc.category_id " +
                    "AND hc.movie_id = ? " +
                    "ORDER BY is_selected DESC, c.name ASC";

    /**
     * The unique identifier of the movie.
     */
    private final int movieId;

    /**
     * Creates a new object for listing all categories with status.
     *
     * @param con     the database connection.
     * @param movieId the unique identifier of the movie.
     */
    public GetAllCategoriesWithStatusDAO(final Connection con, final int movieId) {
        super(con);
        this.movieId = movieId;
    }

    @Override
    protected final void doAccess() throws SQLException {

        List<Selectable<Category>> categories = new ArrayList<>();

        // try-with-resources
        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT)) {
            pstmt.setInt(1, movieId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    categories.add(new Selectable<>(
                            new Category(
                                    rs.getInt("category_id"),
                                    rs.getString("name"),
                                    rs.getString("description"),
                                    rs.getBoolean("is_active")
                            ),
                            rs.getBoolean("is_selected")
                    ));
                }

                LOGGER.info("%d categories have been successfully retrieved.", categories.size());
            }
        }

        outputParam = categories;
    }
}