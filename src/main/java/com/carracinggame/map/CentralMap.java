package com.carracinggame.map;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class CentralMap extends BaseMap {

    public CentralMap(int width, int height, double trackX, double trackW, int lanes) {
        super(width, height, trackX, trackW, lanes);
    }

    @Override
    protected void renderBackground(GraphicsContext gc, int seg) {
        if (seg == 0) gc.setFill(Color.web("#f2d0a7"));      // Huế (vàng đất)
        else if (seg == 1) gc.setFill(Color.web("#ffd7a6")); // Hội An (ấm)
        else gc.setFill(Color.web("#a6d2ff"));               // Hải Vân (biển + trời)
        gc.fillRect(0, 0, width, height);

        gc.setFill(Color.rgb(255, 255, 255, 0.18));
        gc.fillRect(0, 0, trackX, height);
        gc.fillRect(trackX + trackW, 0, width - (trackX + trackW), height);
    }

    @Override
    protected void renderDecor(GraphicsContext gc, int seg) {
        if (seg == 0) { // Huế: cổng thành (đơn giản)
            gc.setFill(Color.rgb(160, 90, 50, 0.75));
            gc.fillRect(30, 260, 130, 160);
            gc.setFill(Color.rgb(120, 70, 40, 0.85));
            gc.fillRect(60, 300, 70, 120); // cổng

        } else if (seg == 1) { // Hội An: đèn lồng
            for (int i = 0; i < 6; i++) {
                double x = 35 + i * 25;
                double y = 120 + (i % 2) * 35;
                gc.setFill(Color.rgb(255, 120, 60, 0.85));
                gc.fillOval(x, y, 18, 26);
            }
            for (int i = 0; i < 6; i++) {
                double x = width - 60 - i * 25;
                double y = 150 + (i % 2) * 35;
                gc.setFill(Color.rgb(255, 200, 60, 0.85));
                gc.fillOval(x, y, 18, 26);
            }

        } else { // Hải Vân: núi + biển bên phải
            gc.setFill(Color.rgb(0, 90, 130, 0.30));
            gc.fillRect(trackX + trackW, 0, width - (trackX + trackW), height);

            gc.setFill(Color.rgb(70, 130, 90, 0.60));
            gc.fillPolygon(new double[]{width - 20, width - 120, width - 260}, new double[]{520, 260, 520}, 3);
        }
    }
}
