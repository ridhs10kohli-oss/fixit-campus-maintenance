package com.fixit.repository;

import com.fixit.model.MaintenanceStaff;
import com.fixit.util.MongoConnection;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository handling MongoDB operations for Maintenance Staff.
 * Collection: "maintenance_staff"
 */
public class MaintenanceStaffRepository {
    private final MongoCollection<Document> collection;

    public MaintenanceStaffRepository() {
        this.collection = MongoConnection.getInstance().getCollection("maintenance_staff");
    }

    /**
     * Inserts a new Maintenance Staff document into MongoDB.
     */
    public boolean createStaff(MaintenanceStaff staff) {
        Document doc = new Document("staffId", staff.getStaffId())
                .append("name", staff.getName())
                .append("email", staff.getEmail().toLowerCase().trim())
                .append("password", staff.getPassword())
                .append("specialization", staff.getSpecialization())
                .append("assignedComplaintIds", staff.getAssignedComplaintIds() != null ? staff.getAssignedComplaintIds() : new ArrayList<String>());

        collection.insertOne(doc);
        ObjectId genId = doc.getObjectId("_id");
        if (genId != null) {
            staff.setId(genId.toHexString());
        }
        return true;
    }

    /**
     * Finds staff by email.
     */
    public MaintenanceStaff findStaffByEmail(String email) {
        if (email == null) return null;
        Document doc = collection.find(Filters.eq("email", email.toLowerCase().trim())).first();
        return mapDocumentToStaff(doc);
    }

    /**
     * Finds staff by unique staffId (e.g. STF-101).
     */
    public MaintenanceStaff findStaffById(String staffId) {
        if (staffId == null) return null;
        Document doc = collection.find(Filters.eq("staffId", staffId.trim().toUpperCase())).first();
        return mapDocumentToStaff(doc);
    }

    /**
     * Returns all registered maintenance staff members.
     */
    public List<MaintenanceStaff> findAllStaff() {
        List<MaintenanceStaff> list = new ArrayList<>();
        FindIterable<Document> docs = collection.find();
        for (Document d : docs) {
            MaintenanceStaff s = mapDocumentToStaff(d);
            if (s != null) list.add(s);
        }
        return list;
    }

    /**
     * Appends a newly assigned complaint ID to the staff member's active list.
     */
    public boolean addComplaintToStaff(String staffId, String complaintId) {
        if (staffId == null || complaintId == null) return false;
        return collection.updateOne(
                Filters.eq("staffId", staffId.trim().toUpperCase()),
                Updates.addToSet("assignedComplaintIds", complaintId.trim().toUpperCase())
        ).getModifiedCount() > 0;
    }

    /**
     * Authenticates maintenance staff with email and password.
     */
    public MaintenanceStaff validateLogin(String email, String password) {
        if (email == null || password == null) return null;
        Document doc = collection.find(Filters.and(
                Filters.eq("email", email.toLowerCase().trim()),
                Filters.eq("password", password)
        )).first();
        return mapDocumentToStaff(doc);
    }

    /**
     * Returns total count of maintenance staff.
     */
    public long countStaff() {
        return collection.countDocuments();
    }

    /**
     * Maps MongoDB document to MaintenanceStaff model object.
     */
    private MaintenanceStaff mapDocumentToStaff(Document doc) {
        if (doc == null) return null;

        String id = doc.getObjectId("_id") != null ? doc.getObjectId("_id").toHexString() : null;
        String staffId = doc.getString("staffId");
        String name = doc.getString("name");
        String email = doc.getString("email");
        String password = doc.getString("password");
        String specialization = doc.getString("specialization");
        List<String> assignedComplaintIds = doc.getList("assignedComplaintIds", String.class, new ArrayList<>());

        return new MaintenanceStaff(id, name, email, password, staffId, specialization, assignedComplaintIds);
    }
}
