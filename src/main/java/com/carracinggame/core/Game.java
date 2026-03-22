package com.carracinggame.core;

import com.carracinggame.car.CarId;
import com.carracinggame.garage.GarageService;
import com.carracinggame.map.MapId;
import com.carracinggame.player.PlayerProfile;
import com.carracinggame.scene.AppScene;
import com.carracinggame.scene.GarageScene;
import com.carracinggame.scene.MapSelectScene;
import com.carracinggame.scene.MenuScene;
import com.carracinggame.scene.RaceScene;
import com.carracinggame.scene.ShopScene;
import com.carracinggame.shop.ShopService;
import javafx.stage.Stage;

public class Game {

    private final Stage stage;

    private GameState state;
    private AppScene currentScene;
    private MapId selectedMap = MapId.NORTH;

    private final PlayerProfile playerProfile;
    private final GarageService garageService;
    private final ShopService shopService;

    public Game(Stage stage) {
        this.stage = stage;
        this.stage.setTitle(GameConfig.GAME_TITLE);

        this.playerProfile = new PlayerProfile("HuuHai", GameConfig.START_COINS, CarId.RED_RACER);
        this.garageService = new GarageService(playerProfile);
        this.shopService = new ShopService();
    }

    public void start() {
        stage.setWidth(GameConfig.WIDTH);
        stage.setHeight(GameConfig.HEIGHT);
        stage.setMinWidth(GameConfig.MIN_WIDTH);
        stage.setMinHeight(GameConfig.MIN_HEIGHT);
        stage.setResizable(false);
        stage.centerOnScreen();

        switchState(GameState.MENU);
        stage.show();
    }

    public void switchState(GameState newState) {
        if (newState == null) {
            return;
        }

        if (currentScene != null) {
            currentScene.onHide();
        }

        this.state = newState;
        this.currentScene = createSceneByState(newState);

        stage.setScene(currentScene.getScene());
        currentScene.onShow();
        currentScene.requestFocus();
    }

    private AppScene createSceneByState(GameState state) {
        return switch (state) {
            case MENU -> new MenuScene(this);
            case MAP_SELECT -> new MapSelectScene(this);
            case GARAGE -> new GarageScene(this);
            case SHOP -> new ShopScene(this);
            case RACE -> new RaceScene(this);
            case LOGIN, RESULT -> new MenuScene(this);
        };
    }

    public void startRace(MapId mapId) {
        this.selectedMap = mapId;
        switchState(GameState.RACE);
    }

    public void goToMenu() {
        switchState(GameState.MENU);
    }

    public void goTo(GameState gameState) {
        switchState(gameState);
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

    public PlayerProfile getPlayerProfile() {
        return playerProfile;
    }

    public GarageService getGarageService() {
        return garageService;
    }

    public ShopService getShopService() {
        return shopService;
    }
}
