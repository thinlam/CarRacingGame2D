package com.carracinggame.car;

import com.carracinggame.core.GameConfig;
import com.carracinggame.input.InputHandler;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;

public class PlayerCar extends Car {

    private final double minSpeed;
    private double currentSpeed;

    public PlayerCar(double x, double y, CarDefinition definition) {
        super(x, y, GameConfig.CAR_WIDTH, GameConfig.CAR_HEIGHT, definition);
        this.minSpeed = Math.max(130, definition.getStats().getMaxSpeed() * 0.42);
        this.currentSpeed = minSpeed;
    }

    public PlayerCar(double playerStartX, double playerStartY, double carW, double carH, Image carSprite, InputHandler input) {
        super();
    }

    public void update() {
        CarStats stats = definition.getStats();

        boolean accelerate = input.isPressed(KeyCode.UP) || input.isPressed(KeyCode.W);
        boolean brake = input.isPressed(KeyCode.DOWN) || input.isPressed(KeyCode.S);
        boolean left = input.isPressed(KeyCode.LEFT) || input.isPressed(KeyCode.A);
        boolean right = input.isPressed(KeyCode.RIGHT) || input.isPressed(KeyCode.D);

        if (accelerate) {
            currentSpeed = Math.min(stats.getMaxSpeed(), currentSpeed + stats.getAcceleration() * deltaTime);
        } else {
            currentSpeed = Math.max(minSpeed, currentSpeed - stats.getAcceleration() * 0.45 * deltaTime);
        }

        if (brake) {
            currentSpeed = Math.max(90, currentSpeed - stats.getAcceleration() * 1.10 * deltaTime);
        }

        double laneShift = stats.getHandling() * deltaTime;
        if (left) {
            x -= laneShift;
        }
        if (right) {
            x += laneShift;
        }

        clampToScreen();
    }

    private void clampToScreen() {
        double minX = GameConfig.ROAD_X + 14;
        double maxX = GameConfig.ROAD_X + GameConfig.ROAD_WIDTH - width - 14;
        if (x < minX) {
            x = minX;
        }
        if (x > maxX) {
            x = maxX;
        }
    }

    public void penalizeCollision() {
        currentSpeed = Math.max(85, currentSpeed * 0.55);
    }

    public void resetPosition() {
        this.x = GameConfig.PLAYER_START_X;
        this.currentSpeed = minSpeed;
    }

    public double getCurrentSpeed() {
        return currentSpeed;
    }
}