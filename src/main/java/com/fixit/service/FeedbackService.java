package com.fixit.service;

import com.fixit.exception.ComplaintNotFoundException;
import com.fixit.model.Complaint;
import com.fixit.model.Feedback;
import com.fixit.model.Status;
import com.fixit.repository.FeedbackRepository;
import com.fixit.util.InputValidator;

import java.util.List;

/**
 * Service layer managing student ratings and feedback submissions.
 * Enforces business rule that complaints must be RESOLVED before feedback is permitted.
 */
public class FeedbackService {
    private final FeedbackRepository feedbackRepository;
    private final ComplaintService complaintService;

    public FeedbackService() {
        this.feedbackRepository = new FeedbackRepository();
        this.complaintService = new ComplaintService();
    }

    public FeedbackService(FeedbackRepository feedbackRepository, ComplaintService complaintService) {
        this.feedbackRepository = feedbackRepository;
        this.complaintService = complaintService;
    }

    /**
     * Submits feedback for a resolved complaint.
     */
    public Feedback submitFeedback(String complaintId, String studentId, int rating, String comment)
            throws ComplaintNotFoundException, IllegalStateException, IllegalArgumentException {

        // Validate rating range
        if (!InputValidator.isValidRating(rating)) {
            throw new IllegalArgumentException("Rating must be an integer between 1 (poor) and 5 (excellent).");
        }

        // Fetch complaint
        Complaint complaint = complaintService.getComplaintById(complaintId);

        // Security check: complaint must belong to the submitting student
        if (!complaint.getStudentId().equalsIgnoreCase(studentId)) {
            throw new SecurityException("Unauthorized: You can only submit feedback for your own complaints.");
        }

        // Business rule: Complaint must be RESOLVED
        if (complaint.getStatus() != Status.RESOLVED) {
            throw new IllegalStateException("Feedback cannot be submitted: Complaint is currently " +
                    complaint.getStatus() + ". Only RESOLVED complaints can receive feedback.");
        }

        // Check if feedback already submitted
        Feedback existing = feedbackRepository.findFeedbackByComplaintId(complaintId);
        if (existing != null) {
            throw new IllegalStateException("Feedback has already been submitted for complaint " + complaintId + ".");
        }

        String feedbackId = feedbackRepository.generateNextFeedbackId();
        Feedback feedback = new Feedback(feedbackId, complaintId, studentId, rating,
                comment != null ? comment.trim() : "");

        boolean success = feedbackRepository.createFeedback(feedback);
        if (!success) {
            throw new RuntimeException("Database error: Could not save feedback.");
        }

        return feedback;
    }

    /**
     * Finds feedback associated with a complaint.
     */
    public Feedback getFeedbackByComplaintId(String complaintId) {
        if (!InputValidator.isNotEmpty(complaintId)) return null;
        return feedbackRepository.findFeedbackByComplaintId(complaintId);
    }

    /**
     * Retrieves all submitted feedback reports.
     */
    public List<Feedback> getAllFeedback() {
        return feedbackRepository.findAllFeedback();
    }
}
