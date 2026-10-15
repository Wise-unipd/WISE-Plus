package it.unipd.dei.webapp.resource;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.StringFormatterMessageFactory;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Abstract base class for all REST resources in the application.
 * It provides the common infrastructure for JSON serialization, setting up
 * the JSON factory and Object Mapper, and handling standard I/O validation and logging.
 * 
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public abstract class AbstractResource implements Resource {
    /**
     * A Logger available for all subclasses to track serialization events or errors.
     */
    protected static final Logger LOGGER = LogManager.getLogger(AbstractResource.class, StringFormatterMessageFactory.INSTANCE);

    /**
     * A central MAPPER used to bridge Java objects and JSON data.
     * Configured to skip empty or null fields.
     */
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * The JSON factory used to create JSON generators and parsers.
     * Configured to not close underlying streams automatically to support Servlet environments.
     */
    protected static final JsonFactory JSON_FACTORY;

    static {
        // Configure the mapper to skip empty or null fields
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        // Set up the JSON factory
        JSON_FACTORY = new JsonFactory();

        // Prevent the generator from automatically closing the underlying target (OutputStream)
        // This is necessary for servlets, as Tomcat manages the Response OutputStream lifecycle.
        JSON_FACTORY.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);

        // Prevent the parser from automatically closing the underlying source (InputStream)
        JSON_FACTORY.disable(JsonParser.Feature.AUTO_CLOSE_SOURCE);

        LOGGER.info("JSON factory and ObjectMapper successfully setup with NON_EMPTY inclusion.");
    }

    /**
     * Public entry point defined by the {@link Resource} interface.
     * Validates the output stream and manages high-level error handling and logging.
     *
     * @param out the output stream where the JSON data will be written.
     * @throws IOException if the stream is null or if an error occurs during serialization.
     */
    @Override
    public void toJSON(final OutputStream out) throws IOException {

        if (out == null) {
            LOGGER.error("The output stream cannot be null.");
            throw new IOException("The output stream cannot be null.");
        }

        try {
            writeJSON(out);
        } catch (Exception e) {
            LOGGER.error("Unable to serialize the resource to JSON.", e);
            throw new IOException("Unable to serialize the resource to JSON.", e);
        }

    }

    /**
     * Internal bridge method that creates a {@link JsonGenerator} and ensures it is flushed.
     * This is protected as it is an internal part of the resource.
     *
     * @param out the output stream where the JSON data will be written.
     * @throws Exception if any error occurs during the specific serialization logic.
     */
    protected void writeJSON(OutputStream out) throws Exception {
        LOGGER.debug("Serializing resource to JSON...");
        try (JsonGenerator jg = JSON_FACTORY.createGenerator(out)) {
            MAPPER.writeValue(jg, this);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Exported JSON: %s", MAPPER.writeValueAsString(this));
            }

            jg.flush();
        }
    }
}