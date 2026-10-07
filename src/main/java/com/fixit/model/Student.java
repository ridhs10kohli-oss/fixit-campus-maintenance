package com.fixit.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Student entity extending User.
 * Demonstrates Inheritance and Encapsulation.
 */
public class Student extends User {
    private String studentId;                  // Unique College Roll / Student ID
    private List<String> complaintIds;         // List of Complaint IDs filed by this student

    public Student() {
        super();
        this.complaintIds = new ArrayList<>();
    }

    public Student(String id, String name, String email, String password, String studentId) {
        super(id, name, email, password);
        this.studentId = studentId;
        this.complaintIds = new ArrayList<>();
    }

    public Student(String id, String name, String email, String password, String studentId, List<String> complaintIds) {
        super(id, name, email, password);
        this.studentId = studentId;
        this.complaintIds = complaintIds != null ? complaintIds : new ArrayList<>();
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public List<String> getComplaintIds() {
        return complaintIds;
    }

    public void setComplaintIds(List<String> complaintIds) {
        this.complaintIds = complaintIds != null ? complaintIds : new ArrayList<>();
    }

    public void addComplaintId(String complaintId) {
        if (complaintId != null && !this.complaintIds.contains(complaintId)) {
            this.complaintIds.add(complaintId);
        }
    }

    @Override
    public String getRole() {
        return "STUDENT";
    }

    @Override
    public String toString() {
        return "Student{" +
                "studentId='" + studentId + '\'' +
                ", name='" + getName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", totalComplaints=" + (complaintIds != null ? complaintIds.size() : 0) +
                '}';
    }
}
