package com.carracinggame.database;

import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;

import static com.mongodb.client.model.Filters.eq;

public class PlayerDAO {

    private final MongoCollection<Document> players =
            MongoDBConnection.getPlayersCollection();

    // Hàm cũ giữ lại để tránh lỗi code cũ
    public boolean registerAccount(String username, String rawPassword) {
        return registerAccount(username, rawPassword, "", "helmet_a");
    }

    // Hàm mới: đăng ký có email + avatar
    public boolean registerAccount(String username, String rawPassword, String email, String selectedAvatar) {
        username = username == null ? "" : username.trim();
        email = email == null ? "" : email.trim();
        selectedAvatar = selectedAvatar == null || selectedAvatar.isBlank()
                ? "helmet_a"
                : selectedAvatar.trim();

        if (username.isEmpty() || rawPassword == null || rawPassword.isEmpty()) {
            return false;
        }

        Document existing = players.find(eq("username", username)).first();
        if (existing != null) {
            return false;
        }

        Document doc = new Document("username", username)
                .append("passwordHash", hashPassword(rawPassword))
                .append("email", email)
                .append("coins", 1000)
                .append("selectedAvatar", selectedAvatar)
                .append("ownedAvatars", Arrays.asList(selectedAvatar))
                .append("createdAt", new Date());

        try {
            players.insertOne(doc);
            return true;
        } catch (MongoWriteException e) {
            return false;
        }
    }

    public boolean validateLogin(String username, String rawPassword) {
        username = username == null ? "" : username.trim();

        if (username.isEmpty() || rawPassword == null || rawPassword.isEmpty()) {
            return false;
        }

        Document user = players.find(eq("username", username)).first();
        if (user == null) {
            return false;
        }

        String savedHash = user.getString("passwordHash");
        return hashPassword(rawPassword).equals(savedHash);
    }

    public Document getPlayerByUsername(String username) {
        if (username == null || username.isBlank()) {
            return null;
        }
        return players.find(eq("username", username.trim())).first();
    }

    public boolean updateSelectedAvatar(String username, String avatarId) {
        if (username == null || username.isBlank() || avatarId == null || avatarId.isBlank()) {
            return false;
        }

        try {
            Document user = players.find(eq("username", username.trim())).first();
            if (user == null) {
                return false;
            }

            players.updateOne(
                    eq("username", username.trim()),
                    new Document("$set", new Document("selectedAvatar", avatarId))
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String hashPassword(String rawPassword) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(rawPassword.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}