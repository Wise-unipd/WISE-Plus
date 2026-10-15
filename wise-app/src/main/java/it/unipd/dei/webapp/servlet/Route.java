package it.unipd.dei.webapp.servlet;

import java.util.regex.Pattern;

import it.unipd.dei.webapp.rest.RestResource;

/**
 * Binds an HTTP method and a URI to a REST resource.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class Route {
    /**
     * The HTTP method (e.g., GET, POST, PUT, DELETE).
     */
    private final String method;

    /**
     * The compiled regular epression pattern for the URI.
     */
    private final Pattern pattern;

    /**
     * The REST resource that serves the request.
     */
    private final Class<? extends RestResource> resourceClass;

    /**
     * Creates a new route mapping.
     *
     * @param method        the HTTP method.
     * @param regex         the regular expression.
     * @param resourceClass the REST resource associated with this route.
     */
    public Route(String method, String regex, Class<? extends RestResource> resourceClass) {
        this.method = method;
        this.pattern = Pattern.compile(regex);
        this.resourceClass = resourceClass;
    }

    /**
     * Returns the HTTP method.
     *
     * @return the HTTP method.
     */
    public String getMethod() {
        return method;
    }

    /**
     * Returns the regular expression pattern.
     *
     * @return the regular expression pattern.
     */
    public Pattern getPattern() {
        return pattern;
    }

    /**
     * Returns the REST resource.
     *
     * @return the REST resource.
     */
    public Class<? extends RestResource> getResourceClass() {
        return resourceClass;
    }
}
