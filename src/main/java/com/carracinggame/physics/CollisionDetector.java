package com.carracinggame.physics;

import com.carracinggame.car.Car;

public class CollisionDetector {

    public void clampToTrack(Car car, double trackX, double trackW, double height) {
        double minX = trackX;
        double maxX = trackX + trackW - car.getW();
        double minY = 0;
        double maxY = height - car.getH();

        if (car.getX() < minX) car.setX(minX);
        if (car.getX() > maxX) car.setX(maxX);
        if (car.getY() < minY) car.setY(minY);
        if (car.getY() > maxY) car.setY(maxY);
    }
}
