package com.carracinggame.core;

import javafx.animation.AnimationTimer;

public class GameLoop extends AnimationTimer {

    public interface Tick {

        /**
         * Kiểu mới: update có deltaTime (giây)
         */
        default void update(double deltaTime) {
            update();
        }

        /**
         * Giữ tương thích với kiểu cũ
         */
        default void update() {
        }

        /**
         * Render mỗi frame
         */
        void render();

        /**
         * Hook khi loop bắt đầu
         */
        default void onStart() {
        }

        /**
         * Hook khi loop dừng
         */
        default void onStop() {
        }
    }

    private final Tick tick;

    private boolean running;
    private boolean paused;

    private long lastTime;
    private long fpsTimer;
    private int frameCount;

    private double deltaTime;
    private double fps;

    public GameLoop(Tick tick) {
        this.tick = tick;
        this.running = false;
        this.paused = false;
        this.lastTime = 0L;
        this.fpsTimer = 0L;
        this.frameCount = 0;
        this.deltaTime = 0.0;
        this.fps = 0.0;
    }

    @Override
    public void start() {
        if (running) {
            return;
        }

        running = true;
        paused = false;
        lastTime = 0L;
        fpsTimer = 0L;
        frameCount = 0;
        deltaTime = 0.0;
        fps = 0.0;

        tick.onStart();
        super.start();
    }

    @Override
    public void stop() {
        if (!running) {
            return;
        }

        running = false;
        paused = false;

        super.stop();
        tick.onStop();
    }

    @Override
    public void handle(long now) {
        if (!running || paused) {
            return;
        }

        // Frame đầu tiên
        if (lastTime == 0L) {
            lastTime = now;
            fpsTimer = now;
            tick.render();
            return;
        }

        deltaTime = (now - lastTime) / 1_000_000_000.0;
        lastTime = now;

        // Chặn deltaTime quá lớn nếu app bị khựng / tab out
        if (deltaTime > 0.1) {
            deltaTime = 0.1;
        }

        tick.update(deltaTime);
        tick.render();

        updateFps(now);
    }

    private void updateFps(long now) {
        frameCount++;

        if (fpsTimer == 0L) {
            fpsTimer = now;
            return;
        }

        long elapsed = now - fpsTimer;
        if (elapsed >= 1_000_000_000L) {
            fps = frameCount / (elapsed / 1_000_000_000.0);
            frameCount = 0;
            fpsTimer = now;
        }
    }

    public void pause() {
        this.paused = true;
    }

    public void resume() {
        if (!running) {
            return;
        }

        this.paused = false;
        this.lastTime = 0L;
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isPaused() {
        return paused;
    }

    public double getDeltaTime() {
        return deltaTime;
    }

    public double getFps() {
        return fps;
    }
}