package com.fixit.model;

/**
 * Status represents the lifecycle states of a complaint.
 * 
 * Workflow:
 *   PENDING -> ASSIGNED -> IN_PROGRESS -> RESOLVED
 *   PENDING -> CANCELLED (student cancellation)
 */
public enum Status {
    PENDING,
    ASSIGNED,
    IN_PROGRESS,
    RESOLVED,
    CANCELLED;

    /**
     * Safely parse status from string (case-insensitive).
     */
    public static Status fromString(String text) {
        if (text == null || text.trim().isEmpty()) {
            return PENDING;
        }
        for (Status s : Status.values()) {
            if (s.name().equalsIgnoreCase(text.trim())) {
                return s;
            }
        }
        throw new IllegalArgumentException("Invalid status: '" + text + "'. Allowed: PENDING, ASSIGNED, IN_PROGRESS, RESOLVED, CANCELLED");
    }

    /**
     * Checks if transitioning from this status to target status is valid according to business rules.
     */
    public boolean canTransitionTo(Status target) {
        if (target == null) return false;
        
        switch (this) {
            case PENDING:
                // Can move to ASSIGNED (by admin) or CANCELLED (by student)
                return target == ASSIGNED || target == CANCELLED;
            case ASSIGNED:
                // Can move to IN_PROGRESS (by assigned staff) or re-ASSIGNED (by admin)
                return target == IN_PROGRESS || target == ASSIGNED;
            case IN_PROGRESS:
                // Can move to RESOLVED (by assigned staff)
                return target == RESOLVED;
            case RESOLVED:
            case CANCELLED:
                // Terminal states: no further transitions allowed
                return false;
            default:
                return false;
        }
    }
}
