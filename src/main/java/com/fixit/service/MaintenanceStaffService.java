package com.fixit.service;

import com.fixit.exception.ComplaintNotFoundException;
import com.fixit.exception.InvalidStatusTransitionException;
import com.fixit.model.Complaint;
import com.fixit.model.MaintenanceStaff;
import com.fixit.model.Status;
import com.fixit.repository.MaintenanceStaffRepository;
import com.fixit.util.InputValidator;

import java.util.List;

/**
 * Service layer managing Maintenance Staff operations:
 * authentication, viewing assignments, and updating task progress to IN_PROGRESS and RESOLVED.
 */
public class MaintenanceStaffService {
    private final MaintenanceStaffRepository staffRepository;
    private final ComplaintService complaintService;

    public MaintenanceStaffService() {
        this.staffRepository = new MaintenanceStaffRepository();
        this.complaintService = new ComplaintService();
    }

    public MaintenanceStaffService(MaintenanceStaffRepository staffRepository, ComplaintService complaintService) {
        this.staffRepository = staffRepository;
        this.complaintService = complaintService;
    }

    /**
     * Authenticates maintenance staff member by email and password.
     */
    public MaintenanceStaff login(String email, String password) {
        if (!InputValidator.isNotEmpty(email) || !InputValidator.isNotEmpty(password)) {
            return null;
        }
        return staffRepository.validateLogin(email, password);
    }

    /**
     * Retrieves all complaints assigned to the logged-in staff member.
     */
    public List<Complaint> getAssignedComplaints(String staffId) {
        return complaintService.getComplaintsByStaff(staffId);
    }

    /**
     * Retrieves details of an assigned complaint, ensuring staff authorization.
     */
    public Complaint getAssignedComplaintDetails(String complaintId, String staffId)
            throws ComplaintNotFoundException, SecurityException {

        Complaint complaint = complaintService.getComplaintById(complaintId);
        if (complaint.getAssignedStaffId() == null ||
                !complaint.getAssignedStaffId().equalsIgnoreCase(staffId)) {
            throw new SecurityException("Unauthorized: Complaint " + complaintId +
                    " is not assigned to you (Staff ID: " + staffId + ").");
        }
        return complaint;
    }

    /**
     * Marks an assigned complaint as IN_PROGRESS.
     */
    public boolean markInProgress(String complaintId, String staffId)
            throws ComplaintNotFoundException, InvalidStatusTransitionException, SecurityException {

        Complaint complaint = getAssignedComplaintDetails(complaintId, staffId);

        if (complaint.getStatus() != Status.ASSIGNED) {
            throw new InvalidStatusTransitionException(
                    "Cannot mark In Progress: Complaint is currently " + complaint.getStatus() +
                    ". Only ASSIGNED complaints can be moved to IN_PROGRESS."
            );
        }

        return complaintService.updateStatus(complaintId, Status.IN_PROGRESS);
    }

    /**
     * Marks an assigned complaint as RESOLVED after work is complete.
     */
    public boolean markResolved(String complaintId, String staffId)
            throws ComplaintNotFoundException, InvalidStatusTransitionException, SecurityException {

        Complaint complaint = getAssignedComplaintDetails(complaintId, staffId);

        if (complaint.getStatus() != Status.IN_PROGRESS) {
            throw new InvalidStatusTransitionException(
                    "Cannot mark Resolved: Complaint is currently " + complaint.getStatus() +
                    ". A complaint must be in IN_PROGRESS state before it can be RESOLVED."
            );
        }

        return complaintService.updateStatus(complaintId, Status.RESOLVED);
    }

    /**
     * Lists all maintenance technicians (used by Admin to allocate complaints).
     */
    public List<MaintenanceStaff> getAllStaff() {
        return staffRepository.findAllStaff();
    }

    /**
     * Finds staff by their staff ID.
     */
    public MaintenanceStaff getStaffById(String staffId) {
        return staffRepository.findStaffById(staffId);
    }
}
