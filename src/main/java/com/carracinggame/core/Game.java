package com.carracinggame.core;

import com.carracinggame.map.MapId;
import com.carracinggame.scene.AppScene;
import com.carracinggame.scene.GarageScene;
import com.carracinggame.scene.MapSelectScene;
import com.carracinggame.scene.MenuScene;
import com.carracinggame.scene.RaceScene;
import com.carracinggame.scene.ResultScene;
import com.carracinggame.scene.ShopScene;
import javafx.stage.Stage;

public class Game {
    private final Stage stage;

    private GameState state;
    private AppScene currentScene;

    private final PlayerProfile playerProfile;
    private MapId selectedMap = MapId.NORTH;
    private RaceSummary lastRaceSummary = RaceSummary.empty();

    public Game(Stage stage) {
        this.stage = stage;
        this.stage.setTitle("Car Racing Game 2D");
        this.playerProfile = PlayerProfile.createDefault();
    }

    public void start() {
        switchState(GameState.MENU);
        stage.show();
    }

    public void switchState(GameState newState) {
        boolean wasMaximized = stage.isMaximized();
        boolean wasFullScreen = stage.isFullScreen();
        double oldWidth = stage.getWidth();
        double oldHeight = stage.getHeight();

        if (currentScene != null) {
            currentScene.onHide();
        }

        this.state = newState;
        this.currentScene = createSceneByState(newState);

        stage.setScene(currentScene.getScene());

        javafx.application.Platform.runLater(() -> {
            if (wasFullScreen) {
                stage.setFullScreen(true);
            } else if (wasMaximized) {
                stage.setMaximized(true);
            } else {
                stage.setWidth(oldWidth);
                stage.setHeight(oldHeight);
                stage.centerOnScreen();
            }
        });

        currentScene.onShow();
    }

    private AppScene createSceneByState(GameState state) {
        return switch (state) {
            case MENU, LOGIN -> new MenuScene(this);
            case GARAGE -> new GarageScene(this);
            case MAP_SELECT -> new MapSelectScene(this);
            case SHOP -> new ShopScene(this);
            case RACE -> new RaceScene(this);
            case RESULT -> new ResultScene(this);
        };
    }

    public void finishRace(int distanceMeters) {
        int baseReward = 300 + mapReward(selectedMap) + playerProfile.getEquippedCar().getBaseRewardBonus();
        playerProfile.addCoins(baseReward);

        MissionProgressResult missionResult = playerProfile.recordRace(distanceMeters);
        lastRaceSummary = new RaceSummary(
                true,
                getSelectedMapLabel(),
                playerProfile.getEquippedCar().getDisplayName(),
                distanceMeters,
                baseReward,
                missionResult.getRewardCoins(),
                missionResult.getClaimedMissionTitles()
        );

        switchState(GameState.RESULT);
    }

    public void failRace(int distanceMeters) {
        lastRaceSummary = new RaceSummary(
                false,
                getSelectedMapLabel(),
                playerProfile.getEquippedCar().getDisplayName(),
                distanceMeters,
                0,
                0,
                java.util.List.of()
        );
        switchState(GameState.RESULT);
    }

    private int mapReward(MapId mapId) {
        return switch (mapId) {
            case NORTH -> 90;
            case CENTRAL -> 120;
            case SOUTH -> 150;
        };
    }

    public void closeGame() {
        stage.close();
    }

    public GameState getState() {
        return state;
    }

    public Stage getStage() {
        return stage;
    }

    public MapId getSelectedMap() {
        return selectedMap;
    }

    public void setSelectedMap(MapId selectedMap) {
        this.selectedMap = selectedMap;
    }

    public String getSelectedMapLabel() {
        return switch (selectedMap) {
            case NORTH -> "Miền Bắc";
            case CENTRAL -> "Miền Trung";
            case SOUTH -> "Miền Nam";
        };
    }

    public PlayerProfile getPlayerProfile() {
        return playerProfile;
    }

    public RaceSummary getLastRaceSummary() {
        return lastRaceSummary;
    }
}
