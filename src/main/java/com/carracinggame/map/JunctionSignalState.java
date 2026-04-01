package com.carracinggame.map;

public final class JunctionSignalState {

    private final TrafficLightState mainRoadState;
    private final TrafficLightState sideRoadState;

    public JunctionSignalState(TrafficLightState mainRoadState, TrafficLightState sideRoadState) {
        this.mainRoadState = mainRoadState;
        this.sideRoadState = sideRoadState;
    }

    public TrafficLightState getMainRoadState() {
        return mainRoadState;
    }

    public TrafficLightState getSideRoadState() {
        return sideRoadState;
    }

    public boolean isMainRoadOpen() {
        return mainRoadState == TrafficLightState.GREEN;
    }

    public boolean isSideRoadOpen() {
        return sideRoadState == TrafficLightState.GREEN;
    }

    @Override
    public String toString() {
        return "JunctionSignalState{" +
                "mainRoadState=" + mainRoadState +
                ", sideRoadState=" + sideRoadState +
                '}';
    }
}