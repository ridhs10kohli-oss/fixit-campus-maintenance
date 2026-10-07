package com.fixit.service;

import com.fixit.exception.ComplaintNotFoundException;
import com.fixit.exception.InvalidComplaintException;
import com.fixit.exception.InvalidStatusTransitionException;
import com.fixit.model.Complaint;
import com.fixit.model.Priority;
import com.fixit.model.Status;
import com.fixit.repository.ComplaintRepository;
import com.fixit.util.InputValidator;

import java.util.List;

/**
 * Service layer containing core business logic for Complaint management,
 * validation, and status lifecycle transitions.
 */
public class ComplaintService {
    private final ComplaintRepository complaintRepository;

    public ComplaintService() {
        this.complaintRepository = new ComplaintRepository();
    }

    public ComplaintService(ComplaintRepository complaintRepository) {
        this.complaintRepository = complaintRepository;
    }

    /**
     * Validates input fields and registers a new complaint in PENDING status.
     */
    public Complaint createComplaint(String studentId, String category, String description,
                                     String location, Priority priority)
            throws InvalidComplaintException {

        // Business Validation
        if (!InputValidator.isNotEmpty(studentId)) {
            throw new InvalidComplaintException("Student ID cannot be empty.");
        }
        if (!InputValidator.isNotEmpty(category)) {
            throw new InvalidComplaintException("Complaint category cannot be empty.");
        }
        if (!InputValidator.isNotEmpty(description) || description.trim().length() < 5) {
            throw new InvalidComplaintException("Description must be at least 5 characters long.");
        }
        if (!InputValidator.isNotEmpty(location)) {
            throw new InvalidComplaintException("Location/Room number cannot be empty.");
        }

        if (priority == null) {
            priority = Priority.MEDIUM;
        }

        // Generate unique complaint ID (e.g., CMP-0001)
        String complaintId = complaintRepository.generateNextComplaintId();

        Complaint complaint = new Complaint(complaintId, studentId, category.trim(),
                description.trim(), location.trim(), priority);

        boolean success = complaintRepository.createComplaint(complaint);
        if (!success) {
            throw new RuntimeException("Database error: Could not save complaint.");
        }

        return complaint;
    }

    /**
     * Retrieves a complaint by ID, throwing ComplaintNotFoundException if absent.
     */
    public Complaint getComplaintById(String complaintId) throws ComplaintNotFoundException {
        if (!InputValidator.isNotEmpty(complaintId)) {
            throw new ComplaintNotFoundException("Complaint ID cannot be empty.");
        }
        Complaint complaint = complaintRepository.findComplaintById(complaintId.trim());
        if (complaint == null) {
            throw new ComplaintNotFoundException("No complaint found with ID: " + complaintId);
        }
        return complaint;
    }

    /**
     * Retrieves all complaints for a given student.
     */
    public List<Complaint> getComplaintsByStudent(String studentId) {
        return complaintRepository.findComplaintsByStudent(studentId);
    }

    /**
     * Retrieves all complaints across the institution.
     */
    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAllComplaints();
    }

    /**
     * Retrieves complaints by their current status.
     */
    public List<Complaint> getComplaintsByStatus(Status status) {
        return complaintRepository.findComplaintsByStatus(status);
    }

    /**
     * Searches complaints by category keyword.
     */
    public List<Complaint> searchComplaintsByCategory(String category) {
        return complaintRepository.findComplaintsByCategory(category);
    }

    /**
     * Searches complaints by location keyword.
     */
    public List<Complaint> searchComplaintsByLocation(String location) {
        return complaintRepository.findComplaintsByLocation(location);
    }

    /**
     * Filters complaints by priority level.
     */
    public List<Complaint> filterComplaintsByPriority(Priority priority) {
        return complaintRepository.findComplaintsByPriority(priority);
    }

    /**
     * Retrieves complaints assigned to a particular maintenance technician.
     */
    public List<Complaint> getComplaintsByStaff(String staffId) {
        return complaintRepository.findComplaintsByAssignedStaff(staffId);
    }

    /**
     * Enforces the strict complaint lifecycle:
     *   PENDING -> ASSIGNED -> IN_PROGRESS -> RESOLVED
     *   PENDING -> CANCELLED
     * 
     * Throws InvalidStatusTransitionException when an illegal transition is attempted.
     */
    public void validateStatusTransition(Status current, Status target)
            throws InvalidStatusTransitionException {

        if (current == null || target == null) {
            throw new InvalidStatusTransitionException("Status cannot be null.");
        }

        if (current == target) {
            throw new InvalidStatusTransitionException("Complaint is already in '" + current + "' status.");
        }

        if (current == Status.CANCELLED) {
            throw new InvalidStatusTransitionException("Complaint is CANCELLED. No further state changes are permitted.");
        }

        if (current == Status.RESOLVED) {
            throw new InvalidStatusTransitionException("Complaint is already RESOLVED. Completed complaints cannot be modified.");
        }

        if (current == Status.PENDING && target == Status.RESOLVED) {
            throw new InvalidStatusTransitionException("Invalid transition: PENDING -> RESOLVED is not allowed! " +
                    "A complaint must first be ASSIGNED and set to IN_PROGRESS.");
        }

        if (current == Status.PENDING && target == Status.IN_PROGRESS) {
            throw new InvalidStatusTransitionException("Invalid transition: PENDING -> IN_PROGRESS is not allowed! " +
                    "A complaint must first be ASSIGNED to a maintenance technician.");
        }

        if (current == Status.ASSIGNED && target == Status.RESOLVED) {
            throw new InvalidStatusTransitionException("Invalid transition: ASSIGNED -> RESOLVED is not allowed! " +
                    "Technician must first start work by changing status to IN_PROGRESS.");
        }

        if (current != Status.PENDING && target == Status.CANCELLED) {
            throw new InvalidStatusTransitionException("Cannot cancel complaint: Only PENDING complaints can be cancelled by students.");
        }

        if (!current.canTransitionTo(target)) {
            throw new InvalidStatusTransitionException("Invalid status transition from " + current + " to " + target + ".");
        }
    }

    /**
     * Transitions complaint status after validating lifecycle rules.
     */
    public boolean updateStatus(String complaintId, Status newStatus)
            throws ComplaintNotFoundException, InvalidStatusTransitionException {

        Complaint complaint = getComplaintById(complaintId);
        validateStatusTransition(complaint.getStatus(), newStatus);

        return complaintRepository.updateComplaintStatus(complaintId, newStatus);
    }

    /**
     * Admin assigns maintenance staff to a complaint.
     */
    public boolean assignStaff(String complaintId, String staffId)
            throws ComplaintNotFoundException, InvalidStatusTransitionException {

        Complaint complaint = getComplaintById(complaintId);

        // Can assign when PENDING or re-assign when ASSIGNED
        if (complaint.getStatus() != Status.PENDING && complaint.getStatus() != Status.ASSIGNED) {
            throw new InvalidStatusTransitionException(
                    "Cannot assign technician: Complaint is currently " + complaint.getStatus() +
                    ". Only PENDING or ASSIGNED complaints can be assigned."
            );
        }

        return complaintRepository.assignStaff(complaintId, staffId);
    }

    /**
     * Student cancels a complaint if it is still in PENDING state.
     */
    public boolean cancelComplaint(String complaintId, String studentId)
            throws ComplaintNotFoundException, InvalidStatusTransitionException {

        Complaint complaint = getComplaintById(complaintId);

        // Security check: must belong to the requesting student
        if (!complaint.getStudentId().equalsIgnoreCase(studentId)) {
            throw new SecurityException("Unauthorized: You can only cancel your own complaints.");
        }

        // Lifecycle check: only PENDING is eligible
        if (complaint.getStatus() != Status.PENDING) {
            throw new InvalidStatusTransitionException(
                    "Cannot cancel complaint: Status is " + complaint.getStatus() +
                    ". Only complaints in PENDING status can be cancelled."
            );
        }

        return complaintRepository.cancelComplaint(complaintId);
    }
}
