package com.carracinggame.core;

import com.carracinggame.car.CarId;
import com.carracinggame.database.PlayerDAO;
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
import com.carracinggame.car.CarDefinition;
import com.carracinggame.car.CarId;
import com.carracinggame.scene.UpgradeScene;
public class Game {

    private final Stage stage;

    private GameState state;
    private AppScene currentScene;
    private MapId selectedMap = MapId.NORTH;
    private CarId selectedUpgradeCarId;
    private final PlayerProfile playerProfile;
    private final GarageService garageService;
    private final ShopService shopService;

    public Game(Stage stage) {
        this.stage = stage;
        this.stage.setTitle(GameConfig.GAME_TITLE);

        PlayerDAO playerDAO = new PlayerDAO();
        String username = UserSession.getUsername();

        PlayerDAO.PlayerGameData savedData = playerDAO.loadGameData(username);

        this.playerProfile = new PlayerProfile(
                savedData.getUsername(),
                savedData.getCoins(),
                savedData.getEquippedCarId() == null ? CarId.RED_RACER : savedData.getEquippedCarId()
        );

        this.garageService = new GarageService(playerProfile);
        this.garageService.loadOwnedCars(
                savedData.getOwnedCarIds(),
                savedData.getEquippedCarId()
        );

        this.shopService = new ShopService();
    }

    public void start() {
        stage.setMinWidth(GameConfig.MIN_WIDTH);
        stage.setMinHeight(GameConfig.MIN_HEIGHT);
        stage.setResizable(true);

        switchState(GameState.MENU);
        stage.setMaximized(true);
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
        stage.setMaximized(true);

        currentScene.onShow();
        currentScene.requestFocus();
    }

    private AppScene createSceneByState(GameState state) {
        return switch (state) {
            case MENU -> new MenuScene(this);
            case MAP_SELECT -> new MapSelectScene(this);
            case GARAGE -> new GarageScene(this);
            case UPGRADE -> currentScene = new UpgradeScene(this);
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

    public void saveProgress() {
        String username = UserSession.getUsername();
        if (username == null || username.isBlank()) {
            return;
        }

        PlayerDAO playerDAO = new PlayerDAO();
        playerDAO.savePlayerProgress(
                username,
                playerProfile.getCoins(),
                garageService.getOwnedCarIds(),
                playerProfile.getEquippedCarId()
        );
    }
    public void openUpgradeScene(CarId carId) {
        this.selectedUpgradeCarId = carId;
        goTo(GameState.UPGRADE);
    }

    public CarDefinition getUpgradeCarDefinition() {
        if (selectedUpgradeCarId == null) {
            return garageService.getEquippedCar();
        }

        return garageService.getOwnedCarDefinitions()
                .stream()
                .filter(car -> car.getId() == selectedUpgradeCarId)
                .findFirst()
                .orElse(garageService.getEquippedCar());
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