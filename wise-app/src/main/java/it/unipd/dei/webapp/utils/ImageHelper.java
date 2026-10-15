package it.unipd.dei.webapp.utils;

import it.unipd.dei.webapp.resource.Message;
import jakarta.servlet.http.Part;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Utility class for handling image validation and storage.
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class ImageHelper {

    /**
     * The required aspect ratio for the poster image.
     */
    private static final double POSTER_RATIO = 0.68;

    /**
     * The tolerance to apply to the actual aspect ratio.
     */
    private static final double TOLERANCE = 0.05;

    /**
     * The minimum width required for the poster image.
     */
    private static final int MIN_WIDTH = 600;

    /**
     * The folder to upload posters to.
     */
    public static final String UPLOAD_DIR = System.getProperty("UPLOAD_PATH");

    private ImageHelper() {
        // Prevent instantiation of utility class
    }

    /**
     * Validates that the uploaded part is an image and matches the required dimensions.
     *
     * @param filePart the uploaded file part.
     * @return null if valid, or a Message object containing the error.
     */
    public static Message validatePoster(Part filePart) {
        // Nothing to validate
        if (filePart == null || filePart.getSize() == 0) {
            return null;
        }

        // Formats allowed
        String photoMediaType = filePart.getContentType();
        if (photoMediaType != null) {
            photoMediaType = photoMediaType.toLowerCase().trim();
        }
        boolean isValidMime = photoMediaType != null && (
            photoMediaType.equals("image/jpeg") ||
            photoMediaType.equals("image/png")
        );

        if (!isValidMime) {
            return new Message( String.format("Unsupported MIME media type %s for poster.", photoMediaType), "E1A1", null);
        }

        // Obtain an InputStream to read Part bytes
        try (InputStream fileContent = filePart.getInputStream()) {
            // Read an image file
            BufferedImage image = ImageIO.read(fileContent);

            // Check if the file is actually an image
            if (image == null) {
                return new Message("The uploaded file is not a valid image.", "E1A1", null);
            }

            // Check minimum dimensions
            if (image.getWidth() < MIN_WIDTH) {
                return new Message(
                    String.format("Poster too small. Minimum size is %dpx.", MIN_WIDTH),
                    "E1A1", null);
            }

            double ratio = (double) image.getWidth() / image.getHeight();

            if (Math.abs(ratio - POSTER_RATIO) > TOLERANCE) {
                return new Message(
                    String.format("Invalid aspect ratio. Provide a vertical poster."),
                    "E1A1", null);
            }
        } catch (IOException e) {
            return new Message("Error reading the image file.", "E0A2", e.getMessage());
        }

        // Image is perfectly valid
        return null; 
    }

    /**
     * Saves the validated poster to the server's upload directory.
     *
     * @param filePart        the uploaded file part.
     * @return the unique name of the saved file (UUID-based).
     * @throws IOException if the file cannot be written.
     */
    public static String savePoster(Part filePart) throws IOException {
        String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();

        String extension = "";
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = fileName.substring(dotIndex).toLowerCase();
        }

        String newFileName = UUID.randomUUID().toString() + extension;

        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String exactFilePath = UPLOAD_DIR + File.separator + newFileName;
        filePart.write(exactFilePath);

        return newFileName;
    }

    /**
     * Delete the poster with the specified name.
     * 
     * @param posterName      the name of the poster to delete.
     */
    public static void deletePoster(String posterName) {
        if (posterName == null || posterName.isEmpty()) return;

        File file = new File(UPLOAD_DIR + File.separator + posterName);
        if (file.exists()) {
            file.delete();
        }
    }
}