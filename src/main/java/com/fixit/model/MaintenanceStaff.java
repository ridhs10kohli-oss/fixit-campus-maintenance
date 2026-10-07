package com.fixit.model;

import java.util.ArrayList;
import java.util.List;

/**
 * MaintenanceStaff entity extending User.
 * Demonstrates Inheritance and Encapsulation.
 */
public class MaintenanceStaff extends User {
    private String staffId;
    private String specialization;                 // e.g., Electrical, Plumbing, HVAC, IT/Network
    private List<String> assignedComplaintIds;     // Complaints assigned to this staff member

    public MaintenanceStaff() {
        super();
        this.assignedComplaintIds = new ArrayList<>();
    }

    public MaintenanceStaff(String id, String name, String email, String password, String staffId, String specialization) {
        super(id, name, email, password);
        this.staffId = staffId;
        this.specialization = specialization;
        this.assignedComplaintIds = new ArrayList<>();
    }

    public MaintenanceStaff(String id, String name, String email, String password, String staffId, String specialization, List<String> assignedComplaintIds) {
        super(id, name, email, password);
        this.staffId = staffId;
        this.specialization = specialization;
        this.assignedComplaintIds = assignedComplaintIds != null ? assignedComplaintIds : new ArrayList<>();
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public List<String> getAssignedComplaintIds() {
        return assignedComplaintIds;
    }

    public void setAssignedComplaintIds(List<String> assignedComplaintIds) {
        this.assignedComplaintIds = assignedComplaintIds != null ? assignedComplaintIds : new ArrayList<>();
    }

    public void addAssignedComplaintId(String complaintId) {
        if (complaintId != null && !this.assignedComplaintIds.contains(complaintId)) {
            this.assignedComplaintIds.add(complaintId);
        }
    }

    @Override
    public String getRole() {
        return "MAINTENANCE_STAFF";
    }

    @Override
    public String toString() {
        return "MaintenanceStaff{" +
                "staffId='" + staffId + '\'' +
                ", name='" + getName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", specialization='" + specialization + '\'' +
                ", activeAssignments=" + (assignedComplaintIds != null ? assignedComplaintIds.size() : 0) +
                '}';
    }
}
