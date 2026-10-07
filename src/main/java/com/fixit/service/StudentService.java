package com.fixit.service;

import com.fixit.exception.ComplaintNotFoundException;
import com.fixit.exception.InvalidComplaintException;
import com.fixit.exception.InvalidStatusTransitionException;
import com.fixit.model.Complaint;
import com.fixit.model.Feedback;
import com.fixit.model.Priority;
import com.fixit.model.Student;
import com.fixit.repository.StudentRepository;
import com.fixit.util.InputValidator;

import java.util.List;

/**
 * Service layer managing Student business operations:
 * registration, login, complaint submission, tracking, cancellation, and feedback.
 */
public class StudentService {
    private final StudentRepository studentRepository;
    private final ComplaintService complaintService;
    private final FeedbackService feedbackService;

    public StudentService() {
        this.studentRepository = new StudentRepository();
        this.complaintService = new ComplaintService();
        this.feedbackService = new FeedbackService();
    }

    public StudentService(StudentRepository studentRepository, ComplaintService complaintService, FeedbackService feedbackService) {
        this.studentRepository = studentRepository;
        this.complaintService = complaintService;
        this.feedbackService = feedbackService;
    }

    /**
     * Registers a new student account after validating format and ensuring uniqueness.
     */
    public Student registerStudent(String name, String email, String password, String studentId)
            throws IllegalArgumentException {

        // Validate name
        if (!InputValidator.isNotEmpty(name)) {
            throw new IllegalArgumentException("Student name cannot be empty.");
        }

        // Validate email
        if (!InputValidator.isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format (e.g., student@college.edu).");
        }

        // Validate password
        if (!InputValidator.isValidPassword(password)) {
            throw new IllegalArgumentException("Password must be at least 4 characters long.");
        }

        // Validate studentId
        if (!InputValidator.isNotEmpty(studentId)) {
            throw new IllegalArgumentException("Student Roll Number / College ID cannot be empty.");
        }

        // Check for duplicate email
        Student existingByEmail = studentRepository.findStudentByEmail(email);
        if (existingByEmail != null) {
            throw new IllegalArgumentException("An account with email '" + email + "' already exists.");
        }

        // Check for duplicate studentId
        Student existingById = studentRepository.findStudentById(studentId);
        if (existingById != null) {
            throw new IllegalArgumentException("Student ID '" + studentId + "' is already registered.");
        }

        Student student = new Student(null, name.trim(), email.trim(), password, studentId.trim());
        boolean success = studentRepository.createStudent(student);
        if (!success) {
            throw new RuntimeException("Database error: Could not register student.");
        }

        return student;
    }

    /**
     * Authenticates student by email and password.
     */
    public Student login(String email, String password) {
        if (!InputValidator.isNotEmpty(email) || !InputValidator.isNotEmpty(password)) {
            return null;
        }
        return studentRepository.validateLogin(email, password);
    }

    /**
     * Submits a complaint on behalf of the student and associates the complaint ID with student's profile.
     */
    public Complaint submitComplaint(String studentId, String category, String description,
                                     String location, Priority priority)
            throws InvalidComplaintException {

        Complaint complaint = complaintService.createComplaint(studentId, category, description, location, priority);

        // Update student's complaint list
        studentRepository.addComplaintToStudent(studentId, complaint.getComplaintId());

        return complaint;
    }

    /**
     * Retrieves all complaints lodged by this student.
     */
    public List<Complaint> getStudentComplaints(String studentId) {
        return complaintService.getComplaintsByStudent(studentId);
    }

    /**
     * Tracks status of a specific complaint belonging to the student.
     */
    public Complaint trackComplaint(String complaintId, String studentId)
            throws ComplaintNotFoundException, SecurityException {

        Complaint complaint = complaintService.getComplaintById(complaintId);
        if (!complaint.getStudentId().equalsIgnoreCase(studentId)) {
            throw new SecurityException("Unauthorized access: You can only track complaints submitted by you.");
        }
        return complaint;
    }

    /**
     * Cancels an eligible complaint (must be in PENDING status).
     */
    public boolean cancelComplaint(String complaintId, String studentId)
            throws ComplaintNotFoundException, InvalidStatusTransitionException, SecurityException {

        return complaintService.cancelComplaint(complaintId, studentId);
    }

    /**
     * Submits feedback for a resolved complaint.
     */
    public Feedback submitFeedback(String complaintId, String studentId, int rating, String comment)
            throws ComplaintNotFoundException, IllegalStateException, IllegalArgumentException {

        return feedbackService.submitFeedback(complaintId, studentId, rating, comment);
    }
}
