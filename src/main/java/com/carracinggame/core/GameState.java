package com.carracinggame.core;

public enum GameState {
    LOGIN,
    MENU,
    MAP_SELECT,
    GARAGE,
    UPGRADE,
    SHOP,
    RACE,
    RESULT;

    public boolean isMenuState() {
        return this == MENU;
    }

    public boolean isRaceState() {
        return this == RACE;
    }

    public boolean isShopState() {
        return this == SHOP;
    }

    public boolean isGarageState() {
        return this == GARAGE;
    }

    public boolean isMapSelectState() {
        return this == MAP_SELECT;
    }

    public boolean isPlayableState() {
        return this == RACE;
    }

    public boolean isNavigationState() {
        return this == MENU
                || this == MAP_SELECT
                || this == GARAGE
                || this == SHOP;
    }
}