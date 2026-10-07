package com.fixit.util;

import com.fixit.model.Priority;
import com.fixit.model.Status;

import java.util.regex.Pattern;

/**
 * Utility class providing validation methods for user input.
 * Ensures data integrity before passing objects to the repository/database layer.
 */
public class InputValidator {

    // RFC 5322 compliant simplified email regex
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    /**
     * Validates that a string is non-null and not just whitespace.
     */
    public static boolean isNotEmpty(String text) {
        return text != null && !text.trim().isEmpty();
    }

    /**
     * Validates email format.
     */
    public static boolean isValidEmail(String email) {
        if (!isNotEmpty(email)) return false;
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Validates password strength (minimum 4 characters, non-blank).
     */
    public static boolean isValidPassword(String password) {
        return isNotEmpty(password) && password.trim().length() >= 4;
    }

    /**
     * Validates feedback rating (1 to 5 inclusive).
     */
    public static boolean isValidRating(int rating) {
        return rating >= 1 && rating <= 5;
    }

    /**
     * Validates whether a priority string matches one of the valid enum values.
     */
    public static boolean isValidPriority(String priorityStr) {
        if (!isNotEmpty(priorityStr)) return false;
        try {
            Priority.fromString(priorityStr);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Validates whether a status string matches one of the valid enum values.
     */
    public static boolean isValidStatus(String statusStr) {
        if (!isNotEmpty(statusStr)) return false;
        try {
            Status.fromString(statusStr);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
