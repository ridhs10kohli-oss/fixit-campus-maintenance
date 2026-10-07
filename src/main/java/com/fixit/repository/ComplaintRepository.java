package com.fixit.repository;

import com.fixit.model.Complaint;
import com.fixit.model.Priority;
import com.fixit.model.Status;
import com.fixit.util.MongoConnection;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Repository handling MongoDB CRUD operations for Complaints.
 * Collection: "complaints"
 */
public class ComplaintRepository {
    private final MongoCollection<Document> collection;

    public ComplaintRepository() {
        this.collection = MongoConnection.getInstance().getCollection("complaints");
    }

    /**
     * Inserts a new Complaint document into MongoDB.
     */
    public boolean createComplaint(Complaint complaint) {
        Document doc = new Document("complaintId", complaint.getComplaintId())
                .append("studentId", complaint.getStudentId())
                .append("category", complaint.getCategory())
                .append("description", complaint.getDescription())
                .append("location", complaint.getLocation())
                .append("priority", complaint.getPriority().name())
                .append("timestamp", complaint.getTimestamp().toString())
                .append("status", complaint.getStatus().name())
                .append("assignedStaffId", complaint.getAssignedStaffId());

        collection.insertOne(doc);
        ObjectId genId = doc.getObjectId("_id");
        if (genId != null) {
            complaint.setId(genId.toHexString());
        }
        return true;
    }

    /**
     * Finds a single complaint by complaintId (e.g., CMP-1001).
     */
    public Complaint findComplaintById(String complaintId) {
        if (complaintId == null) return null;
        Document doc = collection.find(Filters.eq("complaintId", complaintId.trim().toUpperCase())).first();
        return mapDocumentToComplaint(doc);
    }

    /**
     * Finds all complaints reported by a specific student.
     */
    public List<Complaint> findComplaintsByStudent(String studentId) {
        List<Complaint> list = new ArrayList<>();
        if (studentId == null) return list;

        FindIterable<Document> docs = collection.find(Filters.eq("studentId", studentId))
                .sort(Sorts.descending("timestamp"));
        for (Document d : docs) {
            Complaint c = mapDocumentToComplaint(d);
            if (c != null) list.add(c);
        }
        return list;
    }

    /**
     * Returns all complaints in the database.
     */
    public List<Complaint> findAllComplaints() {
        List<Complaint> list = new ArrayList<>();
        FindIterable<Document> docs = collection.find().sort(Sorts.descending("timestamp"));
        for (Document d : docs) {
            Complaint c = mapDocumentToComplaint(d);
            if (c != null) list.add(c);
        }
        return list;
    }

    /**
     * Finds complaints filtered by status.
     */
    public List<Complaint> findComplaintsByStatus(Status status) {
        List<Complaint> list = new ArrayList<>();
        if (status == null) return list;

        FindIterable<Document> docs = collection.find(Filters.eq("status", status.name()))
                .sort(Sorts.descending("timestamp"));
        for (Document d : docs) {
            Complaint c = mapDocumentToComplaint(d);
            if (c != null) list.add(c);
        }
        return list;
    }

    /**
     * Searches complaints by category (case-insensitive substring match).
     */
    public List<Complaint> findComplaintsByCategory(String category) {
        List<Complaint> list = new ArrayList<>();
        if (category == null) return list;

        Pattern regex = Pattern.compile(Pattern.quote(category.trim()), Pattern.CASE_INSENSITIVE);
        FindIterable<Document> docs = collection.find(Filters.regex("category", regex))
                .sort(Sorts.descending("timestamp"));
        for (Document d : docs) {
            Complaint c = mapDocumentToComplaint(d);
            if (c != null) list.add(c);
        }
        return list;
    }

    /**
     * Searches complaints by location (case-insensitive substring match).
     */
    public List<Complaint> findComplaintsByLocation(String location) {
        List<Complaint> list = new ArrayList<>();
        if (location == null) return list;

        Pattern regex = Pattern.compile(Pattern.quote(location.trim()), Pattern.CASE_INSENSITIVE);
        FindIterable<Document> docs = collection.find(Filters.regex("location", regex))
                .sort(Sorts.descending("timestamp"));
        for (Document d : docs) {
            Complaint c = mapDocumentToComplaint(d);
            if (c != null) list.add(c);
        }
        return list;
    }

    /**
     * Finds complaints filtered by priority.
     */
    public List<Complaint> findComplaintsByPriority(Priority priority) {
        List<Complaint> list = new ArrayList<>();
        if (priority == null) return list;

        FindIterable<Document> docs = collection.find(Filters.eq("priority", priority.name()))
                .sort(Sorts.descending("timestamp"));
        for (Document d : docs) {
            Complaint c = mapDocumentToComplaint(d);
            if (c != null) list.add(c);
        }
        return list;
    }

    /**
     * Finds complaints assigned to a specific maintenance staff member.
     */
    public List<Complaint> findComplaintsByAssignedStaff(String staffId) {
        List<Complaint> list = new ArrayList<>();
        if (staffId == null) return list;

        FindIterable<Document> docs = collection.find(Filters.eq("assignedStaffId", staffId))
                .sort(Sorts.descending("timestamp"));
        for (Document d : docs) {
            Complaint c = mapDocumentToComplaint(d);
            if (c != null) list.add(c);
        }
        return list;
    }

    /**
     * Updates complaint status in MongoDB.
     */
    public boolean updateComplaintStatus(String complaintId, Status newStatus) {
        if (complaintId == null || newStatus == null) return false;
        return collection.updateOne(
                Filters.eq("complaintId", complaintId.trim().toUpperCase()),
                Updates.set("status", newStatus.name())
        ).getModifiedCount() > 0;
    }

    /**
     * Assigns maintenance staff to a complaint and transitions status to ASSIGNED.
     */
    public boolean assignStaff(String complaintId, String staffId) {
        if (complaintId == null || staffId == null) return false;
        Bson updates = Updates.combine(
                Updates.set("assignedStaffId", staffId),
                Updates.set("status", Status.ASSIGNED.name())
        );
        return collection.updateOne(
                Filters.eq("complaintId", complaintId.trim().toUpperCase()),
                updates
        ).getModifiedCount() > 0;
    }

    /**
     * Cancels an eligible complaint by updating its status to CANCELLED.
     */
    public boolean cancelComplaint(String complaintId) {
        return updateComplaintStatus(complaintId, Status.CANCELLED);
    }

    /**
     * Generates the next sequential or unique complaint ID.
     */
    public String generateNextComplaintId() {
        long count = collection.countDocuments();
        return String.format("CMP-%04d", count + 1);
    }

    /**
     * Helper method to map a MongoDB Document to a Complaint model object.
     */
    private Complaint mapDocumentToComplaint(Document doc) {
        if (doc == null) return null;

        String id = doc.getObjectId("_id") != null ? doc.getObjectId("_id").toHexString() : null;
        String complaintId = doc.getString("complaintId");
        String studentId = doc.getString("studentId");
        String category = doc.getString("category");
        String description = doc.getString("description");
        String location = doc.getString("location");
        String priorityStr = doc.getString("priority");
        String timestampStr = doc.getString("timestamp");
        String statusStr = doc.getString("status");
        String assignedStaffId = doc.getString("assignedStaffId");

        Priority priority = Priority.MEDIUM;
        if (priorityStr != null) {
            try {
                priority = Priority.valueOf(priorityStr);
            } catch (Exception ignored) {}
        }

        Status status = Status.PENDING;
        if (statusStr != null) {
            try {
                status = Status.valueOf(statusStr);
            } catch (Exception ignored) {}
        }

        LocalDateTime timestamp = LocalDateTime.now();
        if (timestampStr != null) {
            try {
                timestamp = LocalDateTime.parse(timestampStr);
            } catch (Exception ignored) {}
        }

        return new Complaint(id, complaintId, studentId, category, description, location,
                priority, timestamp, status, assignedStaffId);
    }
}
