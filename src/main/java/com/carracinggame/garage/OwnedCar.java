package com.carracinggame.garage;

import com.carracinggame.car.CarId;

public class OwnedCar {
    private final CarId carId;
    private boolean equipped;

    public OwnedCar(CarId carId, boolean equipped) {
        this.carId = carId;
        this.equipped = equipped;
    }

    public CarId getCarId() {
        return carId;
    }

    public boolean isEquipped() {
        return equipped;
    }

    public void setEquipped(boolean equipped) {
        this.equipped = equipped;
    }
}