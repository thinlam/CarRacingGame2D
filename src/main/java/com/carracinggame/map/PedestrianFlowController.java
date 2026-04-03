package com.carracinggame.map;

public final class PedestrianFlowController {

    public double getPedestrianPhase(double elapsedSeconds, int blockIndex) {
        double phase = (elapsedSeconds * CityRoadConfig.PEDESTRIAN_TIME_SPEED)
                + ((blockIndex % 5) * CityRoadConfig.PEDESTRIAN_BLOCK_OFFSET);

        phase = phase - Math.floor(phase);
        return phase;
    }

    public boolean isPedestrianCrossingActive(double elapsedSeconds, int blockIndex) {
        double phase = getPedestrianPhase(elapsedSeconds, blockIndex);

        // active khi phase nằm trong đoạn giữa
        return phase >= 0.20 && phase <= 0.80;
    }

    public double getPedestrianOffsetRatio(double elapsedSeconds, int blockIndex) {
        double phase = getPedestrianPhase(elapsedSeconds, blockIndex);

        // easing đơn giản cho animation
        return phase < 0.5
                ? phase * 2.0
                : (1.0 - phase) * 2.0;
    }
}