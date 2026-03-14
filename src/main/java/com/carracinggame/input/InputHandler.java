package com.carracinggame.input;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.HashSet;
import java.util.Set;

public class InputHandler {

    private final Set<KeyCode> pressed = new HashSet<>();

    private Scene attachedScene;

    private final EventHandler<KeyEvent> keyPressedHandler = event -> {
        pressed.add(event.getCode());
    };

    private final EventHandler<KeyEvent> keyReleasedHandler = event -> {
        pressed.remove(event.getCode());
    };

    public void attach(Scene scene) {
        if (scene == null) {
            return;
        }

        if (attachedScene == scene) {
            return;
        }

        detach();

        attachedScene = scene;
        attachedScene.addEventFilter(KeyEvent.KEY_PRESSED, keyPressedHandler);
        attachedScene.addEventFilter(KeyEvent.KEY_RELEASED, keyReleasedHandler);
    }

    public void detach() {
        if (attachedScene != null) {
            attachedScene.removeEventFilter(KeyEvent.KEY_PRESSED, keyPressedHandler);
            attachedScene.removeEventFilter(KeyEvent.KEY_RELEASED, keyReleasedHandler);
            attachedScene = null;
        }
        clear();
    }

    public boolean isDown(KeyCode code) {
        return pressed.contains(code);
    }

    public boolean isPressed(KeyCode code) {
        return pressed.contains(code);
    }

    public boolean isAnyDown(KeyCode... codes) {
        if (codes == null) {
            return false;
        }

        for (KeyCode code : codes) {
            if (pressed.contains(code)) {
                return true;
            }
        }
        return false;
    }

    public void clear() {
        pressed.clear();
    }

    public Set<KeyCode> getPressedKeys() {
        return new HashSet<>(pressed);
    }
}