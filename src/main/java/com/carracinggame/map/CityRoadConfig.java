package com.carracinggame.map;

public final class CityRoadConfig {

    private CityRoadConfig() {
    }

    public static final double BLOCK_LENGTH = 360.0;
    public static final long INDEX_SEED_STEP = 7_919L;

    // Seed theo map
    public static final long NORTH_SEED = 11_011L;
    public static final long CENTRAL_SEED = 22_022L;
    public static final long SOUTH_SEED = 33_033L;

    // Tỷ lệ sinh block phụ
    public static final double STRAIGHT_SIDE_ALLEY_CHANCE = 0.06;
    public static final double ALLEY_CROSSWALK_CHANCE = 0.18;
    public static final double STRAIGHT_CROSSWALK_CHANCE = 0.05;

    public static final double T_JUNCTION_LIGHT_CHANCE = 0.92;
    public static final double ALLEY_LIGHT_CHANCE = 0.10;
    public static final double STRAIGHT_LIGHT_CHANCE = 0.03;

    // Seed decor
    public static final int LEFT_DECOR_SEED_BASE = 1_000;
    public static final int RIGHT_DECOR_SEED_BASE = 2_000;
    public static final int DECOR_SEED_RANGE = 100_000;

    // Đèn giao thông
    public static final double SIGNAL_CYCLE_SECONDS = 13.0;
    public static final double MAIN_GREEN_SECONDS = 5.5;
    public static final double MAIN_YELLOW_SECONDS = 1.5;
    public static final double BLOCK_SIGNAL_OFFSET = 1.35;

    // Người đi bộ
    public static final double PEDESTRIAN_TIME_SPEED = 0.17;
    public static final double PEDESTRIAN_BLOCK_OFFSET = 0.19;

    // Tùy chọn render / gameplay
    public static final double CROSSWALK_ZONE_RATIO = 0.50;

    public static long getBaseSeed(MapId mapId) {
        return switch (mapId) {
            case NORTH -> NORTH_SEED;
            case CENTRAL -> CENTRAL_SEED;
            case SOUTH -> SOUTH_SEED;
        };
    }
}