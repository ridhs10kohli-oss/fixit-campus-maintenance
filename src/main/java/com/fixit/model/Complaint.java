package com.fixit.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Complaint entity representing a reported college campus maintenance issue.
 * Demonstrates Encapsulation and Enum usage.
 */
public class Complaint {
    private String id;                     // MongoDB document _id
    private String complaintId;            // e.g., CMP-1001
    private String studentId;              // College ID of reporting student
    private String category;               // e.g., Broken fan, AC, Wi-Fi, Plumbing
    private String description;
    private String location;               // e.g., Room 204, Block B
    private Priority priority;             // LOW, MEDIUM, HIGH, URGENT
    private LocalDateTime timestamp;
    private Status status;                 // PENDING, ASSIGNED, IN_PROGRESS, RESOLVED, CANCELLED
    private String assignedStaffId;        // ID of assigned maintenance technician

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Complaint() {
        this.status = Status.PENDING;
        this.priority = Priority.MEDIUM;
        this.timestamp = LocalDateTime.now();
    }

    public Complaint(String complaintId, String studentId, String category, String description,
                     String location, Priority priority) {
        this.complaintId = complaintId;
        this.studentId = studentId;
        this.category = category;
        this.description = description;
        this.location = location;
        this.priority = priority != null ? priority : Priority.MEDIUM;
        this.status = Status.PENDING;
        this.timestamp = LocalDateTime.now();
        this.assignedStaffId = null;
    }

    public Complaint(String id, String complaintId, String studentId, String category, String description,
                     String location, Priority priority, LocalDateTime timestamp, Status status, String assignedStaffId) {
        this.id = id;
        this.complaintId = complaintId;
        this.studentId = studentId;
        this.category = category;
        this.description = description;
        this.location = location;
        this.priority = priority;
        this.timestamp = timestamp;
        this.status = status;
        this.assignedStaffId = assignedStaffId;
    }

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(String complaintId) {
        this.complaintId = complaintId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getFormattedTimestamp() {
        return timestamp != null ? timestamp.format(FORMATTER) : "N/A";
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getAssignedStaffId() {
        return assignedStaffId;
    }

    public void setAssignedStaffId(String assignedStaffId) {
        this.assignedStaffId = assignedStaffId;
    }

    @Override
    public String toString() {
        return "Complaint{" +
                "complaintId='" + complaintId + '\'' +
                ", studentId='" + studentId + '\'' +
                ", category='" + category + '\'' +
                ", location='" + location + '\'' +
                ", priority=" + priority +
                ", status=" + status +
                ", assignedStaffId='" + (assignedStaffId != null ? assignedStaffId : "Unassigned") + '\'' +
                ", reportedAt=" + getFormattedTimestamp() +
                '}';
    }
}
