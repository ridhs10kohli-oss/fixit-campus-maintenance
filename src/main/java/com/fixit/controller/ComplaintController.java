package com.fixit.controller;

import com.fixit.model.Complaint;
import com.fixit.model.Priority;
import com.fixit.service.ComplaintService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    private final ComplaintService complaintService;

    public ComplaintController() {
        this.complaintService = new ComplaintService();
    }

    // =========================
    // CREATE COMPLAINT
    // =========================

    @PostMapping
    public ResponseEntity<?> createComplaint(
            @RequestBody ComplaintRequest request) {

        try {

            // Temporary student ID until login system is connected
            String studentId = request.studentId;

            if (studentId == null || studentId.trim().isEmpty()) {
                studentId = "WEB-STUDENT-001";
            }

            Priority priority = Priority.MEDIUM;

            if (request.priority != null &&
                    !request.priority.trim().isEmpty()) {

                priority = Priority.valueOf(
                        request.priority.trim().toUpperCase()
                );
            }

            Complaint complaint = complaintService.createComplaint(
                    studentId,
                    request.category,
                    request.description,
                    request.location,
                    priority
            );

            return ResponseEntity.ok(complaint);

        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // =========================
    // TRACK COMPLAINT
    // =========================

    @GetMapping("/{complaintId}")
    public ResponseEntity<?> getComplaint(
            @PathVariable String complaintId) {

        try {

            Complaint complaint =
                    complaintService.getComplaintById(complaintId);

            return ResponseEntity.ok(complaint);

        } catch (Exception e) {

            return ResponseEntity
                    .notFound()
                    .build();
        }
    }

    // =========================
    // GET STUDENT COMPLAINTS
    // =========================

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Complaint>> getStudentComplaints(
            @PathVariable String studentId) {

        return ResponseEntity.ok(
                complaintService.getComplaintsByStudent(studentId)
        );
    }

    // =========================
    // GET ALL COMPLAINTS
    // =========================

    @GetMapping
    public ResponseEntity<List<Complaint>> getAllComplaints() {

        return ResponseEntity.ok(
                complaintService.getAllComplaints()
        );
    }

    // =========================
    // REQUEST BODY
    // =========================

    public static class ComplaintRequest {

        public String studentId;
        public String category;
        public String description;
        public String location;
        public String priority;
    }
}