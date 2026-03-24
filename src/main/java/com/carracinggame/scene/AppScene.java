package com.carracinggame.scene;

import javafx.scene.Scene;

public interface AppScene {

    Scene getScene();

    default void onShow() {
    }

    default void onHide() {
    }

    default void onResize(double width, double height) {
    }

    default void requestFocus() {
        if (getScene() != null && getScene().getRoot() != null) {
            getScene().getRoot().requestFocus();
        }
    }

    default String getName() {
        return getClass().getSimpleName();
    }
}