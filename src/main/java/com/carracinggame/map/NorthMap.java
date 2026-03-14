package com.carracinggame.map;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class NorthMap extends BaseMap {

    public NorthMap(int width, int height, double trackX, double trackW, int lanes) {
        super(width, height, trackX, trackW, lanes);
    }

    @Override
    protected void renderBackground(GraphicsContext gc, int seg) {
        // lề 2 bên theo từng cảnh
        if (seg == 0) gc.setFill(Color.web("#9fd7ff"));      // Hồ Gươm (xanh hồ)
        else if (seg == 1) gc.setFill(Color.web("#78c7ff")); // Hạ Long (xanh biển)
        else gc.setFill(Color.web("#b7c7d6"));               // Sa Pa (sương)
        gc.fillRect(0, 0, width, height);

        // tạo “lề” sáng hơn ngoài track
        gc.setFill(Color.rgb(255, 255, 255, 0.20));
        gc.fillRect(0, 0, trackX, height);
        gc.fillRect(trackX + trackW, 0, width - (trackX + trackW), height);
    }

    @Override
    protected void renderDecor(GraphicsContext gc, int seg) {
        if (seg == 0) { // Hồ Gươm: hồ + tháp rùa (đơn giản hoá)
            gc.setFill(Color.rgb(0, 80, 120, 0.35));
            gc.fillRoundRect(25, 120, trackX - 50, 220, 40, 40);

            gc.setFill(Color.rgb(230, 230, 230, 0.8));
            gc.fillRoundRect(80, 210, 60, 70, 10, 10); // tháp
            gc.setFill(Color.rgb(120, 120, 120, 0.8));
            gc.fillRect(80, 205, 60, 10);
        } else if (seg == 1) { // Hạ Long: đảo đá
            gc.setFill(Color.rgb(0, 60, 90, 0.30));
            gc.fillRect(0, 0, trackX, height);
            gc.fillRect(trackX + trackW, 0, width - (trackX + trackW), height);

            gc.setFill(Color.rgb(40, 80, 60, 0.70));
            gc.fillOval(50, 320, 120, 180);
            gc.fillOval(90, 150, 80, 130);
            gc.fillOval(width - 160, 260, 120, 170);
        } else { // Sa Pa: sương + núi
            gc.setFill(Color.rgb(80, 120, 90, 0.55));
            gc.fillPolygon(new double[]{30, 180, 330}, new double[]{520, 260, 520}, 3);
            gc.fillPolygon(new double[]{width - 40, width - 190, width - 340}, new double[]{520, 270, 520}, 3);

            // lớp sương phủ nhẹ
            gc.setFill(Color.rgb(255, 255, 255, 0.22));
            gc.fillRect(0, 0, width, height);
        }
    }
}
