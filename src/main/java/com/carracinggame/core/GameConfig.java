package com.carracinggame.core;

public final class
GameConfig {
    private GameConfig() {}

    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    public static final double DEFAULT_CAR_SPEED = 4.0;

    public static final double CAR_W = 40;
    public static final double CAR_H = 70;

    // Đường đua prototype (tạm), sau này chuyển qua map/
    public static final double TRACK_X = 200;
    public static final double TRACK_W = 400;

    // Bù góc cho ảnh xe để mũi xe khớp hướng chạy (0 / 90 / -90 / 180)
    public static final double CAR_SPRITE_ROT_OFFSET_DEG = 180;
}
