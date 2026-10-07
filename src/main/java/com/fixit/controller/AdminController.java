package com.fixit.controller;

import com.fixit.model.Admin;
import com.fixit.model.Complaint;
import com.fixit.model.MaintenanceStaff;
import com.fixit.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController() {
        this.adminService = new AdminService();
    }

    // ===============================
    // ADMIN LOGIN
    // ===============================

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request) {

        try {

            Admin admin = adminService.login(
                    request.email,
                    request.password
            );

            if (admin == null) {
                return ResponseEntity
                        .status(401)
                        .body("Invalid admin email or password.");
            }

            return ResponseEntity.ok(admin);

        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }


    // ===============================
    // VIEW ALL COMPLAINTS
    // ===============================

    @GetMapping("/complaints")
    public ResponseEntity<List<Complaint>> getAllComplaints() {

        return ResponseEntity.ok(
                adminService.getAllComplaints()
        );
    }


    // ===============================
    // VIEW ALL MAINTENANCE STAFF
    // ===============================

    @GetMapping("/staff")
    public ResponseEntity<List<MaintenanceStaff>> getAllStaff() {

        return ResponseEntity.ok(
                adminService.getAllStaff()
        );
    }


    // ===============================
    // ASSIGN STAFF TO COMPLAINT
    // ===============================

    @PutMapping("/complaints/{complaintId}/assign")
    public ResponseEntity<?> assignStaff(
            @PathVariable String complaintId,
            @RequestBody AssignStaffRequest request) {

        try {

            boolean assigned =
                    adminService.assignMaintenanceStaff(
                            complaintId,
                            request.staffId
                    );

            if (!assigned) {
                return ResponseEntity.badRequest()
                        .body("Staff could not be assigned.");
            }

            return ResponseEntity.ok(
                    "Maintenance staff assigned successfully."
            );

        } catch (Exception e) {

            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }
// ===============================
// UPDATE COMPLAINT STATUS
// ===============================

@PutMapping("/complaints/{complaintId}/status")
public ResponseEntity<?> updateComplaintStatus(
        @PathVariable String complaintId,
        @RequestBody UpdateStatusRequest request) {

    try {

        boolean updated =
                adminService.updateComplaintStatus(
                        complaintId,
                        request.status
                );

        if (!updated) {
            return ResponseEntity.badRequest()
                    .body("Complaint status could not be updated.");
        }

        return ResponseEntity.ok(
                "Complaint status updated successfully."
        );

    } catch (Exception e) {

        return ResponseEntity.badRequest()
                .body(e.getMessage());
    }
}

    // ===============================
    // REQUEST CLASSES
    // ===============================

    public static class LoginRequest {

        public String email;
        public String password;
    }


    public static class AssignStaffRequest {

        public String staffId;
    }
    public static class UpdateStatusRequest {

    public String status;
}
}