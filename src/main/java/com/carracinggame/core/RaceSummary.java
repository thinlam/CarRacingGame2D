package com.carracinggame.core;

import java.util.List;

public class RaceSummary {
    private final boolean victory;
    private final String mapName;
    private final String carName;
    private final int distanceMeters;
    private final int baseReward;
    private final int missionReward;
    private final int totalReward;
    private final List<String> claimedMissions;

    public RaceSummary(
            boolean victory,
            String mapName,
            String carName,
            int distanceMeters,
            int baseReward,
            int missionReward,
            List<String> claimedMissions
    ) {
        this.victory = victory;
        this.mapName = mapName;
        this.carName = carName;
        this.distanceMeters = distanceMeters;
        this.baseReward = baseReward;
        this.missionReward = missionReward;
        this.totalReward = baseReward + missionReward;
        this.claimedMissions = claimedMissions == null ? List.of() : List.copyOf(claimedMissions);
    }

    public static RaceSummary empty() {
        return new RaceSummary(true, "Chưa có", "Chưa có", 0, 0, 0, List.of());
    }

    public boolean isVictory() {
        return victory;
    }

    public String getMapName() {
        return mapName;
    }

    public String getCarName() {
        return carName;
    }

    public int getDistanceMeters() {
        return distanceMeters;
    }

    public int getBaseReward() {
        return baseReward;
    }

    public int getMissionReward() {
        return missionReward;
    }

    public int getTotalReward() {
        return totalReward;
    }

    public List<String> getClaimedMissions() {
        return claimedMissions;
    }

    public String getHeadline() {
        return victory ? "CHIẾN THẮNG" : "GAME OVER";
    }

    public String getSubline() {
        return victory
                ? "Bạn đã hoàn thành chặng đua và nhận thưởng." 
                : "Xe đã va chạm với traffic. Hãy thử lại để chinh phục map.";
    }
}
