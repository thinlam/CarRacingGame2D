package com.carracinggame.reward;

import com.carracinggame.database.MongoDBConnection;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

public class DailyCheckInService {

    private static final int[] REWARD_TABLE = {100, 150, 200, 250, 300, 400, 500};

    private final MongoCollection<Document> players =
            MongoDBConnection.getPlayersCollection();

    public int[] getRewardTable() {
        return REWARD_TABLE.clone();
    }

    public DailyCheckInState getState(String username) {
        if (username == null || username.isBlank()) {
            return new DailyCheckInState(
                    false, 0, 1, 1, REWARD_TABLE[0], 0, null,
                    "Không tìm thấy tài khoản đăng nhập."
            );
        }

        Document player = players.find(eq("username", username)).first();
        if (player == null) {
            return new DailyCheckInState(
                    false, 0, 1, 1, REWARD_TABLE[0], 0, null,
                    "Không tìm thấy người chơi trong database."
            );
        }

        Document dailyCheckIn = player.get("dailyCheckIn", Document.class);

        String lastClaimDateText = null;
        int currentStreak = 0;
        int totalClaimDays = 0;

        if (dailyCheckIn != null) {
            lastClaimDateText = dailyCheckIn.getString("lastClaimDate");
            currentStreak = dailyCheckIn.getInteger("streak", 0);
            totalClaimDays = dailyCheckIn.getInteger("totalClaimDays", 0);
        }

        LocalDate lastClaimDate = parseDate(lastClaimDateText);
        LocalDate today = LocalDate.now();

        boolean canClaimToday;
        int claimStreakIfClaimToday;

        if (lastClaimDate == null) {
            canClaimToday = true;
            claimStreakIfClaimToday = 1;
        } else {
            long daysBetween = ChronoUnit.DAYS.between(lastClaimDate, today);

            if (daysBetween <= 0) {
                canClaimToday = false;
                claimStreakIfClaimToday = Math.max(currentStreak, 1);
            } else if (daysBetween == 1) {
                canClaimToday = true;
                claimStreakIfClaimToday = currentStreak + 1;
            } else {
                canClaimToday = true;
                claimStreakIfClaimToday = 1;
            }
        }

        int cycleDay = toCycleDay(claimStreakIfClaimToday);
        int rewardCoins = REWARD_TABLE[cycleDay - 1];

        String message = canClaimToday
                ? "Hôm nay bạn có thể nhận thưởng ngày " + cycleDay + "."
                : "Bạn đã điểm danh hôm nay rồi.";

        return new DailyCheckInState(
                canClaimToday,
                currentStreak,
                claimStreakIfClaimToday,
                cycleDay,
                rewardCoins,
                totalClaimDays,
                lastClaimDate,
                message
        );
    }

    public DailyCheckInState claimToday(String username) {
        DailyCheckInState state = getState(username);

        if (!state.isCanClaimToday()) {
            return new DailyCheckInState(
                    false,
                    state.getCurrentStreak(),
                    state.getClaimStreakIfClaimToday(),
                    state.getCycleDay(),
                    0,
                    state.getTotalClaimDays(),
                    state.getLastClaimDate(),
                    "Bạn đã nhận thưởng hôm nay rồi."
            );
        }

        LocalDate today = LocalDate.now();
        int newStreak = state.getClaimStreakIfClaimToday();
        int cycleDay = toCycleDay(newStreak);
        int rewardCoins = REWARD_TABLE[cycleDay - 1];
        int newTotalClaimDays = state.getTotalClaimDays() + 1;

        players.updateOne(
                eq("username", username),
                combine(
                        set("dailyCheckIn.lastClaimDate", today.toString()),
                        set("dailyCheckIn.streak", newStreak),
                        set("dailyCheckIn.totalClaimDays", newTotalClaimDays),
                        set("dailyCheckIn.updatedAt", new Date())
                )
        );

        return new DailyCheckInState(
                false,
                newStreak,
                newStreak,
                cycleDay,
                rewardCoins,
                newTotalClaimDays,
                today,
                "Điểm danh thành công."
        );
    }

    private int toCycleDay(int streak) {
        if (streak <= 0) {
            return 1;
        }
        return ((streak - 1) % REWARD_TABLE.length) + 1;
    }

    private LocalDate parseDate(String text) {
        try {
            if (text == null || text.isBlank()) {
                return null;
            }
            return LocalDate.parse(text);
        } catch (Exception e) {
            return null;
        }
    }
}