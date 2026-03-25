package com.carracinggame.shop;

import com.carracinggame.car.CarDefinition;
import com.carracinggame.car.CarId;
import com.carracinggame.car.CarStats;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class ShopCatalog {

    private static final List<CarDefinition> CARS = List.of(
            new CarDefinition(
                    CarId.RED_RACER,
                    "Red Racer",
                    0,
                    "/images/cars/red_car.png",
                    Color.web("#e63946"),
                    new CarStats(410, 250, 320)
            ),
            new CarDefinition(
                    CarId.BLUE_STORM,
                    "Blue Storm",
                    1200,
                    "/images/cars/blue_car.png",
                    Color.web("#1d4ed8"),
                    new CarStats(455, 270, 300)
            ),
            new CarDefinition(
                    CarId.GREEN_SHADOW,
                    "Black Shadow",
                    1750,
                    "/images/cars/black_car.png",
                    Color.web("#111827"),
                    new CarStats(485, 285, 305)
            ),
            new CarDefinition(
                    CarId.YELLOW_FLASH,
                    "Yellow Flash",
                    2300,
                    "/images/cars/yellow_car.png",
                    Color.web("#fbbf24"),
                    new CarStats(430, 260, 355)
            )
    );

    private static final Map<CarId, CarDefinition> CAR_MAP = CARS.stream()
            .collect(Collectors.toUnmodifiableMap(CarDefinition::getId, car -> car));

    private ShopCatalog() {
    }

    public static List<CarDefinition> getCars() {
        return CARS;
    }

    public static CarDefinition getCar(CarId id) {
        return CAR_MAP.get(id);
    }
}