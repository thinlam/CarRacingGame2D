package com.carracinggame.shop;

import com.carracinggame.car.CarDefinition;
import com.carracinggame.car.CarId;
import com.carracinggame.garage.GarageService;
import com.carracinggame.player.PlayerProfile;

import java.util.List;

public class ShopService {

    public BuyResult buyCar(CarId carId, PlayerProfile playerProfile, GarageService garageService) {
        CarDefinition car = ShopCatalog.getCar(carId);

        if (car == null) {
            return new BuyResult(false, "Xe không tồn tại.");
        }

        if (garageService.ownsCar(carId)) {
            return new BuyResult(false, "Bạn đã sở hữu xe này rồi.");
        }

        if (!playerProfile.spendCoins(car.getPrice())) {
            return new BuyResult(false, "Không đủ coin để mua xe.");
        }

        garageService.unlockCar(carId);
        return new BuyResult(true, "Mua " + car.getName() + " thành công.");
    }

    public List<CarDefinition> getCars() {
        return ShopCatalog.getCars();
    }

    public record BuyResult(boolean success, String message) {
    }
}