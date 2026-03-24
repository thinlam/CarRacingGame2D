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
        ownedCars.put(CarId.RED_RACER, new OwnedCar(CarId.RED_RACER, true));
    }

    public boolean ownsCar(CarId carId) {
        return ownedCars.containsKey(carId);
    }

    public void unlockCar(CarId carId) {
        ownedCars.putIfAbsent(carId, new OwnedCar(carId, false));
    }

    public boolean equipCar(CarId carId) {
        if (!ownsCar(carId)) {
            return false;
        }

        ownedCars.values().forEach(ownedCar -> ownedCar.setEquipped(false));
        OwnedCar selected = ownedCars.get(carId);
        selected.setEquipped(true);
        playerProfile.setEquippedCarId(carId);
        return true;
    }

    public CarDefinition getEquippedCar() {
        return ShopCatalog.getCar(playerProfile.getEquippedCarId());
    }

    public List<CarDefinition> getOwnedCarDefinitions() {
        List<CarDefinition> result = new ArrayList<>();
        for (OwnedCar ownedCar : ownedCars.values()) {
            result.add(ShopCatalog.getCar(ownedCar.getCarId()));
        }
        return result;
    }

    public List<OwnedCar> getOwnedCars() {
        return new ArrayList<>(ownedCars.values());
    }
}