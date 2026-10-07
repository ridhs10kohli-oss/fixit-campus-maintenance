package com.fixit.repository;

import com.fixit.model.Student;
import com.fixit.util.MongoConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository handling MongoDB CRUD operations for Students.
 * Collection: "students"
 */
public class StudentRepository {
    private final MongoCollection<Document> collection;

    public StudentRepository() {
        this.collection = MongoConnection.getInstance().getCollection("students");
    }

    /**
     * Inserts a new student document into MongoDB.
     */
    public boolean createStudent(Student student) {
        Document doc = new Document("studentId", student.getStudentId())
                .append("name", student.getName())
                .append("email", student.getEmail().toLowerCase().trim())
                .append("password", student.getPassword())
                .append("complaintIds", student.getComplaintIds() != null ? student.getComplaintIds() : new ArrayList<String>());

        collection.insertOne(doc);
        ObjectId generatedId = doc.getObjectId("_id");
        if (generatedId != null) {
            student.setId(generatedId.toHexString());
        }
        return true;
    }

    /**
     * Finds a student by registered email address.
     */
    public Student findStudentByEmail(String email) {
        if (email == null) return null;
        Document doc = collection.find(Filters.eq("email", email.toLowerCase().trim())).first();
        return mapDocumentToStudent(doc);
    }

    /**
     * Finds a student by unique studentId (roll number).
     */
    public Student findStudentById(String studentId) {
        if (studentId == null) return null;
        Document doc = collection.find(Filters.eq("studentId", studentId)).first();
        return mapDocumentToStudent(doc);
    }

    /**
     * Adds a newly created complaint ID to the student's complaintIds list in MongoDB.
     */
    public boolean addComplaintToStudent(String studentId, String complaintId) {
        return collection.updateOne(
                Filters.eq("studentId", studentId),
                Updates.addToSet("complaintIds", complaintId)
        ).getModifiedCount() > 0;
    }

    /**
     * Validates student login credentials directly from MongoDB.
     */
    public Student validateLogin(String email, String password) {
        if (email == null || password == null) return null;
        Document doc = collection.find(Filters.and(
                Filters.eq("email", email.toLowerCase().trim()),
                Filters.eq("password", password)
        )).first();
        return mapDocumentToStudent(doc);
    }

    /**
     * Helper method to map a MongoDB Document to a Student model object.
     */
    private Student mapDocumentToStudent(Document doc) {
        if (doc == null) return null;

        String id = doc.getObjectId("_id") != null ? doc.getObjectId("_id").toHexString() : null;
        String studentId = doc.getString("studentId");
        String name = doc.getString("name");
        String email = doc.getString("email");
        String password = doc.getString("password");
        List<String> complaintIds = doc.getList("complaintIds", String.class, new ArrayList<>());

        return new Student(id, name, email, password, studentId, complaintIds);
    }
}
