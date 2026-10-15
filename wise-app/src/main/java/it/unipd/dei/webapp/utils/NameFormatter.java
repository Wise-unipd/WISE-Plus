package it.unipd.dei.webapp.utils;

/**
 * Utility class providing string manipulation and formatting operations.
 * <p>
 * This class is designed to standardize text inputs, specifically focusing on
 * proper noun capitalization across the application.
 * </p>
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class NameFormatter {

    /**
     * Private constructor to prevent instantiation.
     * This ensures the class acts strictly as a static utility container.
     */
    private NameFormatter() {
        throw new AssertionError("Utility classes cannot be instantiated.");
    }

    /**
     * Formats a name by capitalizing the first letter of each word.
     * <p>
     * It intelligently handles spaces, hyphens, and apostrophes, ensuring that
     * compound names are capitalized correctly (e.g., "jean-luc o'connor" becomes "Jean-Luc O'Connor").
     * </p>
     *
     * @param input the raw string to format.
     * @return the properly capitalized string, or the original input if it is null or empty.
     */
    public static String formatName(String input) {
        // Return immediately if the string is null or empty to prevent NullPointerExceptions
        if (input == null || input.trim().isEmpty()) {
            return input;
        }

        // Start with everything lowercase to fix erratic inputs
        input = input.trim().toLowerCase();
        StringBuilder result = new StringBuilder(input.length());

        // The first character should always be capitalized
        boolean capitalizeNext = true;

        for (char c : input.toCharArray()) {
            if (Character.isWhitespace(c) || c == '-' || c == '\'' || c == '.') {
                // If the current character is a separator, append it, and mark the NEXT character for capitalization
                capitalizeNext = true;
                result.append(c);
            } else if (capitalizeNext) {
                // Capitalize the current character
                result.append(Character.toTitleCase(c));
                capitalizeNext = false;
            } else {
                // Keep the character lowercase
                result.append(c);
            }
        }

        return result.toString();
    }
}