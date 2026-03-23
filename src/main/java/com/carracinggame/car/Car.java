package com.carracinggame.car;

import javafx.scene.image.Image;

public class Car {
    protected double x;
    protected double y;
    protected double w;
    protected double h;

    protected double angleDeg;
    protected double speed;

    protected Image sprite;

    public Car(double x, double y, double w, double h, Image sprite) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.sprite = sprite;
        this.angleDeg = 0;
        this.speed = 0;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getW() {
        return w;
    }

    public double getH() {
        return h;
    }

    public double getAngleDeg() {
        return angleDeg;
    }

    public double getSpeed() {
        return speed;
    }

    public Image getSprite() {
        return sprite;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setAngleDeg(double angleDeg) {
        this.angleDeg = angleDeg;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void setSprite(Image sprite) {
        this.sprite = sprite;
    }
}
