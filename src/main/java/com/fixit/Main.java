package com.fixit;

import com.fixit.exception.ComplaintNotFoundException;
import com.fixit.exception.InvalidComplaintException;
import com.fixit.exception.InvalidStatusTransitionException;
import com.fixit.model.*;
import com.fixit.service.*;
import com.fixit.util.ConsoleUtil;
import com.fixit.util.DatabaseSeeder;
import com.fixit.util.MongoConnection;

import java.util.List;
import java.util.Scanner;

/**
 * Main entry point for FixIt Java console application.
 * 
 * Architectural Rule:
 *   Main (Presentation Layer) does NOT directly communicate with MongoDB.
 *   All operations flow through the Service Layer.
 */
public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static StudentService studentService;
    private static AdminService adminService;
    private static MaintenanceStaffService staffService;
    private static ComplaintService complaintService;

    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println("     FIXIT JAVA - CAMPUS COMPLAINT & MAINTENANCE SYSTEM     ");
        System.out.println("============================================================");

        // 1. Verify MongoDB Connection
        try {
            System.out.println("[INFO] Initializing MongoDB Atlas connection...");
            MongoConnection conn = MongoConnection.getInstance();
            if (!conn.testConnection()) {
                ConsoleUtil.printError("Could not connect to MongoDB server.");
                ConsoleUtil.printWarning("Please verify your internet connection and MONGODB_URI in '.env'.");
                return;
            }
            ConsoleUtil.printSuccess("Connected successfully to MongoDB Atlas database!");

            // 2. Initialize Services and Seed Demo Data
            studentService = new StudentService();
            adminService = new AdminService();
            staffService = new MaintenanceStaffService();
            complaintService = new ComplaintService();

            DatabaseSeeder.seedInitialData();

        } catch (IllegalStateException e) {
            ConsoleUtil.printError(e.getMessage());
            return;
        } catch (Exception e) {
            ConsoleUtil.printError("Database connection failed. Please try again later.");
            ConsoleUtil.printInfo("Details: " + e.getMessage());
            return;
        }

        // 3. Main Application Loop
        boolean running = true;
        while (running) {
            ConsoleUtil.printHeader("Welcome to FixIt Java");
            System.out.println("1. Student Portal");
            System.out.println("2. Administrator Portal");
            System.out.println("3. Maintenance Staff Portal");
            System.out.println("4. Exit Application");
            System.out.println("------------------------------------------------------------");

            int choice = ConsoleUtil.readInt(scanner, "Enter your choice", 1, 4);

            switch (choice) {
                case 1:
                    handleStudentPortal();
                    break;
                case 2:
                    handleAdminPortal();
                    break;
                case 3:
                    handleStaffPortal();
                    break;
                case 4:
                    running = false;
                    ConsoleUtil.printInfo("Closing database connections and exiting FixIt Java...");
                    MongoConnection.getInstance().close();
                    ConsoleUtil.printSuccess("Thank you for using FixIt Java. Goodbye!");
                    break;
            }
        }
    }

    // =========================================================================
    // STUDENT PORTAL
    // =========================================================================

    private static void handleStudentPortal() {
        boolean back = false;
        while (!back) {
            ConsoleUtil.printHeader("Student Portal");
            System.out.println("1. Register New Student Account");
            System.out.println("2. Login");
            System.out.println("3. Back to Main Menu");
            System.out.println("------------------------------------------------------------");

            int choice = ConsoleUtil.readInt(scanner, "Select option", 1, 3);
            switch (choice) {
                case 1:
                    registerStudent();
                    break;
                case 2:
                    loginStudent();
                    break;
                case 3:
                    back = true;
                    break;
            }
        }
    }

    private static void registerStudent() {
        ConsoleUtil.printSubHeader("Student Registration");
        try {
            String name = ConsoleUtil.readNonEmptyString(scanner, "Enter Full Name", "Name cannot be empty.");
            String email = ConsoleUtil.readEmail(scanner, "Enter College Email");
            String password = ConsoleUtil.readPassword(scanner, "Enter Password (min 4 chars)");
            String studentId = ConsoleUtil.readNonEmptyString(scanner, "Enter Student Roll / College ID (e.g., STU-2026-01)", "Student ID cannot be empty.");

            Student registered = studentService.registerStudent(name, email, password, studentId);
            ConsoleUtil.printSuccess("Registration successful! Welcome, " + registered.getName() + " (" + registered.getStudentId() + ")");
            ConsoleUtil.printInfo("You can now login using your email and password.");
        } catch (IllegalArgumentException e) {
            ConsoleUtil.printError(e.getMessage());
        } catch (Exception e) {
            ConsoleUtil.printError("Registration failed: " + e.getMessage());
        }
        ConsoleUtil.pressEnterToContinue(scanner);
    }

    private static void loginStudent() {
        ConsoleUtil.printSubHeader("Student Login");
        String email = ConsoleUtil.readEmail(scanner, "Enter Registered Email");
        String password = ConsoleUtil.readNonEmptyString(scanner, "Enter Password", "Password cannot be empty.");

        Student loggedIn = studentService.login(email, password);
        if (loggedIn == null) {
            ConsoleUtil.printError("Invalid email or password. Please try again.");
            ConsoleUtil.pressEnterToContinue(scanner);
            return;
        }

        ConsoleUtil.printSuccess("Login successful! Welcome back, " + loggedIn.getName());
        studentDashboard(loggedIn);
    }

    private static void studentDashboard(Student student) {
        boolean loggedIn = true;
        while (loggedIn) {
            ConsoleUtil.printHeader("Student Dashboard - " + student.getName() + " (" + student.getStudentId() + ")");
            System.out.println("1. Submit Complaint");
            System.out.println("2. View My Complaints");
            System.out.println("3. Track Complaint Status");
            System.out.println("4. Cancel Eligible Complaint");
            System.out.println("5. Submit Feedback");
            System.out.println("6. Logout");
            System.out.println("------------------------------------------------------------");

            int choice = ConsoleUtil.readInt(scanner, "Select option", 1, 6);
            switch (choice) {
                case 1:
                    submitComplaintFlow(student);
                    break;
                case 2:
                    viewStudentComplaints(student);
                    break;
                case 3:
                    trackComplaintFlow(student);
                    break;
                case 4:
                    cancelComplaintFlow(student);
                    break;
                case 5:
                    submitFeedbackFlow(student);
                    break;
                case 6:
                    loggedIn = false;
                    ConsoleUtil.printInfo("Logged out of Student account.");
                    break;
            }
        }
    }

    private static void submitComplaintFlow(Student student) {
        ConsoleUtil.printSubHeader("Submit Maintenance Complaint");
        System.out.println("Select Complaint Category:");
        System.out.println("1. Broken Fan");
        System.out.println("2. Broken Air Conditioner (AC)");
        System.out.println("3. Broken Lights / Electrical Issue");
        System.out.println("4. Wi-Fi / Internet Problem");
        System.out.println("5. Furniture Damage (Desks, Chairs, Doors)");
        System.out.println("6. Projector / Audio-Visual Problem");
        System.out.println("7. Water-related / Plumbing Issue");
        System.out.println("8. Other Campus Maintenance Issue");

        int catChoice = ConsoleUtil.readInt(scanner, "Choose Category", 1, 8);
        String category;
        switch (catChoice) {
            case 1: category = "Broken Fan"; break;
            case 2: category = "Broken AC"; break;
            case 3: category = "Broken Lights"; break;
            case 4: category = "Wi-Fi Problem"; break;
            case 5: category = "Furniture Damage"; break;
            case 6: category = "Projector Problem"; break;
            case 7: category = "Water-related Problem"; break;
            default:
                category = ConsoleUtil.readNonEmptyString(scanner, "Specify Custom Category", "Category cannot be empty.");
                break;
        }

        String location = ConsoleUtil.readNonEmptyString(scanner, "Enter Location (e.g., Room 302, Block B, Central Library)", "Location cannot be empty.");
        String description = ConsoleUtil.readNonEmptyString(scanner, "Enter Detailed Problem Description", "Description cannot be empty.");

        System.out.println("\nSelect Urgency / Priority Level:");
        System.out.println("1. LOW       (Minor inconvenience, e.g., loose hinge)");
        System.out.println("2. MEDIUM    (Standard issue, e.g., one tube light flickering)");
        System.out.println("3. HIGH      (Substantial impact, e.g., AC not cooling in exam hall)");
        System.out.println("4. URGENT    (Immediate hazard, e.g., spark, major water leak)");

        int prioChoice = ConsoleUtil.readInt(scanner, "Choose Priority", 1, 4);
        Priority priority;
        switch (prioChoice) {
            case 1: priority = Priority.LOW; break;
            case 3: priority = Priority.HIGH; break;
            case 4: priority = Priority.URGENT; break;
            default: priority = Priority.MEDIUM; break;
        }

        try {
            Complaint complaint = studentService.submitComplaint(student.getStudentId(), category, description, location, priority);
            ConsoleUtil.printSuccess("Complaint filed successfully!");
            System.out.println("------------------------------------------------------------");
            System.out.println("Complaint ID : " + complaint.getComplaintId());
            System.out.println("Category     : " + complaint.getCategory());
            System.out.println("Location     : " + complaint.getLocation());
            System.out.println("Priority     : " + complaint.getPriority());
            System.out.println("Status       : " + complaint.getStatus() + " (Initial state)");
            System.out.println("Reported At  : " + complaint.getFormattedTimestamp());
            System.out.println("------------------------------------------------------------");
            ConsoleUtil.printInfo("Please save your Complaint ID (" + complaint.getComplaintId() + ") for tracking.");
        } catch (InvalidComplaintException e) {
            ConsoleUtil.printError("Invalid complaint data: " + e.getMessage());
        } catch (Exception e) {
            ConsoleUtil.printError("Failed to submit complaint: " + e.getMessage());
        }
        ConsoleUtil.pressEnterToContinue(scanner);
    }

    private static void viewStudentComplaints(Student student) {
        ConsoleUtil.printSubHeader("My Complaints");
        List<Complaint> list = studentService.getStudentComplaints(student.getStudentId());
        if (list.isEmpty()) {
            ConsoleUtil.printInfo("You have not submitted any complaints yet.");
        } else {
            renderComplaintTable(list);
        }
        ConsoleUtil.pressEnterToContinue(scanner);
    }

    private static void trackComplaintFlow(Student student) {
        ConsoleUtil.printSubHeader("Track Complaint Status");
        String complaintId = ConsoleUtil.readNonEmptyString(scanner, "Enter Complaint ID (e.g., CMP-0001)", "Complaint ID cannot be empty.");

        try {
            Complaint complaint = studentService.trackComplaint(complaintId, student.getStudentId());
            displayComplaintDetails(complaint);
        } catch (ComplaintNotFoundException e) {
            ConsoleUtil.printError("Complaint not found: " + e.getMessage());
        } catch (SecurityException e) {
            ConsoleUtil.printError("Unauthorized: " + e.getMessage());
        } catch (Exception e) {
            ConsoleUtil.printError("Error retrieving complaint: " + e.getMessage());
        }
        ConsoleUtil.pressEnterToContinue(scanner);
    }

    private static void cancelComplaintFlow(Student student) {
        ConsoleUtil.printSubHeader("Cancel Eligible Complaint");
        ConsoleUtil.printInfo("Rule: Complaints can only be cancelled while in 'PENDING' status.");
        String complaintId = ConsoleUtil.readNonEmptyString(scanner, "Enter Complaint ID to Cancel", "Complaint ID cannot be empty.");

        try {
            boolean cancelled = studentService.cancelComplaint(complaintId, student.getStudentId());
            if (cancelled) {
                ConsoleUtil.printSuccess("Complaint " + complaintId.toUpperCase() + " has been successfully CANCELLED.");
            } else {
                ConsoleUtil.printError("Could not cancel complaint. Please verify the ID.");
            }
        } catch (ComplaintNotFoundException e) {
            ConsoleUtil.printError(e.getMessage());
        } catch (InvalidStatusTransitionException e) {
            ConsoleUtil.printError("Cancellation Rejected: " + e.getMessage());
        } catch (SecurityException e) {
            ConsoleUtil.printError("Unauthorized: " + e.getMessage());
        } catch (Exception e) {
            ConsoleUtil.printError("Error: " + e.getMessage());
        }
        ConsoleUtil.pressEnterToContinue(scanner);
    }

    private static void submitFeedbackFlow(Student student) {
        ConsoleUtil.printSubHeader("Submit Feedback for Resolved Complaint");
        ConsoleUtil.printInfo("Rule: Feedback can only be submitted after a complaint is marked 'RESOLVED'.");
        String complaintId = ConsoleUtil.readNonEmptyString(scanner, "Enter Resolved Complaint ID", "Complaint ID cannot be empty.");

        try {
            int rating = ConsoleUtil.readInt(scanner, "Enter Rating (1 = Poor, 5 = Excellent)", 1, 5);
            String comment = ConsoleUtil.readString(scanner, "Enter Feedback Comments (Optional)");

            Feedback fb = studentService.submitFeedback(complaintId, student.getStudentId(), rating, comment);
            ConsoleUtil.printSuccess("Thank you! Feedback recorded with ID: " + fb.getFeedbackId());
            System.out.println("Rating: " + fb.getRating() + "/5 stars | Submitted at: " + fb.getFormattedTimestamp());
        } catch (ComplaintNotFoundException e) {
            ConsoleUtil.printError(e.getMessage());
        } catch (IllegalStateException e) {
            ConsoleUtil.printWarning(e.getMessage());
        } catch (SecurityException e) {
            ConsoleUtil.printError("Unauthorized: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            ConsoleUtil.printError(e.getMessage());
        } catch (Exception e) {
            ConsoleUtil.printError("Error submitting feedback: " + e.getMessage());
        }
        ConsoleUtil.pressEnterToContinue(scanner);
    }

    // =========================================================================
    // ADMINISTRATOR PORTAL
    // =========================================================================

    private static void handleAdminPortal() {
        ConsoleUtil.printHeader("Administrator Login");
        ConsoleUtil.printInfo("Demo Default Credentials: admin@fixit.edu / admin123");
        String email = ConsoleUtil.readEmail(scanner, "Enter Admin Email");
        String password = ConsoleUtil.readNonEmptyString(scanner, "Enter Password", "Password cannot be empty.");

        Admin admin = adminService.login(email, password);
        if (admin == null) {
            ConsoleUtil.printError("Invalid Administrator credentials.");
            ConsoleUtil.pressEnterToContinue(scanner);
            return;
        }

        ConsoleUtil.printSuccess("Login successful! Welcome, " + admin.getName() + " (" + admin.getDepartment() + ")");
        adminDashboard(admin);
    }

    private static void adminDashboard(Admin admin) {
        boolean loggedIn = true;
        while (loggedIn) {
            ConsoleUtil.printHeader("Admin Dashboard - " + admin.getName());
            System.out.println("1. View All Complaints");
            System.out.println("2. Search Complaint by ID");
            System.out.println("3. Filter Complaints");
            System.out.println("4. Assign Maintenance Staff to Complaint");
            System.out.println("5. Update / Manage Complaint Details");
            System.out.println("6. View Pending Complaints");
            System.out.println("7. View In-Progress Complaints");
            System.out.println("8. View Resolved Complaints & Feedback");
            System.out.println("9. View All Maintenance Staff");
            System.out.println("10. Logout");
            System.out.println("------------------------------------------------------------");

            int choice = ConsoleUtil.readInt(scanner, "Select option", 1, 10);
            switch (choice) {
                case 1:
                    renderComplaintTable(adminService.getAllComplaints());
                    ConsoleUtil.pressEnterToContinue(scanner);
                    break;
                case 2:
                    searchComplaintFlow();
                    break;
                case 3:
                    filterComplaintsFlow();
                    break;
                case 4:
                    assignStaffFlow();
                    break;
                case 5:
                    manageComplaintFlow();
                    break;
                case 6:
                    renderComplaintTable(adminService.getComplaintsByStatus(Status.PENDING));
                    ConsoleUtil.pressEnterToContinue(scanner);
                    break;
                case 7:
                    renderComplaintTable(adminService.getComplaintsByStatus(Status.IN_PROGRESS));
                    ConsoleUtil.pressEnterToContinue(scanner);
                    break;
                case 8:
                    viewResolvedAndFeedbackFlow();
                    break;
                case 9:
                    viewAllStaffFlow();
                    break;
                case 10:
                    loggedIn = false;
                    ConsoleUtil.printInfo("Logged out of Administrator account.");
                    break;
            }
        }
    }

    private static void searchComplaintFlow() {
        ConsoleUtil.printSubHeader("Search Complaint by ID");
        String complaintId = ConsoleUtil.readNonEmptyString(scanner, "Enter Complaint ID", "ID cannot be empty.");
        try {
            Complaint complaint = adminService.searchComplaintById(complaintId);
            displayComplaintDetails(complaint);
        } catch (ComplaintNotFoundException e) {
            ConsoleUtil.printError(e.getMessage());
        }
        ConsoleUtil.pressEnterToContinue(scanner);
    }

    private static void filterComplaintsFlow() {
        ConsoleUtil.printSubHeader("Filter Complaints");
        System.out.println("1. Filter by Category");
        System.out.println("2. Filter by Location / Block");
        System.out.println("3. Filter by Priority Level");
        System.out.println("4. Filter by Status");
        System.out.println("5. Back");

        int choice = ConsoleUtil.readInt(scanner, "Select filter criterion", 1, 5);
        List<Complaint> results = null;

        switch (choice) {
            case 1:
                String cat = ConsoleUtil.readNonEmptyString(scanner, "Enter category keyword (e.g. fan, AC, light)", "Keyword required.");
                results = adminService.filterByCategory(cat);
                break;
            case 2:
                String loc = ConsoleUtil.readNonEmptyString(scanner, "Enter location keyword (e.g. Block B, Library)", "Keyword required.");
                results = adminService.filterByLocation(loc);
                break;
            case 3:
                System.out.println("1. LOW  2. MEDIUM  3. HIGH  4. URGENT");
                int p = ConsoleUtil.readInt(scanner, "Select Priority", 1, 4);
                Priority priority = switch (p) {
                    case 1 -> Priority.LOW;
                    case 3 -> Priority.HIGH;
                    case 4 -> Priority.URGENT;
                    default -> Priority.MEDIUM;
                };
                results = adminService.filterByPriority(priority);
                break;
            case 4:
                System.out.println("1. PENDING  2. ASSIGNED  3. IN_PROGRESS  4. RESOLVED  5. CANCELLED");
                int s = ConsoleUtil.readInt(scanner, "Select Status", 1, 5);
                Status status = switch (s) {
                    case 1 -> Status.PENDING;
                    case 2 -> Status.ASSIGNED;
                    case 3 -> Status.IN_PROGRESS;
                    case 4 -> Status.RESOLVED;
                    default -> Status.CANCELLED;
                };
                results = adminService.getComplaintsByStatus(status);
                break;
            case 5:
                return;
        }

        if (results != null) {
            renderComplaintTable(results);
        }
        ConsoleUtil.pressEnterToContinue(scanner);
    }

    private static void assignStaffFlow() {
        ConsoleUtil.printSubHeader("Assign Maintenance Staff to Complaint");
        String complaintId = ConsoleUtil.readNonEmptyString(scanner, "Enter Complaint ID", "Complaint ID cannot be empty.");

        try {
            Complaint complaint = adminService.searchComplaintById(complaintId);
            System.out.println("Complaint: " + complaint.getComplaintId() + " | Category: " + complaint.getCategory() +
                    " | Location: " + complaint.getLocation() + " | Current Status: " + complaint.getStatus());

            // Check if eligible for assignment
            if (complaint.getStatus() != Status.PENDING && complaint.getStatus() != Status.ASSIGNED) {
                ConsoleUtil.printWarning("Cannot assign technician: Complaint is currently " + complaint.getStatus() +
                        ". Only PENDING or ASSIGNED complaints can be assigned.");
                ConsoleUtil.pressEnterToContinue(scanner);
                return;
            }

            // Display available staff
            System.out.println("\nAvailable Maintenance Staff:");
            List<MaintenanceStaff> staffList = adminService.getAllStaff();
            if (staffList.isEmpty()) {
                ConsoleUtil.printError("No maintenance staff registered in database.");
                ConsoleUtil.pressEnterToContinue(scanner);
                return;
            }
            for (MaintenanceStaff staff : staffList) {
                System.out.printf("  [%s] %-18s | Specialization: %-22s | Active tasks: %d\n",
                        staff.getStaffId(), staff.getName(), staff.getSpecialization(),
                        staff.getAssignedComplaintIds().size());
            }

            String staffId = ConsoleUtil.readNonEmptyString(scanner, "\nEnter Staff ID to Assign (e.g., STF-101)", "Staff ID required.");

            boolean assigned = adminService.assignMaintenanceStaff(complaintId, staffId);
            if (assigned) {
                ConsoleUtil.printSuccess("Technician " + staffId.toUpperCase() + " successfully assigned to " + complaint.getComplaintId());
                ConsoleUtil.printInfo("Complaint status updated to: ASSIGNED");
            } else {
                ConsoleUtil.printError("Assignment could not be completed.");
            }
        } catch (ComplaintNotFoundException e) {
            ConsoleUtil.printError(e.getMessage());
        } catch (InvalidStatusTransitionException e) {
            ConsoleUtil.printError("Assignment rejected: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            ConsoleUtil.printError(e.getMessage());
        } catch (Exception e) {
            ConsoleUtil.printError("Error: " + e.getMessage());
        }
        ConsoleUtil.pressEnterToContinue(scanner);
    }

    private static void manageComplaintFlow() {
        ConsoleUtil.printSubHeader("Update / Manage Complaint");
        String complaintId = ConsoleUtil.readNonEmptyString(scanner, "Enter Complaint ID", "Complaint ID required.");

        try {
            Complaint complaint = adminService.searchComplaintById(complaintId);
            displayComplaintDetails(complaint);

            System.out.println("Administrative Action Options:");
            System.out.println("1. Re-assign Maintenance Staff");
            System.out.println("2. Cancel Complaint (Administrative override for Pending issues)");
            System.out.println("3. Back");

            int action = ConsoleUtil.readInt(scanner, "Select Action", 1, 3);
            if (action == 1) {
                assignStaffFlow();
            } else if (action == 2) {
                if (complaint.getStatus() == Status.PENDING) {
                    complaintService.updateStatus(complaintId, Status.CANCELLED);
                    ConsoleUtil.printSuccess("Complaint " + complaintId + " marked as CANCELLED.");
                } else {
                    ConsoleUtil.printWarning("Administrative cancellation is only permitted on PENDING complaints.");
                }
                ConsoleUtil.pressEnterToContinue(scanner);
            }
        } catch (ComplaintNotFoundException e) {
            ConsoleUtil.printError(e.getMessage());
            ConsoleUtil.pressEnterToContinue(scanner);
        } catch (Exception e) {
            ConsoleUtil.printError("Error: " + e.getMessage());
            ConsoleUtil.pressEnterToContinue(scanner);
        }
    }

    private static void viewResolvedAndFeedbackFlow() {
        ConsoleUtil.printSubHeader("Resolved Complaints & Student Feedback");
        List<Complaint> resolved = adminService.getComplaintsByStatus(Status.RESOLVED);
        if (resolved.isEmpty()) {
            ConsoleUtil.printInfo("No resolved complaints found yet.");
            ConsoleUtil.pressEnterToContinue(scanner);
            return;
        }

        renderComplaintTable(resolved);

        System.out.println("\nOptions:");
        System.out.println("1. View Student Feedback for a Specific Complaint");
        System.out.println("2. View All Feedback Reports");
        System.out.println("3. Back");

        int choice = ConsoleUtil.readInt(scanner, "Select option", 1, 3);
        if (choice == 1) {
            String cmpId = ConsoleUtil.readNonEmptyString(scanner, "Enter Complaint ID", "Complaint ID required.");
            Feedback fb = adminService.getFeedbackForComplaint(cmpId);
            if (fb != null) {
                System.out.println("------------------------------------------------------------");
                System.out.println("Feedback ID  : " + fb.getFeedbackId());
                System.out.println("Complaint ID : " + fb.getComplaintId());
                System.out.println("Student ID   : " + fb.getStudentId());
                System.out.println("Rating       : " + fb.getRating() + " / 5 stars");
                System.out.println("Comment      : " + (fb.getComment().isEmpty() ? "(No comment provided)" : fb.getComment()));
                System.out.println("Submitted At : " + fb.getFormattedTimestamp());
                System.out.println("------------------------------------------------------------");
            } else {
                ConsoleUtil.printInfo("No feedback submitted by student yet for complaint: " + cmpId);
            }
            ConsoleUtil.pressEnterToContinue(scanner);
        } else if (choice == 2) {
            List<Feedback> allFb = adminService.getAllFeedback();
            if (allFb.isEmpty()) {
                ConsoleUtil.printInfo("No feedback records in database yet.");
            } else {
                System.out.println("\n-------------------------------------------------------------------------------------------------");
                System.out.printf("| %-8s | %-10s | %-12s | %-8s | %-24s | %-19s |\n",
                        "FB ID", "Complaint", "Student ID", "Rating", "Comment", "Date");
                System.out.println("-------------------------------------------------------------------------------------------------");
                for (Feedback f : allFb) {
                    String comment = f.getComment().length() > 22 ? f.getComment().substring(0, 19) + "..." : f.getComment();
                    System.out.printf("| %-8s | %-10s | %-12s | %-8s | %-24s | %-19s |\n",
                            f.getFeedbackId(), f.getComplaintId(), f.getStudentId(),
                            f.getRating() + " / 5", comment, f.getFormattedTimestamp());
                }
                System.out.println("-------------------------------------------------------------------------------------------------");
            }
            ConsoleUtil.pressEnterToContinue(scanner);
        }
    }

    private static void viewAllStaffFlow() {
        ConsoleUtil.printSubHeader("Registered Maintenance Technicians");
        List<MaintenanceStaff> staffList = adminService.getAllStaff();
        if (staffList.isEmpty()) {
            ConsoleUtil.printInfo("No staff members registered.");
        } else {
            System.out.println("-------------------------------------------------------------------------------------------------");
            System.out.printf("| %-9s | %-20s | %-25s | %-22s | %-6s |\n",
                    "Staff ID", "Name", "Email", "Specialization", "Tasks");
            System.out.println("-------------------------------------------------------------------------------------------------");
            for (MaintenanceStaff s : staffList) {
                System.out.printf("| %-9s | %-20s | %-25s | %-22s | %-6d |\n",
                        s.getStaffId(), s.getName(), s.getEmail(), s.getSpecialization(),
                        s.getAssignedComplaintIds().size());
            }
            System.out.println("-------------------------------------------------------------------------------------------------");
        }
        ConsoleUtil.pressEnterToContinue(scanner);
    }

    // =========================================================================
    // MAINTENANCE STAFF PORTAL
    // =========================================================================

    private static void handleStaffPortal() {
        ConsoleUtil.printHeader("Maintenance Staff Login");
        ConsoleUtil.printInfo("Demo Default Credentials: rajesh@fixit.edu / staff123 (or suresh@fixit.edu, amit@fixit.edu)");
        String email = ConsoleUtil.readEmail(scanner, "Enter Staff Email");
        String password = ConsoleUtil.readNonEmptyString(scanner, "Enter Password", "Password cannot be empty.");

        MaintenanceStaff staff = staffService.login(email, password);
        if (staff == null) {
            ConsoleUtil.printError("Invalid staff credentials.");
            ConsoleUtil.pressEnterToContinue(scanner);
            return;
        }

        ConsoleUtil.printSuccess("Login successful! Welcome, " + staff.getName() + " (" + staff.getSpecialization() + ")");
        staffDashboard(staff);
    }

    private static void staffDashboard(MaintenanceStaff staff) {
        boolean loggedIn = true;
        while (loggedIn) {
            ConsoleUtil.printHeader("Staff Dashboard - " + staff.getName() + " [" + staff.getStaffId() + " - " + staff.getSpecialization() + "]");
            System.out.println("1. View Assigned Complaints");
            System.out.println("2. View Complaint Details");
            System.out.println("3. Mark Complaint IN PROGRESS");
            System.out.println("4. Mark Complaint RESOLVED");
            System.out.println("5. Logout");
            System.out.println("------------------------------------------------------------");

            int choice = ConsoleUtil.readInt(scanner, "Select option", 1, 5);
            switch (choice) {
                case 1:
                    viewStaffAssignments(staff);
                    break;
                case 2:
                    viewStaffComplaintDetails(staff);
                    break;
                case 3:
                    markStaffComplaintInProgress(staff);
                    break;
                case 4:
                    markStaffComplaintResolved(staff);
                    break;
                case 5:
                    loggedIn = false;
                    ConsoleUtil.printInfo("Logged out of Staff account.");
                    break;
            }
        }
    }

    private static void viewStaffAssignments(MaintenanceStaff staff) {
        ConsoleUtil.printSubHeader("Complaints Assigned to " + staff.getName());
        List<Complaint> assigned = staffService.getAssignedComplaints(staff.getStaffId());
        if (assigned.isEmpty()) {
            ConsoleUtil.printInfo("You have no complaints assigned at present.");
        } else {
            renderComplaintTable(assigned);
        }
        ConsoleUtil.pressEnterToContinue(scanner);
    }

    private static void viewStaffComplaintDetails(MaintenanceStaff staff) {
        ConsoleUtil.printSubHeader("View Complaint Details");
        String complaintId = ConsoleUtil.readNonEmptyString(scanner, "Enter Complaint ID", "Complaint ID required.");

        try {
            Complaint complaint = staffService.getAssignedComplaintDetails(complaintId, staff.getStaffId());
            displayComplaintDetails(complaint);
        } catch (ComplaintNotFoundException e) {
            ConsoleUtil.printError(e.getMessage());
        } catch (SecurityException e) {
            ConsoleUtil.printError("Access Denied: " + e.getMessage());
        } catch (Exception e) {
            ConsoleUtil.printError("Error: " + e.getMessage());
        }
        ConsoleUtil.pressEnterToContinue(scanner);
    }

    private static void markStaffComplaintInProgress(MaintenanceStaff staff) {
        ConsoleUtil.printSubHeader("Update Complaint Status -> IN_PROGRESS");
        ConsoleUtil.printInfo("Rule: Workflow step must be ASSIGNED -> IN_PROGRESS");
        String complaintId = ConsoleUtil.readNonEmptyString(scanner, "Enter Complaint ID", "Complaint ID required.");

        try {
            boolean updated = staffService.markInProgress(complaintId, staff.getStaffId());
            if (updated) {
                ConsoleUtil.printSuccess("Status updated! Complaint " + complaintId.toUpperCase() + " is now IN_PROGRESS.");
            } else {
                ConsoleUtil.printError("Could not update complaint status.");
            }
        } catch (ComplaintNotFoundException e) {
            ConsoleUtil.printError(e.getMessage());
        } catch (SecurityException e) {
            ConsoleUtil.printError("Access Denied: " + e.getMessage());
        } catch (InvalidStatusTransitionException e) {
            ConsoleUtil.printError("Invalid Status Transition: " + e.getMessage());
        } catch (Exception e) {
            ConsoleUtil.printError("Error: " + e.getMessage());
        }
        ConsoleUtil.pressEnterToContinue(scanner);
    }

    private static void markStaffComplaintResolved(MaintenanceStaff staff) {
        ConsoleUtil.printSubHeader("Update Complaint Status -> RESOLVED");
        ConsoleUtil.printInfo("Rule: Workflow step must be IN_PROGRESS -> RESOLVED");
        String complaintId = ConsoleUtil.readNonEmptyString(scanner, "Enter Complaint ID", "Complaint ID required.");

        try {
            boolean updated = staffService.markResolved(complaintId, staff.getStaffId());
            if (updated) {
                ConsoleUtil.printSuccess("Status updated! Complaint " + complaintId.toUpperCase() + " is now RESOLVED.");
                ConsoleUtil.printInfo("The reporting student can now submit feedback and rating for this ticket.");
            } else {
                ConsoleUtil.printError("Could not update complaint status.");
            }
        } catch (ComplaintNotFoundException e) {
            ConsoleUtil.printError(e.getMessage());
        } catch (SecurityException e) {
            ConsoleUtil.printError("Access Denied: " + e.getMessage());
        } catch (InvalidStatusTransitionException e) {
            ConsoleUtil.printError("Invalid Status Transition: " + e.getMessage());
        } catch (Exception e) {
            ConsoleUtil.printError("Error: " + e.getMessage());
        }
        ConsoleUtil.pressEnterToContinue(scanner);
    }

    // =========================================================================
    // PRESENTATION HELPER METHODS
    // =========================================================================

    private static void displayComplaintDetails(Complaint complaint) {
        System.out.println("\n------------------------------------------------------------");
        System.out.println("COMPLAINT DETAILS: " + complaint.getComplaintId());
        System.out.println("------------------------------------------------------------");
        System.out.println("Status          : " + complaint.getStatus());
        System.out.println("Priority        : " + complaint.getPriority());
        System.out.println("Category        : " + complaint.getCategory());
        System.out.println("Location        : " + complaint.getLocation());
        System.out.println("Student ID      : " + complaint.getStudentId());
        System.out.println("Assigned Staff  : " + (complaint.getAssignedStaffId() != null ? complaint.getAssignedStaffId() : "(Unassigned)"));
        System.out.println("Reported Date   : " + complaint.getFormattedTimestamp());
        System.out.println("Description     : " + complaint.getDescription());
        System.out.println("------------------------------------------------------------");
    }

    private static void renderComplaintTable(List<Complaint> list) {
        if (list == null || list.isEmpty()) {
            ConsoleUtil.printInfo("No complaints to display.");
            return;
        }

        System.out.println("\n------------------------------------------------------------------------------------------------------------------------");
        System.out.printf("| %-10s | %-16s | %-20s | %-9s | %-12s | %-11s | %-19s |\n",
                "ID", "Category", "Location", "Priority", "Status", "Staff ID", "Reported At");
        System.out.println("------------------------------------------------------------------------------------------------------------------------");

        for (Complaint c : list) {
            String cat = c.getCategory().length() > 15 ? c.getCategory().substring(0, 13) + ".." : c.getCategory();
            String loc = c.getLocation().length() > 19 ? c.getLocation().substring(0, 17) + ".." : c.getLocation();
            String staff = c.getAssignedStaffId() != null ? c.getAssignedStaffId() : "-";

            System.out.printf("| %-10s | %-16s | %-20s | %-9s | %-12s | %-11s | %-19s |\n",
                    c.getComplaintId(), cat, loc, c.getPriority(), c.getStatus(), staff, c.getFormattedTimestamp());
        }
        System.out.println("------------------------------------------------------------------------------------------------------------------------");
        System.out.println("Total Records: " + list.size());
    }
}
