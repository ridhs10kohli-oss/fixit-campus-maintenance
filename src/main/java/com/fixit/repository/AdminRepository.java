package com.fixit.repository;

import com.fixit.model.Admin;
import com.fixit.util.MongoConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;

/**
 * Repository handling MongoDB operations for Administrators.
 * Collection: "admins"
 */
public class AdminRepository {
    private final MongoCollection<Document> collection;

    public AdminRepository() {
        this.collection = MongoConnection.getInstance().getCollection("admins");
    }

    /**
     * Creates an admin account document.
     */
    public boolean createAdmin(Admin admin) {
        Document doc = new Document("adminId", admin.getAdminId())
                .append("name", admin.getName())
                .append("email", admin.getEmail().toLowerCase().trim())
                .append("password", admin.getPassword())
                .append("department", admin.getDepartment());

        collection.insertOne(doc);
        ObjectId genId = doc.getObjectId("_id");
        if (genId != null) {
            admin.setId(genId.toHexString());
        }
        return true;
    }

    /**
     * Finds an admin by email.
     */
    public Admin findAdminByEmail(String email) {
        if (email == null) return null;
        Document doc = collection.find(Filters.eq("email", email.toLowerCase().trim())).first();
        return mapDocumentToAdmin(doc);
    }

    /**
     * Finds an admin by adminId.
     */
    public Admin findAdminById(String adminId) {
        if (adminId == null) return null;
        Document doc = collection.find(Filters.eq("adminId", adminId)).first();
        return mapDocumentToAdmin(doc);
    }

    /**
     * Authenticates admin with email and password.
     */
    public Admin validateLogin(String email, String password) {
        if (email == null || password == null) return null;
        Document doc = collection.find(Filters.and(
                Filters.eq("email", email.toLowerCase().trim()),
                Filters.eq("password", password)
        )).first();
        return mapDocumentToAdmin(doc);
    }

    /**
     * Returns total count of registered administrators.
     */
    public long countAdmins() {
        return collection.countDocuments();
    }

    /**
     * Maps MongoDB document to Admin model.
     */
    private Admin mapDocumentToAdmin(Document doc) {
        if (doc == null) return null;

        String id = doc.getObjectId("_id") != null ? doc.getObjectId("_id").toHexString() : null;
        String adminId = doc.getString("adminId");
        String name = doc.getString("name");
        String email = doc.getString("email");
        String password = doc.getString("password");
        String department = doc.getString("department");

        return new Admin(id, name, email, password, adminId, department);
    }
}
