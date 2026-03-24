package com.carracinggame.car;

import com.carracinggame.core.GameConfig;
import com.carracinggame.input.InputHandler;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;

public class PlayerCar extends Car {

    private final CarDefinition definition;
    private final InputHandler input;
    private final double minSpeed;
    private double currentSpeed;

    public PlayerCar(double x, double y, CarDefinition definition) {
        this(
                x,
                y,
                GameConfig.CAR_WIDTH,
                GameConfig.CAR_HEIGHT,
                null,
                definition,
                null
        );
    }

    public PlayerCar(double x, double y, double carW, double carH, Image carSprite, InputHandler input) {
        this(x, y, carW, carH, carSprite, null, input);
    }

    public PlayerCar(double x, double y, double carW, double carH, Image carSprite, CarDefinition definition, InputHandler input) {
        super(x, y, carW, carH, carSprite);
        this.definition = definition;
        this.input = input;

        double maxSpeed = definition != null ? definition.getStats().getMaxSpeed() : 420;
        this.minSpeed = Math.max(120, maxSpeed * 0.35);
        this.currentSpeed = minSpeed;
        this.speed = currentSpeed;
    }

    public void update() {
        update(1.0 / 60.0);
    }

    public void update(double deltaTime) {
        if (input == null) {
            speed = currentSpeed;
            return;
        }

        double maxSpeed = definition != null ? definition.getStats().getMaxSpeed() : 420;
        double acceleration = definition != null ? definition.getStats().getAcceleration() : 240;
        double handling = definition != null ? definition.getStats().getHandling() : 300;

        boolean accelerate = input.isPressed(KeyCode.UP) || input.isPressed(KeyCode.W);
        boolean brake = input.isPressed(KeyCode.DOWN) || input.isPressed(KeyCode.S);
        boolean left = input.isPressed(KeyCode.LEFT) || input.isPressed(KeyCode.A);
        boolean right = input.isPressed(KeyCode.RIGHT) || input.isPressed(KeyCode.D);

        if (accelerate) {
            currentSpeed = Math.min(maxSpeed, currentSpeed + acceleration * deltaTime);
        } else {
            currentSpeed = Math.max(minSpeed, currentSpeed - acceleration * 0.45 * deltaTime);
        }

        if (brake) {
            currentSpeed = Math.max(90, currentSpeed - acceleration * 1.1 * deltaTime);
        }

        double laneShift = handling * deltaTime;
        if (left) {
            x -= laneShift;
        }
        if (right) {
            x += laneShift;
        }

        clampToScreen();
        speed = currentSpeed;
    }

    private void clampToScreen() {
        double minX = GameConfig.ROAD_X + 14;
        double maxX = GameConfig.ROAD_X + GameConfig.ROAD_WIDTH - w - 14;
        if (x < minX) {
            x = minX;
        }
        if (x > maxX) {
            x = maxX;
        }
    }

    public void penalizeCollision() {
        currentSpeed = Math.max(85, currentSpeed * 0.55);
        speed = currentSpeed;
    }

    public void resetPosition() {
        this.x = GameConfig.PLAYER_START_X;
        this.y = GameConfig.PLAYER_START_Y;
        this.currentSpeed = minSpeed;
        this.speed = currentSpeed;
    }

    public double getCurrentSpeed() {
        return currentSpeed;
    }
}
