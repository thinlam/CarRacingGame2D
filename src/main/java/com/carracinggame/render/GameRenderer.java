package com.carracinggame.render;

import com.carracinggame.car.Car;
import com.carracinggame.core.GameConfig;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class GameRenderer {

    public void drawBackground(GraphicsContext gc, int width, int height) {
        gc.setFill(Color.DARKGRAY);
        gc.fillRect(0, 0, width, height);
    }

    public void drawTrack(GraphicsContext gc, double x, double w, double height) {
        gc.setFill(Color.GRAY);
        gc.fillRect(x, 0, w, height);
    }

    public void drawCar(GraphicsContext gc, Car car) {
        if (car.getSprite() == null) return;

        gc.save();
        gc.translate(car.getX() + car.getW() / 2.0, car.getY() + car.getH() / 2.0);
        gc.rotate(car.getAngleDeg() + GameConfig.CAR_SPRITE_ROT_OFFSET_DEG);
        gc.drawImage(car.getSprite(), -car.getW() / 2.0, -car.getH() / 2.0, car.getW(), car.getH());
        gc.restore();
    }
}
