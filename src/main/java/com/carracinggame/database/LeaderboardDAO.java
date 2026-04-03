package com.carracinggame.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.exists;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Sorts.descending;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.max;
import static com.mongodb.client.model.Updates.setOnInsert;

public class LeaderboardDAO {

    public record Entry(String username, double distanceKm) {
    }

    private final MongoCollection<Document> playersCollection;

    public LeaderboardDAO() {
        this.playersCollection = MongoDBConnection.getPlayersCollection();
    }

    public void saveBestDistance(String username, double distanceKm) {
        if (username == null || username.isBlank()) {
            return;
        }

        playersCollection.updateOne(
                new Document("username", username.trim()),
                new Document("$max", new Document("bestDistanceKm", distanceKm))
                        .append("$setOnInsert", new Document("username", username.trim())),
                new com.mongodb.client.model.UpdateOptions().upsert(true)
        );
    }
    public List<Entry> getTop10() {
        List<Entry> result = new ArrayList<>();

        for (Document doc : playersCollection.find(exists("bestDistanceKm"))
                .sort(descending("bestDistanceKm"))
                .limit(10)) {

            String username = doc.getString("username");
            Object value = doc.get("bestDistanceKm");

            double km = 0;
            if (value instanceof Number number) {
                km = number.doubleValue();
            }

            if (username == null || username.isBlank()) {
                username = "Player";
            }

            result.add(new Entry(username, km));
        }

        return result;
    }
}