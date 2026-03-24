package com.carracinggame.physics;

import com.carracinggame.car.Car;
import javafx.geometry.Rectangle2D;

public class CollisionDetector {

    public boolean clampToTrack(Car car, double trackX, double trackW, double height) {
        boolean clamped = false;

        double minX = trackX;
        double maxX = trackX + trackW - car.getW();
        double minY = 0;
        double maxY = height - car.getH();

        if (car.getX() < minX) {
            car.setX(minX);
            clamped = true;
        }
        if (car.getX() > maxX) {
            car.setX(maxX);
            clamped = true;
        }
        if (car.getY() < minY) {
            car.setY(minY);
            clamped = true;
        }
        if (car.getY() > maxY) {
            car.setY(maxY);
            clamped = true;
        }

        return clamped;
    }

    public boolean isOutOfTrack(Car car, double trackX, double trackW) {
        return car.getX() < trackX || (car.getX() + car.getW()) > (trackX + trackW);
    }

    public boolean isColliding(Car a, Car b) {
        if (a == null || b == null) {
            return false;
        }
        return intersects(a.getBounds(), b.getBounds());
    }

    public boolean isColliding(Car car, Rectangle2D rect) {
        if (car == null || rect == null) {
            return false;
        }
        return intersects(car.getBounds(), rect);
    }

    public boolean intersects(Rectangle2D a, Rectangle2D b) {
        if (a == null || b == null) {
            return false;
        }
        return a.intersects(b);
    }

    public double clampXToTrack(double x, double objectWidth, double trackX, double trackW) {
        double minX = trackX;
        double maxX = trackX + trackW - objectWidth;

        if (x < minX) {
            return minX;
        }
        if (x > maxX) {
            return maxX;
        }
        return x;
    }

    public double clampYToScreen(double y, double objectHeight, double screenHeight) {
        double minY = 0;
        double maxY = screenHeight - objectHeight;

        if (y < minY) {
            return minY;
        }
        if (y > maxY) {
            return maxY;
        }
        return y;
    }
}