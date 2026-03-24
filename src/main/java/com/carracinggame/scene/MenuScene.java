package com.carracinggame.scene;

import com.carracinggame.car.CarId;
import com.carracinggame.core.Game;
import com.carracinggame.core.GameConfig;
import com.carracinggame.core.GameState;
import com.carracinggame.core.UserSession;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class MenuScene implements AppScene {

    private final Game game;
    private final Scene scene;

    private final Label coinValue;
    private final Label playerNameValue;
    private final Label equippedCarValue;
    private final Label selectedMapValue;

    private final StackPane showroomHolder;
    private final StackPane carHolder;
    private final StackPane characterHolder;

    public MenuScene(Game game) {
        this.game = game;

        this.coinValue = new Label("0");
        this.playerNameValue = new Label("Player");
        this.equippedCarValue = new Label();
        this.selectedMapValue = new Label();

        this.showroomHolder = new StackPane();
        this.carHolder = new StackPane();
        this.characterHolder = new StackPane();

        BorderPane root = new BorderPane();
        root.setPrefSize(GameConfig.WIDTH, GameConfig.HEIGHT);
        root.setStyle("""
            -fx-background-color:
                radial-gradient(center 18% 10%, radius 38%, rgba(255,255,255,0.30), transparent 60%),
                radial-gradient(center 85% 16%, radius 30%, rgba(255,255,255,0.16), transparent 60%),
                linear-gradient(to bottom, #8fd2ff 0%, #bfe6ff 42%, #dff3ff 100%);
        """);

        root.setTop(buildTopBar());
        root.setCenter(buildCenterArea());
        root.setRight(buildRightActions());
        root.setBottom(buildBottomNav());

        this.scene = new Scene(root, GameConfig.WIDTH, GameConfig.HEIGHT);
        refreshData();
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

    private Node buildTopBar() {
        HBox topBar = new HBox(16);
        topBar.setPadding(new Insets(18, 20, 12, 20));
        topBar.setAlignment(Pos.CENTER_LEFT);

        Node profileCard = buildProfileCard();

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Node coinBar = buildCoinBar();

        topBar.getChildren().addAll(profileCard, spacer, coinBar);
        return topBar;
    }

    private Node buildProfileCard() {
        HBox card = new HBox(14);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(14, 18, 14, 18));
        card.setStyle("""
            -fx-background-color: rgba(255,255,255,0.50);
            -fx-background-radius: 24;
            -fx-border-color: rgba(255,255,255,0.72);
            -fx-border-radius: 24;
        """);

        Circle avatar = new Circle(31, Color.web("#5b7cf0"));

        VBox info = new VBox(4);

        playerNameValue.setStyle("""
            -fx-font-size: 16px;
            -fx-font-weight: 900;
            -fx-text-fill: #0d2b4f;
        """);

        Label sub = new Label("Menu chính");
        sub.setStyle("""
            -fx-font-size: 12px;
            -fx-text-fill: #204a73;
        """);

        info.getChildren().addAll(playerNameValue, sub);
        card.getChildren().addAll(avatar, info);

        return card;
    }

    private Node buildCoinBar() {
        HBox shell = new HBox();
        shell.setAlignment(Pos.CENTER_RIGHT);
        shell.setPadding(new Insets(10, 12, 10, 12));
        shell.setStyle("""
            -fx-background-color: rgba(255,255,255,0.50);
            -fx-background-radius: 24;
            -fx-border-color: rgba(255,255,255,0.72);
            -fx-border-radius: 24;
        """);

        HBox coinPill = new HBox(8);
        coinPill.setAlignment(Pos.CENTER_LEFT);
        coinPill.setPadding(new Insets(10, 16, 10, 16));
        coinPill.setStyle("""
            -fx-background-color: rgba(0,0,0,0.08);
            -fx-background-radius: 18;
        """);

        Label icon = new Label("🪙");
        icon.setStyle("-fx-font-size: 14px;");

        Label label = new Label("Coin:");
        label.setStyle("""
            -fx-font-size: 12px;
            -fx-font-weight: 900;
            -fx-text-fill: #0d2b4f;
        """);

        coinValue.setStyle("""
            -fx-font-size: 12px;
            -fx-font-weight: 900;
            -fx-text-fill: #0d2b4f;
        """);

        coinPill.getChildren().addAll(icon, label, coinValue);
        shell.getChildren().add(coinPill);

        return shell;
    }

    private Node buildCenterArea() {
        StackPane center = new StackPane();
        center.setPadding(new Insets(12, 28, 18, 28));

        Rectangle panel = new Rectangle(GameConfig.WIDTH * 0.70, GameConfig.HEIGHT * 0.60);
        panel.setArcWidth(42);
        panel.setArcHeight(42);
        panel.setFill(Color.rgb(255, 255, 255, 0.24));
        panel.setStroke(Color.rgb(255, 255, 255, 0.36));

        showroomHolder.setPrefSize(860, 470);
        showroomHolder.setMinSize(860, 470);
        showroomHolder.setMaxSize(860, 470);

        Pane showroom = buildShowroomStage();

        VBox infoBox = new VBox(10, equippedCarValue, selectedMapValue);
        infoBox.setAlignment(Pos.CENTER);
        infoBox.setPadding(new Insets(14, 18, 14, 18));
        infoBox.setMaxWidth(430);
        infoBox.setStyle("""
            -fx-background-color: rgba(255,255,255,0.42);
            -fx-background-radius: 22;
            -fx-border-color: rgba(255,255,255,0.40);
            -fx-border-radius: 22;
        """);

        equippedCarValue.setStyle("""
            -fx-font-size: 20px;
            -fx-font-weight: 900;
            -fx-text-fill: #0d2b4f;
        """);

        selectedMapValue.setStyle("""
            -fx-font-size: 14px;
            -fx-font-weight: 800;
            -fx-text-fill: #204a73;
        """);

        VBox content = new VBox(20, showroomHolder, infoBox);
        content.setAlignment(Pos.CENTER);

        center.getChildren().addAll(panel, content);
        showroomHolder.getChildren().add(showroom);

        return center;
    }

    private Pane buildShowroomStage() {
        Pane stage = new Pane();
        stage.setPrefSize(860, 470);

        Rectangle floorCard = new Rectangle(780, 382);
        floorCard.setArcWidth(36);
        floorCard.setArcHeight(36);
        floorCard.setFill(Color.rgb(255, 255, 255, 0.18));
        floorCard.setStroke(Color.rgb(255, 255, 255, 0.20));
        floorCard.setX(40);
        floorCard.setY(26);

        Circle lightLeft = new Circle(220, 118, 94, Color.rgb(255, 255, 255, 0.15));
        Circle lightRight = new Circle(640, 118, 82, Color.rgb(255, 255, 255, 0.11));

        Rectangle carPodium = new Rectangle(270, 275);
        carPodium.setX(92);
        carPodium.setY(68);
        carPodium.setArcWidth(30);
        carPodium.setArcHeight(30);
        carPodium.setFill(Color.rgb(255, 255, 255, 0.14));
        carPodium.setStroke(Color.rgb(255, 255, 255, 0.18));

        Rectangle charPodium = new Rectangle(248, 320);
        charPodium.setX(500);
        charPodium.setY(50);
        charPodium.setArcWidth(30);
        charPodium.setArcHeight(30);
        charPodium.setFill(Color.rgb(255, 255, 255, 0.14));
        charPodium.setStroke(Color.rgb(255, 255, 255, 0.18));

        Label carTitle = new Label("SHOWROOM XE");
        carTitle.setLayoutX(145);
        carTitle.setLayoutY(28);
        carTitle.setStyle("""
            -fx-font-size: 18px;
            -fx-font-weight: 900;
            -fx-text-fill: #11365d;
        """);

        Label charTitle = new Label("RACER");
        charTitle.setLayoutX(590);
        charTitle.setLayoutY(28);
        charTitle.setStyle("""
            -fx-font-size: 18px;
            -fx-font-weight: 900;
            -fx-text-fill: #11365d;
        """);

        Rectangle carShadow = new Rectangle(170, 18);
        carShadow.setX(142);
        carShadow.setY(304);
        carShadow.setArcWidth(18);
        carShadow.setArcHeight(18);
        carShadow.setFill(Color.rgb(0, 0, 0, 0.10));

        Rectangle charShadow = new Rectangle(124, 20);
        charShadow.setX(560);
        charShadow.setY(318);
        charShadow.setArcWidth(18);
        charShadow.setArcHeight(18);
        charShadow.setFill(Color.rgb(0, 0, 0, 0.10));

        carHolder.setPrefSize(240, 250);
        carHolder.setMinSize(240, 250);
        carHolder.setMaxSize(240, 250);
        carHolder.setLayoutX(107);
        carHolder.setLayoutY(102);

        characterHolder.setPrefSize(220, 280);
        characterHolder.setMinSize(220, 280);
        characterHolder.setMaxSize(220, 280);
        characterHolder.setLayoutX(515);
        characterHolder.setLayoutY(78);

        Label tip = new Label("Xe đang chọn trong Garage sẽ hiển thị ở đây");
        tip.setLayoutX(218);
        tip.setLayoutY(392);
        tip.setStyle("""
            -fx-font-size: 12px;
            -fx-font-weight: 700;
            -fx-text-fill: #2d5b86;
        """);

        stage.getChildren().addAll(
                floorCard,
                lightLeft,
                lightRight,
                carPodium,
                charPodium,
                carTitle,
                charTitle,
                carShadow,
                charShadow,
                carHolder,
                characterHolder,
                tip
        );

        return stage;
    }

    private Node buildRightActions() {
        VBox actions = new VBox(14);
        actions.setPadding(new Insets(92, 18, 18, 14));
        actions.setAlignment(Pos.TOP_RIGHT);

        Button btnStart = bigAction("🏁  Start Race", "#3bc484");
        Button btnShop = bigAction("🛒  Shop", "#4195e5");
        Button btnGarage = bigAction("🚗  Garage", "#fb8d3d");
        Button btnBonus = bigAction("🪙  +500 Coin", "#9a5bf0");
        Button btnLogout = bigAction("⎋  Đăng xuất", "#fb6666");

        btnStart.setOnAction(e -> game.switchState(GameState.MAP_SELECT));
        btnShop.setOnAction(e -> game.switchState(GameState.SHOP));
        btnGarage.setOnAction(e -> game.switchState(GameState.GARAGE));
        btnBonus.setOnAction(e -> {
            game.getPlayerProfile().addCoins(500);
            refreshData();
        });
        btnLogout.setOnAction(e -> logout());

        actions.getChildren().addAll(btnStart, btnShop, btnGarage, btnBonus, btnLogout);
        return actions;
    }

    private Node buildBottomNav() {
        HBox bottom = new HBox(10);
        bottom.setPadding(new Insets(10, 14, 14, 14));
        bottom.setAlignment(Pos.CENTER_LEFT);
        bottom.setStyle("""
            -fx-background-color: rgba(255,255,255,0.52);
            -fx-background-radius: 20;
        """);

        Button home = navBtn("🏠 Home");
        Button shop = navBtn("🛒 Shop");
        Button garage = navBtn("🚗 Garage");
        Button map = navBtn("🗺 Map");

        home.setOnAction(e -> refreshData());
        shop.setOnAction(e -> game.switchState(GameState.SHOP));
        garage.setOnAction(e -> game.switchState(GameState.GARAGE));
        map.setOnAction(e -> game.switchState(GameState.MAP_SELECT));

        bottom.getChildren().addAll(home, shop, garage, map);
        return bottom;
    }

    private Button bigAction(String text, String hex) {
        Button b = new Button(text);
        b.setMinWidth(190);
        b.setMinHeight(56);
        b.setStyle("""
            -fx-background-radius: 20;
            -fx-font-size: 15px;
            -fx-font-weight: 900;
            -fx-text-fill: white;
            -fx-background-color: %s;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.14), 14, 0, 0, 7);
        """.formatted(hex));
        return b;
    }

    private Button navBtn(String text) {
        Button b = new Button(text);
        b.setStyle("""
            -fx-background-radius: 14;
            -fx-background-color: rgba(255,255,255,0.78);
            -fx-text-fill: #0d2b4f;
            -fx-font-weight: 900;
        """);
        return b;
    }

    private void refreshData() {
        coinValue.setText(String.valueOf(game.getPlayerProfile().getCoins()));

        String username = UserSession.getUsername();
        if (username == null || username.isBlank()) {
            username = game.getPlayerProfile().getPlayerName();
        }
        playerNameValue.setText(username);

        String carName = "Chưa có";
        CarId equippedId = null;

        if (game.getGarageService() != null && game.getGarageService().getEquippedCar() != null) {
            carName = game.getGarageService().getEquippedCar().getName();
            equippedId = game.getGarageService().getEquippedCar().getId();
        }

        equippedCarValue.setText("Xe hiện tại: " + carName);
        selectedMapValue.setText("Map hiện tại: " + toDisplayName());

        refreshShowroom(equippedId);
    }

    private void refreshShowroom(CarId equippedId) {
        carHolder.getChildren().clear();
        characterHolder.getChildren().clear();

        if (equippedId != null) {
            Node car = CarViewFactory.createLobbyPreview(equippedId);
            carHolder.getChildren().add(car);
        } else {
            Label empty = new Label("Chưa có xe");
            empty.setStyle("""
                -fx-font-size: 15px;
                -fx-font-weight: 800;
                -fx-text-fill: #204a73;
            """);
            carHolder.getChildren().add(empty);
        }

        characterHolder.getChildren().add(buildLobbyCharacter(equippedId));
    }

    private Node buildLobbyCharacter(CarId equippedId) {
        Pane character = new Pane();
        character.setPrefSize(190, 280);

        Color suitColor = getCharacterPrimary(equippedId);
        Color detailColor = getCharacterSecondary(equippedId);
        Color skin = Color.web("#ecd6b0");
        Color hair = Color.web("#1f2937");
        Color pants = Color.web("#243041");
        Color shoes = Color.web("#111827");

        Circle hairBack = new Circle(95, 42, 36, hair);
        Circle head = new Circle(95, 56, 33, skin);

        Circle eyeL = new Circle(82, 52, 2.5, Color.web("#1f2937"));
        Circle eyeR = new Circle(108, 52, 2.5, Color.web("#1f2937"));

        Rectangle neck = new Rectangle(82, 83, 26, 16);
        neck.setArcWidth(10);
        neck.setArcHeight(10);
        neck.setFill(skin);

        Rectangle jacket = new Rectangle(62, 98, 68, 102);
        jacket.setArcWidth(24);
        jacket.setArcHeight(24);
        jacket.setFill(suitColor);

        Rectangle jacketCenter = new Rectangle(91, 98, 10, 102);
        jacketCenter.setArcWidth(10);
        jacketCenter.setArcHeight(10);
        jacketCenter.setFill(detailColor);

        Rectangle badge = new Rectangle(74, 118, 16, 16);
        badge.setArcWidth(7);
        badge.setArcHeight(7);
        badge.setFill(Color.web("#ffffff"));

        Rectangle armL = new Rectangle(42, 108, 20, 80);
        armL.setArcWidth(18);
        armL.setArcHeight(18);
        armL.setFill(suitColor);

        Rectangle armR = new Rectangle(130, 108, 20, 80);
        armR.setArcWidth(18);
        armR.setArcHeight(18);
        armR.setFill(suitColor);

        Circle handL = new Circle(52, 196, 10, skin);
        Circle handR = new Circle(140, 196, 10, skin);

        Rectangle legL = new Rectangle(73, 198, 20, 66);
        legL.setArcWidth(12);
        legL.setArcHeight(12);
        legL.setFill(pants);

        Rectangle legR = new Rectangle(98, 198, 20, 66);
        legR.setArcWidth(12);
        legR.setArcHeight(12);
        legR.setFill(pants);

        Rectangle shoeL = new Rectangle(66, 260, 34, 14);
        shoeL.setArcWidth(8);
        shoeL.setArcHeight(8);
        shoeL.setFill(shoes);

        Rectangle shoeR = new Rectangle(95, 260, 34, 14);
        shoeR.setArcWidth(8);
        shoeR.setArcHeight(8);
        shoeR.setFill(shoes);

        character.getChildren().addAll(
                hairBack, head, eyeL, eyeR, neck,
                jacket, jacketCenter, badge,
                armL, armR, handL, handR,
                legL, legR, shoeL, shoeR
        );

        return character;
    }

    private Color getCharacterPrimary(CarId carId) {
        if (carId == null) {
            return Color.web("#f2d34f");
        }

        return switch (carId) {
            case RED_RACER -> Color.web("#f2d34f");
            case BLUE_STORM -> Color.web("#66b3ff");
            case BLACK_SHADOW -> Color.web("#7c67ff");
            default -> Color.web("#94a3b8");
        };
    }

    private Color getCharacterSecondary(CarId carId) {
        if (carId == null) {
            return Color.web("#ef4444");
        }

        return switch (carId) {
            case RED_RACER -> Color.web("#ef4444");
            case BLUE_STORM -> Color.web("#1d4ed8");
            case BLACK_SHADOW -> Color.web("#111827");
            default -> Color.web("#475569");
        };
    }

    private String toDisplayName() {
        if (game.getSelectedMap() == null) {
            return "Chưa chọn";
        }

        return switch (game.getSelectedMap()) {
            case NORTH -> "Miền Bắc";
            case CENTRAL -> "Miền Trung";
            case SOUTH -> "Miền Nam";
        };
    }

    private void logout() {
        try {
            UserSession.clear();

            Stage stage = (Stage) scene.getWindow();
            if (stage != null) {
                stage.setScene(LoginScene.create(stage, () -> {
                    Game newGame = new Game(stage);
                    newGame.start();
                }));
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText(null);
            alert.setContentText("Đăng xuất lỗi: " + e.getMessage());
            alert.showAndWait();
        }
    }
}