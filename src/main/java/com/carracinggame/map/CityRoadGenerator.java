package com.carracinggame.map;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class CityRoadGenerator {

    public static final double BLOCK_LENGTH = CityRoadConfig.BLOCK_LENGTH;

    private final long baseSeed;
    private final TrafficSignalController signalController;
    private final PedestrianFlowController pedestrianFlowController;

    public CityRoadGenerator(MapId mapId) {
        this.baseSeed = CityRoadConfig.getBaseSeed(mapId);
        this.signalController = new TrafficSignalController();
        this.pedestrianFlowController = new PedestrianFlowController();
    }

    public CityRoadBlock getBlock(int index) {
        if (index < 0) {
            throw new IllegalArgumentException("Block index must be >= 0");
        }

        Random random = createRandomForIndex(index);
        CityRoadBlockType type = pickType(random, index);

        boolean leftAlley = hasLeftAlley(type, random);
        boolean rightAlley = hasRightAlley(type, random);
        boolean crosswalk = hasCrosswalk(type, random);
        boolean trafficLight = hasTrafficLight(type, random);

        return new CityRoadBlock(
                index,
                getBlockStartDistance(index),
                CityRoadConfig.BLOCK_LENGTH,
                type,
                leftAlley,
                rightAlley,
                crosswalk,
                trafficLight,
                createDecorSeed(random, CityRoadConfig.LEFT_DECOR_SEED_BASE),
                createDecorSeed(random, CityRoadConfig.RIGHT_DECOR_SEED_BASE)
        );
    }

    public List<CityRoadBlock> getVisibleBlocks(double playerDistance,
                                                double beforeDistance,
                                                double afterDistance) {
        int startIndex = (int) Math.floor((playerDistance - beforeDistance) / CityRoadConfig.BLOCK_LENGTH) - 1;
        int endIndex = (int) Math.ceil((playerDistance + afterDistance) / CityRoadConfig.BLOCK_LENGTH) + 1;

        startIndex = Math.max(0, startIndex);

        List<CityRoadBlock> blocks = new ArrayList<>();
        for (int i = startIndex; i <= endIndex; i++) {
            blocks.add(getBlock(i));
        }
        return blocks;
    }

    /**
     * Giữ lại để tương thích code cũ.
     * Dùng cho đường chính hoặc chỗ render đèn đơn giản.
     */
    public TrafficLightState getTrafficLightState(double elapsedSeconds, int blockIndex) {
        return signalController.getStraightSignalState(elapsedSeconds, blockIndex);
    }

    /**
     * Mới: dùng cho ngã 3 / ngã 4.
     */
    public JunctionSignalState getJunctionSignalState(double elapsedSeconds, int blockIndex) {
        return signalController.getJunctionSignalState(elapsedSeconds, blockIndex);
    }

    /**
     * Giữ lại để tương thích code cũ.
     */
    public double getPedestrianPhase(double elapsedSeconds, int blockIndex) {
        return pedestrianFlowController.getPedestrianPhase(elapsedSeconds, blockIndex);
    }

    public boolean isPedestrianCrossingActive(double elapsedSeconds, int blockIndex) {
        return pedestrianFlowController.isPedestrianCrossingActive(elapsedSeconds, blockIndex);
    }

    public double getPedestrianOffsetRatio(double elapsedSeconds, int blockIndex) {
        return pedestrianFlowController.getPedestrianOffsetRatio(elapsedSeconds, blockIndex);
    }

    private Random createRandomForIndex(int index) {
        long seed = baseSeed + (long) index * CityRoadConfig.INDEX_SEED_STEP;
        return new Random(seed);
    }

    private double getBlockStartDistance(int index) {
        return index * CityRoadConfig.BLOCK_LENGTH;
    }

    private int createDecorSeed(Random random, int baseValue) {
        return baseValue + random.nextInt(CityRoadConfig.DECOR_SEED_RANGE);
    }

    private boolean hasLeftAlley(CityRoadBlockType type, Random random) {
        return switch (type) {
            case ALLEY_LEFT, T_JUNCTION_LEFT, CROSSROAD -> true;
            case STRAIGHT -> random.nextDouble() < CityRoadConfig.STRAIGHT_SIDE_ALLEY_CHANCE;
            default -> false;
        };
    }

    private boolean hasRightAlley(CityRoadBlockType type, Random random) {
        return switch (type) {
            case ALLEY_RIGHT, T_JUNCTION_RIGHT, CROSSROAD -> true;
            case STRAIGHT -> random.nextDouble() < CityRoadConfig.STRAIGHT_SIDE_ALLEY_CHANCE;
            default -> false;
        };
    }

    private boolean hasCrosswalk(CityRoadBlockType type, Random random) {
        return switch (type) {
            case CROSSROAD, T_JUNCTION_LEFT, T_JUNCTION_RIGHT -> true;
            case ALLEY_LEFT, ALLEY_RIGHT -> random.nextDouble() < CityRoadConfig.ALLEY_CROSSWALK_CHANCE;
            case STRAIGHT -> random.nextDouble() < CityRoadConfig.STRAIGHT_CROSSWALK_CHANCE;
        };
    }

    private boolean hasTrafficLight(CityRoadBlockType type, Random random) {
        return switch (type) {
            case CROSSROAD -> true;
            case T_JUNCTION_LEFT, T_JUNCTION_RIGHT -> random.nextDouble() < CityRoadConfig.T_JUNCTION_LIGHT_CHANCE;
            case ALLEY_LEFT, ALLEY_RIGHT -> random.nextDouble() < CityRoadConfig.ALLEY_LIGHT_CHANCE;
            case STRAIGHT -> random.nextDouble() < CityRoadConfig.STRAIGHT_LIGHT_CHANCE;
        };
    }

    private CityRoadBlockType pickType(Random random, int index) {
        if (index <= 1) {
            return CityRoadBlockType.STRAIGHT;
        }

        int phase = Math.floorMod(index, 6);
        int roll = random.nextInt(100);

        return switch (phase) {
            case 0 -> pickPhase0(random, roll);
            case 1 -> pickPhase1(roll);
            case 2 -> pickPhase2(roll);
            case 3 -> pickPhase3(roll);
            case 4 -> pickPhase4(random, roll);
            default -> pickPhase5(roll);
        };
    }

    private CityRoadBlockType pickPhase0(Random random, int roll) {
        if (roll < 58) return CityRoadBlockType.STRAIGHT;
        if (roll < 74) return CityRoadBlockType.ALLEY_LEFT;
        if (roll < 90) return CityRoadBlockType.ALLEY_RIGHT;
        return random.nextBoolean()
                ? CityRoadBlockType.T_JUNCTION_LEFT
                : CityRoadBlockType.T_JUNCTION_RIGHT;
    }

    private CityRoadBlockType pickPhase1(int roll) {
        if (roll < 72) return CityRoadBlockType.STRAIGHT;
        if (roll < 86) return CityRoadBlockType.ALLEY_LEFT;
        return CityRoadBlockType.ALLEY_RIGHT;
    }

    private CityRoadBlockType pickPhase2(int roll) {
        if (roll < 26) return CityRoadBlockType.STRAIGHT;
        if (roll < 50) return CityRoadBlockType.T_JUNCTION_LEFT;
        if (roll < 74) return CityRoadBlockType.T_JUNCTION_RIGHT;
        return CityRoadBlockType.CROSSROAD;
    }

    private CityRoadBlockType pickPhase3(int roll) {
        if (roll < 68) return CityRoadBlockType.STRAIGHT;
        if (roll < 84) return CityRoadBlockType.ALLEY_LEFT;
        return CityRoadBlockType.ALLEY_RIGHT;
    }

    private CityRoadBlockType pickPhase4(Random random, int roll) {
        if (roll < 56) return CityRoadBlockType.STRAIGHT;
        if (roll < 74) return CityRoadBlockType.ALLEY_LEFT;
        if (roll < 92) return CityRoadBlockType.ALLEY_RIGHT;
        return random.nextBoolean()
                ? CityRoadBlockType.T_JUNCTION_LEFT
                : CityRoadBlockType.T_JUNCTION_RIGHT;
    }

    private CityRoadBlockType pickPhase5(int roll) {
        if (roll < 22) return CityRoadBlockType.STRAIGHT;
        if (roll < 46) return CityRoadBlockType.T_JUNCTION_LEFT;
        if (roll < 70) return CityRoadBlockType.T_JUNCTION_RIGHT;
        return CityRoadBlockType.CROSSROAD;
    }
}