package com.carracinggame.reward;

import java.time.LocalDate;

public class DailyCheckInState {

    private final boolean canClaimToday;
    private final int currentStreak;
    private final int claimStreakIfClaimToday;
    private final int cycleDay;
    private final int rewardCoins;
    private final int totalClaimDays;
    private final LocalDate lastClaimDate;
    private final String message;

    public DailyCheckInState(boolean canClaimToday,
                             int currentStreak,
                             int claimStreakIfClaimToday,
                             int cycleDay,
                             int rewardCoins,
                             int totalClaimDays,
                             LocalDate lastClaimDate,
                             String message) {
        this.canClaimToday = canClaimToday;
        this.currentStreak = currentStreak;
        this.claimStreakIfClaimToday = claimStreakIfClaimToday;
        this.cycleDay = cycleDay;
        this.rewardCoins = rewardCoins;
        this.totalClaimDays = totalClaimDays;
        this.lastClaimDate = lastClaimDate;
        this.message = message;
    }

    public boolean isCanClaimToday() {
        return canClaimToday;
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public int getClaimStreakIfClaimToday() {
        return claimStreakIfClaimToday;
    }

    public int getCycleDay() {
        return cycleDay;
    }

    public int getRewardCoins() {
        return rewardCoins;
    }

    public int getTotalClaimDays() {
        return totalClaimDays;
    }

    public LocalDate getLastClaimDate() {
        return lastClaimDate;
    }

    public String getMessage() {
        return message;
    }
}