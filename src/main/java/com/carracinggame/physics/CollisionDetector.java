package com.carracinggame.physics;

import com.carracinggame.car.Car;

public class CollisionDetector {

    public void clampToTrack(Car car, double trackX, double trackW, double height) {
        double minX = trackX + 8;
        double maxX = trackX + trackW - car.getW() - 8;
        double minY = 0;
        double maxY = height - car.getH();

        if (car.getX() < minX) car.setX(minX);
        if (car.getX() > maxX) car.setX(maxX);
        if (car.getY() < minY) car.setY(minY);
        if (car.getY() > maxY) car.setY(maxY);
    }

    public boolean intersects(Car a, Car b) {
        return a.getX() < b.getX() + b.getW()
                && a.getX() + a.getW() > b.getX()
                && a.getY() < b.getY() + b.getH()
                && a.getY() + a.getH() > b.getY();
    }
}
