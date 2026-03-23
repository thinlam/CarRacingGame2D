package com.carracinggame.scene;

import javafx.scene.Scene;

public interface AppScene {
    Scene getScene();

    default void onShow() {}
    default void onHide() {}
}