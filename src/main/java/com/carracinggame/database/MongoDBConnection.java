package com.carracinggame.database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.io.InputStream;
import java.util.Properties;

public class MongoDBConnection {

    private static MongoClient mongoClient;
    private static MongoDatabase database;

    private static String loadMongoUri() {
        try (InputStream input = MongoDBConnection.class
                .getClassLoader()
                .getResourceAsStream("application.properties")) {

            if (input == null) {
                throw new RuntimeException("Không tìm thấy file application.properties trong resources");
            }

            Properties props = new Properties();
            props.load(input);

            String uri = props.getProperty("MONGODB_URI");
            if (uri == null || uri.isBlank()) {
                throw new RuntimeException("Thiếu key MONGODB_URI trong application.properties");
            }

            return uri.trim();
        } catch (Exception e) {
            throw new RuntimeException("Không đọc được application.properties", e);
        }
    }

    public static void init() {
        if (mongoClient != null) return;

        String uri = loadMongoUri();

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(uri))
                .build();

        mongoClient = MongoClients.create(settings);
        database = mongoClient.getDatabase("carracinggame");

        database.runCommand(new Document("ping", 1));
        System.out.println("Connected to MongoDB Atlas");
    }

    public static MongoDatabase getDatabase() {
        if (database == null) {
            init();
        }
        return database;
    }

    public static MongoCollection<Document> getPlayersCollection() {
        return getDatabase().getCollection("players");
    }

    public static void close() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}