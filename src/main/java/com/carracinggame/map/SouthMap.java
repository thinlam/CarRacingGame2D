package com.carracinggame.map;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class SouthMap extends BaseMap {

    public SouthMap(int width, int height, double trackX, double trackW, int lanes) {
        super(width, height, trackX, trackW, lanes);
    }

    @Override
    protected void renderBackground(GraphicsContext gc, int seg) {
        if (seg == 0) gc.setFill(Color.web("#bfe9ff"));      // Sài Gòn (sáng)
        else if (seg == 1) gc.setFill(Color.web("#e8c2a0")); // Củ Chi (đất đỏ)
        else gc.setFill(Color.web("#9fe2c1"));               // Mekong (xanh nước)
        gc.fillRect(0, 0, width, height);

        gc.setFill(Color.rgb(255, 255, 255, 0.18));
        gc.fillRect(0, 0, trackX, height);
        gc.fillRect(trackX + trackW, 0, width - (trackX + trackW), height);
    }

    @Override
    protected void renderDecor(GraphicsContext gc, int seg) {
        if (seg == 0) { // Bến Thành (đơn giản: tháp đồng hồ)
            gc.setFill(Color.rgb(220, 170, 100, 0.85));
            gc.fillRect(30, 260, 140, 140);
            gc.setFill(Color.rgb(200, 140, 80, 0.9));
            gc.fillRect(80, 200, 40, 60); // tháp
            gc.setFill(Color.rgb(30, 30, 30, 0.8));
            gc.fillOval(88, 212, 24, 24); // “đồng hồ”

        } else if (seg == 1) { // Củ Chi: cây + bụi
            gc.setFill(Color.rgb(60, 120, 70, 0.75));
            gc.fillOval(40, 120, 70, 70);
            gc.fillOval(85, 160, 70, 70);
            gc.fillOval(width - 140, 140, 80, 80);

            gc.setFill(Color.rgb(255, 255, 255, 0.10));
            gc.fillRect(0, 0, width, height); // bụi nhẹ

        } else { // Mekong: nước + thuyền (decor)
            gc.setFill(Color.rgb(0, 90, 110, 0.28));
            gc.fillRect(0, 0, trackX, height);
            gc.fillRect(trackX + trackW, 0, width - (trackX + trackW), height);

            gc.setFill(Color.rgb(120, 70, 30, 0.8));
            gc.fillRoundRect(70, 420, 70, 22, 10, 10); // thuyền
            gc.fillRoundRect(width - 140, 360, 80, 24, 10, 10);
        }
    }
}
