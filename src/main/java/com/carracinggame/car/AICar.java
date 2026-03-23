package com.carracinggame.car;

import javafx.scene.image.Image;

public class AICar extends Car {
    private final double fallSpeed;

    public AICar(double x, double y, double w, double h, Image sprite, double fallSpeed) {
        super(x, y, w, h, sprite);
        this.fallSpeed = fallSpeed;
    }

    public void update(double roadSpeed) {
        y += Math.max(2.5, fallSpeed + roadSpeed * 0.28);
    }

    public boolean isOutOfScreen(double height) {
        return y > height + h + 30;
    }
}
