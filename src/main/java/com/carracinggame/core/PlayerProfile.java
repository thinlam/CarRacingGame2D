package com.carracinggame.core;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class PlayerProfile {
    private final String playerName;
    private final String rankName;
    private int coins;
    private GarageCar equippedCar;

    private int totalRaces;
    private int totalDistanceMeters;
    private int garageVisits;

    private final List<DailyMission> dailyMissions;
    private final EnumSet<GarageCar> ownedCars;

    public PlayerProfile(
            String playerName,
            String rankName,
            int coins,
            GarageCar equippedCar,
            List<DailyMission> dailyMissions,
            Set<GarageCar> ownedCars
    ) {
        this.playerName = playerName;
        this.rankName = rankName;
        this.coins = coins;
        this.equippedCar = equippedCar;
        this.dailyMissions = dailyMissions;
        this.ownedCars = EnumSet.noneOf(GarageCar.class);
        if (ownedCars != null) {
            this.ownedCars.addAll(ownedCars);
        }
        if (equippedCar != null) {
            this.ownedCars.add(equippedCar);
        }
    }

    public static PlayerProfile createDefault() {
        return new PlayerProfile(
                "Chip Chip",
                "Diamond IV",
                22000,
                GarageCar.BLAZE_X1,
                List.of(
                        new DailyMission("finish_race", "Hoàn thành 1 race", 1, 500, " race"),
                        new DailyMission("distance", "Chạy tổng 2500m", 2500, 350, "m"),
                        new DailyMission("garage", "Vào Garage 1 lần", 1, 250, " lần")
                ),
                EnumSet.of(GarageCar.BLAZE_X1)
        );
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getRankName() {
        return rankName;
    }

    public int getCoins() {
        return coins;
    }

    public GarageCar getEquippedCar() {
        return equippedCar;
    }

    public int getTotalRaces() {
        return totalRaces;
    }

    public int getTotalDistanceMeters() {
        return totalDistanceMeters;
    }

    public int getGarageVisits() {
        return garageVisits;
    }

    public List<DailyMission> getDailyMissions() {
        return dailyMissions;
    }

    public Set<GarageCar> getOwnedCars() {
        return Collections.unmodifiableSet(ownedCars);
    }

    public int getOwnedCarCount() {
        return ownedCars.size();
    }

    public boolean ownsCar(GarageCar car) {
        return car != null && ownedCars.contains(car);
    }

    public boolean canBuy(GarageCar car) {
        return car != null && !ownsCar(car) && coins >= car.getPrice();
    }

    public boolean buyCar(GarageCar car) {
        if (car == null || ownsCar(car) || coins < car.getPrice()) {
            return false;
        }
        coins -= car.getPrice();
        ownedCars.add(car);
        return true;
    }

    public void addCoins(int amount) {
        coins += Math.max(0, amount);
    }

    public void equipCar(GarageCar car) {
        if (ownsCar(car)) {
            equippedCar = car;
        }
    }

    public MissionProgressResult recordGarageVisit() {
        garageVisits++;
        MissionProgressResult result = new MissionProgressResult();

        for (DailyMission mission : dailyMissions) {
            if ("garage".equals(mission.getId())) {
                mission.addProgress(1);
                int reward = mission.claimIfCompleted();
                if (reward > 0) {
                    addCoins(reward);
                    result.addClaim(mission.getTitle(), reward);
                }
            }
        }
        return result;
    }

    public MissionProgressResult recordRace(int distanceMeters) {
        totalRaces++;
        totalDistanceMeters += Math.max(0, distanceMeters);

        MissionProgressResult result = new MissionProgressResult();

        for (DailyMission mission : dailyMissions) {
            switch (mission.getId()) {
                case "finish_race" -> mission.addProgress(1);
                case "distance" -> mission.addProgress(Math.max(0, distanceMeters));
                default -> {
                }
            }

            int reward = mission.claimIfCompleted();
            if (reward > 0) {
                addCoins(reward);
                result.addClaim(mission.getTitle(), reward);
            }
        }
        return result;
    }
}
