package it.unipd.dei.webapp.rest;

import it.unipd.dei.webapp.resource.Actions;
import it.unipd.dei.webapp.resource.LogContext;
import it.unipd.dei.webapp.resource.Message;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.Set;

/**
 * Strictly manages the lifecycle of a single REST operation.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public abstract class AbstractRR implements RestResource {
    /**
     * A LOGGER available for all the subclasses.
     */
    protected static final Logger LOGGER = LogManager.getLogger(AbstractRR.class);

    /**
     * The JSON MIME media type.
     */
    protected static final String JSON_MEDIA_TYPE = "application/json";

    /**
     * The JSON UTF-8 MIME media type.
     */
    protected static final String JSON_UTF_8_MEDIA_TYPE = "application/json; charset=utf-8";

    /**
     * The "any" MIME media type.
     */
    protected static final String ALL_MEDIA_TYPE = "*/*";

    /**
     * The set of supported HTTP methods.
     */
    private static final Set<String> SUPPORTED_METHODS = Set.of("GET", "POST", "PUT", "DELETE");

    /**
     * The set of methods that are supposed to send a body.
     */
    private static final Set<String> METHODS_WITH_BODY = Set.of("POST", "PUT");

    /**
     * The HTTP request.
     */
    protected final HttpServletRequest req;

    /**
     * The HTTP response.
     */
    protected final HttpServletResponse res;

    /**
     * The database connection.
     */
    protected final Connection con;

    /**
     * The {@link Actions} performed by the REST resource.
     */
    private final String action;

    /**
     * Creates a new REST resource.
     *
     * @param action the action performed.
     * @param req    the HTTP request.
     * @param res    the HTTP response.
     * @param con    the database connection.
     */
    public AbstractRR(final String action, final HttpServletRequest req, final HttpServletResponse res, final Connection con) {

        if (req == null) {
            LOGGER.error("The HTTP request cannot be null.");
            throw new NullPointerException("The HTTP request cannot be null.");
        }
        if (res == null) {
            LOGGER.error("The HTTP response cannot be null.");
            throw new NullPointerException("The HTTP response cannot be null.");
        }
        if (con == null) {
            LOGGER.error("The connection cannot be null.");
            throw new NullPointerException("The connection cannot be null.");
        }
        if (action == null || action.isBlank()) {
            LOGGER.warn("Action is null or empty.");
        }

        this.action = action;
        LogContext.setAction(action);
        this.req = req;
        this.res = res;
        this.con = con;
    }

    @Override
    public final void serve() throws IOException {
        try {
            // check if the method and the input/output media type are allowed
            if (!checkMethod() || !checkOutputMediaType()) {
                return;
            }

            doServe();
        } catch (Throwable t) {
            LOGGER.error("Unexpected error while processing the REST request.", t);
            final Message m = new Message(String.format("Unable to serve the REST request: %s.", action), "E0A3", t.getMessage());
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            m.toJSON(res.getOutputStream());
        } finally {
            LogContext.removeAction();
            LogContext.removeResource();
        }
    }

    /**
     * Performs the actual logic needed to serve the REST request.
     *
     * @throws IOException if any error occurs during the client/server communication.
     */
    protected abstract void doServe() throws IOException;

    /**
     * Validates the output media type.
     *
     * @return {@code true} if validation ends successfully; {@code false} otherwise.
     * @throws IOException if any error occurs during the client/server communication.
     */
    protected boolean checkOutputMediaType() throws IOException {
        final String accept = req.getHeader("Accept");
        final OutputStream out = res.getOutputStream();

        if (accept == null) {
            LOGGER.error("Cannot determine output media type: the Accept request header is missing.");
            final Message m = new Message("Cannot determine output media type.", "E0A7", "The Accept request header is missing.");
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            m.toJSON(out);
            return false;
        }

        if (!accept.contains(JSON_MEDIA_TYPE) && !accept.equals(ALL_MEDIA_TYPE)) {
            LOGGER.error("Unsupported output media type: {}. Only application/json is supported.", accept);
            final Message m = new Message("Unsupported output media type.", "E0A8", String.format("The format %s is not supported. Use application/json", accept));
            res.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            m.toJSON(out);
            return false;
        }

        return true;
    }

    /**
     * Validates the input media type.
     *
     * @return {@code true} if validation ends successfully; {@code false} otherwise.
     * @throws IOException if any error occurs during the client/server communication.
     */
    protected boolean checkInputMediaType() throws IOException {
        final String contentType = req.getHeader("Content-Type");
        final OutputStream out = res.getOutputStream();

        if (contentType == null) {
            LOGGER.error("Cannot determine input media type: the Content-Type request header is missing.");
            final Message m = new Message("Cannot determine input media type.", "E0A7", "The Content-Type request header is missing.");
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            m.toJSON(out);
            return false;
        }

        if (!contentType.contains(JSON_MEDIA_TYPE)) {
            LOGGER.error("Unsupported input media type: {}. Only application/json is supported.", contentType);
            final Message m = new Message("Unsupported input media type.", "E0A8", String.format("The format %s is not supported. Use application/json", contentType));
            res.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
            m.toJSON(out);
            return false;
        }

        return true;
    }

    /**
     * Validates the HTTP method.
     *
     * @return {@code true} if validation ends successfully; {@code false} otherwise.
     * @throws IOException if any error occurs during the client/server communication.
     */
    protected boolean checkMethod() throws IOException {
        final String method = req.getMethod();
        final OutputStream out = res.getOutputStream();

        if (!SUPPORTED_METHODS.contains(method)) {
            LOGGER.error("Unsupported operation: {}. Only {} are supported.", method, SUPPORTED_METHODS.toString());
            final Message m = new Message("Unsupported operation.", "E0A9", String.format("The operation %s is not supported. Choose between %s", method, SUPPORTED_METHODS.toString()));
            res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            m.toJSON(out);
            return false;
        }

        if (METHODS_WITH_BODY.contains(method)) {
            return checkInputMediaType();
        }

        return true;
    }
}
