# FixIt Java - College Complaint & Maintenance Management System

An individual B.Tech academic software engineering project built using **Java 17**, **Maven**, and **MongoDB Atlas** following a clean, decoupled **Layered Architecture**.

---

## 1. Project Title
**FixIt Java – Console-Based Campus Maintenance & Facility Issue Tracking System**

---

## 2. Project Overview
In educational institutions, campus maintenance issues—such as non-functioning ceiling fans, malfunctioning air conditioners, water leakage in restrooms, damaged classroom furniture, and projector failures—are often reported through paper registers or informal channels. This leads to untracked delays, lack of accountability, and student dissatisfaction.

**FixIt Java** is a menu-driven, console-based management solution designed specifically for college campuses. It bridges the gap between **Students**, **Administrators**, and **Maintenance Technicians**:
- **Students** can submit complaints, track live statuses, cancel pending requests, and rate the resolution quality.
- **Administrators** can inspect, search, filter, and assign maintenance staff to complaints.
- **Maintenance Staff** can inspect their assigned tasks, mark tickets as `IN_PROGRESS`, and mark them `RESOLVED`.

---

## 3. Features

### Student Module
- **Registration & Validation**: Enforces valid email regex, non-empty names, minimum password lengths, and duplicate email prevention.
- **Authentication**: Secure verification against MongoDB documents.
- **File Complaints**: Auto-generates unique complaint identifiers (`CMP-0001`), attaches timestamps, and defaults status to `PENDING`.
- **View My Complaints**: Filtered view displaying only issues submitted by the logged-in student.
- **Track Status**: Look up real-time progress, assigned technician ID, and timestamp.
- **Cancel Complaint**: Students can cancel complaints that are still in `PENDING` state.
- **Submit Feedback**: Rate resolved complaints from 1 to 5 stars with comments (restricted strictly to `RESOLVED` complaints).

### Administrator Module
- **Complete Visibility**: View all registered complaints across the campus in a formatted tabular layout.
- **Advanced Search & Filtering**:
  - Search by Complaint ID.
  - Filter by Category (e.g., AC, Fan, Plumbing, Wi-Fi).
  - Filter by Location / Block / Room.
  - Filter by Priority (`LOW`, `MEDIUM`, `HIGH`, `URGENT`).
  - Filter by Status (`PENDING`, `ASSIGNED`, `IN_PROGRESS`, `RESOLVED`, `CANCELLED`).
- **Technician Allocation**: View technician specializations and active task loads before assigning them to pending complaints.
- **Complaint Management**: Cancel pending tickets or reassign staff if required.
- **Feedback & Quality Monitoring**: Review star ratings and student feedback for resolved complaints.

### Maintenance Staff Module
- **Staff Authentication**: Secure login for campus electricians, plumbers, HVAC technicians, etc.
- **Assignment View**: Displays only complaints assigned directly to the logged-in technician.
- **Progress Tracking**:
  - Transition ticket status from `ASSIGNED` to `IN_PROGRESS`.
  - Transition ticket status from `IN_PROGRESS` to `RESOLVED`.
- **Authorization Guard**: Staff members cannot view or tamper with tickets assigned to other technicians.

---

## 4. Technologies Used
- **Language**: Java 17 (JDK 17 LTS)
- **Build Tool**: Apache Maven 3.9.x
- **Database**: MongoDB Atlas (Cloud NoSQL Database)
- **Database Driver**: MongoDB Synchronous Java Driver (`org.mongodb:mongodb-driver-sync:4.11.1`)
- **Logging**: SLF4J Simple Logger (`org.slf4j:slf4j-simple:2.0.9`)
- **IDE Compatibility**: VS Code, IntelliJ IDEA, Eclipse, NetBeans
- **Configuration**: Zero-dependency environment loader (`.env` or system variables)

---

## 5. Architecture
The application strictly enforces a **Layered Architecture**. The UI layer (`Main.java`) never talks directly to the database layer (`MongoConnection`).

```
+-----------------------------------------------------------+
|               Presentation Layer (Main.java)              |
|        - Console Menus & User Interface                   |
|        - Input capture via ConsoleUtil                    |
+-----------------------------+-----------------------------+
                              |
                              v
+-----------------------------------------------------------+
|                       Service Layer                       |
|   - StudentService           - ComplaintService           |
|   - AdminService             - MaintenanceStaffService    |
|   - FeedbackService          - InputValidator             |
|   (Enforces business rules, transitions, & authorization) |
+-----------------------------+-----------------------------+
                              |
                              v
+-----------------------------------------------------------+
|                     Repository Layer                      |
|   - StudentRepository        - ComplaintRepository        |
|   - AdminRepository          - MaintenanceStaffRepository |
|   - FeedbackRepository                                    |
|   (Executes MongoDB CRUD, BSON filters, document mapping) |
+-----------------------------+-----------------------------+
                              |
                              v
+-----------------------------------------------------------+
|              Database Utility (Singleton)                 |
|   - MongoConnection.java                                  |
|   - EnvLoader.java                                        |
+-----------------------------+-----------------------------+
                              |
                              v
+-----------------------------------------------------------+
|                   MongoDB Atlas Cluster                   |
|   Collections: students, admins, maintenance_staff,       |
|                complaints, feedback                       |
+-----------------------------------------------------------+
```

---

## 6. Folder Structure
```
fixit-java/
├── pom.xml
├── .gitignore
├── .env.example
├── sample_documents.json
├── README.md
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── fixit/
        │           ├── Main.java
        │           ├── exception/
        │           │   ├── ComplaintNotFoundException.java
        │           │   ├── InvalidComplaintException.java
        │           │   └── InvalidStatusTransitionException.java
        │           ├── model/
        │           │   ├── Admin.java
        │           │   ├── Complaint.java
        │           │   ├── Feedback.java
        │           │   ├── MaintenanceStaff.java
        │           │   ├── Priority.java
        │           │   ├── Status.java
        │           │   ├── Student.java
        │           │   └── User.java
        │           ├── repository/
        │           │   ├── AdminRepository.java
        │           │   ├── ComplaintRepository.java
        │           │   ├── FeedbackRepository.java
        │           │   ├── MaintenanceStaffRepository.java
        │           │   └── StudentRepository.java
        │           ├── service/
        │           │   ├── AdminService.java
        │           │   ├── ComplaintService.java
        │           │   ├── FeedbackService.java
        │           │   ├── MaintenanceStaffService.java
        │           │   └── StudentService.java
        │           └── util/
        │               ├── ConsoleUtil.java
        │               ├── DatabaseSeeder.java
        │               ├── EnvLoader.java
        │               ├── InputValidator.java
        │               └── MongoConnection.java
        └── resources/
            └── simplelogger.properties
```

---

## 7. MongoDB Atlas Setup
1. Create a free account at [MongoDB Atlas](https://www.mongodb.com/cloud/atlas).
2. Create a free **M0 Sandbox Cluster**.
3. Under **Database Access**, create a database user (e.g. `fixit_user`) and record the password.
4. Under **Network Access**, add IP `0.0.0.0/0` (Allow access from anywhere) or your current public IP address.
5. Under **Clusters**, click **Connect** -> **Drivers** -> Select **Java** (version 4.3 or later).
6. Copy the connection string format:
   ```
   mongodb+srv://<username>:<password>@cluster0.abcde.mongodb.net/?retryWrites=true&w=majority
   ```

---

## 8. Environment Variable Setup
To protect sensitive credentials, the database URI is **never hardcoded** in the Java source files.

1. In the project root folder, copy `.env.example` to `.env`:
   ```bash
   cp .env.example .env
   ```
2. Open `.env` and fill in your actual credentials:
   ```properties
   MONGODB_URI=mongodb+srv://fixit_user:YourSecretPass123@cluster0.abcde.mongodb.net/?retryWrites=true&w=majority
   MONGODB_DATABASE=fixit_db
   ```
3. Alternatively, set system environment variables:
   - **Windows (PowerShell)**:
     ```powershell
     $env:MONGODB_URI="mongodb+srv://fixit_user:YourSecretPass123@cluster0.abcde.mongodb.net/?retryWrites=true&w=majority"
     $env:MONGODB_DATABASE="fixit_db"
     ```
   - **Linux / macOS**:
     ```bash
     export MONGODB_URI="mongodb+srv://fixit_user:YourSecretPass123@cluster0.abcde.mongodb.net/?retryWrites=true&w=majority"
     export MONGODB_DATABASE="fixit_db"
     ```

---

## 9. Maven Setup & Prerequisites
Verify your environment:
```bash
java -version    # Must show Java 17 or higher
mvn -version     # Must show Maven 3.9.x (or 3.8+)
```

---

## 10. How to Run

### Compile the Project
```bash
mvn clean compile
```

### Run using Maven Exec Plugin
```bash
mvn exec:java
```

### Or Build and Run Executable JAR
```bash
mvn clean package
java -jar target/fixit-java-1.0.0.jar
```

---

## 11. Sample Console Menus

### Initial Main Menu
```
============================================================
   WELCOME TO FIXIT JAVA
============================================================
1. Student Portal
2. Administrator Portal
3. Maintenance Staff Portal
4. Exit Application
------------------------------------------------------------
Enter your choice (1-4): 
```

### Student Dashboard
```
============================================================
   STUDENT DASHBOARD - AARAV SHARMA (STU-2026-01)
============================================================
1. Submit Complaint
2. View My Complaints
3. Track Complaint Status
4. Cancel Eligible Complaint
5. Submit Feedback
6. Logout
------------------------------------------------------------
```

### Admin Dashboard
```
============================================================
   ADMIN DASHBOARD - SYSTEM ADMINISTRATOR
============================================================
1. View All Complaints
2. Search Complaint by ID
3. Filter Complaints
4. Assign Maintenance Staff to Complaint
5. Update / Manage Complaint Details
6. View Pending Complaints
7. View In-Progress Complaints
8. View Resolved Complaints & Feedback
9. View All Maintenance Staff
10. Logout
------------------------------------------------------------
```

### Maintenance Staff Dashboard
```
============================================================
   STAFF DASHBOARD - RAJESH KUMAR [STF-101 - ELECTRICAL]
============================================================
1. View Assigned Complaints
2. View Complaint Details
3. Mark Complaint IN PROGRESS
4. Mark Complaint RESOLVED
5. Logout
------------------------------------------------------------
```

---

## 12. Database Collections & Schema Design

| Collection Name | Purpose | Primary Fields |
|---|---|---|
| `students` | Registered student accounts | `studentId`, `name`, `email`, `password`, `complaintIds` |
| `admins` | Administrative personnel | `adminId`, `name`, `email`, `password`, `department` |
| `maintenance_staff` | Technicians and repair staff | `staffId`, `name`, `email`, `password`, `specialization`, `assignedComplaintIds` |
| `complaints` | Maintenance tickets filed | `complaintId`, `studentId`, `category`, `description`, `location`, `priority`, `timestamp`, `status`, `assignedStaffId` |
| `feedback` | Post-resolution student reviews | `feedbackId`, `complaintId`, `studentId`, `rating` (1-5), `comment`, `timestamp` |

*(Refer to [sample_documents.json](sample_documents.json) for sample BSON/JSON records).*

---

## 13. OOP Concepts Demonstrated
1. **Encapsulation**:
   - All entity fields in `User`, `Student`, `Admin`, `MaintenanceStaff`, `Complaint`, and `Feedback` are `private`.
   - Access is mediated through validated getters, setters, and business methods.
2. **Inheritance**:
   - `User` serves as an abstract base class containing common fields (`id`, `name`, `email`, `password`).
   - `Student`, `Admin`, and `MaintenanceStaff` inherit from `User`, adding role-specific attributes.
3. **Abstraction**:
   - `User` declares an abstract method `public abstract String getRole()`.
   - Presentation layer operates against service interfaces without knowing underlying MongoDB driver specifics.
4. **Polymorphism**:
   - Each subclass of `User` overrides `getRole()` and `toString()`.
5. **Enums**:
   - Strongly-typed `Priority` (`LOW`, `MEDIUM`, `HIGH`, `URGENT`) and `Status` (`PENDING`, `ASSIGNED`, `IN_PROGRESS`, `RESOLVED`, `CANCELLED`).
6. **Design Patterns**:
   - **Singleton Pattern**: Implemented in `MongoConnection.java` to guarantee a single, thread-safe connection pool across the application.
   - **Repository Pattern**: Decouples domain logic from persistence mechanisms.
   - **Layered Architecture**: UI -> Service -> Repository -> Database.

---

## 14. Exception Handling
Custom business exceptions prevent invalid operations and ensure clean separation of concerns:
- `ComplaintNotFoundException`: Thrown when an invalid or nonexistent complaint ID is requested.
- `InvalidComplaintException`: Thrown when fields fail validation (e.g. empty location, blank description).
- `InvalidStatusTransitionException`: Thrown when attempting an illegal lifecycle transition (e.g., `PENDING` -> `RESOLVED`).
- Safe I/O Handling: `ConsoleUtil` catches `NumberFormatException` and avoids Scanner newline bugs, preventing terminal crashes from non-numeric input.

---

## 15. MongoDB CRUD Operations Demonstrated

| Operation | Feature / Method | Location |
|---|---|---|
| **CREATE** | Register Student (`insertOne`) | `StudentRepository.createStudent()` |
| | File Complaint (`insertOne`) | `ComplaintRepository.createComplaint()` |
| | Submit Feedback (`insertOne`) | `FeedbackRepository.createFeedback()` |
| **READ** | Login Authentication (`find`, `Filters.and`) | `StudentRepository.validateLogin()` |
| | View My Complaints (`find`, `Filters.eq`) | `ComplaintRepository.findComplaintsByStudent()` |
| | Search & Filter (`regex`, `Filters.eq`) | `ComplaintRepository.findComplaintsByCategory()` |
| | Track Complaint Status (`find.first()`) | `ComplaintRepository.findComplaintById()` |
| **UPDATE** | Assign Technician (`updateOne`, `Updates.combine`) | `ComplaintRepository.assignStaff()` |
| | Update Progress (`updateOne`, `Updates.set`) | `ComplaintRepository.updateComplaintStatus()` |
| | Append to Array (`Updates.addToSet`) | `StudentRepository.addComplaintToStudent()` |
| **DELETE / CANCEL** | Cancel Pending Complaint (Status to `CANCELLED`) | `ComplaintRepository.cancelComplaint()` |

---

## 16. Development Demo Accounts (Seed Data)
For immediate evaluation without manual setup, `DatabaseSeeder.java` seeds the following initial accounts if the collections are empty:

- **Admin Account**:
  - Email: `admin@fixit.edu`
  - Password: `admin123`
- **Maintenance Staff Accounts**:
  - Electrical: `rajesh@fixit.edu` / `staff123`
  - Plumbing: `suresh@fixit.edu` / `staff123`
  - HVAC: `amit@fixit.edu` / `staff123`

---

## 17. Testing Checklist

| # | Test Scenario | Expected Result | Verified? |
|---|---|---|:---:|
| 1 | Student Registration with valid inputs | Account created, unique Roll/Student ID assigned | [x] |
| 2 | Duplicate email registration | Rejects with descriptive error message | [x] |
| 3 | Student Login with wrong credentials | Rejects with "Invalid email or password" | [x] |
| 4 | File complaint with blank location | Catches `InvalidComplaintException` | [x] |
| 5 | Submit complaint with valid data | Creates ticket in `PENDING` state with `CMP-xxxx` ID | [x] |
| 6 | Student cancels `PENDING` complaint | Status changes to `CANCELLED` | [x] |
| 7 | Student cancels `ASSIGNED` complaint | Throws `InvalidStatusTransitionException` | [x] |
| 8 | Admin assigns technician to `PENDING` complaint | Status changes to `ASSIGNED`, staff ID updated | [x] |
| 9 | Staff updates assigned ticket | `ASSIGNED` -> `IN_PROGRESS` -> `RESOLVED` succeeds | [x] |
| 10 | Staff attempts invalid transition (`ASSIGNED` -> `RESOLVED`) | Throws `InvalidStatusTransitionException` | [x] |
| 11 | Student submits feedback before resolution | Rejects: Only `RESOLVED` complaints allow feedback | [x] |
| 12 | Student submits rating outside 1–5 | Rejects with validation error | [x] |
| 13 | Student submits feedback after `RESOLVED` | Successfully recorded and visible to Admin | [x] |
| 14 | Missing MongoDB connection string | Gracefully alerts user to check `.env` without crashing | [x] |

---

## 18. Future Improvements
1. **Password Hashing**: Implement BCrypt hashing for production credential storage.
2. **Notification Service**: SMS/Email alerts to students when tickets are assigned or resolved.
3. **Analytics Dashboard**: Graphical reports on average resolution turnaround time per department.
4. **REST API & Web UI**: Wrap the existing service layer with Spring Boot / REST controllers for mobile and web frontends.
