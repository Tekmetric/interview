package com.interview.util;

import java.util.regex.Pattern;
import lombok.experimental.UtilityClass;
import org.springframework.web.util.HtmlUtils;

/**
 * Utility class for sanitizing input using Spring's HtmlUtils.
 *
 * <p>Leverages Spring's built-in HTML escaping to prevent XSS attacks
 * while providing additional validation for specific field types.
 */
@UtilityClass
public class InputSanitizer {

    // Valid character patterns for different field types
    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\p{L}\\s'.-]+$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9\\s()\\-.]{1,20}$");

    /**
     * Sanitize input using Spring's HTML escaping.
     */
    public static String sanitize(String input) {
        if (input == null || input.trim().isEmpty()) {
            return input;
        }
        return HtmlUtils.htmlEscape(input.trim());
    }

    /**
     * Validate name fields (letters, spaces, apostrophes, hyphens, dots only).
     */
    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        return NAME_PATTERN.matcher(name.trim()).matches();
    }

    /**
     * Validate email format.
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Validate phone number format.
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return true; // Phone is optional
        }
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    /**
     * Check if input contains potentially unsafe content by comparing before/after sanitization.
     */
    public static boolean containsUnsafeContent(String input) {
        if (input == null) {
            return false;
        }
        String sanitized = sanitize(input);
        return !sanitized.equals(input.trim());
    }
}