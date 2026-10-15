package it.unipd.dei.webapp.servlet.authentication;



import org.mindrot.jbcrypt.BCrypt;
import it.unipd.dei.webapp.database.authentication.RegisterDAO;
import it.unipd.dei.webapp.database.stats.GetKPIDAO;
import it.unipd.dei.webapp.resource.authentication.AppUser;
import it.unipd.dei.webapp.resource.LogContext;
import it.unipd.dei.webapp.resource.Message;
import it.unipd.dei.webapp.resource.stats.KPI;
import it.unipd.dei.webapp.servlet.AbstractDatabaseServlet;
import it.unipd.dei.webapp.utils.EmailSender;
import it.unipd.dei.webapp.utils.JWTUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Pattern;


/**
 * Handles the registration of new users.
 * 
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class RegisterServlet extends AbstractDatabaseServlet {

    /**
     * Standard RFC 5322-compliant Regex for Email Address
     */
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    /**
     * Shows the registration form.
     *
     * @param request  the HTTP request.
     * @param response the HTTP response.
     * @throws ServletException if any error occurs.
     * @throws IOException      if any error occurs.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        LogContext.setIPAddress(request.getRemoteAddr());
        LogContext.setResource(request.getRequestURI());

        LOGGER.info("Showing registration form.");

        try {
            AppUser loggedUser = JWTUtils.getUserFromCookies(request);

            boolean loggedIn = (loggedUser != null);

            if (loggedIn) {
                LOGGER.info("User already logged in. Redirecting to profile.");
                response.sendRedirect(request.getContextPath() + "/auth/profile");
                return;
            }

            // Fetch KPIs for the registration page
            try {
                KPI kpi = new GetKPIDAO(getConnection()).access().getOutputParam();
                request.setAttribute("kpi", kpi);
            } catch (SQLException e) {
                LOGGER.error("Unable to fetch KPIs for registration page.", e);
            }

            request.getRequestDispatcher("/jsp/register.jsp").forward(request, response);
        }  finally {
            LogContext.removeIPAddress();
            LogContext.removeResource();
        }
    }

    /**
     * Processes the registration request.
     *
     * @param req the HTTP request.
     * @param res the HTTP response.
     * @throws ServletException if any error occurs.
     * @throws IOException      if any error occurs.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setResource(req.getRequestURI());

        LOGGER.info("Register user.");

        try {
            String email = req.getParameter("email");
            String password = req.getParameter("password");
            String confirmPassword = req.getParameter("confirmPassword"); // Fetch confirm password
            String firstName = req.getParameter("firstName");
            String lastName = req.getParameter("lastName");

            // Fetch KPIs for the registration page in case of error
            try {
                KPI kpi = new GetKPIDAO(getConnection()).access().getOutputParam();
                req.setAttribute("kpi", kpi);
            } catch (SQLException e) {
                LOGGER.error("Unable to fetch KPIs for registration page.", e);
            }

            // Check if the email is valid
            if (email == null || email.trim().isEmpty() || !EMAIL_PATTERN.matcher(email.trim()).matches()) {
                LOGGER.info("Registration failed: invalid email.");
                req.setAttribute("message", new Message("Invalid email. Please enter a valid address.", "E1A1", null));
                req.getRequestDispatcher("/jsp/register.jsp").forward(req, res);
                return;
            }

            email = email.trim();

            // Check if the password is valid
            if (password == null || password.trim().isEmpty() || password.length() < 8) {
                LOGGER.info("Registration failed for email %s: invalid password.", email);
                req.setAttribute("message", new Message("Password must be at least 8 characters long and cannot contain only spaces.", "E1A1", null));
                req.getRequestDispatcher("/jsp/register.jsp").forward(req, res);
                return;
            }

            // Check if passwords match
            if (!password.equals(confirmPassword)) {
                LOGGER.info("Registration failed for email %s: passwords do not match.", email);
                req.setAttribute("message", new Message("Passwords do not match. Please try again.", "E2A5", null));
                req.getRequestDispatcher("/jsp/register.jsp").forward(req, res);
                return;
            }

            if (firstName != null && firstName.trim().isEmpty()) {
                firstName = null;
            }
            if (lastName != null && lastName.trim().isEmpty()) {
                lastName = null;
            }

            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            AppUser newUser = new AppUser.UserBuilder()
                    .email(email)
                    .password(hashedPassword)
                    .firstName(firstName)
                    .lastName(lastName)
                    .isActive(true)
                    .build();

            RegisterDAO registerDAO = new RegisterDAO(getConnection(), newUser);
            AppUser registeredUser = registerDAO.access().getOutputParam();

            if (registeredUser == null) {
                if (registerDAO.isDuplicateEmail()) {
                    LOGGER.info("Registration failed: email %s already registered.", email);
                    req.setAttribute("message", new Message("Email already registered. Please login.", "E2A6", null));

                } else {
                    LOGGER.error("Registration failed for user %s: unknown database error.", email);
                    req.setAttribute("message", new Message("Registration failed. Please try again.", "E0A1", null));

                }
                req.getRequestDispatcher("/jsp/register.jsp").forward(req, res);

            } else {
                LOGGER.info("User %s successfully registered. Redirecting to home.", email);
                // send the email to the user as confirmation of its rent
                EmailSender.sendEmail(newUser.getEmail(), "Welcome in WISE!", "Thank for your registration!");

                try {
                    JWTUtils.setJWTCookieAndRedirect(registeredUser, req, res, req.getContextPath() + "/auth/profile");

                } catch (Exception e) {
                    LOGGER.error("Unable to generate JWT after registration for %s.", email, e);
                    res.sendRedirect(req.getContextPath() + "/login");

                }
            }

        } catch (SQLException e) {
            LOGGER.error("Unable to register user.", e);
            req.setAttribute("message", new Message("An error occurred while accessing the database.", "E0A1", e.getMessage()));
            req.getRequestDispatcher("/jsp/register.jsp").forward(req, res);

        } catch (Exception e) {
            throw new RuntimeException(e);

        } finally {
            LogContext.removeIPAddress();
            LogContext.removeResource();

        }
    }
}
