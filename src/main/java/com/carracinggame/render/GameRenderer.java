package com.carracinggame.render;

import com.carracinggame.car.Car;
import com.carracinggame.core.GameConfig;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.geometry.VPos;

public class GameRenderer {

    public void clear(GraphicsContext gc, int width, int height) {
        gc.clearRect(0, 0, width, height);
    }

    public void drawBackground(GraphicsContext gc, int width, int height) {
        gc.setFill(Color.web("#2f855a"));
        gc.fillRect(0, 0, width, height);

        gc.setFill(Color.web("#276749"));
        gc.fillRect(0, 0, width, 90);
    }

    public void drawTrack(GraphicsContext gc, double x, double w, double height) {
        gc.setFill(Color.web("#5f5f5f"));
        gc.fillRect(x, 0, w, height);

        gc.setStroke(Color.WHITE);
        gc.setLineWidth(3);
        gc.strokeLine(x, 0, x, height);
        gc.strokeLine(x + w, 0, x + w, height);

        drawLaneMarkers(gc, x, w, height, 3);
    }

    public void drawLaneMarkers(GraphicsContext gc, double x, double w, double height, int lanes) {
        if (lanes <= 1) {
            return;
        }

        double laneWidth = w / lanes;
        double dashWidth = 4;
        double dashHeight = 28;
        double gap = 18;

        gc.setFill(Color.WHITE);

        for (int lane = 1; lane < lanes; lane++) {
            double lineX = x + lane * laneWidth - dashWidth / 2.0;
            for (double y = 0; y < height; y += dashHeight + gap) {
                gc.fillRect(lineX, y, dashWidth, dashHeight);
            }
        }
    }

    public void drawCar(GraphicsContext gc, Car car) {
        if (car == null) {
            return;
        }

        Image sprite = car.getSprite();

        gc.save();
        gc.translate(car.getX() + car.getW() / 2.0, car.getY() + car.getH() / 2.0);
        gc.rotate(car.getAngleDeg() + GameConfig.CAR_SPRITE_ROT_OFFSET_DEG);

        if (sprite != null) {
            gc.drawImage(sprite, -car.getW() / 2.0, -car.getH() / 2.0, car.getW(), car.getH());
        } else {
            drawCarFallback(gc, car.getW(), car.getH());
        }

        gc.restore();
    }

    private void drawCarFallback(GraphicsContext gc, double w, double h) {
        gc.setFill(Color.RED);
        gc.fillRoundRect(-w / 2.0, -h / 2.0, w, h, 12, 12);

        gc.setFill(Color.rgb(255, 255, 255, 0.85));
        gc.fillRoundRect(-w / 2.0 + 6, -h / 2.0 + 8, w - 12, 14, 8, 8);

        gc.setFill(Color.rgb(30, 41, 59, 0.95));
        gc.fillRoundRect(-w / 2.0 + 6, -h / 2.0 + 26, w - 12, h - 42, 8, 8);
    }

    public void drawHudBox(GraphicsContext gc, double x, double y, double w, double h) {
        gc.setFill(Color.rgb(15, 23, 42, 0.75));
        gc.fillRoundRect(x, y, w, h, 16, 16);
    }

    public void drawText(GraphicsContext gc, String text, double x, double y, int size, Color color) {
        gc.setFill(color);
        gc.setFont(Font.font("Arial", size));
        gc.fillText(text, x, y);
    }

    public void drawCenteredText(GraphicsContext gc, String text, double centerX, double centerY, int size, Color color) {
        gc.save();
        gc.setFill(color);
        gc.setFont(Font.font("Arial", size));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.fillText(text, centerX, centerY);
        gc.restore();
    }
}