package com.carracinggame.scene;

import com.carracinggame.car.CarId;
import com.carracinggame.core.Game;
import com.carracinggame.core.GameState;
import com.carracinggame.core.UserSession;
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

import java.io.InputStream;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.prefs.Preferences;

public class MenuScene implements AppScene {

    private static final int[] DAILY_REWARDS = {100, 150, 200, 250, 300, 400, 500};

    private final Game game;
    private final Scene scene;

    private final Label coinValue;
    private final Label playerNameValue;
    private final Label equippedCarValue;
    private final Label selectedMapValue;

    private final StackPane carHolder;
    private final StackPane characterHolder;

    private final Preferences prefs = Preferences.userNodeForPackage(MenuScene.class);

    public MenuScene(Game game) {
        this.game = game;

        this.coinValue = new Label("0");
        this.playerNameValue = new Label("Player");
        this.equippedCarValue = new Label();
        this.selectedMapValue = new Label();

        this.carHolder = new StackPane();
        this.characterHolder = new StackPane();

        BorderPane root = new BorderPane();
        root.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        root.setStyle("""
            -fx-background-color:
                radial-gradient(center 18% 10%, radius 36%, rgba(255,255,255,0.30), transparent 60%),
                radial-gradient(center 84% 12%, radius 28%, rgba(255,255,255,0.14), transparent 60%),
                linear-gradient(to bottom, #8fcfff 0%, #b9e4ff 46%, #dff3ff 100%);
        """);

        root.setTop(buildTopBar());
        root.setCenter(buildCenterArea());
        root.setRight(buildRightActions());
        root.setBottom(buildBottomNav());

        this.scene = new Scene(root);

        root.prefWidthProperty().bind(scene.widthProperty());
        root.prefHeightProperty().bind(scene.heightProperty());

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
        card.setPadding(new Insets(16, 18, 16, 18));
        card.setStyle("""
            -fx-background-color: rgba(255,255,255,0.52);
            -fx-background-radius: 26;
            -fx-border-color: rgba(255,255,255,0.76);
            -fx-border-radius: 26;
        """);

        Circle avatar = new Circle(34, Color.web("#5b7cf0"));

        VBox info = new VBox(4);

        playerNameValue.setStyle("""
            -fx-font-size: 17px;
            -fx-font-weight: 900;
            -fx-text-fill: #0d2b4f;
        """);

        Label sub = new Label("Menu chính");
        sub.setStyle("""
            -fx-font-size: 12px;
            -fx-text-fill: #28517c;
        """);

        info.getChildren().addAll(playerNameValue, sub);
        card.getChildren().addAll(avatar, info);

        return card;
    }

    private Node buildCoinBar() {
        HBox shell = new HBox();
        shell.setAlignment(Pos.CENTER_RIGHT);
        shell.setPadding(new Insets(12, 14, 12, 14));
        shell.setStyle("""
            -fx-background-color: rgba(255,255,255,0.52);
            -fx-background-radius: 26;
            -fx-border-color: rgba(255,255,255,0.76);
            -fx-border-radius: 26;
        """);

        HBox coinPill = new HBox(8);
        coinPill.setAlignment(Pos.CENTER_LEFT);
        coinPill.setPadding(new Insets(12, 16, 12, 16));
        coinPill.setStyle("""
            -fx-background-color: rgba(0,0,0,0.08);
            -fx-background-radius: 18;
        """);

        Label icon = new Label("COIN");
        icon.setStyle("""
            -fx-font-size: 11px;
            -fx-font-weight: 900;
            -fx-text-fill: #0d2b4f;
        """);

        Label label = new Label("Số dư:");
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
        center.setPadding(new Insets(8, 26, 18, 26));

        VBox showroomCard = new VBox(18);
        showroomCard.setAlignment(Pos.TOP_CENTER);
        showroomCard.setPadding(new Insets(20, 22, 20, 22));
        showroomCard.setMaxWidth(920);
        showroomCard.setStyle("""
            -fx-background-color: rgba(255,255,255,0.28);
            -fx-background-radius: 34;
            -fx-border-color: rgba(255,255,255,0.36);
            -fx-border-radius: 34;
        """);

        Label title = new Label("SHOWROOM");
        title.setStyle("""
            -fx-font-size: 28px;
            -fx-font-weight: 900;
            -fx-text-fill: #123a63;
        """);

        Label subtitle = new Label("Xe đang chọn trong Garage sẽ hiển thị tại đây");
        subtitle.setStyle("""
            -fx-font-size: 13px;
            -fx-font-weight: 700;
            -fx-text-fill: #3c6794;
        """);

        HBox displayRow = new HBox(22);
        displayRow.setAlignment(Pos.CENTER);

        VBox carBox = buildDisplayBox("XE ĐUA", carHolder, 320, 290);
        VBox racerBox = buildDisplayBox("RACER", characterHolder, 300, 290);

        displayRow.getChildren().addAll(carBox, racerBox);

        VBox infoBox = new VBox(10, equippedCarValue, selectedMapValue);
        infoBox.setAlignment(Pos.CENTER);
        infoBox.setPadding(new Insets(16, 18, 16, 18));
        infoBox.setMaxWidth(460);
        infoBox.setStyle("""
            -fx-background-color: rgba(255,255,255,0.45);
            -fx-background-radius: 24;
            -fx-border-color: rgba(255,255,255,0.42);
            -fx-border-radius: 24;
        """);

        equippedCarValue.setStyle("""
            -fx-font-size: 20px;
            -fx-font-weight: 900;
            -fx-text-fill: #0d2b4f;
        """);

        selectedMapValue.setStyle("""
            -fx-font-size: 14px;
            -fx-font-weight: 800;
            -fx-text-fill: #28517c;
        """);

        showroomCard.getChildren().addAll(title, subtitle, displayRow, infoBox);
        center.getChildren().add(showroomCard);

        return center;
    }

    private VBox buildDisplayBox(String title, StackPane holder, double width, double height) {
        Label boxTitle = new Label(title);
        boxTitle.setStyle("""
            -fx-font-size: 20px;
            -fx-font-weight: 900;
            -fx-text-fill: #123a63;
        """);

        StackPane frame = new StackPane();
        frame.setPrefSize(width, height);
        frame.setMinSize(width, height);
        frame.setMaxSize(width, height);
        frame.setStyle("""
            -fx-background-color: rgba(255,255,255,0.18);
            -fx-background-radius: 30;
            -fx-border-color: rgba(255,255,255,0.22);
            -fx-border-radius: 30;
        """);

        Circle glow = new Circle(width * 0.22, Color.rgb(255, 255, 255, 0.12));
        glow.setTranslateY(-60);

        Rectangle baseShadow = new Rectangle(width * 0.46, 22);
        baseShadow.setArcWidth(20);
        baseShadow.setArcHeight(20);
        baseShadow.setFill(Color.rgb(0, 0, 0, 0.10));
        baseShadow.setTranslateY(height * 0.31);

        holder.setPrefSize(width - 40, height - 40);
        holder.setMinSize(width - 40, height - 40);
        holder.setMaxSize(width - 40, height - 40);

        frame.getChildren().addAll(glow, baseShadow, holder);

        VBox box = new VBox(10, boxTitle, frame);
        box.setAlignment(Pos.TOP_CENTER);
        return box;
    }

    private Node buildRightActions() {
        VBox actions = new VBox(16);
        actions.setPadding(new Insets(88, 20, 18, 14));
        actions.setAlignment(Pos.TOP_RIGHT);

        Button btnStart = bigAction("🏁  Start Race", "#41c784");
        Button btnShop = bigAction("🛒  Shop", "#4693e2");
        Button btnGarage = bigAction("🚗  Garage", "#fb8f3f");
        Button btnBonus = bigAction("🪙  Điểm danh", "#9660ea");
        Button btnLogout = bigAction("⎋  Đăng xuất", "#fb6666");

        btnStart.setOnAction(e -> game.switchState(GameState.MAP_SELECT));
        btnShop.setOnAction(e -> game.switchState(GameState.SHOP));
        btnGarage.setOnAction(e -> game.switchState(GameState.GARAGE));
        btnBonus.setOnAction(e -> openDailyCheckInDialog());
        btnLogout.setOnAction(e -> logout());

        actions.getChildren().addAll(btnStart, btnShop, btnGarage, btnBonus, btnLogout);
        return actions;
    }

    private Node buildBottomNav() {
        HBox bottom = new HBox(10);
        bottom.setPadding(new Insets(10, 14, 14, 14));
        bottom.setAlignment(Pos.CENTER_LEFT);
        bottom.setStyle("""
            -fx-background-color: rgba(255,255,255,0.54);
            -fx-background-radius: 22;
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
        b.setMinWidth(200);
        b.setMinHeight(58);
        b.setStyle("""
            -fx-background-radius: 22;
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
            -fx-background-color: rgba(255,255,255,0.80);
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

        characterHolder.getChildren().add(buildLobbyCharacter());
    }

    private Node buildLobbyCharacter() {
        InputStream stream = getClass().getResourceAsStream("/images/cars/ui/racer_chibi.png");

        if (stream == null) {
            Label fallback = new Label("Chưa có ảnh racer");
            fallback.setStyle("""
                -fx-font-size: 15px;
                -fx-font-weight: 800;
                -fx-text-fill: #204a73;
            """);

            StackPane wrapper = new StackPane(fallback);
            wrapper.setPrefSize(220, 280);
            wrapper.setMinSize(220, 280);
            wrapper.setMaxSize(220, 280);
            return wrapper;
        }

        Image image = new Image(stream);
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(220);
        imageView.setFitHeight(260);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        StackPane wrapper = new StackPane(imageView);
        wrapper.setPrefSize(220, 280);
        wrapper.setMinSize(220, 280);
        wrapper.setMaxSize(220, 280);

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

    // ==========================
    // DAILY CHECK-IN
    // ==========================

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
        dialog.getDialogPane().setPrefSize(860, 640);
        dialog.getDialogPane().setStyle("""
            -fx-background-color: linear-gradient(to bottom, #f8fbff 0%, #edf4fb 100%);
            -fx-border-color: #d9e6f2;
            -fx-border-width: 1;
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
                -fx-background-color: white;
                -fx-text-fill: #24476b;
                -fx-font-size: 13px;
                -fx-font-weight: 800;
                -fx-background-radius: 14;
                -fx-border-color: #c9d8e6;
                -fx-border-radius: 14;
                -fx-border-width: 1.2;
            """);
        }

        if (info.canClaimToday) {
            Button claimBtn = (Button) dialog.getDialogPane().lookupButton(claimType);
            if (claimBtn != null) {
                claimBtn.setMinHeight(42);
                claimBtn.setMinWidth(190);
                claimBtn.setStyle("""
                    -fx-background-color: linear-gradient(to bottom, #1ea7ff 0%, #0d8df2 100%);
                    -fx-text-fill: white;
                    -fx-font-size: 13px;
                    -fx-font-weight: 900;
                    -fx-background-radius: 14;
                    -fx-border-radius: 14;
                    -fx-effect: dropshadow(gaussian, rgba(13,141,242,0.28), 14, 0, 0, 5);
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
                radial-gradient(center 20% 20%, radius 60%, rgba(255,255,255,0.20), transparent 60%),
                linear-gradient(to right, #0f4c8a 0%, #1769b2 55%, #1f86d6 100%);
            -fx-background-radius: 24;
            -fx-effect: dropshadow(gaussian, rgba(19,74,129,0.20), 18, 0, 0, 6);
        """);

        Label title = new Label("ĐIỂM DANH HẰNG NGÀY");
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

        VBox stat1 = createCheckInStatCard("Chuỗi hiện tại", info.currentStreak + " ngày", "#1d7df2");
        VBox stat2 = createCheckInStatCard("Tổng số ngày", String.valueOf(info.totalClaimDays), "#16a085");
        VBox stat3 = createCheckInStatCard("Thưởng hôm nay", info.todayReward + " coin", "#f39c12");

        statsRow.getChildren().addAll(stat1, stat2, stat3);

        HBox sectionHeader = new HBox(14);
        sectionHeader.setAlignment(Pos.CENTER_LEFT);

        Label sectionTitle = new Label("Lịch thưởng 7 ngày");
        sectionTitle.setStyle("""
            -fx-font-size: 18px;
            -fx-font-weight: 900;
            -fx-text-fill: #143a63;
        """);

        Region line = new Region();
        line.setPrefHeight(2);
        line.setMaxWidth(Double.MAX_VALUE);
        line.setStyle("""
            -fx-background-color: linear-gradient(to right, #d8e6f3, transparent);
        """);
        HBox.setHgrow(line, Priority.ALWAYS);

        sectionHeader.getChildren().addAll(sectionTitle, line);

        GridPane grid = buildCheckInGrid(info);

        Label note = new Label("Lưu ý: Nếu bỏ lỡ quá 1 ngày, chuỗi điểm danh sẽ quay về ngày 1.");
        note.setWrapText(true);
        note.setStyle("""
            -fx-font-size: 12px;
            -fx-font-weight: 700;
            -fx-text-fill: #5c7390;
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
        card.setPrefWidth(245);
        card.setMinHeight(92);
        card.setStyle("""
            -fx-background-color: white;
            -fx-background-radius: 20;
            -fx-border-color: #dbe7f2;
            -fx-border-width: 1;
            -fx-border-radius: 20;
            -fx-effect: dropshadow(gaussian, rgba(31,73,125,0.08), 14, 0, 0, 4);
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
            -fx-text-fill: #6c849d;
        """);

        Label valueLabel = new Label(value);
        valueLabel.setStyle("""
            -fx-font-size: 22px;
            -fx-font-weight: 900;
            -fx-text-fill: #10365e;
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
                    radial-gradient(center 20% 15%, radius 70%, rgba(255,255,255,0.16), transparent 60%),
                    linear-gradient(to bottom right, #1687ff 0%, #0b6ed6 100%);
                -fx-background-radius: 22;
                -fx-border-color: #7fc0ff;
                -fx-border-width: 1.5;
                -fx-border-radius: 22;
                -fx-effect: dropshadow(gaussian, rgba(22,135,255,0.24), 18, 0, 0, 6);
            """;
            badgeText = "HÔM NAY";
            badgeStyle = """
                -fx-background-color: rgba(255,255,255,0.20);
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
                -fx-border-color: #78e2ac;
                -fx-border-width: 1.5;
                -fx-border-radius: 22;
                -fx-effect: dropshadow(gaussian, rgba(15,157,88,0.20), 18, 0, 0, 5);
            """;
            badgeText = "ĐÃ NHẬN";
            badgeStyle = """
                -fx-background-color: rgba(255,255,255,0.20);
                -fx-text-fill: white;
                -fx-font-size: 11px;
                -fx-font-weight: 900;
                -fx-background-radius: 999;
                -fx-padding: 4 10 4 10;
            """;
        } else if (isCompleted) {
            cardStyle = """
                -fx-background-color:
                    linear-gradient(to bottom, #f2fbf6 0%, #e4f8ec 100%);
                -fx-background-radius: 22;
                -fx-border-color: #b8e6c9;
                -fx-border-width: 1.2;
                -fx-border-radius: 22;
            """;
            badgeText = "HOÀN THÀNH";
            badgeStyle = """
                -fx-background-color: #dff4e7;
                -fx-text-fill: #14834f;
                -fx-font-size: 11px;
                -fx-font-weight: 900;
                -fx-background-radius: 999;
                -fx-padding: 4 10 4 10;
            """;
        } else {
            cardStyle = """
                -fx-background-color:
                    linear-gradient(to bottom, #ffffff 0%, #f8fbff 100%);
                -fx-background-radius: 22;
                -fx-border-color: #d9e5ef;
                -fx-border-width: 1.2;
                -fx-border-radius: 22;
                -fx-effect: dropshadow(gaussian, rgba(31,73,125,0.05), 12, 0, 0, 3);
            """;
            badgeText = "SẮP MỞ";
            badgeStyle = """
                -fx-background-color: #eef4fa;
                -fx-text-fill: #6f86a0;
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
                  -fx-text-fill: #123a63;
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
                  -fx-text-fill: #7890a8;
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
                  -fx-text-fill: #113761;
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
                  -fx-text-fill: #6f86a0;
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
            -fx-background-color: linear-gradient(to bottom, #f8fbff, #eef4fb);
            -fx-font-family: "Segoe UI", "Inter", "Arial";
        """);

        Button ok = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
        if (ok != null) {
            ok.setText("OK");
            ok.setStyle("""
                -fx-background-color: linear-gradient(to bottom, #1ea7ff, #0d8df2);
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
        @SuppressWarnings("unused")
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