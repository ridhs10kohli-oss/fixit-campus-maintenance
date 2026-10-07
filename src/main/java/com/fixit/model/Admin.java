package com.fixit.model;

/**
 * Admin entity extending User.
 * Demonstrates Inheritance and Encapsulation.
 */
public class Admin extends User {
    private String adminId;
    private String department;

    public Admin() {
        super();
    }

    public Admin(String id, String name, String email, String password, String adminId, String department) {
        super(id, name, email, password);
        this.adminId = adminId;
        this.department = department;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public String getRole() {
        return "ADMIN";
    }

    @Override
    public String toString() {
        return "Admin{" +
                "adminId='" + adminId + '\'' +
                ", name='" + getName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", department='" + department + '\'' +
                '}';
    }
}
