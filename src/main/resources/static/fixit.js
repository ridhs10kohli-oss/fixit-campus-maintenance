// ============================================================
// FIXIT - COMPLETE FRONTEND JAVASCRIPT
// ============================================================

document.addEventListener("DOMContentLoaded", function () {
// ================= NAVBAR HOME =================

const homeNav = document.querySelector('nav a[href="#home"]');

if (homeNav) {
    homeNav.addEventListener("click", function (event) {
        event.preventDefault();

        // Hide all application sections
        document.querySelectorAll("section").forEach(function (section) {
            section.style.display = "none";
        });

        // Show the homepage sections
        document.querySelector(".hero").style.display = "flex";
        document.querySelector(".stats").style.display = "grid";
        document.querySelector(".features").style.display = "block";
        document.querySelector(".how-it-works").style.display = "block";
        document.querySelector(".ai-section").style.display = "block";
        document.querySelector(".faq").style.display = "block";
        document.querySelector(".final-cta").style.display = "block";

        // Scroll to homepage
        document.querySelector(".hero").scrollIntoView({
            behavior: "smooth"
        });
    });
}
// ================= NAVBAR FEATURES =================

const featuresNav = document.querySelector('nav a[href="#features"]');

if (featuresNav) {
    featuresNav.addEventListener("click", function (event) {
        event.preventDefault();

        document.querySelectorAll("section").forEach(function (section) {
            section.style.display = "none";
        });

        const features = document.querySelector(".features");

        if (features) {
            features.style.display = "block";
            features.scrollIntoView({
                behavior: "smooth"
            });
        }
    });
}


// ================= NAVBAR HOW IT WORKS =================

const howNav = document.querySelector('nav a[href="#how-it-works"]');

if (howNav) {
    howNav.addEventListener("click", function (event) {
        event.preventDefault();

        document.querySelectorAll("section").forEach(function (section) {
            section.style.display = "none";
        });

        const howSection = document.querySelector(".how-it-works");

        if (howSection) {
            howSection.style.display = "block";
            howSection.scrollIntoView({
                behavior: "smooth"
            });
        }
    });
}


// ================= NAVBAR FAQ =================

const faqNav = document.querySelector('nav a[href="#faq"]');

if (faqNav) {
    faqNav.addEventListener("click", function (event) {
        event.preventDefault();

        document.querySelectorAll("section").forEach(function (section) {
            section.style.display = "none";
        });

        const faq = document.querySelector(".faq");

        if (faq) {
            faq.style.display = "block";
            faq.scrollIntoView({
                behavior: "smooth"
            });
        }
    });
}
    console.log("FixIt website loaded successfully!");



    // ========================================================
    // ELEMENTS
    // ========================================================

    const reportBtn = document.getElementById("reportBtn");
    const finalReportBtn = document.getElementById("finalReportBtn");
    const trackBtn = document.getElementById("trackBtn");

    const reportSection = document.getElementById("report-section");
    const complaintForm = document.getElementById("complaintForm");

    const authSection = document.getElementById("auth-section");
    const authTitle = document.getElementById("auth-title");
    const authSubtitle = document.getElementById("auth-subtitle");

    const registerForm = document.getElementById("registerForm");
    const loginForm = document.getElementById("loginForm");

    const switchAuthBtn = document.getElementById("switchAuthBtn");
    const authSwitchText = document.getElementById("auth-switch-text");

    const loginBtn = document.querySelector(".login-btn");
    const signupBtn = document.querySelector(".signup-btn");

    const studentDashboard =
        document.getElementById("student-dashboard");
    const studentLogoutBtn =
    document.getElementById("studentLogoutBtn");
    if (studentLogoutBtn) {
    studentLogoutBtn.onclick = function () {

        localStorage.removeItem("fixitStudentId");
        localStorage.removeItem("fixitStudentName");

        // Hide application sections
        document.getElementById("student-dashboard").style.display = "none";
        document.getElementById("report-section").style.display = "none";
        document.getElementById("auth-section").style.display = "none";
        document.getElementById("admin-auth-section").style.display = "none";
        document.getElementById("admin-dashboard").style.display = "none";
        document.getElementById("staff-auth-section").style.display = "none";
        document.getElementById("staff-dashboard").style.display = "none";

        // Show homepage
        document.querySelector(".hero").style.display = "flex";
        document.querySelector(".stats").style.display = "grid";
        document.querySelector(".features").style.display = "block";
        document.querySelector(".how-it-works").style.display = "block";
        document.querySelector(".ai-section").style.display = "block";
        document.querySelector(".faq").style.display = "block";
        document.querySelector(".final-cta").style.display = "block";

        // Go to top
        window.scrollTo({
            top: 0,
            behavior: "smooth"
        });

        alert("You have been logged out successfully. 👋");
    };
}
    const dashboardReportBtn =
        document.getElementById("dashboardReportBtn");

    const viewComplaintsBtn =
        document.getElementById("viewComplaintsBtn");
    const dashboardTrackBtn =
        document.getElementById("dashboardTrackBtn");
    const adminDashboard =
        document.getElementById("admin-dashboard");
    const adminComplaintsList =
        document.getElementById("adminComplaintsList");
    const staffLoginBtn = document.getElementById("staffLoginBtn");
    const staffAuthSection = document.getElementById("staff-auth-section");
    const staffLoginForm = document.getElementById("staffLoginForm");


    // ========================================================
    // HELPER: OPEN REPORT FORM
    // ========================================================

    function openReportForm() {

        if (!reportSection) {
            alert("Report section could not be found.");
            return;
        }

        reportSection.style.display = "block";

        reportSection.scrollIntoView({
            behavior: "smooth",
            block: "start"
        });
    }


    // ========================================================
    // HELPER: OPEN STUDENT DASHBOARD
    // ========================================================

    function openStudentDashboard() {

        if (!studentDashboard) {
            return;
        }

        studentDashboard.style.display = "block";

        studentDashboard.scrollIntoView({
            behavior: "smooth",
            block: "start"
        });
    }


    // ========================================================
    // MAIN "REPORT AN ISSUE" BUTTON
    // ========================================================

    if (reportBtn) {

        reportBtn.addEventListener("click", function () {
            openReportForm();
        });

    }


    // ========================================================
    // FINAL "REPORT AN ISSUE" BUTTON
    // ========================================================

    if (finalReportBtn) {

        finalReportBtn.addEventListener("click", function () {
            openReportForm();
        });

    }


    // ========================================================
    // DASHBOARD "REPORT AN ISSUE"
    // ========================================================

    if (dashboardReportBtn) {

        dashboardReportBtn.addEventListener("click", function () {
            openReportForm();
        });

    }


    // ========================================================
    // STUDENT REGISTRATION
    // ========================================================

    if (registerForm) {

        registerForm.addEventListener("submit", async function (event) {

            event.preventDefault();

            const name =
                document.getElementById("registerName").value.trim();

            const studentId =
                document.getElementById("registerStudentId").value.trim();

            const email =
                document.getElementById("registerEmail").value.trim();

            const password =
                document.getElementById("registerPassword").value;

            try {

                const response = await fetch("/api/students/register", {

                    method: "POST",

                    headers: {
                        "Content-Type": "application/json"
                    },

                    body: JSON.stringify({
                        name: name,
                        studentId: studentId,
                        email: email,
                        password: password
                    })

                });


                const result = await response.text();


                if (!response.ok) {
                    throw new Error(result);
                }


                alert(
                    "Account created successfully! 🎉\n\n" +
                    "You can now login."
                );


                registerForm.reset();


                // Show login form
                registerForm.style.display = "none";
                loginForm.style.display = "block";

                if (authTitle) {
                    authTitle.textContent = "Welcome Back";
                }

                if (authSubtitle) {
                    authSubtitle.textContent =
                        "Login to manage your campus complaints.";
                }

                if (authSwitchText) {
                    authSwitchText.textContent =
                        "Don't have an account?";
                }

                if (switchAuthBtn) {
                    switchAuthBtn.textContent = "Create Account";
                }


            } catch (error) {

                console.error("Registration error:", error);

                alert(
                    "Registration failed.\n\n" +
                    error.message
                );

            }

        });

    }


    // ========================================================
    // STUDENT LOGIN
    // ========================================================

    if (loginForm) {

        loginForm.addEventListener("submit", async function (event) {

            event.preventDefault();


            const email =
                document.getElementById("loginEmail").value.trim();

            const password =
                document.getElementById("loginPassword").value;


            try {

                const response = await fetch("/api/students/login", {

                    method: "POST",

                    headers: {
                        "Content-Type": "application/json"
                    },

                    body: JSON.stringify({
                        email: email,
                        password: password
                    })

                });


                const result = await response.text();


                if (!response.ok) {
                    throw new Error(result);
                }


                const student = JSON.parse(result);


                // Save logged-in student's ID
                localStorage.setItem(
                    "fixitStudentId",
                    student.studentId
                );


                localStorage.setItem(
                    "fixitStudentName",
                    student.name
                );


                alert(
                    "Login successful! 🎉\n\n" +
                    "Welcome, " + student.name + "!"
                );


                loginForm.reset();


                // Hide login
                authSection.style.display = "none";


                // Open dashboard
                openStudentDashboard();

            } catch (error) {

                console.error("Login error:", error);

                alert(
                    "Login failed.\n\n" +
                    error.message
                );

            }

        });

    }


    // ========================================================
    // LOGIN / SIGNUP SWITCH
    // ========================================================

    if (switchAuthBtn) {

        switchAuthBtn.addEventListener("click", function () {

            if (registerForm.style.display !== "none") {

                // Registration → Login

                registerForm.style.display = "none";
                loginForm.style.display = "block";

                if (authTitle) {
                    authTitle.textContent = "Welcome Back";
                }

                if (authSubtitle) {
                    authSubtitle.textContent =
                        "Login to manage your campus complaints.";
                }

                if (authSwitchText) {
                    authSwitchText.textContent =
                        "Don't have an account?";
                }

                switchAuthBtn.textContent =
                    "Create Account";

            } else {

                // Login → Registration

                registerForm.style.display = "block";
                loginForm.style.display = "none";

                if (authTitle) {
                    authTitle.textContent =
                        "Create Your Account";
                }

                if (authSubtitle) {
                    authSubtitle.textContent =
                        "Register to report and track campus issues.";
                }

                if (authSwitchText) {
                    authSwitchText.textContent =
                        "Already have an account?";
                }

                switchAuthBtn.textContent =
                    "Login";

            }

        });

    }


    // ========================================================
    // NAVBAR LOGIN BUTTON
    // ========================================================

    if (loginBtn) {

        loginBtn.addEventListener("click", function () {

            if (!authSection) {
                return;
            }

            authSection.style.display = "block";

            authSection.scrollIntoView({
                behavior: "smooth",
                block: "start"
            });

        });

    }


    // ========================================================
    // NAVBAR GET STARTED BUTTON
    // ========================================================

    if (signupBtn) {

        signupBtn.addEventListener("click", function () {

            if (!authSection) {
                return;
            }

            authSection.style.display = "block";

            registerForm.style.display = "block";
            loginForm.style.display = "none";

            authSection.scrollIntoView({
                behavior: "smooth",
                block: "start"
            });

        });

    }


    // ========================================================
    // SUBMIT COMPLAINT
    // ========================================================

    if (complaintForm) {

        complaintForm.addEventListener(
            "submit",
            async function (event) {

                event.preventDefault();


                // Check login
                const studentId =
                    localStorage.getItem("fixitStudentId");


                if (!studentId) {

                    alert(
                        "Please login before submitting a complaint."
                    );

                    return;
                }


                const category =
                    document.getElementById("category").value;

                const location =
                    document.getElementById("location").value.trim();

                const description =
                    document.getElementById("description").value.trim();


                const priorityElement =
                    document.querySelector(
                        'input[name="priority"]:checked'
                    );


                const priority =
                    priorityElement
                        ? priorityElement.value
                        : "MEDIUM";


                if (!category || !location || !description) {

                    alert(
                        "Please fill all required fields."
                    );

                    return;
                }


                try {

                    const response = await fetch(
                        "/api/complaints",
                        {

                            method: "POST",

                            headers: {
                                "Content-Type": "application/json"
                            },

                            body: JSON.stringify({

                                studentId: studentId,

                                category: category,

                                description: description,

                                location: location,

                                priority: priority

                            })

                        }
                    );


                    const result = await response.text();


                    if (!response.ok) {
                        throw new Error(result);
                    }


                    const complaint =
                        JSON.parse(result);


                    alert(
                        "Complaint submitted successfully! 🎉\n\n" +
                        "Complaint ID: " +
                        complaint.complaintId +
                        "\n\n" +
                        "Status: " +
                        complaint.status
                    );


                    // Reset only the form
                    complaintForm.reset();


                    // Keep user logged in
                    // Keep dashboard available

                } catch (error) {

                    console.error(
                        "Complaint submission error:",
                        error
                    );

                    alert(
                        "Complaint could not be submitted.\n\n" +
                        error.message
                    );

                }

            }
        );

    }


    // ========================================================
    // MY COMPLAINTS
    // ========================================================

    if (viewComplaintsBtn) {

        viewComplaintsBtn.addEventListener(
            "click",
            async function () {

                const studentId =
                    localStorage.getItem("fixitStudentId");


                if (!studentId) {

                    alert(
                        "Please login first."
                    );

                    return;
                }


                try {

                    const response = await fetch(
                        "/api/complaints/student/" +
                        encodeURIComponent(studentId)
                    );


                    if (!response.ok) {
                        throw new Error(
                            "Could not load complaints."
                        );
                    }


                    const complaints =
                        await response.json();


                    if (!complaints ||
                        complaints.length === 0) {

                        alert(
                            "You have not submitted any complaints yet."
                        );

                        return;
                    }


                    const complaintsList =
    document.getElementById("studentComplaintsList");

if (complaintsList) {

    complaintsList.innerHTML = "";

    complaints.forEach(function (complaint) {

        const card =
            document.createElement("div");

        card.className =
            "student-complaint-card";

        card.innerHTML = `
            <div class="complaint-card-top">
                <h3>${complaint.complaintId}</h3>
                <span class="complaint-status status-${complaint.status.toLowerCase().replace("_", "-")}">
                    ${complaint.status}
                </span>
            </div>

            <div class="complaint-card-details">
                <p><strong>Category:</strong> ${complaint.category}</p>
                <p><strong>Location:</strong> ${complaint.location}</p>
                <p><strong>Priority:</strong> ${complaint.priority}</p>
                <p><strong>Description:</strong> ${complaint.description}</p>
            </div>
        `;

        complaintsList.appendChild(card);
    });

    const complaintsContainer =
        document.getElementById("my-complaints-container");

    if (complaintsContainer) {
        complaintsContainer.style.display = "block";
        complaintsContainer.scrollIntoView({
            behavior: "smooth"
        });
    }
}

                } catch (error) {

                    console.error(
                        "My Complaints error:",
                        error
                    );

                    alert(
                        "Could not load your complaints.\n\n" +
                        error.message
                    );

                }

            }
        );

    }


    // ========================================================
    // TRACK COMPLAINT - MAIN BUTTON
    // ========================================================

   async function trackComplaint() {

    const complaintIdInput =
        document.getElementById("trackComplaintId");

    const resultContainer =
        document.getElementById("trackComplaintResult");

    if (!complaintIdInput || !resultContainer) {
        return;
    }

    const complaintId =
        complaintIdInput.value.trim();

    if (!complaintId) {
        alert("Please enter a Complaint ID.");
        return;
    }

    try {

        const response = await fetch(
            "/api/complaints/" +
            encodeURIComponent(complaintId)
        );

        if (!response.ok) {

            if (response.status === 404) {
                throw new Error("Complaint not found.");
            }

            throw new Error("Unable to track complaint.");
        }

        const complaint =
            await response.json();

        resultContainer.innerHTML = `
            <div class="track-result-card">

                <div class="track-result-top">
                    <h3>${complaint.complaintId}</h3>

                    <span class="complaint-status status-${complaint.status.toLowerCase().replace("_", "-")}">
                        ${complaint.status}
                    </span>
                </div>

                <div class="track-result-details">

                    <p>
                        <strong>Category:</strong>
                        ${complaint.category}
                    </p>

                    <p>
                        <strong>Location:</strong>
                        ${complaint.location}
                    </p>

                    <p>
                        <strong>Priority:</strong>
                        ${complaint.priority}
                    </p>

                    <p>
                        <strong>Description:</strong>
                        ${complaint.description}
                    </p>

                </div>

            </div>
        `;

    } catch (error) {

        console.error(
            "Track Complaint error:",
            error
        );

        resultContainer.innerHTML = `
            <div class="track-error">
                ❌ ${error.message}
            </div>
        `;
    }
}
const trackComplaintSubmit =
    document.getElementById("trackComplaintSubmit");

if (trackComplaintSubmit) {

    trackComplaintSubmit.addEventListener(
        "click",
        function () {
            trackComplaint();
        }
    );

}


    // ========================================================
    // HERO TRACK BUTTON
    // ========================================================

    if (trackBtn) {

        trackBtn.addEventListener(
            "click",
            function () {
                trackComplaint();
            }
        );

    }


    // ========================================================
    // DASHBOARD TRACK BUTTON
    // ========================================================

  if (dashboardTrackBtn) {

    dashboardTrackBtn.addEventListener(
        "click",
        function () {

            const trackContainer =
                document.getElementById("track-complaint-container");

            if (trackContainer) {
                trackContainer.style.display = "block";

                trackContainer.scrollIntoView({
                    behavior: "smooth"
                });
            }

        }
    );

}

    // ========================================================
    // RESTORE LOGIN STATE AFTER PAGE REFRESH
    // ========================================================

    const savedStudentId =
        localStorage.getItem("fixitStudentId");


    if (savedStudentId) {

        console.log(
            "Student already logged in:",
            savedStudentId
        );

    }


    // ================= ADMIN PORTAL =================

    const adminLoginBtn =
        document.getElementById("adminLoginBtn");

    const adminAuthSection =
        document.getElementById("admin-auth-section");

    const adminLoginForm =
        document.getElementById("adminLoginForm");


    if (adminLoginBtn) {

        adminLoginBtn.addEventListener("click", function () {

            if (!adminAuthSection) {
                return;
            }

            adminAuthSection.style.display = "block";

            adminAuthSection.scrollIntoView({
                behavior: "smooth",
                block: "start"
            });

        });

    }


    if (adminLoginForm) {

        adminLoginForm.addEventListener(
            "submit",
            async function (event) {

                event.preventDefault();

                const email =
                    document.getElementById("adminEmail").value.trim();

                const password =
                    document.getElementById("adminPassword").value;


                try {

                    const response = await fetch(
                        "/api/admin/login",
                        {
                            method: "POST",

                            headers: {
                                "Content-Type": "application/json"
                            },

                            body: JSON.stringify({
                                email: email,
                                password: password
                            })
                        }
                    );


                    const result =
                        await response.text();


                    if (!response.ok) {
                        throw new Error(result);
                    }


                    const admin =
                        JSON.parse(result);


                    alert(
                        "Admin Login Successful! 🎉\n\n" +
                        "Welcome, " +
                        admin.name +
                        "!"
                    );


                    adminLoginForm.reset();

                    adminAuthSection.style.display = "none";
                    adminDashboard.style.display = "block";

                    loadAdminComplaints();

                    adminDashboard.scrollIntoView({
                        behavior: "smooth",
                        block: "start"
                    });

                } catch (error) {

                    console.error(
                        "Admin login error:",
                        error
                    );

                    alert(
                        "Admin Login Failed.\n\n" +
                        error.message
                    );

                }

            }
        );

    }
    // ================= ADMIN DASHBOARD =================
    async function loadAdminComplaints() {

        if (!adminComplaintsList) {
            return;
        }

        try {

            const complaintsResponse =
                await fetch("/api/admin/complaints");

            const staffResponse =
                await fetch("/api/admin/staff");

            if (!complaintsResponse.ok || !staffResponse.ok) {
                throw new Error("Could not load admin data.");
            }

            const complaints =
                await complaintsResponse.json();

            const staffList =
                await staffResponse.json();

            if (!complaints || complaints.length === 0) {

                adminComplaintsList.innerHTML =
                    "<p>No complaints found.</p>";

                return;
            }

            adminComplaintsList.innerHTML = "";

            complaints.forEach(function (complaint) {

                const card =
                    document.createElement("div");

                card.className = "admin-complaint-card";
                let staffOptions =
                    '<option value="">Select Staff</option>';

                staffList.forEach(function (staff) {

                    staffOptions += `
                    <option value="${staff.staffId}">
                        ${staff.name} (${staff.staffId})
                    </option>
                `;

                });

                card.innerHTML = `
                <h3>${complaint.complaintId}</h3>

                <p>
                    <strong>Category:</strong>
                    ${complaint.category}
                </p>

                <p>
                    <strong>Location:</strong>
                    ${complaint.location}
                </p>

                <p>
                    <strong>Priority:</strong>
                    ${complaint.priority}
                </p>

                <div class="status-section">

    <label>
        <strong>Update Status:</strong>
    </label>

    <select class="status-select">
        <option value="PENDING" ${complaint.status === "PENDING" ? "selected" : ""}>
            PENDING
        </option>

        <option value="ASSIGNED" ${complaint.status === "ASSIGNED" ? "selected" : ""}>
            ASSIGNED
        </option>

        <option value="IN_PROGRESS" ${complaint.status === "IN_PROGRESS" ? "selected" : ""}>
            IN PROGRESS
        </option>

        <option value="RESOLVED" ${complaint.status === "RESOLVED" ? "selected" : ""}>
            RESOLVED
        </option>

        <option value="CANCELLED" ${complaint.status === "CANCELLED" ? "selected" : ""}>
            CANCELLED
        </option>
    </select>

    <button
        class="primary-btn update-status-btn"
        data-complaint-id="${complaint.complaintId}">
        Update Status
    </button>

</div>

                <p>
                    <strong>Description:</strong>
                    ${complaint.description}
                </p>

                <p>
                    <strong>Student ID:</strong>
                    ${complaint.studentId}
                </p>

                <div class="assign-section">

                    <label>
                        <strong>Assign Maintenance Staff:</strong>
                    </label>

                    <select class="staff-select">
                        ${staffOptions}
                    </select>

                    <button
                        class="primary-btn assign-staff-btn"
                        data-complaint-id="${complaint.complaintId}">
                        Assign Staff
                    </button>

                </div>
            `;

                adminComplaintsList.appendChild(card);

            });

            // Assign staff buttons

            document
                .querySelectorAll(".assign-staff-btn")
                .forEach(function (button) {

                    button.addEventListener(
                        "click",
                        async function () {

                            const complaintId =
                                button.dataset.complaintId;

                            const card =
                                button.closest(
                                    ".admin-complaint-card"
                                );

                            const select =
                                card.querySelector(
                                    ".staff-select"
                                );

                            const staffId =
                                select.value;

                            if (!staffId) {

                                alert(
                                    "Please select a maintenance staff member."
                                );

                                return;
                            }

                            try {

                                const response =
                                    await fetch(
                                        "/api/admin/complaints/" +
                                        encodeURIComponent(
                                            complaintId
                                        ) +
                                        "/assign",
                                        {
                                            method: "PUT",

                                            headers: {
                                                "Content-Type":
                                                    "application/json"
                                            },

                                            body: JSON.stringify({
                                                staffId: staffId
                                            })
                                        }
                                    );

                                const result =
                                    await response.text();

                                if (!response.ok) {
                                    throw new Error(result);
                                }

                                alert(
                                    "Maintenance staff assigned successfully! 🎉"
                                );

                                loadAdminComplaints();

                            } catch (error) {

                                console.error(
                                    "Assign staff error:",
                                    error
                                );

                                alert(
                                    "Could not assign staff.\n\n" +
                                    error.message
                                );

                            }

                        }
                    );

                });
            // Update status buttons

            document
                .querySelectorAll(".update-status-btn")
                .forEach(function (button) {

                    button.addEventListener(
                        "click",
                        async function () {

                            const complaintId =
                                button.dataset.complaintId;

                            const card =
                                button.closest(".admin-complaint-card");

                            const select =
                                card.querySelector(".status-select");

                            const status =
                                select.value;

                            try {

                                const response =
                                    await fetch(
                                        "/api/admin/complaints/" +
                                        encodeURIComponent(complaintId) +
                                        "/status",
                                        {
                                            method: "PUT",

                                            headers: {
                                                "Content-Type":
                                                    "application/json"
                                            },

                                            body: JSON.stringify({
                                                status: status
                                            })
                                        }
                                    );

                                const result =
                                    await response.text();

                                if (!response.ok) {
                                    throw new Error(result);
                                }

                                alert(
                                    "Complaint status updated successfully! 🎉"
                                );

                                loadAdminComplaints();

                            } catch (error) {

                                console.error(
                                    "Update status error:",
                                    error
                                );

                                alert(
                                    "Could not update complaint status.\n\n" +
                                    error.message
                                );
                            }
                        }
                    );
                });

        } catch (error) {

            console.error(
                "Admin complaints error:",
                error
            );

            adminComplaintsList.innerHTML =
                "<p>Could not load complaints.</p>";
        }
    }
    if (staffLoginForm) {
        staffLoginForm.addEventListener("submit", async function (event) {
            event.preventDefault();

            const email = document.getElementById("staffEmail").value.trim();
            const password = document.getElementById("staffPassword").value;

            try {
                const response = await fetch("/api/staff/login", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify({
                        email: email,
                        password: password
                    })
                });

                const result = await response.json();

                if (!response.ok) {
                    throw new Error(
                        typeof result === "string"
                            ? result
                            : "Invalid staff email or password."
                    );
                }

                localStorage.setItem("fixitStaffId", result.staffId);
                localStorage.setItem("fixitStaffName", result.name);

                staffAuthSection.style.display = "none";

                const staffDashboard =
                    document.getElementById("staff-dashboard");

                staffDashboard.style.display = "block";

                loadStaffComplaints();

                staffDashboard.scrollIntoView({
                    behavior: "smooth"
                });

                alert("Staff login successful! 🎉");

            } catch (error) {
                console.error("Staff login error:", error);
                alert(
                    "Staff login failed.\n\n" +
                    error.message
                );
            }
        });
    }
    // ================= STAFF DASHBOARD =================

    async function loadStaffComplaints() {

        const staffId =
            localStorage.getItem("fixitStaffId");

        const staffComplaintsList =
            document.getElementById("staffComplaintsList");

        if (!staffId || !staffComplaintsList) {
            return;
        }

        try {

            const response =
                await fetch(
                    "/api/staff/" +
                    encodeURIComponent(staffId) +
                    "/complaints"
                );

            if (!response.ok) {
                throw new Error("Could not load assigned complaints.");
            }

            const complaints =
                await response.json();

            if (!complaints || complaints.length === 0) {

                staffComplaintsList.innerHTML =
                    "<p>No complaints have been assigned to you.</p>";

                return;
            }

            staffComplaintsList.innerHTML = "";

            complaints.forEach(function (complaint) {

                const card =
                    document.createElement("div");

                card.className = "admin-complaint-card";

                card.innerHTML = `
                <h3>${complaint.complaintId}</h3>

                <p>
                    <strong>Category:</strong>
                    ${complaint.category}
                </p>

                <p>
                    <strong>Location:</strong>
                    ${complaint.location}
                </p>

                <p>
                    <strong>Priority:</strong>
                    ${complaint.priority}
                </p>
                <p>
                   <strong>Status:</strong>
                   <span class="complaint-status status-${complaint.status.toLowerCase().replace('_', '-')}">${complaint.status}</span>
                </p>

                <p>
                    <strong>Description:</strong>
                    ${complaint.description}
                </p>
            `;
            const statusButtons = document.createElement("div");
statusButtons.className = "staff-status-buttons";
statusButtons.innerHTML = `
    <button onclick="updateStaffComplaintStatus('${complaint.complaintId}', 'IN_PROGRESS')">
        Mark In Progress
    </button>

    <button onclick="updateStaffComplaintStatus('${complaint.complaintId}', 'RESOLVED')">
        Mark Resolved
    </button>
`;

card.appendChild(statusButtons);

                staffComplaintsList.appendChild(card);

            });

        } catch (error) {

            console.error(
                "Staff complaints error:",
                error
            );

            staffComplaintsList.innerHTML =
                "<p>Could not load assigned complaints.</p>";
        }
    }
});
window.openStaffPortal = function () {
    console.log("Staff Portal button clicked");

    document.querySelectorAll("section").forEach(function (section) {
        section.style.display = "none";
    });

    const staffAuthSection =
        document.getElementById("staff-auth-section");

    if (staffAuthSection) {
        staffAuthSection.style.display = "block";

        staffAuthSection.scrollIntoView({
            behavior: "smooth"
        });
    }
};
window.updateStaffComplaintStatus = async function (complaintId, status) {
    try {
        const response = await fetch(
            "/api/staff/complaints/" + encodeURIComponent(complaintId) + "/status",
            {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    status: status
                })
            }
        );

        const result = await response.text();

        if (!response.ok) {
            throw new Error(result || "Could not update complaint status.");
        }

        alert("Complaint status updated successfully! ✅");

        loadStaffComplaints();

    } catch (error) {
        console.error("Status update error:", error);
        alert("Could not update complaint status.\n\n" + error.message);
    }
};