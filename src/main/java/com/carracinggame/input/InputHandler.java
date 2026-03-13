package com.carracinggame.input;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.HashSet;
import java.util.Set;

public class InputHandler {
    private final Set<KeyCode> pressed = new HashSet<>();

    public void attach(Scene scene) {
        scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> pressed.add(e.getCode()));
        scene.addEventFilter(KeyEvent.KEY_RELEASED, e -> pressed.remove(e.getCode()));
    }

    public boolean isDown(KeyCode code) {
        return pressed.contains(code);
    }

    public void clear() {
        pressed.clear();
    }
}
