package com.fixit.service;

import com.fixit.exception.ComplaintNotFoundException;
import com.fixit.exception.InvalidStatusTransitionException;
import com.fixit.model.*;
import com.fixit.repository.AdminRepository;
import com.fixit.repository.MaintenanceStaffRepository;
import com.fixit.util.InputValidator;

import java.util.List;

/**
 * Service layer managing Administrator operations:
 * viewing complaints, advanced search/filter, technician assignment, and resolution monitoring.
 */
public class AdminService {
    private final AdminRepository adminRepository;
    private final ComplaintService complaintService;
    private final MaintenanceStaffRepository staffRepository;
    private final FeedbackService feedbackService;

    public AdminService() {
        this.adminRepository = new AdminRepository();
        this.complaintService = new ComplaintService();
        this.staffRepository = new MaintenanceStaffRepository();
        this.feedbackService = new FeedbackService();
    }

    public AdminService(AdminRepository adminRepository, ComplaintService complaintService,
                        MaintenanceStaffRepository staffRepository, FeedbackService feedbackService) {
        this.adminRepository = adminRepository;
        this.complaintService = complaintService;
        this.staffRepository = staffRepository;
        this.feedbackService = feedbackService;
    }

    /**
     * Authenticates administrator by email and password.
     */
    public Admin login(String email, String password) {
        if (!InputValidator.isNotEmpty(email) || !InputValidator.isNotEmpty(password)) {
            return null;
        }
        return adminRepository.validateLogin(email, password);
    }

    /**
     * Retrieves all campus maintenance complaints.
     */
    public List<Complaint> getAllComplaints() {
        return complaintService.getAllComplaints();
    }

    /**
     * Searches a single complaint by complaint ID.
     */
    public Complaint searchComplaintById(String complaintId) throws ComplaintNotFoundException {
        return complaintService.getComplaintById(complaintId);
    }

    /**
     * Filters complaints by category keyword.
     */
    public List<Complaint> filterByCategory(String category) {
        return complaintService.searchComplaintsByCategory(category);
    }

    /**
     * Filters complaints by location keyword.
     */
    public List<Complaint> filterByLocation(String location) {
        return complaintService.searchComplaintsByLocation(location);
    }

    /**
     * Filters complaints by priority level.
     */
    public List<Complaint> filterByPriority(Priority priority) {
        return complaintService.filterComplaintsByPriority(priority);
    }

    /**
     * Retrieves complaints by status (e.g. PENDING, IN_PROGRESS, RESOLVED).
     */
    public List<Complaint> getComplaintsByStatus(Status status) {
        return complaintService.getComplaintsByStatus(status);
    }

    /**
     * Assigns a maintenance staff member to a complaint.
     * Validates that both the complaint and the staff member exist.
     */
    public boolean assignMaintenanceStaff(String complaintId, String staffId)
            throws ComplaintNotFoundException, InvalidStatusTransitionException, IllegalArgumentException {

        // Validate staff existence
        MaintenanceStaff staff = staffRepository.findStaffById(staffId);
        if (staff == null) {
            throw new IllegalArgumentException("No maintenance staff found with Staff ID: " + staffId);
        }

        // Assign technician on complaint
        boolean assigned = complaintService.assignStaff(complaintId, staff.getStaffId());
        if (assigned) {
            // Update technician's assigned complaint list
            staffRepository.addComplaintToStaff(staff.getStaffId(), complaintId.trim().toUpperCase());
        }

        return assigned;
    }
public boolean updateComplaintStatus(
        String complaintId,
        String status) throws Exception {

    Status newStatus = Status.fromString(status);

    return complaintService.updateStatus(
            complaintId,
            newStatus
    );
}
    /**
     * Lists all registered maintenance staff members for assignment.
     */
    public List<MaintenanceStaff> getAllStaff() {
        return staffRepository.findAllStaff();
    }

    /**
     * Retrieves student feedback for a resolved complaint.
     */
    public Feedback getFeedbackForComplaint(String complaintId) {
        return feedbackService.getFeedbackByComplaintId(complaintId);
    }

    /**
     * Retrieves all student feedback submissions.
     */
    public List<Feedback> getAllFeedback() {
        return feedbackService.getAllFeedback();
    }
}
