package com.carracinggame.core;

import javafx.animation.AnimationTimer;

public class GameLoop extends AnimationTimer {

    public interface Tick {
        void update();
        void render();
    }

    private final Tick tick;

    public GameLoop(Tick tick) {
        this.tick = tick;
    }

    @Override
    public void handle(long now) {
        tick.update();
        tick.render();
    }
}
