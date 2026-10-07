package com.fixit.controller;

import com.fixit.model.Student;
import com.fixit.service.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController() {
        this.studentService = new StudentService();
    }

    // Student Registration
    @PostMapping("/register")
    public ResponseEntity<?> registerStudent(
            @RequestBody RegisterRequest request) {

        try {

            Student student = studentService.registerStudent(
                    request.name,
                    request.email,
                    request.password,
                    request.studentId
            );

            return ResponseEntity.ok(student);

        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // Student Login
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request) {

        try {

            Student student =
                    studentService.login(
                            request.email,
                            request.password
                    );

            if (student == null) {
                return ResponseEntity
                        .status(401)
                        .body("Invalid email or password.");
            }

            return ResponseEntity.ok(student);

        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // Request class for registration
    public static class RegisterRequest {
        public String name;
        public String email;
        public String password;
        public String studentId;
    }

    // Request class for login
    public static class LoginRequest {
        public String email;
        public String password;
    }
}