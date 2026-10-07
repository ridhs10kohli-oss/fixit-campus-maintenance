package com.fixit.repository;

import com.fixit.model.Feedback;
import com.fixit.util.MongoConnection;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository handling MongoDB operations for Feedback.
 * Collection: "feedback"
 */
public class FeedbackRepository {
    private final MongoCollection<Document> collection;

    public FeedbackRepository() {
        this.collection = MongoConnection.getInstance().getCollection("feedback");
    }

    /**
     * Inserts student feedback document into MongoDB.
     */
    public boolean createFeedback(Feedback feedback) {
        Document doc = new Document("feedbackId", feedback.getFeedbackId())
                .append("complaintId", feedback.getComplaintId())
                .append("studentId", feedback.getStudentId())
                .append("rating", feedback.getRating())
                .append("comment", feedback.getComment())
                .append("timestamp", feedback.getTimestamp().toString());

        collection.insertOne(doc);
        ObjectId genId = doc.getObjectId("_id");
        if (genId != null) {
            feedback.setId(genId.toHexString());
        }
        return true;
    }

    /**
     * Retrieves feedback for a specific complaint ID.
     */
    public Feedback findFeedbackByComplaintId(String complaintId) {
        if (complaintId == null) return null;
        Document doc = collection.find(Filters.eq("complaintId", complaintId.trim().toUpperCase())).first();
        return mapDocumentToFeedback(doc);
    }

    /**
     * Retrieves all feedback documents ordered by latest first.
     */
    public List<Feedback> findAllFeedback() {
        List<Feedback> list = new ArrayList<>();
        FindIterable<Document> docs = collection.find().sort(Sorts.descending("timestamp"));
        for (Document d : docs) {
            Feedback f = mapDocumentToFeedback(d);
            if (f != null) list.add(f);
        }
        return list;
    }

    /**
     * Generates a new feedback ID.
     */
    public String generateNextFeedbackId() {
        long count = collection.countDocuments();
        return String.format("FB-%04d", count + 1);
    }

    /**
     * Maps MongoDB document to Feedback model object.
     */
    private Feedback mapDocumentToFeedback(Document doc) {
        if (doc == null) return null;

        String id = doc.getObjectId("_id") != null ? doc.getObjectId("_id").toHexString() : null;
        String feedbackId = doc.getString("feedbackId");
        String complaintId = doc.getString("complaintId");
        String studentId = doc.getString("studentId");
        Integer rating = doc.getInteger("rating", 5);
        String comment = doc.getString("comment");
        String timestampStr = doc.getString("timestamp");

        LocalDateTime timestamp = LocalDateTime.now();
        if (timestampStr != null) {
            try {
                timestamp = LocalDateTime.parse(timestampStr);
            } catch (Exception ignored) {}
        }

        return new Feedback(id, feedbackId, complaintId, studentId, rating, comment, timestamp);
    }
}
