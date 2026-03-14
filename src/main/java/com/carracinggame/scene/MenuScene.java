package com.carracinggame.scene;

import com.carracinggame.core.Game;
import com.carracinggame.core.GameConfig;
import com.carracinggame.core.GameState;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class MenuScene implements AppScene {

    private final Game game;
    private final Scene scene;

    private Label coinValue;
    private Label playerNameValue;
    private Label equippedCarValue;
    private Label selectedMapValue;

    public MenuScene(Game game) {
        this.game = game;

        BorderPane root = new BorderPane();
        root.setPrefSize(GameConfig.WIDTH, GameConfig.HEIGHT);

        root.setStyle("""
            -fx-background-color: linear-gradient(to bottom, #7fd2ff 0%, #bfe9ff 40%, #e7f7ff 100%);
        """);

        HBox topBar = new HBox(12);
        topBar.setPadding(new Insets(14, 14, 10, 14));
        topBar.setAlignment(Pos.CENTER_LEFT);

        Node profileCard = buildProfileCard();
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Node coinBar = buildCoinBar();

        topBar.getChildren().addAll(profileCard, spacer, coinBar);
        root.setTop(topBar);

        StackPane center = new StackPane();
        center.setPadding(new Insets(10));

        Rectangle stage = new Rectangle(GameConfig.WIDTH * 0.88, GameConfig.HEIGHT * 0.62);
        stage.setArcWidth(40);
        stage.setArcHeight(40);
        stage.setFill(Color.rgb(255, 255, 255, 0.35));
        stage.setStroke(Color.rgb(255, 255, 255, 0.45));

        Rectangle car = new Rectangle(240, 95);
        car.setArcWidth(28);
        car.setArcHeight(28);
        car.setFill(Color.rgb(40, 90, 160, 0.85));
        car.setStroke(Color.rgb(255, 255, 255, 0.55));
        car.setTranslateX(-90);
        car.setTranslateY(70);

        VBox character = new VBox(10);
        character.setAlignment(Pos.CENTER);

        Circle head = new Circle(44, Color.rgb(255, 235, 200));
        head.setStroke(Color.rgb(0, 0, 0, 0.15));

        Rectangle body = new Rectangle(130, 190);
        body.setArcWidth(26);
        body.setArcHeight(26);
        body.setFill(Color.rgb(255, 220, 80));
        body.setStroke(Color.rgb(0, 0, 0, 0.12));

        Label hint = new Label("Nhân vật / xe hiện tại (placeholder)");
        hint.setStyle("-fx-font-size: 12px; -fx-text-fill: #0d2b4f; -fx-font-weight: 700;");

        equippedCarValue = new Label();
        equippedCarValue.setStyle("-fx-font-size: 13px; -fx-text-fill: #0d2b4f; -fx-font-weight: 900;");

        selectedMapValue = new Label();
        selectedMapValue.setStyle("-fx-font-size: 12px; -fx-text-fill: #204a73; -fx-font-weight: 800;");

        character.getChildren().addAll(head, body, hint, equippedCarValue, selectedMapValue);
        character.setTranslateX(80);
        character.setTranslateY(20);

        center.getChildren().addAll(stage, car, character);
        root.setCenter(center);

        VBox actions = new VBox(12);
        actions.setPadding(new Insets(90, 16, 16, 16));
        actions.setAlignment(Pos.TOP_RIGHT);

        Button btnStart = bigAction("🏁  Start Race", "#35c88b");
        Button btnShop = bigAction("🛒  Shop", "#3aa0ff");
        Button btnGarage = bigAction("🚗  Garage", "#ff8c42");
        Button btnBonus = bigAction("🪙  +500 Coin", "#a35dff");

        btnStart.setOnAction(e -> game.switchState(GameState.MAP_SELECT));
        btnShop.setOnAction(e -> game.switchState(GameState.SHOP));
        btnGarage.setOnAction(e -> game.switchState(GameState.GARAGE));
        btnBonus.setOnAction(e -> {
            game.getPlayerProfile().addCoins(500);
            refreshData();
        });

        actions.getChildren().addAll(btnStart, btnShop, btnGarage, btnBonus);
        root.setRight(actions);

        HBox bottom = new HBox(10);
        bottom.setPadding(new Insets(10, 14, 14, 14));
        bottom.setAlignment(Pos.CENTER_LEFT);
        bottom.setStyle("""
            -fx-background-color: rgba(255,255,255,0.55);
            -fx-background-radius: 18;
        """);

        Button home = navBtn("🏠 Home");
        Button shop = navBtn("🛒 Shop");
        Button garage = navBtn("🚗 Garage");
        Button map = navBtn("🗺 Map");

        home.setOnAction(e -> {
        });
        shop.setOnAction(e -> game.switchState(GameState.SHOP));
        garage.setOnAction(e -> game.switchState(GameState.GARAGE));
        map.setOnAction(e -> game.switchState(GameState.MAP_SELECT));

        bottom.getChildren().addAll(home, shop, garage, map);
        root.setBottom(bottom);

        this.scene = new Scene(root, GameConfig.WIDTH, GameConfig.HEIGHT);
    }

    @Override
    public Scene getScene() {
        return scene;
    }

    @Override
    public void onShow() {
        refreshData();
        scene.getRoot().requestFocus();
    }

    @Override
    public void onHide() {
    }

    private Node buildProfileCard() {
        HBox card = new HBox(10);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(10, 12, 10, 12));
        card.setStyle("""
            -fx-background-color: rgba(255,255,255,0.60);
            -fx-background-radius: 18;
            -fx-border-radius: 18;
            -fx-border-color: rgba(255,255,255,0.75);
        """);

        Circle avatar = new Circle(22, Color.rgb(70, 120, 255, 0.85));
        avatar.setStroke(Color.rgb(255, 255, 255, 0.7));

        VBox info = new VBox(2);

        playerNameValue = new Label();
        playerNameValue.setStyle("-fx-font-size: 15px; -fx-font-weight: 900; -fx-text-fill: #0d2b4f;");

        Label sub = new Label("Menu chính - Hữu Hải");
        sub.setStyle("-fx-font-size: 12px; -fx-text-fill: #204a73;");

        info.getChildren().addAll(playerNameValue, sub);
        card.getChildren().addAll(avatar, info);

        return card;
    }

    private Node buildCoinBar() {
        HBox bar = new HBox(10);
        bar.setAlignment(Pos.CENTER_RIGHT);
        bar.setPadding(new Insets(10, 12, 10, 12));
        bar.setStyle("""
            -fx-background-color: rgba(255,255,255,0.60);
            -fx-background-radius: 18;
            -fx-border-radius: 18;
            -fx-border-color: rgba(255,255,255,0.75);
        """);

        HBox coinPill = new HBox(6);
        coinPill.setAlignment(Pos.CENTER_LEFT);
        coinPill.setPadding(new Insets(6, 10, 6, 10));
        coinPill.setStyle("""
            -fx-background-color: rgba(0,0,0,0.10);
            -fx-background-radius: 14;
        """);

        Label icon = new Label("🪙");
        icon.setStyle("-fx-font-size: 14px;");

        Label label = new Label("Coin:");
        label.setStyle("-fx-font-size: 12px; -fx-text-fill: #0d2b4f; -fx-font-weight: 800;");

        coinValue = new Label("0");
        coinValue.setStyle("-fx-font-size: 12px; -fx-text-fill: #0d2b4f; -fx-font-weight: 900;");

        coinPill.getChildren().addAll(icon, label, coinValue);
        bar.getChildren().addAll(coinPill);

        return bar;
    }

    private Button bigAction(String text, String hex) {
        Button b = new Button(text);
        b.setMinWidth(190);
        b.setMinHeight(52);
        b.setStyle("""
            -fx-background-radius: 18;
            -fx-font-size: 15px;
            -fx-font-weight: 900;
            -fx-text-fill: white;
            -fx-background-color: %s;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 12, 0, 0, 6);
        """.formatted(hex));
        return b;
    }

    private Button navBtn(String text) {
        Button b = new Button(text);
        b.setStyle("""
            -fx-background-radius: 14;
            -fx-background-color: rgba(255,255,255,0.75);
            -fx-text-fill: #0d2b4f;
            -fx-font-weight: 900;
        """);
        return b;
    }

    private void refreshData() {
        if (coinValue != null) {
            coinValue.setText(String.valueOf(game.getPlayerProfile().getCoins()));
        }

        if (playerNameValue != null) {
            playerNameValue.setText(game.getPlayerProfile().getPlayerName());
        }

        if (equippedCarValue != null) {
            equippedCarValue.setText("Xe hiện tại: " + game.getGarageService().getEquippedCar().getName());
        }

        if (selectedMapValue != null) {
            selectedMapValue.setText("Map hiện tại: " + toDisplayName());
        }
    }

    private String toDisplayName() {
        return switch (game.getSelectedMap()) {
            case NORTH -> "Miền Bắc";
            case CENTRAL -> "Miền Trung";
            case SOUTH -> "Miền Nam";
        };
    }

    private void comingSoon(String feature) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Coming soon");
        alert.setHeaderText(null);
        alert.setContentText(feature + " sẽ được cập nhật thêm.");
        alert.showAndWait();
    }
}