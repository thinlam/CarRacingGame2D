package com.carracinggame.core;

import com.carracinggame.map.MapId;
import com.carracinggame.scene.AppScene;
import com.carracinggame.scene.MapSelectScene;
import com.carracinggame.scene.MenuScene;
import com.carracinggame.scene.RaceScene;
import javafx.stage.Stage;

public class Game {
    private final Stage stage;

    private GameState state;
    private AppScene currentScene;

    // NEW: map đang chọn
    private MapId selectedMap = MapId.NORTH;

    public Game(Stage stage) {
        this.stage = stage;
        this.stage.setTitle("Car Racing Game 2D");
    }

    public void start() {
        switchState(GameState.MENU);
        stage.show();
    }

    public void switchState(GameState newState) {
        if (currentScene != null) {
            currentScene.onHide();
        }

        this.state = newState;
        this.currentScene = createSceneByState(newState);

        stage.setScene(currentScene.getScene());
        currentScene.onShow();
    }

    private AppScene createSceneByState(GameState state) {
        return switch (state) {
            case MENU -> new MenuScene(this);
            case MAP_SELECT -> new MapSelectScene(this); // <-- NEW
            case RACE -> new RaceScene(this);

            // các màn làm sau
            case LOGIN, GARAGE, SHOP, RESULT -> new MenuScene(this);
        };
    }

    public GameState getState() {
        return state;
    }

    public Stage getStage() {
        return stage;
    }

    // ===== NEW getters/setters =====
    public MapId getSelectedMap() {
        return selectedMap;
    }

    public void setSelectedMap(MapId selectedMap) {
        this.selectedMap = selectedMap;
    }
}
