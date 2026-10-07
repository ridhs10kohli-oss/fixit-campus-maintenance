package com.fixit.util;

import com.fixit.model.Admin;
import com.fixit.model.MaintenanceStaff;
import com.fixit.repository.AdminRepository;
import com.fixit.repository.MaintenanceStaffRepository;

/**
 * Seeds default accounts when the database is empty.
 * Designed specifically for college project demonstration and evaluation.
 * 
 * Note: These demo credentials are strictly for development/viva testing and
 * should be updated or removed before any real-world production deployment.
 */
public class DatabaseSeeder {

    public static void seedInitialData() {
        try {
            AdminRepository adminRepo = new AdminRepository();
            MaintenanceStaffRepository staffRepo = new MaintenanceStaffRepository();

            // 1. Seed Default Admin if none exists
            if (adminRepo.countAdmins() == 0) {
                Admin defaultAdmin = new Admin(
                        null,
                        "System Administrator",
                        "admin@fixit.edu",
                        "admin123",
                        "ADM-001",
                        "Campus Estate & Facilities"
                );
                adminRepo.createAdmin(defaultAdmin);
                System.out.println("[DEV NOTICE] Initial Admin seeded: admin@fixit.edu (Password: admin123)");
            }

            // 2. Seed Default Maintenance Staff if none exists
            if (staffRepo.countStaff() == 0) {
                MaintenanceStaff staff1 = new MaintenanceStaff(
                        null, "Rajesh Kumar", "rajesh@fixit.edu", "staff123", "STF-101", "Electrical & Lighting"
                );
                MaintenanceStaff staff2 = new MaintenanceStaff(
                        null, "Suresh Sharma", "suresh@fixit.edu", "staff123", "STF-102", "Plumbing & Water"
                );
                MaintenanceStaff staff3 = new MaintenanceStaff(
                        null, "Amit Patel", "amit@fixit.edu", "staff123", "STF-103", "HVAC & Air Conditioning"
                );

                staffRepo.createStaff(staff1);
                staffRepo.createStaff(staff2);
                staffRepo.createStaff(staff3);
                System.out.println("[DEV NOTICE] Sample Maintenance Staff seeded: rajesh@fixit.edu / staff123 (Electrical), suresh@fixit.edu (Plumbing), amit@fixit.edu (HVAC)");
            }
        } catch (Exception e) {
            // Seeder failure shouldn't crash application startup, but should log a notice
            System.err.println("[WARN] Seeder encountered an issue: " + e.getMessage());
        }
    }
}
