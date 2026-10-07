package com.fixit.exception;

/**
 * Thrown when an illegal complaint lifecycle transition is attempted
 * (e.g., PENDING -> RESOLVED or ASSIGNED -> CANCELLED).
 */
public class InvalidStatusTransitionException extends Exception {
    public InvalidStatusTransitionException(String message) {
        super(message);
    }
}
