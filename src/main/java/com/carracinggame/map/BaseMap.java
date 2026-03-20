package com.carracinggame.map;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseMap implements GameMap {

    protected final int width;
    protected final int height;

    protected final double trackX;
    protected final double trackW;
    protected final int lanes;

    // chạy theo quãng đường để đổi “chặng/địa danh”
    protected double distance = 0;
    protected double segmentLen = 1800; // chỉnh: càng lớn đổi cảnh càng chậm

    // vạch lane marker
    private final List<Double> markerY = new ArrayList<>();
    private final double markerLen = 42;
    private final double markerGap = 70;
    private final double markerW = 4;

    protected BaseMap(int width, int height, double trackX, double trackW, int lanes) {
        this.width = width;
        this.height = height;
        this.trackX = trackX;
        this.trackW = trackW;
        this.lanes = lanes;

        double step = markerLen + markerGap;
        for (double y = -step * 3; y < height + step * 3; y += step) {
            markerY.add(y);
        }
    }

    @Override
    public void update(double scrollSpeed) {
        distance += scrollSpeed;

        // update vạch đứt
        double step = markerLen + markerGap;
        for (int i = 0; i < markerY.size(); i++) {
            double y = markerY.get(i) + scrollSpeed;
            if (y > height + step * 2) y = -step * 2;
            markerY.set(i, y);
        }

        onUpdate(scrollSpeed);
    }

    protected void onUpdate(double scrollSpeed) {
        // map con override nếu cần animate thêm
    }

    protected int segmentIndex() {
        return (int) (distance / segmentLen) % 3; // 0,1,2
    }

    @Override
    public void render(GraphicsContext gc) {
        // 1) nền theo segment
        renderBackground(gc, segmentIndex());

        // 2) track
        gc.setFill(Color.web("#6b6b6b"));
        gc.fillRect(trackX, 0, trackW, height);

        // viền track
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(3);
        gc.strokeLine(trackX, 0, trackX, height);
        gc.strokeLine(trackX + trackW, 0, trackX + trackW, height);

        // 3) lane markers
        if (lanes > 1) {
            double laneW = trackW / lanes;
            gc.setFill(Color.WHITE);

            for (int lane = 1; lane < lanes; lane++) {
                double lineX = trackX + lane * laneW;
                for (double y : markerY) {
                    gc.fillRect(lineX - markerW / 2.0, y, markerW, markerLen);
                }
            }
        }

        // 4) decor đặc trưng (địa danh)
        renderDecor(gc, segmentIndex());
    }

    protected abstract void renderBackground(GraphicsContext gc, int seg);
    protected abstract void renderDecor(GraphicsContext gc, int seg);

    @Override public double getTrackX() { return trackX; }
    @Override public double getTrackW() { return trackW; }
    @Override public int getLanes() { return lanes; }
}
