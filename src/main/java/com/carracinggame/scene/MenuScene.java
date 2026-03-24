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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
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

public class MenuScene implements AppScene {

    private final Game game;
    private final Scene scene;

    private final Label coinValue;
    private final Label playerNameValue;
    private final Label equippedCarValue;
    private final Label selectedMapValue;

    private final StackPane carHolder;
    private final StackPane characterHolder;

    public MenuScene(Game game) {
        this.game = game;

        this.coinValue = new Label("0");
        this.playerNameValue = new Label("Player");
        this.equippedCarValue = new Label();
        this.selectedMapValue = new Label();

        this.carHolder = new StackPane();
        this.characterHolder = new StackPane();

        BorderPane root = new BorderPane();
        root.setPrefSize(GameConfig.WIDTH, GameConfig.HEIGHT);
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
        Button btnBonus = bigAction("🪙  +500 Coin", "#9660ea");
        Button btnLogout = bigAction("⎋  Đăng xuất", "#fb6666");

        btnStart.setOnAction(e -> game.switchState(GameState.MAP_SELECT));
        btnShop.setOnAction(e -> game.switchState(GameState.SHOP));
        btnGarage.setOnAction(e -> game.switchState(GameState.GARAGE));
        btnBonus.setOnAction(e -> {
            game.getPlayerProfile().addCoins(500);
            game.saveProgress();
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
}