package com.carracinggame.scene;

import com.carracinggame.car.CarId;
import com.carracinggame.core.Game;
import com.carracinggame.core.GameState;
import com.carracinggame.core.UserSession;
import com.carracinggame.database.PlayerDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.bson.Document;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.prefs.Preferences;

public class MenuScene implements AppScene {

    private static final int[] DAILY_REWARDS = {100, 150, 200, 250, 300, 400, 500};

    private final Game game;
    private final Scene scene;

    private final Label coinValue;
    private final Label playerNameValue;
    private final Label playerBannerValue;
    private final Label equippedCarValue;
    private final Label selectedMapValue;

    private final StackPane carHolder;
    private final StackPane characterHolder;
    private final StackPane profileAvatarHolder;

    private final Preferences prefs = Preferences.userNodeForPackage(MenuScene.class);
    private final PlayerDAO playerDAO = new PlayerDAO();

    public MenuScene(Game game) {
        this.game = game;

        this.coinValue = new Label("0");
        this.playerNameValue = new Label("PLAYER");
        this.playerBannerValue = new Label("PILOT");
        this.equippedCarValue = new Label("Chưa có xe");
        this.selectedMapValue = new Label("Chưa chọn map");

        this.carHolder = new StackPane();
        this.characterHolder = new StackPane();
        this.profileAvatarHolder = new StackPane();

        BorderPane content = new BorderPane();
        content.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        content.setMinHeight(760);
        content.setStyle("""
            -fx-background-color:
                radial-gradient(center 18% 10%, radius 34%, rgba(0,220,255,0.18), transparent 56%),
                radial-gradient(center 84% 12%, radius 30%, rgba(147,89,255,0.18), transparent 50%),
                radial-gradient(center 50% 88%, radius 28%, rgba(255,119,48,0.12), transparent 50%),
                linear-gradient(to bottom, #02050c 0%, #071120 38%, #09192b 72%, #050913 100%);
            -fx-font-family: "Segoe UI", "Inter", "Arial";
        """);

        content.setTop(buildTopBar());
        content.setCenter(buildCenterArea());
        content.setRight(buildRightActions());
        content.setBottom(buildBottomNav());

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setPannable(true);
        scrollPane.setStyle("""
            -fx-background: transparent;
            -fx-background-color: transparent;
            -fx-padding: 0;
        """);

        this.scene = new Scene(scrollPane);

        content.prefWidthProperty().bind(scene.widthProperty().subtract(18));

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
        HBox topBar = new HBox(14);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(16, 18, 10, 18));

        Node profileCard = buildProfileCard();
        Node brandCard = buildBrandCard();

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Node coinBar = buildCoinBar();

        topBar.getChildren().addAll(profileCard, brandCard, spacer, coinBar);
        return topBar;
    }

    private Node buildProfileCard() {
        HBox card = new HBox(14);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(14, 18, 14, 18));
        card.setStyle("""
            -fx-background-color:
                linear-gradient(to bottom right, rgba(8,18,34,0.94), rgba(11,26,47,0.92));
            -fx-background-radius: 24;
            -fx-border-color: rgba(67,207,255,0.34);
            -fx-border-width: 1.2;
            -fx-border-radius: 24;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.35), 18, 0, 0, 8);
        """);

        StackPane avatarShell = new StackPane();
        avatarShell.setPrefSize(72, 72);
        avatarShell.setMinSize(72, 72);
        avatarShell.setMaxSize(72, 72);
        avatarShell.setStyle("""
            -fx-background-color:
                radial-gradient(center 35% 30%, radius 75%, rgba(85,225,255,0.42), rgba(18,34,58,0.96));
            -fx-background-radius: 999;
            -fx-border-color: rgba(134,236,255,0.82);
            -fx-border-width: 2.0;
            -fx-border-radius: 999;
            -fx-effect: dropshadow(gaussian, rgba(61,199,255,0.28), 16, 0, 0, 0);
        """);

        profileAvatarHolder.setPrefSize(62, 62);
        profileAvatarHolder.setMinSize(62, 62);
        profileAvatarHolder.setMaxSize(62, 62);

        avatarShell.getChildren().add(profileAvatarHolder);

        VBox info = new VBox(3);
        info.setAlignment(Pos.CENTER_LEFT);

        Label small = new Label("PILOT PROFILE");
        small.setStyle("""
            -fx-font-size: 10px;
            -fx-font-weight: 800;
            -fx-text-fill: #78cfff;
            -fx-letter-spacing: 1px;
        """);

        playerNameValue.setStyle("""
            -fx-font-size: 18px;
            -fx-font-weight: 900;
            -fx-text-fill: #f4fbff;
        """);

        Label sub = new Label("Garage hub offline");
        sub.setStyle("""
            -fx-font-size: 12px;
            -fx-font-weight: 700;
            -fx-text-fill: #7d9ebb;
        """);

        info.getChildren().addAll(small, playerNameValue, sub);
        card.getChildren().addAll(avatarShell, info);

        return card;
    }

    private Node buildBrandCard() {
        VBox card = new VBox(2);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(12, 18, 12, 18));
        card.setStyle("""
            -fx-background-color:
                linear-gradient(to bottom right, rgba(6,14,26,0.88), rgba(9,20,37,0.84));
            -fx-background-radius: 20;
            -fx-border-color: rgba(255,255,255,0.08);
            -fx-border-radius: 20;
        """);

        Label title = new Label("RACING LOBBY");
        title.setStyle("""
            -fx-font-size: 20px;
            -fx-font-weight: 900;
            -fx-text-fill: white;
            -fx-letter-spacing: 1px;
        """);

        Label sub = new Label("Select car • upgrade • start the race");
        sub.setStyle("""
            -fx-font-size: 11px;
            -fx-font-weight: 700;
            -fx-text-fill: #7f97b2;
        """);

        card.getChildren().addAll(title, sub);
        return card;
    }

    private Node buildCoinBar() {
        HBox shell = new HBox(12);
        shell.setAlignment(Pos.CENTER_RIGHT);
        shell.setPadding(new Insets(10, 12, 10, 12));
        shell.setStyle("""
            -fx-background-color:
                linear-gradient(to bottom right, rgba(8,18,34,0.96), rgba(12,28,50,0.94));
            -fx-background-radius: 22;
            -fx-border-color: rgba(255,204,64,0.30);
            -fx-border-width: 1.1;
            -fx-border-radius: 22;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.32), 16, 0, 0, 7);
        """);

        VBox tagBox = new VBox(1);
        Label tag = new Label("CREDITS");
        tag.setStyle("""
            -fx-font-size: 10px;
            -fx-font-weight: 800;
            -fx-text-fill: #ffd66c;
            -fx-letter-spacing: 1px;
        """);

        Label hint = new Label("Số dư hiện tại");
        hint.setStyle("""
            -fx-font-size: 11px;
            -fx-font-weight: 700;
            -fx-text-fill: #7f97b2;
        """);

        tagBox.getChildren().addAll(tag, hint);

        HBox coinPill = new HBox(10);
        coinPill.setAlignment(Pos.CENTER);
        coinPill.setPadding(new Insets(10, 16, 10, 16));
        coinPill.setStyle("""
            -fx-background-color:
                linear-gradient(to bottom, rgba(255,201,59,0.18), rgba(255,169,31,0.08));
            -fx-background-radius: 18;
            -fx-border-color: rgba(255,204,64,0.42);
            -fx-border-radius: 18;
        """);

        Label icon = new Label("◉");
        icon.setStyle("""
            -fx-font-size: 13px;
            -fx-font-weight: 900;
            -fx-text-fill: #ffd54a;
        """);

        coinValue.setStyle("""
            -fx-font-size: 17px;
            -fx-font-weight: 900;
            -fx-text-fill: #fff3c1;
        """);

        coinPill.getChildren().addAll(icon, coinValue);
        shell.getChildren().addAll(tagBox, coinPill);

        return shell;
    }

    private Node buildCenterArea() {
        StackPane center = new StackPane();
        center.setPadding(new Insets(4, 14, 10, 14));

        VBox showroomCard = new VBox(14);
        showroomCard.setAlignment(Pos.TOP_CENTER);
        showroomCard.setPadding(new Insets(18, 18, 18, 18));
        showroomCard.setMaxWidth(860);
        showroomCard.setStyle("""
            -fx-background-color:
                linear-gradient(to bottom, rgba(8,18,34,0.94), rgba(7,15,28,0.94));
            -fx-background-radius: 28;
            -fx-border-color: rgba(74,214,255,0.20);
            -fx-border-width: 1.1;
            -fx-border-radius: 28;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.34), 18, 0, 0, 8);
        """);

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(3);
        Label miniTag = new Label("SHOWCASE");
        miniTag.setStyle("""
            -fx-font-size: 10px;
            -fx-font-weight: 800;
            -fx-text-fill: #72d6ff;
            -fx-letter-spacing: 1px;
        """);

        Label title = new Label("GARAGE DISPLAY");
        title.setStyle("""
            -fx-font-size: 24px;
            -fx-font-weight: 900;
            -fx-text-fill: white;
        """);

        Label subtitle = new Label("Xe đang trang bị và avatar racer của bạn");
        subtitle.setStyle("""
            -fx-font-size: 12px;
            -fx-font-weight: 700;
            -fx-text-fill: #7f97b2;
        """);

        titleBox.getChildren().addAll(miniTag, title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label liveChip = new Label("MAIN BASE");
        liveChip.setPadding(new Insets(6, 12, 6, 12));
        liveChip.setStyle("""
            -fx-background-color:
                linear-gradient(to bottom, rgba(54,214,255,0.18), rgba(54,214,255,0.08));
            -fx-background-radius: 999;
            -fx-border-color: rgba(111,227,255,0.42);
            -fx-border-radius: 999;
            -fx-text-fill: #b9f4ff;
            -fx-font-size: 10px;
            -fx-font-weight: 900;
        """);

        header.getChildren().addAll(titleBox, spacer, liveChip);

        HBox displayRow = new HBox(16);
        displayRow.setAlignment(Pos.CENTER);

        VBox carBox = buildDisplayBox("RACE CAR", "Xe đang được chọn để đua", carHolder, 285, 240);
        VBox racerBox = buildDisplayBox("RACER AVATAR", "Hồ sơ người chơi hiện tại", characterHolder, 255, 240);

        displayRow.getChildren().addAll(carBox, racerBox);

        HBox infoRow = new HBox(12);
        infoRow.setAlignment(Pos.CENTER);
        infoRow.getChildren().addAll(
                buildHudInfoCard("EQUIPPED", equippedCarValue, "#ffb14a"),
                buildHudInfoCard("PILOT", playerBannerValue, "#47d7ff"),
                buildHudInfoCard("SELECTED MAP", selectedMapValue, "#18d486")
        );
        showroomCard.getChildren().addAll(header, displayRow, infoRow);
        center.getChildren().add(showroomCard);

        return center;
    }

    private VBox buildDisplayBox(String title, String subtitle, StackPane holder, double width, double height) {
        VBox box = new VBox(8);
        box.setAlignment(Pos.TOP_CENTER);

        Label boxTitle = new Label(title);
        boxTitle.setStyle("""
            -fx-font-size: 16px;
            -fx-font-weight: 900;
            -fx-text-fill: #f5fbff;
        """);

        Label boxSub = new Label(subtitle);
        boxSub.setStyle("""
            -fx-font-size: 10px;
            -fx-font-weight: 700;
            -fx-text-fill: #7f97b2;
        """);

        StackPane frame = new StackPane();
        frame.setPrefSize(width, height);
        frame.setMinSize(width, height);
        frame.setMaxSize(width, height);
        frame.setStyle("""
            -fx-background-color:
                radial-gradient(center 50% 26%, radius 68%, rgba(35,103,196,0.26), rgba(8,18,34,0.96));
            -fx-background-radius: 26;
            -fx-border-color: rgba(255,255,255,0.08);
            -fx-border-width: 1.1;
            -fx-border-radius: 26;
        """);

        Rectangle lane = new Rectangle(width - 34, height - 42);
        lane.setArcWidth(24);
        lane.setArcHeight(24);
        lane.setFill(Color.rgb(255, 255, 255, 0.02));
        lane.setStroke(Color.rgb(71, 215, 255, 0.10));
        lane.getStrokeDashArray().addAll(12.0, 9.0);

        Circle glow = new Circle(width * 0.17, Color.rgb(68, 209, 255, 0.14));
        glow.setTranslateY(-50);

        Rectangle shadow = new Rectangle(width * 0.40, 14);
        shadow.setArcWidth(16);
        shadow.setArcHeight(16);
        shadow.setFill(Color.rgb(0, 0, 0, 0.28));
        shadow.setTranslateY(height * 0.32);

        holder.setPrefSize(width - 40, height - 34);
        holder.setMinSize(width - 40, height - 34);
        holder.setMaxSize(width - 40, height - 34);

        frame.getChildren().addAll(lane, glow, shadow, holder);

        box.getChildren().addAll(boxTitle, boxSub, frame);
        return box;
    }

    private VBox buildHudInfoCard(String title, Label valueLabel, String accent) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(12, 14, 12, 14));
        card.setPrefWidth(240);
        card.setMinHeight(82);
        card.setStyle("""
            -fx-background-color:
                linear-gradient(to bottom right, rgba(10,21,39,0.96), rgba(7,16,30,0.96));
            -fx-background-radius: 18;
            -fx-border-color: rgba(255,255,255,0.08);
            -fx-border-width: 1;
            -fx-border-radius: 18;
        """);

        Region accentBar = new Region();
        accentBar.setPrefHeight(3);
        accentBar.setMaxWidth(Double.MAX_VALUE);
        accentBar.setStyle("""
            -fx-background-color: %s;
            -fx-background-radius: 999;
        """.formatted(accent));

        Label titleLabel = new Label(title);
        titleLabel.setStyle("""
            -fx-font-size: 10px;
            -fx-font-weight: 800;
            -fx-text-fill: #7f97b2;
            -fx-letter-spacing: 1px;
        """);

        valueLabel.setStyle("""
            -fx-font-size: 15px;
            -fx-font-weight: 900;
            -fx-text-fill: #ffffff;
        """);
        valueLabel.setWrapText(true);

        card.getChildren().addAll(accentBar, titleLabel, valueLabel);
        return card;
    }

    private Node buildRightActions() {
        VBox wrapper = new VBox();
        wrapper.setAlignment(Pos.TOP_RIGHT);
        wrapper.setPadding(new Insets(54, 18, 12, 8));

        VBox dock = new VBox(10);
        dock.setPrefWidth(240);
        dock.setPadding(new Insets(14, 14, 14, 14));
        dock.setStyle("""
        -fx-background-color:
            linear-gradient(to bottom, rgba(7,16,31,0.95), rgba(6,12,24,0.96));
        -fx-background-radius: 24;
        -fx-border-color: rgba(255,255,255,0.08);
        -fx-border-width: 1.1;
        -fx-border-radius: 24;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.34), 16, 0, 0, 8);
    """);

        Label title = new Label("CONTROL PANEL");
        title.setStyle("""
        -fx-font-size: 18px;
        -fx-font-weight: 900;
        -fx-text-fill: white;
    """);

        Label sub = new Label("Các thao tác nhanh trong game");
        sub.setStyle("""
        -fx-font-size: 10px;
        -fx-font-weight: 700;
        -fx-text-fill: #7f97b2;
    """);

        Region line = new Region();
        line.setPrefHeight(1);
        line.setMaxWidth(Double.MAX_VALUE);
        line.setStyle("""
        -fx-background-color: rgba(255,255,255,0.08);
    """);

        Button btnStart = bigAction("START RACE", "#10d37f", "#0a8f57");
        Button btnShop = bigAction("SHOP", "#3aa7ff", "#226dd8");
        Button btnGarage = bigAction("GARAGE", "#ff9d3c", "#d86a1f");
        Button btnLeaderboard = bigAction("BẢNG XẾP HẠNG", "#f5b041", "#d68910");
        Button btnBonus = bigAction("DAILY REWARD", "#9b6dff", "#6e49d8");
        Button btnLogout = bigAction("LOG OUT", "#ff6d6d", "#d44444");

        btnStart.setOnAction(e -> game.switchState(GameState.MAP_SELECT));
        btnShop.setOnAction(e -> game.switchState(GameState.SHOP));
        btnGarage.setOnAction(e -> game.switchState(GameState.GARAGE));
        btnLeaderboard.setOnAction(e -> game.showLeaderboard());
        btnBonus.setOnAction(e -> openDailyCheckInDialog());
        btnLogout.setOnAction(e -> logout());

        dock.getChildren().addAll(
                title, sub, line,
                btnStart, btnShop, btnGarage, btnLeaderboard, btnBonus, btnLogout
        );
        wrapper.getChildren().add(dock);

        return wrapper;
    }
    private Node buildBottomNav() {
        HBox bottom = new HBox(10);
        bottom.setAlignment(Pos.CENTER_LEFT);
        bottom.setPadding(new Insets(6, 14, 12, 14));

        HBox dock = new HBox(8);
        dock.setAlignment(Pos.CENTER_LEFT);
        dock.setPadding(new Insets(10, 12, 10, 12));
        dock.setStyle("""
        -fx-background-color:
            linear-gradient(to bottom, rgba(7,16,31,0.95), rgba(6,12,24,0.96));
        -fx-background-radius: 22;
        -fx-border-color: rgba(255,255,255,0.08);
        -fx-border-radius: 22;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.28), 16, 0, 0, 8);
    """);

        Button home = navBtn("HOME");
        Button shop = navBtn("SHOP");
        Button garage = navBtn("GARAGE");
        Button map = navBtn("MAP SELECT");
        Button leaderboard = navBtn("BXH");

        home.setOnAction(e -> refreshData());
        shop.setOnAction(e -> game.switchState(GameState.SHOP));
        garage.setOnAction(e -> game.switchState(GameState.GARAGE));
        map.setOnAction(e -> game.switchState(GameState.MAP_SELECT));
        leaderboard.setOnAction(e -> game.showLeaderboard());

        dock.getChildren().addAll(home, shop, garage, map, leaderboard);
        bottom.getChildren().add(dock);

        return bottom;
    }

    private Button bigAction(String text, String topColor, String bottomColor) {
        Button b = new Button(text);
        b.setPrefWidth(210);
        b.setMinHeight(48);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setStyle("""
            -fx-background-color:
                linear-gradient(to bottom, %s 0%%, %s 100%%);
            -fx-background-radius: 16;
            -fx-border-color: rgba(255,255,255,0.14);
            -fx-border-width: 1;
            -fx-border-radius: 16;
            -fx-text-fill: white;
            -fx-font-size: 13px;
            -fx-font-weight: 900;
            -fx-letter-spacing: 1px;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.24), 12, 0, 0, 5);
        """.formatted(topColor, bottomColor));
        return b;
    }

    private Button navBtn(String text) {
        Button b = new Button(text);
        b.setMinHeight(36);
        b.setStyle("""
            -fx-background-color:
                linear-gradient(to bottom, rgba(28,50,78,0.92), rgba(16,31,53,0.92));
            -fx-background-radius: 12;
            -fx-border-color: rgba(94,205,255,0.18);
            -fx-border-width: 1;
            -fx-border-radius: 12;
            -fx-text-fill: #d9f7ff;
            -fx-font-size: 11px;
            -fx-font-weight: 800;
            -fx-padding: 7 14 7 14;
        """);
        return b;
    }

    private void refreshData() {
        coinValue.setText(String.valueOf(game.getPlayerProfile().getCoins()));

        String username = UserSession.getUsername();
        if (username == null || username.isBlank()) {
            username = game.getPlayerProfile().getPlayerName();
        }
        if (username == null || username.isBlank()) {
            username = "PLAYER";
        }

        playerNameValue.setText(username);
        playerBannerValue.setText(username.toUpperCase());

        refreshProfileAvatar(username);

        String carName = "Chưa có xe";
        CarId equippedId = null;

        if (game.getGarageService() != null && game.getGarageService().getEquippedCar() != null) {
            carName = game.getGarageService().getEquippedCar().getName();
            equippedId = game.getGarageService().getEquippedCar().getId();
        }

        equippedCarValue.setText(carName);
        selectedMapValue.setText(toDisplayName());

        refreshShowroom(equippedId);
    }

    private void refreshProfileAvatar(String username) {
        profileAvatarHolder.getChildren().clear();
        String avatarId = loadSelectedAvatarId(username);
        profileAvatarHolder.getChildren().add(buildProfileAvatarNode(avatarId, username));
    }

    private String loadSelectedAvatarId(String username) {
        String avatarId = tryLoadAvatarIdFromPlayerDao(username);

        if (avatarId == null || avatarId.isBlank()) {
            avatarId = tryLoadAvatarIdFromProfile();
        }

        if (avatarId == null || avatarId.isBlank()) {
            avatarId = "helmet_a";
        }

        return avatarId.trim();
    }

    private String tryLoadAvatarIdFromPlayerDao(String username) {
        if (username == null || username.isBlank()) {
            return null;
        }

        List<String> candidateMethods = List.of(
                "getPlayerByUsername",
                "findByUsername",
                "findPlayerByUsername",
                "getUserByUsername",
                "findUserByUsername"
        );

        for (String methodName : candidateMethods) {
            try {
                Object result = playerDAO.getClass()
                        .getMethod(methodName, String.class)
                        .invoke(playerDAO, username);

                String avatarId = extractAvatarId(result);
                if (avatarId != null && !avatarId.isBlank()) {
                    return avatarId;
                }
            } catch (Exception ignored) {
            }
        }

        return null;
    }

    private String tryLoadAvatarIdFromProfile() {
        Object profile = game.getPlayerProfile();
        if (profile == null) {
            return null;
        }

        List<String> candidateMethods = List.of(
                "getSelectedAvatar",
                "getSelectedAvatarId",
                "selectedAvatar",
                "getAvatarId"
        );

        for (String methodName : candidateMethods) {
            try {
                Object result = profile.getClass().getMethod(methodName).invoke(profile);
                if (result != null) {
                    String avatarId = String.valueOf(result).trim();
                    if (!avatarId.isBlank()) {
                        return avatarId;
                    }
                }
            } catch (Exception ignored) {
            }
        }

        return null;
    }

    private String extractAvatarId(Object result) {
        if (result == null) {
            return null;
        }

        if (result instanceof Document document) {
            return document.getString("selectedAvatar");
        }

        if (result instanceof Map<?, ?> map) {
            Object value = map.get("selectedAvatar");
            return value == null ? null : String.valueOf(value);
        }

        try {
            Object value = result.getClass()
                    .getMethod("getString", String.class)
                    .invoke(result, "selectedAvatar");
            return value == null ? null : String.valueOf(value);
        } catch (Exception ignored) {
        }

        try {
            Object value = result.getClass()
                    .getMethod("get", Object.class)
                    .invoke(result, "selectedAvatar");
            return value == null ? null : String.valueOf(value);
        } catch (Exception ignored) {
        }

        return null;
    }

    private Node buildProfileAvatarNode(String avatarId, String username) {
        String imagePath = findExistingAvatarPath(avatarId);

        if (imagePath != null) {
            InputStream stream = getClass().getResourceAsStream(imagePath);
            if (stream != null) {
                Image image = new Image(stream);
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(62);
                imageView.setFitHeight(62);
                imageView.setPreserveRatio(false);
                imageView.setSmooth(true);

                Circle clip = new Circle(31);
                clip.setCenterX(31);
                clip.setCenterY(31);
                imageView.setClip(clip);

                StackPane wrapper = new StackPane(imageView);
                wrapper.setPrefSize(62, 62);
                wrapper.setMinSize(62, 62);
                wrapper.setMaxSize(62, 62);
                return wrapper;
            }
        }

        Circle fallbackCircle = new Circle(31, Color.web("#4e6cff"));
        String firstChar = (username != null && !username.isBlank())
                ? username.substring(0, 1).toUpperCase()
                : "P";

        Label fallbackText = new Label(firstChar);
        fallbackText.setStyle("""
            -fx-font-size: 22px;
            -fx-font-weight: 900;
            -fx-text-fill: white;
        """);

        StackPane wrapper = new StackPane(fallbackCircle, fallbackText);
        wrapper.setPrefSize(62, 62);
        wrapper.setMinSize(62, 62);
        wrapper.setMaxSize(62, 62);
        return wrapper;
    }

    private String findExistingAvatarPath(String avatarId) {
        String safeId = (avatarId == null || avatarId.isBlank()) ? "helmet_a" : avatarId.trim();
        String dashed = safeId.replace('_', '-');
        String underscored = safeId.replace('-', '_');

        List<String> candidates = List.of(
                "/images/avatars/" + safeId + ".png",
                "/images/avatars/" + dashed + ".png",
                "/images/avatars/" + underscored + ".png",

                "/images/avatar/" + safeId + ".png",
                "/images/avatar/" + dashed + ".png",
                "/images/avatar/" + underscored + ".png",

                "/images/ui/" + safeId + ".png",
                "/images/ui/" + dashed + ".png",
                "/images/ui/" + underscored + ".png",

                "/images/ui/avatars/" + safeId + ".png",
                "/images/ui/avatars/" + dashed + ".png",
                "/images/ui/avatars/" + underscored + ".png",

                "/images/cars/ui/" + safeId + ".png",
                "/images/cars/ui/" + dashed + ".png",
                "/images/cars/ui/" + underscored + ".png",
                "/images/cars/ui/avatar-" + safeId + ".png",
                "/images/cars/ui/avatar-" + dashed + ".png",
                "/images/cars/ui/avatar_" + safeId + ".png",
                "/images/cars/ui/avatar_" + underscored + ".png"
        );

        for (String path : candidates) {
            if (resourceExists(path)) {
                return path;
            }
        }

        List<String> fallbacks = List.of(
                "/images/avatars/helmet_a.png",
                "/images/avatars/helmet-a.png",
                "/images/cars/ui/avatar-helmet-a.png",
                "/images/cars/ui/avatar_helmet_a.png",
                "/images/cars/ui/racer_chibi.png"
        );

        for (String path : fallbacks) {
            if (resourceExists(path)) {
                return path;
            }
        }

        return null;
    }

    private boolean resourceExists(String path) {
        return getClass().getResource(path) != null;
    }

    private void refreshShowroom(CarId equippedId) {
        carHolder.getChildren().clear();
        characterHolder.getChildren().clear();

        if (equippedId != null) {
            Node car = CarViewFactory.createLobbyPreview(equippedId);
            carHolder.getChildren().add(car);
        } else {
            Label empty = new Label("NO CAR");
            empty.setStyle("""
                -fx-font-size: 18px;
                -fx-font-weight: 800;
                -fx-text-fill: #77a8d1;
            """);
            carHolder.getChildren().add(empty);
        }

        characterHolder.getChildren().add(buildLobbyCharacter());
    }

    private Node buildLobbyCharacter() {
        InputStream stream = getClass().getResourceAsStream("/images/cars/ui/racer_chibi.png");

        if (stream == null) {
            Label fallback = new Label("NO RACER IMAGE");
            fallback.setStyle("""
                -fx-font-size: 15px;
                -fx-font-weight: 800;
                -fx-text-fill: #77a8d1;
            """);

            StackPane wrapper = new StackPane(fallback);
            wrapper.setPrefSize(200, 210);
            wrapper.setMinSize(200, 210);
            wrapper.setMaxSize(200, 210);
            return wrapper;
        }

        Image image = new Image(stream);
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(180);
        imageView.setFitHeight(190);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        StackPane wrapper = new StackPane(imageView);
        wrapper.setPrefSize(200, 210);
        wrapper.setMinSize(200, 210);
        wrapper.setMaxSize(200, 210);

        return wrapper;
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

    private void openDailyCheckInDialog() {
        String username = getCurrentUsername();
        if (username == null || username.isBlank()) {
            showInfo("Điểm danh", "Không tìm thấy tài khoản đăng nhập.");
            return;
        }

        CheckInInfo info = buildCheckInInfo(username);

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Điểm danh hằng ngày");
        dialog.setHeaderText(null);
        dialog.getDialogPane().setPrefSize(900, 660);
        dialog.getDialogPane().setStyle("""
            -fx-background-color:
                linear-gradient(to bottom, #06101e 0%, #09192c 55%, #07111f 100%);
            -fx-border-color: rgba(77,214,255,0.26);
            -fx-border-width: 1.2;
            -fx-font-family: "Segoe UI", "Inter", "Arial";
        """);

        ButtonType claimType = new ButtonType(
                "Nhận thưởng +" + info.todayReward + " coin",
                ButtonBar.ButtonData.OK_DONE
        );

        if (info.canClaimToday) {
            dialog.getDialogPane().getButtonTypes().addAll(claimType, ButtonType.CLOSE);
        } else {
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        }

        dialog.getDialogPane().setContent(buildCheckInPanel(info));

        Button closeBtn = (Button) dialog.getDialogPane().lookupButton(ButtonType.CLOSE);
        if (closeBtn != null) {
            closeBtn.setText("Đóng");
            closeBtn.setMinHeight(42);
            closeBtn.setMinWidth(120);
            closeBtn.setStyle("""
                -fx-background-color:
                    linear-gradient(to bottom, #203650, #15263c);
                -fx-text-fill: #d6f6ff;
                -fx-font-size: 13px;
                -fx-font-weight: 800;
                -fx-background-radius: 14;
                -fx-border-color: rgba(99,214,255,0.18);
                -fx-border-radius: 14;
                -fx-border-width: 1;
            """);
        }

        if (info.canClaimToday) {
            Button claimBtn = (Button) dialog.getDialogPane().lookupButton(claimType);
            if (claimBtn != null) {
                claimBtn.setMinHeight(42);
                claimBtn.setMinWidth(220);
                claimBtn.setStyle("""
                    -fx-background-color:
                        linear-gradient(to bottom, #18d486 0%, #0f9f63 100%);
                    -fx-text-fill: white;
                    -fx-font-size: 13px;
                    -fx-font-weight: 900;
                    -fx-background-radius: 14;
                    -fx-border-radius: 14;
                    -fx-effect: dropshadow(gaussian, rgba(24,212,134,0.24), 14, 0, 0, 5);
                """);
            }
        }

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == claimType) {
            claimDailyReward(username);
        }
    }

    private Node buildCheckInPanel(CheckInInfo info) {
        VBox root = new VBox(18);
        root.setPadding(new Insets(24, 26, 22, 26));
        root.setStyle("-fx-background-color: transparent;");

        VBox headerBox = new VBox(8);
        headerBox.setPadding(new Insets(22, 24, 22, 24));
        headerBox.setStyle("""
            -fx-background-color:
                radial-gradient(center 20% 20%, radius 60%, rgba(255,255,255,0.10), transparent 60%),
                linear-gradient(to right, #0f2f59 0%, #124a88 55%, #1878c7 100%);
            -fx-background-radius: 24;
            -fx-effect: dropshadow(gaussian, rgba(19,74,129,0.24), 18, 0, 0, 6);
        """);

        Label title = new Label("DAILY LOGIN REWARD");
        title.setStyle("""
            -fx-font-size: 28px;
            -fx-font-weight: 900;
            -fx-text-fill: white;
        """);

        Label subtitle = new Label(info.message);
        subtitle.setWrapText(true);
        subtitle.setStyle("""
            -fx-font-size: 13px;
            -fx-font-weight: 700;
            -fx-text-fill: rgba(255,255,255,0.92);
        """);

        headerBox.getChildren().addAll(title, subtitle);

        HBox statsRow = new HBox(14);
        statsRow.setAlignment(Pos.CENTER);

        VBox stat1 = createCheckInStatCard("Chuỗi hiện tại", info.currentStreak + " ngày", "#1dd7ff");
        VBox stat2 = createCheckInStatCard("Tổng số ngày", String.valueOf(info.totalClaimDays), "#18d486");
        VBox stat3 = createCheckInStatCard("Thưởng hôm nay", info.todayReward + " coin", "#ffb547");

        statsRow.getChildren().addAll(stat1, stat2, stat3);

        HBox sectionHeader = new HBox(14);
        sectionHeader.setAlignment(Pos.CENTER_LEFT);

        Label sectionTitle = new Label("Lịch thưởng 7 ngày");
        sectionTitle.setStyle("""
            -fx-font-size: 18px;
            -fx-font-weight: 900;
            -fx-text-fill: #eaf8ff;
        """);

        Region line = new Region();
        line.setPrefHeight(2);
        line.setMaxWidth(Double.MAX_VALUE);
        line.setStyle("""
            -fx-background-color: linear-gradient(to right, rgba(74,214,255,0.36), transparent);
        """);
        HBox.setHgrow(line, Priority.ALWAYS);

        sectionHeader.getChildren().addAll(sectionTitle, line);

        GridPane grid = buildCheckInGrid(info);

        Label note = new Label("Lưu ý: Nếu bỏ lỡ quá 1 ngày, chuỗi điểm danh sẽ quay về ngày 1.");
        note.setWrapText(true);
        note.setStyle("""
            -fx-font-size: 12px;
            -fx-font-weight: 700;
            -fx-text-fill: #87a4bf;
            -fx-padding: 6 4 0 4;
        """);

        root.getChildren().addAll(headerBox, statsRow, sectionHeader, grid, note);
        return root;
    }

    private GridPane buildCheckInGrid(CheckInInfo info) {
        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(16);
        grid.setAlignment(Pos.CENTER);

        int completedDays = info.claimedToday
                ? info.cycleDay
                : Math.max(info.cycleDay - 1, 0);

        for (int i = 0; i < DAILY_REWARDS.length; i++) {
            int day = i + 1;

            boolean isTodayCard = day == info.cycleDay;
            boolean isCompleted = day <= completedDays;
            boolean isClaimableToday = isTodayCard && info.canClaimToday;
            boolean isClaimedToday = isTodayCard && info.claimedToday;

            VBox card = createCheckInDayCard(
                    day,
                    DAILY_REWARDS[i],
                    isCompleted,
                    isClaimableToday,
                    isClaimedToday
            );

            grid.add(card, i % 4, i / 4);
        }

        return grid;
    }

    private VBox createCheckInStatCard(String title, String value, String accentColor) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(16, 18, 16, 18));
        card.setPrefWidth(255);
        card.setMinHeight(96);
        card.setStyle("""
            -fx-background-color:
                linear-gradient(to bottom right, rgba(10,21,39,0.96), rgba(7,16,30,0.96));
            -fx-background-radius: 20;
            -fx-border-color: rgba(255,255,255,0.08);
            -fx-border-width: 1;
            -fx-border-radius: 20;
        """);

        Region accent = new Region();
        accent.setPrefHeight(4);
        accent.setMaxWidth(Double.MAX_VALUE);
        accent.setStyle("""
            -fx-background-color: %s;
            -fx-background-radius: 999;
        """.formatted(accentColor));

        Label titleLabel = new Label(title);
        titleLabel.setStyle("""
            -fx-font-size: 12px;
            -fx-font-weight: 700;
            -fx-text-fill: #86a4bf;
        """);

        Label valueLabel = new Label(value);
        valueLabel.setStyle("""
            -fx-font-size: 22px;
            -fx-font-weight: 900;
            -fx-text-fill: #ffffff;
        """);

        card.getChildren().addAll(accent, titleLabel, valueLabel);
        return card;
    }

    private VBox createCheckInDayCard(int day,
                                      int reward,
                                      boolean isCompleted,
                                      boolean isClaimableToday,
                                      boolean isClaimedToday) {

        VBox card = new VBox(10);
        card.setAlignment(Pos.TOP_LEFT);
        card.setPadding(new Insets(16));
        card.setPrefSize(170, 135);
        card.setMinSize(170, 135);
        card.setMaxSize(170, 135);

        String cardStyle;
        String badgeText;
        String badgeStyle;

        if (isClaimableToday) {
            cardStyle = """
                -fx-background-color:
                    radial-gradient(center 20% 15%, radius 70%, rgba(255,255,255,0.12), transparent 60%),
                    linear-gradient(to bottom right, #149dff 0%, #0e5cd3 100%);
                -fx-background-radius: 22;
                -fx-border-color: rgba(122,212,255,0.74);
                -fx-border-width: 1.5;
                -fx-border-radius: 22;
                -fx-effect: dropshadow(gaussian, rgba(20,157,255,0.24), 18, 0, 0, 6);
            """;
            badgeText = "HÔM NAY";
            badgeStyle = """
                -fx-background-color: rgba(255,255,255,0.18);
                -fx-text-fill: white;
                -fx-font-size: 11px;
                -fx-font-weight: 900;
                -fx-background-radius: 999;
                -fx-padding: 4 10 4 10;
            """;
        } else if (isClaimedToday) {
            cardStyle = """
                -fx-background-color:
                    linear-gradient(to bottom right, #15b86a 0%, #0f9d58 100%);
                -fx-background-radius: 22;
                -fx-border-color: rgba(120,226,172,0.78);
                -fx-border-width: 1.5;
                -fx-border-radius: 22;
                -fx-effect: dropshadow(gaussian, rgba(15,157,88,0.20), 18, 0, 0, 5);
            """;
            badgeText = "ĐÃ NHẬN";
            badgeStyle = """
                -fx-background-color: rgba(255,255,255,0.18);
                -fx-text-fill: white;
                -fx-font-size: 11px;
                -fx-font-weight: 900;
                -fx-background-radius: 999;
                -fx-padding: 4 10 4 10;
            """;
        } else if (isCompleted) {
            cardStyle = """
                -fx-background-color:
                    linear-gradient(to bottom, rgba(17,51,32,0.96) 0%, rgba(11,37,24,0.96) 100%);
                -fx-background-radius: 22;
                -fx-border-color: rgba(88,201,136,0.26);
                -fx-border-width: 1.2;
                -fx-border-radius: 22;
            """;
            badgeText = "HOÀN THÀNH";
            badgeStyle = """
                -fx-background-color: rgba(29,184,108,0.18);
                -fx-text-fill: #8ff2ba;
                -fx-font-size: 11px;
                -fx-font-weight: 900;
                -fx-background-radius: 999;
                -fx-padding: 4 10 4 10;
            """;
        } else {
            cardStyle = """
                -fx-background-color:
                    linear-gradient(to bottom, rgba(10,21,39,0.96) 0%, rgba(7,16,30,0.96) 100%);
                -fx-background-radius: 22;
                -fx-border-color: rgba(255,255,255,0.08);
                -fx-border-width: 1.2;
                -fx-border-radius: 22;
            """;
            badgeText = "SẮP MỞ";
            badgeStyle = """
                -fx-background-color: rgba(255,255,255,0.08);
                -fx-text-fill: #87a4bf;
                -fx-font-size: 11px;
                -fx-font-weight: 900;
                -fx-background-radius: 999;
                -fx-padding: 4 10 4 10;
            """;
        }

        card.setStyle(cardStyle);

        HBox top = new HBox();
        top.setAlignment(Pos.CENTER_LEFT);

        Label dayLabel = new Label("Ngày " + day);
        dayLabel.setStyle(isClaimableToday || isClaimedToday
                ? """
                  -fx-font-size: 20px;
                  -fx-font-weight: 900;
                  -fx-text-fill: white;
                  """
                : """
                  -fx-font-size: 20px;
                  -fx-font-weight: 900;
                  -fx-text-fill: #f4fbff;
                  """);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label badge = new Label(badgeText);
        badge.setStyle(badgeStyle);

        top.getChildren().addAll(dayLabel, spacer, badge);

        Label rewardTitle = new Label("Phần thưởng");
        rewardTitle.setStyle(isClaimableToday || isClaimedToday
                ? """
                  -fx-font-size: 12px;
                  -fx-font-weight: 700;
                  -fx-text-fill: rgba(255,255,255,0.84);
                  """
                : """
                  -fx-font-size: 12px;
                  -fx-font-weight: 700;
                  -fx-text-fill: #87a4bf;
                  """);

        Label rewardValue = new Label(reward + " coin");
        rewardValue.setStyle(isClaimableToday || isClaimedToday
                ? """
                  -fx-font-size: 24px;
                  -fx-font-weight: 900;
                  -fx-text-fill: white;
                  """
                : """
                  -fx-font-size: 24px;
                  -fx-font-weight: 900;
                  -fx-text-fill: #ffffff;
                  """);

        Label desc = new Label(
                isClaimableToday ? "Đăng nhập hôm nay để nhận thưởng."
                        : isClaimedToday ? "Bạn đã nhận phần thưởng hôm nay."
                        : isCompleted ? "Mốc thưởng đã hoàn thành."
                        : "Tiếp tục đăng nhập để mở mốc này."
        );
        desc.setWrapText(true);
        desc.setStyle(isClaimableToday || isClaimedToday
                ? """
                  -fx-font-size: 11px;
                  -fx-font-weight: 600;
                  -fx-text-fill: rgba(255,255,255,0.88);
                  """
                : """
                  -fx-font-size: 11px;
                  -fx-font-weight: 600;
                  -fx-text-fill: #87a4bf;
                  """);

        card.getChildren().addAll(top, rewardTitle, rewardValue, desc);
        return card;
    }

    private void claimDailyReward(String username) {
        CheckInInfo info = buildCheckInInfo(username);

        if (!info.canClaimToday) {
            showInfo("Điểm danh", "Bạn đã nhận thưởng hôm nay rồi.");
            return;
        }

        int newStreak = info.claimStreakIfClaimToday;
        int cycleDay = toCycleDay(newStreak);
        int reward = DAILY_REWARDS[cycleDay - 1];
        int newTotalClaimDays = getTotalClaimDays(username) + 1;

        setLastClaimDate(username, LocalDate.now());
        setStreak(username, newStreak);
        setTotalClaimDays(username, newTotalClaimDays);

        game.getPlayerProfile().addCoins(reward);
        game.saveProgress();
        refreshData();

        showInfo(
                "Điểm danh thành công",
                "Bạn nhận được +" + reward + " coin"
                        + "\nNgày điểm danh: " + cycleDay
                        + "\nChuỗi liên tiếp: " + newStreak + " ngày"
                        + "\nTổng coin hiện tại: " + game.getPlayerProfile().getCoins()
        );
    }

    private CheckInInfo buildCheckInInfo(String username) {
        LocalDate today = LocalDate.now();
        LocalDate lastClaimDate = getLastClaimDate(username);
        int currentStreak = getStreak(username);
        int totalClaimDays = getTotalClaimDays(username);

        boolean canClaimToday;
        int claimStreakIfClaimToday;

        if (lastClaimDate == null) {
            canClaimToday = true;
            claimStreakIfClaimToday = 1;
        } else {
            long daysBetween = ChronoUnit.DAYS.between(lastClaimDate, today);

            if (daysBetween <= 0) {
                canClaimToday = false;
                claimStreakIfClaimToday = Math.max(currentStreak, 1);
            } else if (daysBetween == 1) {
                canClaimToday = true;
                claimStreakIfClaimToday = currentStreak + 1;
            } else {
                canClaimToday = true;
                claimStreakIfClaimToday = 1;
            }
        }

        int cycleDay = toCycleDay(claimStreakIfClaimToday);
        int reward = DAILY_REWARDS[cycleDay - 1];
        boolean claimedToday = lastClaimDate != null && lastClaimDate.equals(today);

        String message = canClaimToday
                ? "Hôm nay bạn có thể nhận thưởng ngày " + cycleDay + "."
                : "Bạn đã điểm danh hôm nay rồi.";

        return new CheckInInfo(
                canClaimToday,
                claimedToday,
                currentStreak,
                claimStreakIfClaimToday,
                cycleDay,
                reward,
                totalClaimDays,
                lastClaimDate,
                message
        );
    }

    private int toCycleDay(int streak) {
        if (streak <= 0) {
            return 1;
        }
        return ((streak - 1) % DAILY_REWARDS.length) + 1;
    }

    private String getCurrentUsername() {
        String username = UserSession.getUsername();
        if (username == null || username.isBlank()) {
            username = game.getPlayerProfile().getPlayerName();
        }
        return username == null ? "" : username.trim();
    }

    private String key(String username, String suffix) {
        return "dailyCheckIn." + username + "." + suffix;
    }

    private LocalDate getLastClaimDate(String username) {
        String value = prefs.get(key(username, "lastClaimDate"), "");
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return LocalDate.parse(value);
        } catch (Exception e) {
            return null;
        }
    }

    private void setLastClaimDate(String username, LocalDate date) {
        prefs.put(key(username, "lastClaimDate"), date.toString());
    }

    private int getStreak(String username) {
        return prefs.getInt(key(username, "streak"), 0);
    }

    private void setStreak(String username, int streak) {
        prefs.putInt(key(username, "streak"), streak);
    }

    private int getTotalClaimDays(String username) {
        return prefs.getInt(key(username, "totalClaimDays"), 0);
    }

    private void setTotalClaimDays(String username, int total) {
        prefs.putInt(key(username, "totalClaimDays"), total);
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().setStyle("""
            -fx-background-color:
                linear-gradient(to bottom, #06101e, #09192c);
            -fx-font-family: "Segoe UI", "Inter", "Arial";
        """);

        Button ok = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
        if (ok != null) {
            ok.setText("OK");
            ok.setStyle("""
                -fx-background-color:
                    linear-gradient(to bottom, #19a8ff, #0d73d8);
                -fx-text-fill: white;
                -fx-font-weight: 900;
                -fx-background-radius: 12;
                -fx-padding: 8 18 8 18;
            """);
        }

        alert.showAndWait();
    }

    private void logout() {
        try {
            game.saveProgress();
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

    private static class CheckInInfo {
        private final boolean canClaimToday;
        private final boolean claimedToday;
        private final int currentStreak;
        private final int claimStreakIfClaimToday;
        private final int cycleDay;
        private final int todayReward;
        private final int totalClaimDays;
        private final LocalDate lastClaimDate;
        private final String message;

        private CheckInInfo(boolean canClaimToday,
                            boolean claimedToday,
                            int currentStreak,
                            int claimStreakIfClaimToday,
                            int cycleDay,
                            int todayReward,
                            int totalClaimDays,
                            LocalDate lastClaimDate,
                            String message) {
            this.canClaimToday = canClaimToday;
            this.claimedToday = claimedToday;
            this.currentStreak = currentStreak;
            this.claimStreakIfClaimToday = claimStreakIfClaimToday;
            this.cycleDay = cycleDay;
            this.todayReward = todayReward;
            this.totalClaimDays = totalClaimDays;
            this.lastClaimDate = lastClaimDate;
            this.message = message;
        }
    }
}