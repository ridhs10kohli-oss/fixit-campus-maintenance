package com.fixit.model;

/**
 * Priority represents the urgency level of a maintenance complaint.
 * Demonstrates Java Enum concept.
 */
public enum Priority {
    LOW,
    MEDIUM,
    HIGH,
    URGENT;

    /**
     * Safely parse priority from user input string (case-insensitive).
     */
    public static Priority fromString(String text) {
        if (text == null || text.trim().isEmpty()) {
            return MEDIUM; // sensible default
        }
        for (Priority p : Priority.values()) {
            if (p.name().equalsIgnoreCase(text.trim())) {
                return p;
            }
        }
        throw new IllegalArgumentException("Invalid priority value: '" + text + "'. Allowed: LOW, MEDIUM, HIGH, URGENT");
    }
}
