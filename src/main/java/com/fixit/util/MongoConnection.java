package com.fixit.util;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.concurrent.TimeUnit;

public class MongoConnection {

    private static volatile MongoConnection instance;
    private MongoClient mongoClient;
    private MongoDatabase database;
    private final String databaseName;

    private MongoConnection() {
        String mongoUri = EnvLoader.get("MONGODB_URI");

        if (mongoUri == null || mongoUri.trim().isEmpty()
                || mongoUri.contains("<username>")) {
            throw new IllegalStateException(
                    "MONGODB_URI is missing or contains placeholder values. "
                    + "Check your .env file."
            );
        }

        this.databaseName = EnvLoader.get("MONGODB_DATABASE", "fixit_db");

        try {
            ConnectionString connectionString =
                    new ConnectionString(mongoUri);

            MongoClientSettings settings =
                    MongoClientSettings.builder()
                            .applyConnectionString(connectionString)
                            .applyToSocketSettings(builder ->
                                    builder.connectTimeout(
                                            10, TimeUnit.SECONDS
                                    ).readTimeout(
                                            15, TimeUnit.SECONDS
                                    )
                            )
                            .applyToClusterSettings(builder ->
                                    builder.serverSelectionTimeout(
                                            10, TimeUnit.SECONDS
                                    )
                            )
                            .build();

            this.mongoClient = MongoClients.create(settings);
            this.database = mongoClient.getDatabase(databaseName);

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to initialize MongoDB client: "
                    + e.getMessage(), e
            );
        }
    }

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

    public MongoDatabase getDatabase() {
        return database;
    }

    public MongoCollection<Document> getCollection(String collectionName) {
        return database.getCollection(collectionName);
    }

    public boolean testConnection() {
        try {
            database.runCommand(new Document("ping", 1));
            System.out.println("[DATABASE] MongoDB connection successful.");
            return true;
        } catch (Exception e) {
            System.err.println(
                    "[DATABASE ERROR] Could not reach MongoDB: "
                    + e.getMessage()
            );
            return false;
        }
    }

    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
            mongoClient = null;
            database = null;
            instance = null;
        }
    }
}