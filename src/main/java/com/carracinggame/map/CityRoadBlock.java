package com.carracinggame.map;

public final class CityRoadBlock {

    private final int index;
    private final double startDistance;
    private final double length;
    private final CityRoadBlockType type;

    private final boolean leftAlley;
    private final boolean rightAlley;
    private final boolean crosswalk;
    private final boolean trafficLight;

    private final int leftDecorSeed;
    private final int rightDecorSeed;

    public CityRoadBlock(int index,
                         double startDistance,
                         double length,
                         CityRoadBlockType type,
                         boolean leftAlley,
                         boolean rightAlley,
                         boolean crosswalk,
                         boolean trafficLight,
                         int leftDecorSeed,
                         int rightDecorSeed) {
        this.index = index;
        this.startDistance = startDistance;
        this.length = length;
        this.type = type;
        this.leftAlley = leftAlley;
        this.rightAlley = rightAlley;
        this.crosswalk = crosswalk;
        this.trafficLight = trafficLight;
        this.leftDecorSeed = leftDecorSeed;
        this.rightDecorSeed = rightDecorSeed;
    }

    public int getIndex() {
        return index;
    }

    public double getStartDistance() {
        return startDistance;
    }

    public double getLength() {
        return length;
    }

    public double getEndDistance() {
        return startDistance + length;
    }

    public CityRoadBlockType getType() {
        return type;
    }

    public boolean hasLeftAlley() {
        return leftAlley;
    }

    public boolean hasRightAlley() {
        return rightAlley;
    }

    public boolean hasCrosswalk() {
        return crosswalk;
    }

    public boolean hasTrafficLight() {
        return trafficLight;
    }

    public int getLeftDecorSeed() {
        return leftDecorSeed;
    }

    public int getRightDecorSeed() {
        return rightDecorSeed;
    }

    // Alias để tương thích với RaceScene
    public int getLeftHouseSeed() {
        return leftDecorSeed;
    }

    public int getRightHouseSeed() {
        return rightDecorSeed;
    }

    public boolean isCrossroad() {
        return type == CityRoadBlockType.CROSSROAD;
    }

    public boolean isTJunction() {
        return type == CityRoadBlockType.T_JUNCTION_LEFT
                || type == CityRoadBlockType.T_JUNCTION_RIGHT;
    }

    public boolean isJunction() {
        return isCrossroad() || isTJunction();
    }

    public boolean isStraight() {
        return type == CityRoadBlockType.STRAIGHT;
    }
}