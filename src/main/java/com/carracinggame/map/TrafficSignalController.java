package com.carracinggame.map;

public final class TrafficSignalController {

    public TrafficLightState getStraightSignalState(double elapsedSeconds, int blockIndex) {
        double localTime = getLocalTime(elapsedSeconds, blockIndex);

        if (localTime < CityRoadConfig.MAIN_GREEN_SECONDS) {
            return TrafficLightState.GREEN;
        }

        if (localTime < CityRoadConfig.MAIN_GREEN_SECONDS + CityRoadConfig.MAIN_YELLOW_SECONDS) {
            return TrafficLightState.YELLOW;
        }

        return TrafficLightState.RED;
    }

    public JunctionSignalState getJunctionSignalState(double elapsedSeconds, int blockIndex) {
        double localTime = getLocalTime(elapsedSeconds, blockIndex);

        if (localTime < CityRoadConfig.MAIN_GREEN_SECONDS) {
            return new JunctionSignalState(
                    TrafficLightState.GREEN,
                    TrafficLightState.RED
            );
        }

        if (localTime < CityRoadConfig.MAIN_GREEN_SECONDS + CityRoadConfig.MAIN_YELLOW_SECONDS) {
            return new JunctionSignalState(
                    TrafficLightState.YELLOW,
                    TrafficLightState.RED
            );
        }

        return new JunctionSignalState(
                TrafficLightState.RED,
                TrafficLightState.GREEN
        );
    }

    public boolean canMainRoadCarsGo(double elapsedSeconds, int blockIndex) {
        TrafficLightState state = getStraightSignalState(elapsedSeconds, blockIndex);
        return state == TrafficLightState.GREEN;
    }

    public boolean shouldMainRoadCarsSlowDown(double elapsedSeconds, int blockIndex) {
        TrafficLightState state = getStraightSignalState(elapsedSeconds, blockIndex);
        return state == TrafficLightState.YELLOW;
    }

    public boolean mustMainRoadCarsStop(double elapsedSeconds, int blockIndex) {
        TrafficLightState state = getStraightSignalState(elapsedSeconds, blockIndex);
        return state == TrafficLightState.RED;
    }

    public boolean canSideRoadCarsGo(double elapsedSeconds, int blockIndex) {
        return getJunctionSignalState(elapsedSeconds, blockIndex).isSideRoadOpen();
    }

    private double getLocalTime(double elapsedSeconds, int blockIndex) {
        double shifted = elapsedSeconds + blockIndex * CityRoadConfig.BLOCK_SIGNAL_OFFSET;
        double cycle = CityRoadConfig.SIGNAL_CYCLE_SECONDS;

        double local = shifted % cycle;
        if (local < 0) {
            local += cycle;
        }
        return local;
    }
}