package com.carracinggame.shop;

import com.carracinggame.car.CarDefinition;

public class ShopItem {
    private final ShopType type;
    private final CarDefinition carDefinition;

    public ShopItem(ShopType type, CarDefinition carDefinition) {
        this.type = type;
        this.carDefinition = carDefinition;
    }

    public ShopType getType() {
        return type;
    }

    public CarDefinition getCarDefinition() {
        return carDefinition;
    }
}