package com.carracinggame.database;

import com.carracinggame.car.CarId;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class PlayerDAO {

    private final MongoCollection<Document> players =
            MongoDBConnection.getPlayersCollection();

    public static class PlayerGameData {
        private final String username;
        private final int coins;
        private final CarId equippedCarId;
        private final List<CarId> ownedCarIds;

        public PlayerGameData(String username, int coins, CarId equippedCarId, List<CarId> ownedCarIds) {
            this.username = username;
            this.coins = coins;
            this.equippedCarId = equippedCarId;
            this.ownedCarIds = ownedCarIds;
        }

        public String getUsername() {
            return username;
        }

        public int getCoins() {
            return coins;
        }

        public CarId getEquippedCarId() {
            return equippedCarId;
        }

        public List<CarId> getOwnedCarIds() {
            return ownedCarIds;
        }
    }

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
                .append("ownedCars", Arrays.asList(CarId.RED_RACER.name()))
                .append("equippedCar", CarId.RED_RACER.name())
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

    public PlayerGameData loadGameData(String username) {
        if (username == null || username.isBlank()) {
            return createDefaultData("Player");
        }

        Document user = getPlayerByUsername(username);
        if (user == null) {
            return createDefaultData(username);
        }

        int coins = 1000;
        Object coinsObj = user.get("coins");
        if (coinsObj instanceof Number number) {
            coins = number.intValue();
        }

        CarId equippedCarId = parseCarId(user.getString("equippedCar"), CarId.RED_RACER);

        List<CarId> ownedCarIds = new ArrayList<>();
        Object rawOwnedCars = user.get("ownedCars");
        if (rawOwnedCars instanceof List<?> list) {
            for (Object item : list) {
                CarId parsed = parseCarId(String.valueOf(item), null);
                if (parsed != null && !ownedCarIds.contains(parsed)) {
                    ownedCarIds.add(parsed);
                }
            }
        }

        if (!ownedCarIds.contains(CarId.RED_RACER)) {
            ownedCarIds.add(0, CarId.RED_RACER);
        }

        if (!ownedCarIds.contains(equippedCarId)) {
            ownedCarIds.add(equippedCarId);
        }

        return new PlayerGameData(
                user.getString("username"),
                coins,
                equippedCarId,
                ownedCarIds
        );
    }

    public boolean savePlayerProgress(String username, int coins, List<CarId> ownedCars, CarId equippedCar) {
        if (username == null || username.isBlank()) {
            return false;
        }

        List<String> ownedCarNames = new ArrayList<>();
        if (ownedCars != null) {
            for (CarId carId : ownedCars) {
                if (carId != null && !ownedCarNames.contains(carId.name())) {
                    ownedCarNames.add(carId.name());
                }
            }
        }

        if (!ownedCarNames.contains(CarId.RED_RACER.name())) {
            ownedCarNames.add(0, CarId.RED_RACER.name());
        }

        CarId safeEquipped = equippedCar == null ? CarId.RED_RACER : equippedCar;
        if (!ownedCarNames.contains(safeEquipped.name())) {
            ownedCarNames.add(safeEquipped.name());
        }

        try {
            Document updateFields = new Document("coins", coins)
                    .append("ownedCars", ownedCarNames)
                    .append("equippedCar", safeEquipped.name());

            players.updateOne(
                    eq("username", username.trim()),
                    new Document("$set", updateFields)
            );
            return true;
        } catch (Exception e) {
            return false;
        }
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

    private PlayerGameData createDefaultData(String username) {
        List<CarId> defaultCars = new ArrayList<>();
        defaultCars.add(CarId.RED_RACER);
        return new PlayerGameData(username, 1000, CarId.RED_RACER, defaultCars);
    }

    private CarId parseCarId(String value, CarId fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }

        try {
            return CarId.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            return fallback;
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