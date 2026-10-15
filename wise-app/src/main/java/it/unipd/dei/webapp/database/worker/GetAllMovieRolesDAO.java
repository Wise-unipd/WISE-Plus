package it.unipd.dei.webapp.database.worker;

import it.unipd.dei.webapp.database.AbstractDAO;
import it.unipd.dei.webapp.resource.movie.MovieRole;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Lists all the movie roles available in the database.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class GetAllMovieRolesDAO extends AbstractDAO<List<MovieRole>> {

    /**
     * The SQL statement to be executed.
     */
    private static final String STATEMENT = "SELECT * FROM MovieRole ORDER BY name ASC";

    /**
     * Creates a new object to list all movie roles.
     *
     * @param con the database connection.
     */
    public GetAllMovieRolesDAO(final Connection con) {
        super(con);
    }

    @Override
    protected final void doAccess() throws SQLException {

        List<MovieRole> movieRoles = new ArrayList<>();

        try (PreparedStatement pstmt = con.prepareStatement(STATEMENT);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                movieRoles.add(new MovieRole(
                        rs.getInt("role_id"),
                        rs.getString("name"))
                );
            }

            LOGGER.info("%d movie roles have been successfully retrieved.", movieRoles.size());
        }

        outputParam = movieRoles;
    }
}