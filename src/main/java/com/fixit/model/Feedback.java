package com.fixit.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Feedback entity representing student evaluation submitted after complaint resolution.
 * Demonstrates Encapsulation and Data Validation.
 */
public class Feedback {
    private String id;                 // MongoDB document _id
    private String feedbackId;        // e.g., FB-1001
    private String complaintId;       // Associated resolved complaint
    private String studentId;         // Author of the feedback
    private int rating;               // Value between 1 (poor) and 5 (excellent)
    private String comment;
    private LocalDateTime timestamp;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Feedback() {
        this.timestamp = LocalDateTime.now();
    }

    public Feedback(String feedbackId, String complaintId, String studentId, int rating, String comment) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be an integer between 1 and 5.");
        }
        this.feedbackId = feedbackId;
        this.complaintId = complaintId;
        this.studentId = studentId;
        this.rating = rating;
        this.comment = comment;
        this.timestamp = LocalDateTime.now();
    }

    public Feedback(String id, String feedbackId, String complaintId, String studentId, int rating, String comment, LocalDateTime timestamp) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be an integer between 1 and 5.");
        }
        this.id = id;
        this.feedbackId = feedbackId;
        this.complaintId = complaintId;
        this.studentId = studentId;
        this.rating = rating;
        this.comment = comment;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(String feedbackId) {
        this.feedbackId = feedbackId;
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

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

    @Override
    public String toString() {
        return "Feedback{" +
                "feedbackId='" + feedbackId + '\'' +
                ", complaintId='" + complaintId + '\'' +
                ", studentId='" + studentId + '\'' +
                ", rating=" + rating + "/5" +
                ", comment='" + comment + '\'' +
                ", submittedAt=" + getFormattedTimestamp() +
                '}';
    }
}
