package com.carracinggame.core;

public class DailyMission {
    private final String id;
    private final String title;
    private final int target;
    private final int rewardCoins;
    private final String unit;

    private int progress;
    private boolean claimed;

    public DailyMission(String id, String title, int target, int rewardCoins, String unit) {
        this.id = id;
        this.title = title;
        this.target = target;
        this.rewardCoins = rewardCoins;
        this.unit = unit;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getTarget() {
        return target;
    }

    public int getRewardCoins() {
        return rewardCoins;
    }

    public String getUnit() {
        return unit;
    }

    public int getProgress() {
        return progress;
    }

    public boolean isClaimed() {
        return claimed;
    }

    public boolean isCompleted() {
        return progress >= target;
    }

    public void addProgress(int amount) {
        if (claimed || amount <= 0) {
            return;
        }
        progress = Math.min(target, progress + amount);
    }

    public int claimIfCompleted() {
        if (!claimed && isCompleted()) {
            claimed = true;
            return rewardCoins;
        }
        return 0;
    }

    public String getProgressText() {
        if (claimed) {
            return "Đã nhận";
        }
        return Math.min(progress, target) + "/" + target + unit;
    }

    public String getRewardText() {
        return "+" + rewardCoins + " coin";
    }
}