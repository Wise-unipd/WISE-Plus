package it.unipd.dei.webapp.utils;

import java.security.SecureRandom;
import java.util.Base64;


/**
 * Utility class for generating secure tokens.
 * 
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class TokenGenerator {
    /**
     * A secure random number generator.
     */
    private static final SecureRandom secureRandom = new SecureRandom();

    /**
     * A Base64 encoder.
     */
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder().withoutPadding();

    /**
     * Generates a 256-bit secure token encoded in Base64.
     *
     * @return a secure token.
     */
    public static String generateToken() {
        byte[] randomBytes = new byte[32]; // 256-bit token
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }
}
