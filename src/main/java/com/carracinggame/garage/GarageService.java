package com.carracinggame.garage;

import com.carracinggame.car.CarDefinition;
import com.carracinggame.car.CarId;
import com.carracinggame.player.PlayerProfile;
import com.carracinggame.shop.ShopCatalog;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GarageService {

    private final PlayerProfile playerProfile;
    private final Map<CarId, OwnedCar> ownedCars = new LinkedHashMap<>();

    public GarageService(PlayerProfile playerProfile) {
        this.playerProfile = playerProfile;

        CarId initialEquipped = playerProfile.getEquippedCarId() == null
                ? CarId.RED_RACER
                : playerProfile.getEquippedCarId();

        ownedCars.put(CarId.RED_RACER, new OwnedCar(CarId.RED_RACER, false));
        ownedCars.putIfAbsent(initialEquipped, new OwnedCar(initialEquipped, false));

        equipCar(initialEquipped);
    }

    public void loadOwnedCars(List<CarId> savedOwnedCars, CarId equippedCarId) {
        ownedCars.clear();

        ownedCars.put(CarId.RED_RACER, new OwnedCar(CarId.RED_RACER, false));

        if (savedOwnedCars != null) {
            for (CarId carId : savedOwnedCars) {
                if (carId != null) {
                    ownedCars.putIfAbsent(carId, new OwnedCar(carId, false));
                }
            }
        }

        CarId safeEquipped = equippedCarId;
        if (safeEquipped == null || !ownedCars.containsKey(safeEquipped)) {
            safeEquipped = CarId.RED_RACER;
        }

        equipCar(safeEquipped);
    }

    public boolean ownsCar(CarId carId) {
        return carId != null && ownedCars.containsKey(carId);
    }

    public void unlockCar(CarId carId) {
        if (carId == null) {
            return;
        }
        ownedCars.putIfAbsent(carId, new OwnedCar(carId, false));
    }

    public boolean equipCar(CarId carId) {
        if (!ownsCar(carId)) {
            return false;
        }

        for (OwnedCar ownedCar : ownedCars.values()) {
            ownedCar.setEquipped(false);
        }

        OwnedCar selected = ownedCars.get(carId);
        if (selected != null) {
            selected.setEquipped(true);
        }

        playerProfile.setEquippedCarId(carId);
        return true;
    }

    public CarDefinition getEquippedCar() {
        CarId equippedId = playerProfile.getEquippedCarId();
        if (equippedId == null) {
            equippedId = CarId.RED_RACER;
        }

        CarDefinition car = ShopCatalog.getCar(equippedId);
        return car != null ? car : ShopCatalog.getCar(CarId.RED_RACER);
    }

    public List<CarDefinition> getOwnedCarDefinitions() {
        List<CarDefinition> result = new ArrayList<>();
        for (OwnedCar ownedCar : ownedCars.values()) {
            CarDefinition car = ShopCatalog.getCar(ownedCar.getCarId());
            if (car != null) {
                result.add(car);
            }
        }
        return result;
    }

    public List<OwnedCar> getOwnedCars() {
        return new ArrayList<>(ownedCars.values());
    }

    public List<CarId> getOwnedCarIds() {
        List<CarId> result = new ArrayList<>();
        for (OwnedCar ownedCar : ownedCars.values()) {
            result.add(ownedCar.getCarId());
        }
        return result;
    }
}