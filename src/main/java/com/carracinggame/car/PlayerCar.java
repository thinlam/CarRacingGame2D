package com.carracinggame.car;

import com.carracinggame.input.InputHandler;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;

public class PlayerCar extends Car {

    private final InputHandler input;
    private final Image straightSprite;
    private final Image leftSprite;
    private final Image rightSprite;
    private final Image crashSprite;

    private final double accel = 0.12;
    private final double brake = 0.20;
    private final double friction = 0.06;
    private final double maxSpeed;
    private final double moveStep;

    private boolean crashed;

    public PlayerCar(
            double x,
            double y,
            double w,
            double h,
            Image straightSprite,
            Image leftSprite,
            Image rightSprite,
            Image crashSprite,
            InputHandler input,
            double maxSpeed,
            double moveStep
    ) {
        super(x, y, w, h, straightSprite);
        this.input = input;
        this.straightSprite = straightSprite;
        this.leftSprite = leftSprite;
        this.rightSprite = rightSprite;
        this.crashSprite = crashSprite;
        this.maxSpeed = maxSpeed;
        this.moveStep = moveStep;
    }

    public void update() {
        if (crashed) {
            speed = Math.max(0, speed - 0.25);
            sprite = crashSprite;
            angleDeg = 18;
            return;
        }

        boolean left = input.isDown(KeyCode.LEFT) || input.isDown(KeyCode.A);
        boolean right = input.isDown(KeyCode.RIGHT) || input.isDown(KeyCode.D);
        boolean up = input.isDown(KeyCode.UP) || input.isDown(KeyCode.W);
        boolean down = input.isDown(KeyCode.DOWN) || input.isDown(KeyCode.S);

        if (left && !right) {
            x -= moveStep;
            angleDeg = -8;
            sprite = leftSprite;
        } else if (right && !left) {
            x += moveStep;
            angleDeg = 8;
            sprite = rightSprite;
        } else {
            angleDeg = 0;
            sprite = straightSprite;
        }

        if (up && !down) {
            speed = Math.min(maxSpeed, speed + accel);
        } else if (down && !up) {
            speed = Math.max(1.5, speed - brake);
        } else {
            speed = Math.max(2.0, speed - friction);
        }
    }

    public void reset(double x, double y) {
        this.x = x;
        this.y = y;
        this.speed = 2.4;
        this.angleDeg = 0;
        this.crashed = false;
        this.sprite = straightSprite;
    }

    public void crash() {
        crashed = true;
        sprite = crashSprite;
        angleDeg = 18;
    }

    public boolean isCrashed() {
        return crashed;
    }
}
