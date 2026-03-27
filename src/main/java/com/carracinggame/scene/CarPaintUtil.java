package com.carracinggame.scene;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorInput;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

public final class CarPaintUtil {

    private CarPaintUtil() {
    }

    public static void applyPaint(Node node, String paintName) {
        Color color = getPaintColor(paintName);
        if (color == null) {
            clear(node);
            return;
        }
        apply(node, color);
    }

    public static void clear(Node node) {
        if (node == null) return;

        if (node instanceof ImageView imageView) {
            imageView.setEffect(null);
            return;
        }

        if (node instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                clear(child);
            }
        }
    }

    private static void apply(Node node, Color color) {
        if (node == null) return;

        if (node instanceof ImageView imageView) {
            if (imageView.getImage() == null) return;

            double w = imageView.getImage().getWidth();
            double h = imageView.getImage().getHeight();

            Blend blend = new Blend();
            blend.setMode(BlendMode.SRC_ATOP);
            blend.setTopInput(new ColorInput(0, 0, w, h, color));
            imageView.setEffect(blend);
            return;
        }

        if (node instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                apply(child, color);
            }
        }
    }

    private static Color getPaintColor(String paintName) {
        return switch (paintName) {
            case "Xanh Neon" -> Color.web("#2dd4bf", 0.72);
            case "Đỏ Flame" -> Color.web("#ef4444", 0.68);
            case "Vàng Gold" -> Color.web("#facc15", 0.78);
            default -> null;
        };
    }
}