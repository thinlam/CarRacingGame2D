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

    // quãng đường đã chạy, dùng để đổi chặng / địa danh
    protected double distance = 0;

    // độ dài 1 chặng
    protected double segmentLen = 1800;

    // lane markers
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

        initMarkers();
    }

    private void initMarkers() {
        markerY.clear();
        double step = markerLen + markerGap;
        for (double y = -step * 3; y < height + step * 3; y += step) {
            markerY.add(y);
        }
    }

    @Override
    public void update(double scrollSpeed) {
        distance += scrollSpeed;

        double step = markerLen + markerGap;
        for (int i = 0; i < markerY.size(); i++) {
            double y = markerY.get(i) + scrollSpeed;
            if (y > height + step * 2) {
                y = -step * 2;
            }
            markerY.set(i, y);
        }

        onUpdate(scrollSpeed);
    }

    protected void onUpdate(double scrollSpeed) {
        // map con override nếu cần thêm animation riêng
    }

    protected int segmentIndex() {
        if (segmentLen <= 0) {
            return 0;
        }
        return (int) (distance / segmentLen) % 3;
    }

    @Override
    public void render(GraphicsContext gc) {
        int seg = segmentIndex();

        // 1) nền
        renderBackground(gc, seg);

        // 2) mặt đường
        gc.setFill(getTrackColor(seg));
        gc.fillRect(trackX, 0, trackW, height);

        // 3) viền đường
        gc.setStroke(getBorderColor(seg));
        gc.setLineWidth(getBorderWidth());
        gc.strokeLine(trackX, 0, trackX, height);
        gc.strokeLine(trackX + trackW, 0, trackX + trackW, height);

        // 4) lane markers
        renderLaneMarkers(gc);

        // 5) decor riêng
        renderDecor(gc, seg);
    }

    protected void renderLaneMarkers(GraphicsContext gc) {
        if (lanes <= 1) {
            return;
        }

        double laneW = trackW / lanes;
        gc.setFill(getMarkerColor());

        for (int lane = 1; lane < lanes; lane++) {
            double lineX = trackX + lane * laneW;
            for (double y : markerY) {
                gc.fillRect(lineX - markerW / 2.0, y, markerW, markerLen);
            }
        }
    }

    protected Color getTrackColor(int seg) {
        return Color.web("#6b6b6b");
    }

    protected Color getBorderColor(int seg) {
        return Color.WHITE;
    }

    protected double getBorderWidth() {
        return 3;
    }

    protected Color getMarkerColor() {
        return Color.WHITE;
    }

    public void reset() {
        distance = 0;
        initMarkers();
        onReset();
    }

    protected void onReset() {
        // map con override nếu cần reset thêm dữ liệu riêng
    }

    public int getSegmentIndex() {
        return segmentIndex();
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = Math.max(0, distance);
    }

    public double getSegmentLen() {
        return segmentLen;
    }

    public void setSegmentLen(double segmentLen) {
        if (segmentLen > 0) {
            this.segmentLen = segmentLen;
        }
    }

    public double getLaneWidth() {
        return trackW / lanes;
    }

    public double getLaneCenterX(int laneIndex) {
        if (laneIndex < 0) {
            laneIndex = 0;
        }
        if (laneIndex >= lanes) {
            laneIndex = lanes - 1;
        }
        return trackX + laneIndex * getLaneWidth() + getLaneWidth() / 2.0;
    }

    public boolean isInsideTrack(double x, double objectWidth) {
        return x >= trackX && (x + objectWidth) <= (trackX + trackW);
    }

    public double clampXInsideTrack(double x, double objectWidth) {
        double minX = trackX;
        double maxX = trackX + trackW - objectWidth;

        if (x < minX) {
            return minX;
        }
        if (x > maxX) {
            return maxX;
        }
        return x;
    }

    public double getTrackCenterX() {
        return trackX + trackW / 2.0;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public double getTrackX() {
        return trackX;
    }

    @Override
    public double getTrackW() {
        return trackW;
    }

    @Override
    public int getLanes() {
        return lanes;
    }

    protected abstract void renderBackground(GraphicsContext gc, int seg);

    protected abstract void renderDecor(GraphicsContext gc, int seg);
}