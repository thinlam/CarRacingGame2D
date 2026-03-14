package com.carracinggame.car;

public enum CarId {
    RED_RACER("Red Racer"),
    BLUE_STORM("Blue Storm"),
    BLACK_SHADOW("Black Shadow"),
    YELLOW_FLASH("Yellow Flash");

    private final String displayName;

    CarId(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}