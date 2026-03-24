package com.carracinggame.car;

public class CarStats {
    private final double maxSpeed;
    private final double acceleration;
    private final double handling;

    public CarStats(double maxSpeed, double acceleration, double handling) {
        this.maxSpeed = maxSpeed;
        this.acceleration = acceleration;
        this.handling = handling;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public double getAcceleration() {
        return acceleration;
    }

    public double getHandling() {
        return handling;
    }

    public String toPrettyText() {
        return String.format("Speed %.0f | Accel %.0f | Handling %.0f", maxSpeed, acceleration, handling);
    }
}