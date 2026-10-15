package it.unipd.dei.webapp.utils;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.*;
import com.nimbusds.jwt.*;
import it.unipd.dei.webapp.resource.authentication.AppUser;
import it.unipd.dei.webapp.resource.authentication.UserRole;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Date;

/**
 * Utility class for managing JSON Web Tokens (JWT).
 * 
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class JWTUtils {

    /**
     * A LOGGER for logging events.
     */
    private static final Logger LOGGER = LogManager.getLogger(JWTUtils.class);

    /**
     * Secret key for JWT signing. Should be at least 256 bits.
     */
    private static final String SECRET_KEY = getSecretKey();

    /**
     * Helper method to retrieve the secret key from environment variables or system properties.
     *
     * @return the secret key.
     */
    private static String getSecretKey() {
        String secret = System.getenv("JWT_SECRET");
        if (secret == null || secret.isEmpty()) {
            secret = System.getProperty("JWT_SECRET");
        }
        if (secret == null || secret.isEmpty()) {
            secret = "this_is_a_very_secret_and_long_key_for_jwt_256_bits_minimum_32_chars";
            LOGGER.warn("JWT_SECRET not found in environment or system properties. Using default insecure key.");
        }
        return secret;
    }

    /**
     * Token expiration time (1 hour).
     */
    private static final long EXPIRATION_TIME = 3600 * 1000 * 2;

    /**
     * Generates a JWT, sets it in a cookie and redirects to the given URL.
     *
     * @param user        the user for whom the token is generated.
     * @param req         the HTTP request.
     * @param res         the HTTP response.
     * @param redirectUrl the URL to redirect to.
     * @throws JOSEException if there is an error signing the token.
     * @throws IOException   if there is an error during redirection.
     */
    public static void setJWTCookieAndRedirect(AppUser user, HttpServletRequest req, HttpServletResponse res, String redirectUrl) throws JOSEException, IOException {
        setJWTCookie(user, req, res);
        res.sendRedirect(redirectUrl);
    }

    /**
     * Generates a JWT and sets it in a cookie.
     * Also sets a non-HttpOnly cookie for JS access if needed.
     *
     * @param user the user for whom the token is generated.
     * @param req  the HTTP request.
     * @param res  the HTTP response.
     * @throws JOSEException if there is an error signing the token.
     */
    public static void setJWTCookie(AppUser user, HttpServletRequest req, HttpServletResponse res) throws JOSEException {
        String token = generateToken(user);

        // HttpOnly cookie for server-side security
        Cookie jwtCookie = new Cookie("JWT_TOKEN", token);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath(req.getContextPath().isEmpty() ? "/" : req.getContextPath());
        jwtCookie.setMaxAge((int) (EXPIRATION_TIME / 1000));
        res.addCookie(jwtCookie);
    }

    /**
     * Generates a JWT for the given user.
     *
     * @param user the user for whom the token is generated.
     * @return the serialized JWT.
     * @throws JOSEException if there is an error signing the token.
     */
    public static String generateToken(AppUser user) throws JOSEException {
        JWSSigner signer = new MACSigner(SECRET_KEY.getBytes());

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .issuer("it.unipd.dei.webapp.wise")
                .expirationTime(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .claim("id", user.getId())
                .claim("firstName", user.getFirstName())
                .claim("lastName", user.getLastName())
                .claim("roleId", user.getUserRole().getId())
                .claim("roleDesc", user.getUserRole().getDescription())
                .build();

        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
        signedJWT.sign(signer);

        return signedJWT.serialize();
    }

    /**
     * Validates a JWT and returns the corresponding AppUser.
     *
     * @param token the JWT to validate.
     * @return the reconstructed AppUser if the token is valid, null otherwise.
     */
    public static AppUser validateToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = new MACVerifier(SECRET_KEY.getBytes());

            if (signedJWT.verify(verifier)) {
                JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

                // Check expiration
                if (new Date().after(claims.getExpirationTime())) {
                    LOGGER.warn("Token expired for user: {}", claims.getSubject());
                    return null;
                }

                // Reconstruct UserRole
                UserRole role = new UserRole.RoleBuilder()
                        .id(((Long) claims.getClaim("roleId")).intValue())
                        .description((String) claims.getClaim("roleDesc"))
                        .build();

                // Reconstruct AppUser
                return new AppUser.UserBuilder()
                        .id(((Long) claims.getClaim("id")).intValue())
                        .email(claims.getSubject())
                        .firstName((String) claims.getClaim("firstName"))
                        .lastName((String) claims.getClaim("lastName"))
                        .role(role)
                        .isActive(true) // Assuming active if authenticated
                        .build();
            } else {
                LOGGER.warn("Token signature verification failed.");
            }
        } catch (Exception e) {
            LOGGER.error("Error validating JWT: {}", e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves the user from the JWT token in the cookies or Authorization header of the given request.
     *
     * @param req the HTTP request.
     * @return the AppUser if a valid token is present, null otherwise.
     */
    public static AppUser getUser(HttpServletRequest req) {
        // 1. Try Authorization header (Bearer token)
        String authHeader = req.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            AppUser user = validateToken(token);
            if (user != null) {
                return user;
            }
        }

        // 2. Fallback to cookies
        return getUserFromCookies(req);
    }

    /**
     * Retrieves the user from the JWT token in the cookies of the given request.
     *
     * @param req the HTTP request.
     * @return the AppUser if a valid token is present, null otherwise.
     */
    public static AppUser getUserFromCookies(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("JWT_TOKEN".equals(cookie.getName())) {
                    return validateToken(cookie.getValue());
                }
            }
        }
        return null;
    }
}
