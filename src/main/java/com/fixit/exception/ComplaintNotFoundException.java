package com.fixit.exception;

/**
 * Thrown when a complaint with the requested ID does not exist in the database.
 */
public class ComplaintNotFoundException extends Exception {
    public ComplaintNotFoundException(String message) {
        super(message);
    }
}
