package com.fixit.util;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.concurrent.TimeUnit;

/**
 * Singleton-based MongoDB Connection Manager.
 * 
 * Demonstrates:
 *   1. Singleton Design Pattern (single connection pool shared across the entire application).
 *   2. Secure configuration (retrieves connection string from environment variable or .env file).
 *   3. Graceful connection testing and cleanup.
 */
public class MongoConnection {

    private static volatile MongoConnection instance;
    private MongoClient mongoClient;
    private MongoDatabase database;
    private final String databaseName;

    private MongoConnection() {
        String mongoUri = EnvLoader.get("MONGODB_URI");
        System.out.println("[DEBUG] MONGODB_URI loaded: " +
        (mongoUri != null && !mongoUri.trim().isEmpty()));

        if (mongoUri == null || mongoUri.trim().isEmpty() || mongoUri.contains("<username>")) {
            throw new IllegalStateException(
                    "\n[CONFIG ERROR] 'MONGODB_URI' is not configured or still contains placeholder values!\n" +
                    "Please create a '.env' file in the project root directory or set the MONGODB_URI environment variable.\n" +
                    "Example:\n" +
                    "  MONGODB_URI=mongodb+srv://yourUser:yourPassword@cluster0.abcde.mongodb.net/?retryWrites=true&w=majority\n" +
                    "See '.env.example' for guidance."
            );
        }

        this.databaseName = EnvLoader.get("MONGODB_DATABASE", "fixit_db");

        try {
            ConnectionString connectionString = new ConnectionString(mongoUri);
            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(connectionString)
                    .applyToSocketSettings(builder -> 
                            builder.connectTimeout(10, TimeUnit.SECONDS)
                                   .readTimeout(15, TimeUnit.SECONDS))
                    .applyToClusterSettings(builder ->
                            builder.serverSelectionTimeout(10, TimeUnit.SECONDS))
                    .build();

            this.mongoClient = MongoClients.create(settings);
            this.database = mongoClient.getDatabase(databaseName);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize MongoDB Client: " + e.getMessage(), e);
        }
    }

    /**
     * Thread-safe double-checked locking Singleton getInstance().
     */
    public static MongoConnection getInstance() {
        if (instance == null) {
            synchronized (MongoConnection.class) {
                if (instance == null) {
                    instance = new MongoConnection();
                }
            }
        }
        return instance;
    }

    /**
     * Obtains the target MongoDatabase instance.
     */
    public MongoDatabase getDatabase() {
        return database;
    }

    /**
     * Convenience method to fetch a collection by name.
     */
    public MongoCollection<Document> getCollection(String collectionName) {
        return database.getCollection(collectionName);
    }

    /**
     * Verifies live connectivity with MongoDB Atlas or local daemon by executing ping command.
     */
    public boolean testConnection() {
        try {
            Document ping = new Document("ping", 1);
            database.runCommand(ping);
            return true;
        } catch (Exception e) {
            System.err.println("[DATABASE ERROR] Could not reach MongoDB server: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Closes the MongoDB connection pool when application exits.
     */
    public void close() {
        if (mongoClient != null) {
            try {
                mongoClient.close();
                instance = null;
            } catch (Exception e) {
                System.err.println("Error closing MongoDB client: " + e.getMessage());
            }
        }
    }
}
