package com.carracinggame.core;

public final class GameConfig {

    private GameConfig() {
    }

    // =========================
    // Cửa sổ game
    // =========================
    public static final String GAME_TITLE = "Car Racing Game 2D";

    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    public static final int MIN_WIDTH = 800;
    public static final int MIN_HEIGHT = 600;

    // =========================
    // Xe
    // =========================
    public static final double DEFAULT_CAR_SPEED = 4.0;
    public static final double MAX_CAR_SPEED = 8.0;
    public static final double MIN_CAR_SPEED = 0.0;

    public static final double DEFAULT_ACCELERATION = 0.18;
    public static final double DEFAULT_BRAKE_FORCE = 0.25;
    public static final double DEFAULT_TURN_SPEED = 3.0;

    public static final double CAR_W = 40;
    public static final double CAR_H = 70;

    // Alias để tương thích nếu file khác dùng tên khác
    public static final double CAR_WIDTH = CAR_W;
    public static final double CAR_HEIGHT = CAR_H;

    // Góc bù cho ảnh xe để đầu xe khớp với hướng chạy
    public static final double CAR_SPRITE_ROT_OFFSET_DEG = 180;

    // =========================
    // Đường đua / map
    // =========================
    // Prototype track
    public static final double TRACK_X = 200;
    public static final double TRACK_W = 400;

    // Alias để tương thích với code dùng ROAD_*
    public static final double ROAD_X = TRACK_X;
    public static final double ROAD_WIDTH = TRACK_W;

    public static final double TRACK_LEFT = TRACK_X;
    public static final double TRACK_RIGHT = TRACK_X + TRACK_W;

    public static final double TRACK_MARGIN = 10;

    public static final double PLAYER_START_X = TRACK_X + (TRACK_W - CAR_W) / 2.0;
    public static final double PLAYER_START_Y = HEIGHT - CAR_H - 40;

    // Nếu obstacle/map cuộn theo vòng lặp
    public static final double OBSTACLE_CYCLE_HEIGHT = 2400;

    // =========================
    // Lane / vạch đường
    // =========================
    public static final int LANE_COUNT = 3;

    public static final double LANE_1_X = TRACK_X + TRACK_W / 3.0;
    public static final double LANE_2_X = TRACK_X + TRACK_W * 2.0 / 3.0;

    public static final double LANE_DASH_WIDTH = 6;
    public static final double LANE_DASH_HEIGHT = 30;
    public static final double LANE_DASH_GAP = 20;

    // =========================
    // UI
    // =========================
    public static final double UI_PADDING = 16;
    public static final double BUTTON_WIDTH = 220;
    public static final double BUTTON_HEIGHT = 44;

    // =========================
    // Gameplay
    // =========================
    public static final int START_COINS = 3500;
    public static final int BONUS_COINS = 500;

    public static final double COLLISION_SPEED_PENALTY_RATE = 0.55;
    public static final double COLLISION_MIN_SPEED = 1.0;

    // =========================
    // FPS / loop
    // =========================
    public static final double TARGET_FPS = 60.0;
    public static final double FRAME_TIME = 1.0 / TARGET_FPS;
}