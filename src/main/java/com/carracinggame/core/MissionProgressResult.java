package com.carracinggame.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MissionProgressResult {
    private int rewardCoins;
    private final List<String> claimedMissionTitles = new ArrayList<>();

    public void addClaim(String title, int reward) {
        if (reward <= 0) {
            return;
        }
        rewardCoins += reward;
        claimedMissionTitles.add(title);
    }

    public int getRewardCoins() {
        return rewardCoins;
    }

    public List<String> getClaimedMissionTitles() {
        return Collections.unmodifiableList(claimedMissionTitles);
    }
}