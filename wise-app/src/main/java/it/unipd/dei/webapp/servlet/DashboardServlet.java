package it.unipd.dei.webapp.servlet;

import java.io.IOException;

import it.unipd.dei.webapp.resource.LogContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Manages access to the dashboard.
 * Access is restricted to administrators only.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class DashboardServlet extends AbstractDatabaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setResource(req.getRequestURI());

        LOGGER.info("Showing Administration Page.");

        try {
            req.getRequestDispatcher("/auth/staff/dashboard.jsp").forward(req, res);
        } finally {
            LogContext.removeIPAddress();
            LogContext.removeResource();
        }
    }
}
