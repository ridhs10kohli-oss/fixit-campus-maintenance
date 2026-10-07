package com.fixit.controller;

import com.fixit.model.Complaint;
import com.fixit.model.MaintenanceStaff;
import com.fixit.service.ComplaintService;
import com.fixit.repository.MaintenanceStaffRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
public class MaintenanceStaffController {

    private final MaintenanceStaffRepository staffRepository;
    private final ComplaintService complaintService;

    public MaintenanceStaffController() {
        this.staffRepository = new MaintenanceStaffRepository();
        this.complaintService = new ComplaintService();
    }

    // ================= STAFF LOGIN =================

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request) {

        try {

            MaintenanceStaff staff =
                    staffRepository.validateLogin(
                            request.email,
                            request.password
                    );

            if (staff == null) {

                return ResponseEntity
                        .status(401)
                        .body("Invalid staff email or password.");
            }

            return ResponseEntity.ok(staff);

        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // ================= STAFF COMPLAINTS =================

    @GetMapping("/{staffId}/complaints")
    public ResponseEntity<?> getStaffComplaints(
            @PathVariable String staffId) {

        try {

            MaintenanceStaff staff =
                    staffRepository.findStaffById(staffId);

            if (staff == null) {

                return ResponseEntity
                        .notFound()
                        .build();
            }

            List<Complaint> allComplaints =
                    complaintService.getAllComplaints();

            List<Complaint> assignedComplaints =
                    allComplaints.stream()
                            .filter(complaint ->
                                    staff.getAssignedComplaintIds()
                                            .contains(
                                                    complaint.getComplaintId()
                                            )
                            )
                            .toList();

            return ResponseEntity.ok(assignedComplaints);

        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // ================= UPDATE STATUS =================

    @PutMapping("/complaints/{complaintId}/status")
    public ResponseEntity<?> updateComplaintStatus(
            @PathVariable String complaintId,
            @RequestBody UpdateStatusRequest request) {

        try {

            boolean updated =
                    complaintService.updateStatus(
                            complaintId,
                            com.fixit.model.Status.fromString(
                                    request.status
                            )
                    );

            if (!updated) {

                return ResponseEntity
                        .badRequest()
                        .body("Complaint status could not be updated.");
            }

            return ResponseEntity.ok(
                    "Complaint status updated successfully."
            );

        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // ================= REQUEST CLASSES =================

    public static class LoginRequest {

        public String email;
        public String password;
    }

    public static class UpdateStatusRequest {

        public String status;
    }
}