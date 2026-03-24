package com.carracinggame.scene;

import com.carracinggame.car.CarId;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public final class CarViewFactory {

    private static final double RAW_W = 48;
    private static final double RAW_H = 86;

    private CarViewFactory() {
    }

    public static Node createShopPreview(CarId carId) {
        return createPreview(carId, 140, 95, 1.15, 0);
    }

    public static Node createLobbyPreview(CarId carId) {
        return createPreview(carId, 240, 250, 2.35, 18);
    }

    public static Node createRaceCar(CarId carId) {
        return buildTopViewCar(carId);
    }

    private static Node createPreview(CarId carId,
                                      double boxW,
                                      double boxH,
                                      double scale,
                                      double yOffset) {
        Pane wrapper = new Pane();
        wrapper.setPrefSize(boxW, boxH);
        wrapper.setMinSize(boxW, boxH);
        wrapper.setMaxSize(boxW, boxH);

        Group car = buildTopViewCar(carId);
        car.setScaleX(scale);
        car.setScaleY(scale);

        double scaledW = RAW_W * scale;
        double scaledH = RAW_H * scale;

        double x = (boxW - scaledW) / 2.0;
        double y = (boxH - scaledH) / 2.0 + yOffset;

        if (y < 0) {
            y = 0;
        }

        car.setLayoutX(x);
        car.setLayoutY(y);

        wrapper.getChildren().add(car);
        return wrapper;
    }

    private static Group buildTopViewCar(CarId carId) {
        Group sprite = new Group();

        Color bodyColor = getBodyColor(carId);
        Color centerColor = getCenterColor(carId);
        Color wingColor = bodyColor.darker();
        Color tireColor = Color.web("#3b3b45");
        Color whiteColor = Color.web("#f8fafc");
        Color noseLight = Color.web("#fde68a");

        Rectangle rearWing = rect(16, 4, wingColor, 16, 2, 3, 3);
        Rectangle rearBody = rect(12, 8, bodyColor, 18, 7, 4, 4);
        Rectangle body = rect(18, 38, bodyColor, 15, 15, 6, 6);
        Rectangle cockpit = rect(10, 18, centerColor, 19, 24, 4, 4);
        Rectangle nose = rect(10, 10, whiteColor, 19, 54, 4, 4);
        Rectangle frontWing = rect(18, 4, whiteColor, 15, 67, 3, 3);

        Rectangle tireLT = rect(4, 10, tireColor, 11, 18, 2, 2);
        Rectangle tireRT = rect(4, 10, tireColor, 33, 18, 2, 2);
        Rectangle tireLB = rect(4, 10, tireColor, 11, 46, 2, 2);
        Rectangle tireRB = rect(4, 10, tireColor, 33, 46, 2, 2);

        Rectangle sideLeftTop = rect(3, 8, wingColor, 13, 28, 2, 2);
        Rectangle sideRightTop = rect(3, 8, wingColor, 32, 28, 2, 2);
        Rectangle sideLeftBottom = rect(3, 8, wingColor, 13, 40, 2, 2);
        Rectangle sideRightBottom = rect(3, 8, wingColor, 32, 40, 2, 2);

        Rectangle rearLight = rect(8, 3, whiteColor, 20, 10, 2, 2);
        Rectangle frontLight = rect(8, 3, noseLight, 20, 61, 2, 2);

        sprite.getChildren().addAll(
                rearWing, rearBody, body, cockpit, nose, frontWing,
                tireLT, tireRT, tireLB, tireRB,
                sideLeftTop, sideRightTop, sideLeftBottom, sideRightBottom,
                rearLight, frontLight
        );

        return sprite;
    }

    private static Rectangle rect(double w, double h, Color color,
                                  double x, double y, double arcW, double arcH) {
        Rectangle r = new Rectangle(w, h, color);
        r.setX(x);
        r.setY(y);
        r.setArcWidth(arcW);
        r.setArcHeight(arcH);
        return r;
    }

    private static Color getBodyColor(CarId carId) {
        return switch (carId) {
            case RED_RACER -> Color.web("#ef4444");
            case BLUE_STORM -> Color.web("#2563eb");
            case BLACK_SHADOW -> Color.web("#111827");
            default -> Color.web("#9ca3af");
        };
    }

    private static Color getCenterColor(CarId carId) {
        return switch (carId) {
            case RED_RACER -> Color.web("#facc15");
            case BLUE_STORM -> Color.web("#93c5fd");
            case BLACK_SHADOW -> Color.web("#a78bfa");
            default -> Color.web("#e5e7eb");
        };
    }
}