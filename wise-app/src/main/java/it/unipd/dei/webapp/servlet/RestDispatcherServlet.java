package it.unipd.dei.webapp.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import it.unipd.dei.webapp.resource.authentication.AppUser;
import it.unipd.dei.webapp.resource.LogContext;
import it.unipd.dei.webapp.resource.Message;
//import it.unipd.dei.webapp.rest.*;
import it.unipd.dei.webapp.rest.RestResource;
import it.unipd.dei.webapp.rest.SearchRentalsByTextRR;
import it.unipd.dei.webapp.rest.SearchUsersByTextRR;
import it.unipd.dei.webapp.rest.SearchWorkersByTextRR;
import it.unipd.dei.webapp.rest.catalog.AddLikeRR;
import it.unipd.dei.webapp.rest.catalog.AddToWishlistRR;
import it.unipd.dei.webapp.rest.catalog.RemoveFromWishlistRR;
import it.unipd.dei.webapp.rest.catalog.RemoveLikeRR;
import it.unipd.dei.webapp.rest.movie.*;
import it.unipd.dei.webapp.utils.JWTUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Dispatches HTTP requests to the proper {@link RestResource} based on the URI and method.
 * The dispatcher uses:
 * - Regular expressions to find the route.
 * - Java Reflection to instantiate the required REST resource dynamically.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class RestDispatcherServlet extends AbstractDatabaseServlet {
    /**
     * The JSON UTF-8 MIME media type.
     */
    private static final String JSON_UTF_8_MEDIA_TYPE = "application/json; charset=utf-8";

    /**
     * The list of routes.
     */
    private static final List<Route> ROUTES = new ArrayList<>();

    static {
        /**
         * REGEX TIPS:
         * ----------------
         * 1. [0-9+]      -> Matches one or more digits.
         * 2. [^/]+       -> Matches any character EXCEPT the slash.
         * 3. (sub-regex) -> Capturing group.
         * 4. /?          -> The trailing slash is optional.
         */
        ROUTES.add(new Route("POST", "/wishlist/movie/([0-9]+)/?", AddToWishlistRR.class));
        ROUTES.add(new Route("DELETE", "/wishlist/movie/([0-9]+)/?", RemoveFromWishlistRR.class));
        ROUTES.add(new Route("POST", "/movie/([0-9]+)/like/?", AddLikeRR.class));
        ROUTES.add(new Route("DELETE", "/movie/([0-9]+)/like/?", RemoveLikeRR.class));
        ROUTES.add(new Route("GET", "/search/?", SearchMoviesByTextRR.class));
        ROUTES.add(new Route("GET", "/movies/?", StaffSearchMoviesByTextRR.class));
        ROUTES.add(new Route("GET", "/workers/?", SearchWorkersByTextRR.class));
        ROUTES.add(new Route("GET", "/profiles/?", SearchUsersByTextRR.class));
        ROUTES.add(new Route("GET", "/rentals/?", SearchRentalsByTextRR.class));
        ROUTES.add(new Route("POST", "/movie/([0-9]+)/genre/([0-9]+)/?", AddGenreToMovieRR.class));
        ROUTES.add(new Route("DELETE", "/movie/([0-9]+)/genre/([0-9]+)/?", RemoveGenreFromMovieRR.class));
        ROUTES.add(new Route("POST", "/movie/([0-9]+)/audio/([0-9]+)/?", AddAudioToMovieRR.class));
        ROUTES.add(new Route("DELETE", "/movie/([0-9]+)/audio/([0-9]+)/?", RemoveAudioFromMovieRR.class));
        ROUTES.add(new Route("POST", "/movie/([0-9]+)/subtitle/([0-9]+)/?", AddSubtitleToMovieRR.class));
        ROUTES.add(new Route("DELETE", "/movie/([0-9]+)/subtitle/([0-9]+)/?", RemoveSubtitleFromMovieRR.class));
        ROUTES.add(new Route("POST", "/movie/([0-9]+)/category/([0-9]+)/?", AddCategoryToMovieRR.class));
        ROUTES.add(new Route("DELETE", "/movie/([0-9]+)/category/([0-9]+)/?", RemoveCategoryFromMovieRR.class));
        ROUTES.add(new Route("POST", "/movie/([0-9]+)/worker/([0-9]+)/role/([0-9]+)/?", AddCreditToMovieRR.class));
        ROUTES.add(new Route("DELETE", "/movie/([0-9]+)/worker/([0-9]+)/role/([0-9]+)/?", RemoveCreditFromMovieRR.class));
    }

    @Override
    protected void service(final HttpServletRequest req, final HttpServletResponse res) throws IOException {
        LogContext.setIPAddress(req.getRemoteAddr());
        final OutputStream out = res.getOutputStream();

        // all REST responses are JSON
        res.setContentType(JSON_UTF_8_MEDIA_TYPE);

        try {
            // retrieves the user from the JWT header (or cookies as fallback)
            AppUser loggedUser = JWTUtils.getUser(req);
            boolean loggedIn = (loggedUser != null);

            // checks if the user is logged in (since our operations are under auth)
            if (!loggedIn) {
                LOGGER.warn("Unauthorized REST access attempt to: %s", req.getRequestURI());
                final Message m = new Message("Authentication required.", "E2A0", null);
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                m.toJSON(out);
                return;
            }

            String path = req.getPathInfo();
            String method = req.getMethod();
            Matcher matcher = null;
            Route machedRoute = null;

            for (Route route : ROUTES) {
                // create a "comparison engine"
                matcher = route.getPattern().matcher(path);
                // check if the method and the COMPLETE URI match the defined route
                if (route.getMethod().equalsIgnoreCase(method) && matcher.matches()) {
                    machedRoute = route;
                    break;
                }
            }

            if (machedRoute != null) {
                Map<String, String> restParameters = new HashMap<>();

                // extract capturing groups and store them in a map
                for (int i = 1; i <= matcher.groupCount(); i++) {
                    restParameters.put("param" + i, matcher.group(i));
                }

                // make parameters easily accessible for the REST resource
                req.setAttribute("restParameters", restParameters);
                req.setAttribute("AppUser", loggedUser);

                // dynamically find the constructor
                Constructor<? extends RestResource> constructor = machedRoute.getResourceClass().getConstructor(HttpServletRequest.class, HttpServletResponse.class, Connection.class);

                // instantiate the REST resource
                RestResource rr = constructor.newInstance(req, res, getConnection());
                // delegate the job
                rr.serve();
            } else {
                LOGGER.warn("Unknown resource requested: %s %s.", method, req.getRequestURI());
                final Message m = new Message("Unknown resource requested.", "E0A6", String.format("No route matches %s %s.", method, path));
                res.setStatus(HttpServletResponse.SC_NOT_FOUND);
                m.toJSON(out);
            }
        } catch (Throwable t) {
            LOGGER.error("Unexpected error while processing REST resource.", t);
            final Message m = new Message("Unexpected error.", "E0A2", t.getMessage());
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            m.toJSON(out);
        } finally {
            // flush and close the output stream
            if (out != null) {
                out.flush();
                out.close();
            }

            LogContext.removeIPAddress();
        }
    }
}
