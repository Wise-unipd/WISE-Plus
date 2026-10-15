package it.unipd.dei.webapp.utils;

import it.unipd.dei.webapp.resource.Message;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/**
 * Utility class providing credit card validation operations.
 * <p>
 * This class standardizes the server-side validation for payments,
 * checking card format, expiration date logic, CVV integrity, and
 * applying the Luhn algorithm to ensure the card number is mathematically valid.
 * </p>
 * * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public final class PaymentValidator {

    private static final Pattern CARD_PATTERN = Pattern.compile("^(?:\\d{4}\\s?){4}$|^\\d{16}$");
    private static final Pattern EXPIRY_PATTERN = Pattern.compile("^(0[1-9]|1[0-2])/([0-9]{2})$");
    private static final Pattern CVV_PATTERN = Pattern.compile("^[0-9]{3,4}$");


    /**
     * Private constructor to prevent instantiation.
     * This ensures the class acts strictly as a static utility container.
     */
    private PaymentValidator() {
        throw new AssertionError("Utility classes cannot be instantiated.");
    }

    /**
     * Validates the payment components to ensure card accuracy and date logic integrity.
     *
     * @param cardNumber the submitted credit card number.
     * @param expiryDate the submitted expiration date in MM/YY format.
     * @param cvv        the submitted 3 or 4 digit CVV code.
     * @return a Message object containing an error if validation fails, or null if it passes.
     */
    public static Message validatePayment(String cardNumber, String expiryDate, String cvv) {
        if (cardNumber == null || cardNumber.isBlank()) {
            return new Message("Card number is missing.", "E4A0", null);
        }
        if (expiryDate == null || expiryDate.isBlank()) {
            return new Message("Expiration date is missing.", "E4A0", null);
        }
        if (cvv == null || cvv.isBlank()) {
            return new Message("CVV code is missing.", "E4A0", null);
        }

        // Clean spaces for strict validation and Luhn algorithm
        String cleanCard = cardNumber.replaceAll("\\s+", "");

        // Pattern matching
        if (!CARD_PATTERN.matcher(cleanCard).matches()) {
            return new Message("Invalid card format. Must be 16 digits.", "E4A0", null);
        }
        if (!EXPIRY_PATTERN.matcher(expiryDate.trim()).matches()) {
            return new Message("Invalid expiration format. Use MM/YY.", "E4A0", null);
        }
        if (!CVV_PATTERN.matcher(cvv.trim()).matches()) {
            return new Message("Invalid CVV format. Must be 3 or 4 digits.", "E4A0", null);
        }

        // Mathematical check: Luhn algorithm
        if (!isValidLuhn(cleanCard)) {
            return new Message("Invalid credit card number. Please check your input.", "E4A0", null);
        }
        // Logic Validation: Date Expiration Context
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
            YearMonth expDate = YearMonth.parse(expiryDate.trim(), formatter);
            if (expDate.isBefore(YearMonth.now())) {
                return new Message("The provided credit card has expired.", "E4A0", null);
            }
        } catch (DateTimeParseException e) {
            return new Message("Invalid date structure detected.", "E4A0", null);
        }

        return null;
    }

    /**
     * Applies the Luhn algorithm to verify the checksum of the credit card number.
     *
     * @param cardNumber the 16-digit credit card number without spaces.
     * @return true if the card number passes the Luhn check, false otherwise.
     */
    private static boolean isValidLuhn(String cardNumber) {
        int sum = 0;
        boolean alternate = false;

        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(cardNumber.substring(i, i + 1));
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n = (n % 10) + 1;
                }
            }
            sum += n;
            alternate = !alternate;
        }
        return (sum % 10 == 0);
    }
}


