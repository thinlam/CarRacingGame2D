package com.carracinggame.car;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;

public class Car {

    protected double x;
    protected double y;
    protected double w;
    protected double h;

    // 0 độ = hướng lên
    protected double angleDeg;

    // tốc độ hiện tại
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

    // =========================
    // Getter
    // =========================
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

    public double getCenterX() {
        return x + w / 2.0;
    }

    public double getCenterY() {
        return y + h / 2.0;
    }

    public Rectangle2D getBounds() {
        return new Rectangle2D(x, y, w, h);
    }

    // =========================
    // Setter
    // =========================
    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setW(double w) {
        this.w = w;
    }

    public void setH(double h) {
        this.h = h;
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

    // =========================
    // Logic chung
    // =========================
    public void move(double dx, double dy) {
        this.x += dx;
        this.y += dy;
    }

    /**
     * Di chuyển theo hướng angleDeg hiện tại.
     * 0 độ = đi lên, 90 độ = sang phải, 180 độ = xuống, 270 độ = sang trái
     */
    public void moveForward() {
        double rad = Math.toRadians(angleDeg);

        double dx = speed * Math.sin(rad);
        double dy = -speed * Math.cos(rad);

        this.x += dx;
        this.y += dy;
    }

    public void accelerate(double amount) {
        this.speed += amount;
    }

    public void brake(double amount) {
        this.speed -= amount;
        if (this.speed < 0) {
            this.speed = 0;
        }
    }

    public void stop() {
        this.speed = 0;
    }

    public void rotate(double deltaAngle) {
        this.angleDeg += deltaAngle;
    }

    public boolean intersects(Car other) {
        return this.getBounds().intersects(other.getBounds());
    }

    public boolean intersects(Rectangle2D rect) {
        return this.getBounds().intersects(rect);
    }
}