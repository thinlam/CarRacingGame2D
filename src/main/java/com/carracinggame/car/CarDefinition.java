package com.carracinggame.car;

import javafx.scene.paint.Color;

public class CarDefinition {
    private final CarId id;
    private final String name;
    private final int price;
    private final String spritePath;
    private final Color accentColor;
    private final CarStats stats;

    public CarDefinition(CarId id, String name, int price, String spritePath, Color accentColor, CarStats stats) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.spritePath = spritePath;
        this.accentColor = accentColor;
        this.stats = stats;
    }

    public CarId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getSpritePath() {
        return spritePath;
    }

    public Color getAccentColor() {
        return accentColor;
    }

    public CarStats getStats() {
        return stats;
    }

    public CarSkill getSkill() {
        return CarSkill.forCar(id);
    }
}