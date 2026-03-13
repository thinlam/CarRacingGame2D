package com.carracinggame.car;

import com.carracinggame.input.InputHandler;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;

public class PlayerCar extends Car {

    private final InputHandler input;

    private final double accel = 0.18;
    private final double brake = 0.30;
    private final double friction = 0.06;
    private final double maxSpeed = 6.5;
    private final double reverseMax = 2.8;     // tốc độ lùi tối đa
    private final double turnSpeedDeg = 3.2;

    public PlayerCar(double x, double y, double w, double h, Image sprite, InputHandler input) {
        super(x, y, w, h, sprite);
        this.input = input;

        this.angleDeg = 0; // ảnh của bạn đầu xe hướng lên -> 0° là chuẩn
        this.speed = 0;
    }

    public void update() {
        // xoay (thường chỉ cho xoay khi xe đang chạy để "thật" hơn)
        if (Math.abs(speed) > 0.05) {
            if (input.isDown(KeyCode.LEFT))  angleDeg -= turnSpeedDeg;
            if (input.isDown(KeyCode.RIGHT)) angleDeg += turnSpeedDeg;
        }

        boolean up = input.isDown(KeyCode.UP);
        boolean down = input.isDown(KeyCode.DOWN);

        // UP: tăng tốc tiến
        if (up && !down) {
            speed += accel;

            // DOWN: phanh trước, chỉ lùi khi đã dừng/đang lùi
        } else if (down && !up) {
            if (speed > 0) {
                speed = Math.max(0, speed - brake); // phanh về 0
            } else {
                speed = Math.max(-reverseMax, speed - accel); // lùi chậm
            }

            // không bấm gì: ma sát
        } else {
            if (speed > 0) speed = Math.max(0, speed - friction);
            if (speed < 0) speed = Math.min(0, speed + friction);
        }

        // clamp tốc độ tiến
        if (speed > maxSpeed) speed = maxSpeed;

        // di chuyển theo góc (0° = lên)
        double rad = Math.toRadians(angleDeg);
        x += Math.sin(rad) * speed;
        y -= Math.cos(rad) * speed;
    }
}
