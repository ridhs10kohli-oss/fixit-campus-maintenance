package com.fixit.exception;

/**
 * Thrown when complaint data fails business validation rules
 * (e.g., empty category, blank description, missing location).
 */
public class InvalidComplaintException extends Exception {
    public InvalidComplaintException(String message) {
        super(message);
    }
}
