package com.carracinggame.player;

import com.carracinggame.car.CarId;

public class PlayerProfile {
    private String playerName;
    private int coins;
    private CarId equippedCarId;

    public PlayerProfile(String playerName, int coins, CarId equippedCarId) {
        this.playerName = playerName;
        this.coins = coins;
        this.equippedCarId = equippedCarId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getCoins() {
        return coins;
    }

    public CarId getEquippedCarId() {
        return equippedCarId;
    }

    public void setEquippedCarId(CarId equippedCarId) {
        this.equippedCarId = equippedCarId;
    }

    public boolean spendCoins(int amount) {
        if (amount < 0 || coins < amount) {
            return false;
        }
        coins -= amount;
        return true;
    }

    public void addCoins(int amount) {
        if (amount > 0) {
            coins += amount;
        }
    }
}