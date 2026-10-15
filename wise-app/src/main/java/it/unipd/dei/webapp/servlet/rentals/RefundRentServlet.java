package it.unipd.dei.webapp.servlet.rentals;

import it.unipd.dei.webapp.database.rentals.ProcessRefundDAO;
import it.unipd.dei.webapp.resource.authentication.AppUser;
import it.unipd.dei.webapp.resource.LogContext;
import it.unipd.dei.webapp.servlet.AbstractDatabaseServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Servlet responsible for processing financial refund requests for rentals.
 * This servlet acts as the secure controller between the frontend refund modal
 * and the database layer. It performs strict validation checks (Authentication,
 * Role-Based Authorization, and logical bounds) before delegating the actual
 * transaction to the DAO.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class RefundRentServlet extends AbstractDatabaseServlet {

    /**
     * Handles the POST request submitted by the refund modal form.
     *
     * @param req the HTTP request containing parameters: targetUserId, rentId, and amount.
     * @param res the HTTP response used to redirect on success or send HTTP errors on failure.
     * @throws ServletException if a servlet-specific error occurs.
     * @throws IOException      if an I/O error occurs during the request handling.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setResource(req.getRequestURI());

        try {
            // authorization check, block standard users from accessing this endpoint
            AppUser loggedUser = (AppUser) req.getAttribute("AppUser");
            if (loggedUser.getUserRole().getId() == 0) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "You don't have permission to access this page.");
                return;
            }

            // prevent staff/admins from refunding their personal rentals
            int targetUserId = Integer.parseInt(req.getParameter("targetUserId"));
            if (targetUserId == loggedUser.getId()) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "Action denied: You cannot refund yourself.");
                return;
            }

            // parse the form data submitted from the modal
            int rentId = Integer.parseInt(req.getParameter("rentId"));
            double requestedAmount = Double.parseDouble(req.getParameter("amount"));

            // ensure the user didn't bypass HTML frontend validation to submit negative numbers or zero
            if (requestedAmount <= 0) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Refund amount must be greater than zero.");
                return;
            }

            // delegate the transaction to  ProcessRefundDAO.
            boolean success = new ProcessRefundDAO(getConnection(), rentId, requestedAmount).access().getOutputParam();

            // if success is false the database rejected the transaction
            if (!success) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid refund amount. Limit exceeded.");
                return;
            }

            // if the transaction succeeds, redirect the admin/staff to the profile they were viewing
            LOGGER.info("Refund of %.2f EUR processed successfully for rent %d (target user: %d) by staff/admin %d.",
                    requestedAmount, rentId, targetUserId, loggedUser.getId());

            res.sendRedirect(req.getContextPath() + "/auth/profile/" + targetUserId);

        } catch (NumberFormatException e) {
            LOGGER.error("Invalid number format in refund parameters.", e);
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Malformed input data.");

        } catch (Exception e) {
            LOGGER.error("Error processing refund.", e);
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An unexpected error occurred while processing the request.");

        } finally {
            LogContext.removeIPAddress();
            LogContext.removeResource();
        }

    }

}