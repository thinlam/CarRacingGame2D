package com.carracinggame.scene;

import com.carracinggame.core.Game;
import com.carracinggame.core.GameConfig;
import com.carracinggame.core.GameState;
import com.carracinggame.core.GarageCar;
import com.carracinggame.core.PlayerProfile;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

import java.util.EnumMap;
import java.util.Map;

public class GarageScene implements AppScene {

    private final Game game;
    private final PlayerProfile profile;
    private final Scene scene;

    private GarageCar selectedCar;
    private final Map<GarageCar, VBox> carCards = new EnumMap<>(GarageCar.class);

    private Label selectedName;
    private Label selectedSub;
    private Label ownedState;
    private Label powerValue;
    private Label handlingValue;
    private Label boostValue;
    private Label rewardValue;
    private Label garageCoins;
    private Label equippedText;
    private Button actionButton;
    private ImageView preview;

    // CSS base style cho toàn bộ ứng dụng
    private static final String FONT_FAMILY = "-fx-font-family: 'Inter', 'Segoe UI', 'Poppins', sans-serif;";
    private static final String BASE_STYLE = FONT_FAMILY +
            "-fx-background-radius: 18; -fx-border-radius: 18;";

    public GarageScene(Game game) {
        this.game = game;
        this.profile = game.getPlayerProfile();
        this.selectedCar = profile.getEquippedCar();

        BorderPane root = new BorderPane();
        root.setPrefSize(GameConfig.WIDTH, GameConfig.HEIGHT);
        root.setStyle("""
            -fx-background-color:
                radial-gradient(center 20% 15%, radius 40%, rgba(0,180,255,0.12), transparent 65%),
                radial-gradient(center 88% 25%, radius 35%, rgba(143,104,255,0.12), transparent 70%),
                linear-gradient(to bottom, #050b16 0%, #0a1324 55%, #07101d 100%);
        """);

        VBox page = new VBox(20);
        page.setPadding(new Insets(24, 28, 24, 28));
        page.getChildren().addAll(buildHeader(), buildMainContent(), buildFooter());

        root.setCenter(page);
        this.scene = new Scene(root, GameConfig.WIDTH, GameConfig.HEIGHT);
        refreshView();
    }

    @Override
    public Scene getScene() {
        return scene;
    }

    @Override
    public void onShow() {
        profile.recordGarageVisit();
        refreshView();
        scene.getRoot().requestFocus();
    }

    private Node buildHeader() {
        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(6);
        titleBox.getChildren().addAll(
                label("GARAGE", 28, true, "#ffffff"),
                label("Chọn xe, xem thông số và trang bị ngay", 12, false, "#9bb4d0")
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        garageCoins = label("", 14, true, "#ffffff");
        equippedText = label("", 14, true, "#ffffff");
        header.getChildren().addAll(titleBox, spacer, pill("COINS", garageCoins), pill("EQUIPPED", equippedText));
        return header;
    }

    private Node buildMainContent() {
        HBox main = new HBox(24);
        main.setAlignment(Pos.CENTER_LEFT);

        VBox listPanel = panelCard();
        listPanel.setPrefWidth(260);
        listPanel.getChildren().add(label("DANH SÁCH XE", 14, true, "#dff8ff"));
        for (GarageCar car : GarageCar.values()) {
            VBox card = buildCarCard(car);
            carCards.put(car, card);
            listPanel.getChildren().add(card);
        }

        StackPane previewPanel = new StackPane();
        previewPanel.setPrefSize(280, 380);
        previewPanel.setStyle("""
            -fx-background-color: linear-gradient(to bottom, rgba(12,24,46,0.96), rgba(8,15,30,0.96));
            -fx-background-radius: 28;
            -fx-border-radius: 28;
            -fx-border-color: rgba(91,210,255,0.25);
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 20, 0, 0, 8);
        """);

        preview = new ImageView();
        preview.setFitHeight(200);
        preview.setPreserveRatio(true);
        preview.setRotate(-5);
        StackPane.setAlignment(preview, Pos.CENTER);

        Label ready = label("SELECTED CAR", 10, true, "#e6fbff");
        ready.setPadding(new Insets(6, 14, 6, 14));
        ready.setStyle("-fx-background-color: rgba(0,190,255,0.2); -fx-background-radius: 999;");
        StackPane.setAlignment(ready, Pos.BOTTOM_CENTER);
        ready.setTranslateY(-20);

        previewPanel.getChildren().addAll(preview, ready);

        VBox detailPanel = panelCard();
        detailPanel.setPrefWidth(260);
        selectedName = label("", 26, true, "#ffffff");
        selectedSub = label("", 12, false, "#8ea9c8");
        ownedState = label("", 11, true, "#e0fbff");
        ownedState.setPadding(new Insets(6, 12, 6, 12));
        ownedState.setStyle("-fx-background-color: rgba(255,255,255,0.08); -fx-background-radius: 999;");

        powerValue = label("", 13, true, "#ffffff");
        handlingValue = label("", 13, true, "#ffffff");
        boostValue = label("", 13, true, "#ffffff");
        rewardValue = label("", 13, true, "#7bd9a8");

        actionButton = createActionButton();

        Button goShop = secondaryButton("🛒 QUA SHOP", e -> game.switchState(GameState.SHOP));
        Button play = secondaryButton("▶ CHỌN MAP", e -> game.switchState(GameState.MAP_SELECT));

        detailPanel.getChildren().addAll(
                selectedName,
                selectedSub,
                ownedState,
                statRow("⚡ Power", powerValue),
                statRow("🔄 Handling", handlingValue),
                statRow("🚀 Boost", boostValue),
                statRow("💰 Reward", rewardValue),
                actionButton,
                goShop,
                play
        );

        main.getChildren().addAll(listPanel, previewPanel, detailPanel);
        return main;
    }

    private VBox buildCarCard(GarageCar car) {
        boolean owned = profile.ownsCar(car);
        boolean equipped = profile.getEquippedCar() == car;
        boolean selected = selectedCar == car;

        VBox card = new VBox(10);
        card.setAlignment(Pos.TOP_LEFT);
        card.setPadding(new Insets(16));
        card.setPrefWidth(280);
        card.setMinHeight(155);
        card.setStyle(baseCardStyle(selected));

        Label title = label(car.getDisplayName(), 16, true, "#ffffff");
        Label subtitle = label(car.getSubtitle(), 11, false, "#9ab5d4");
        Label stats = label(car.getStatLine(), 11, true, "#cdeeff");

        Label statusBadge = new Label(owned ? "✓ Đã sở hữu" : "🛒 Chưa mua");
        statusBadge.setStyle("""
        -fx-background-color: %s;
        -fx-text-fill: %s;
        -fx-background-radius: 14;
        -fx-padding: 6 12 6 12;
        -fx-font-size: 11px;
        -fx-font-weight: 700;
    """.formatted(
                owned ? "rgba(36, 190, 120, 0.18)" : "rgba(255, 152, 82, 0.18)",
                owned ? "#7be4a0" : "#ffba7d"
        ));

        HBox badgeRow = new HBox(8);
        badgeRow.setAlignment(Pos.CENTER_LEFT);
        badgeRow.getChildren().add(statusBadge);

        if (equipped) {
            Label equippedBadge = new Label("Đang dùng");
            equippedBadge.setStyle("""
            -fx-background-color: rgba(67, 156, 255, 0.18);
            -fx-text-fill: #7fc2ff;
            -fx-background-radius: 14;
            -fx-padding: 6 12 6 12;
            -fx-font-size: 11px;
            -fx-font-weight: 700;
        """);
            badgeRow.getChildren().add(equippedBadge);
        }

        if (selected) {
            Label selectedBadge = new Label("Đã chọn");
            selectedBadge.setStyle("""
            -fx-background-color: rgba(170, 120, 255, 0.18);
            -fx-text-fill: #c9a7ff;
            -fx-background-radius: 14;
            -fx-padding: 6 12 6 12;
            -fx-font-size: 11px;
            -fx-font-weight: 700;
        """);
            badgeRow.getChildren().add(selectedBadge);
        }

        card.getChildren().addAll(title, subtitle, stats, badgeRow);

        card.setOnMouseClicked(e -> {
            selectedCar = car;
            refreshView();
        });

        card.setOnMouseEntered(e -> {
            if (selectedCar != car) {
                card.setStyle(baseCardStyle(true));
            }
        });

        card.setOnMouseExited(e -> {
            card.setStyle(baseCardStyle(selectedCar == car));
        });

        return card;
    }

    private Node buildFooter() {
        HBox footer = new HBox(12);
        footer.setAlignment(Pos.CENTER_LEFT);
        footer.getChildren().addAll(
                navButton("MENU", false, e -> game.switchState(GameState.MENU)),
                navButton("GARAGE", true, e -> game.switchState(GameState.GARAGE)),
                navButton("SHOP", false, e -> game.switchState(GameState.SHOP)),
                navButton("MAP", false, e -> game.switchState(GameState.MAP_SELECT)),
                new Region(),
                label("Garage: chỉ trang bị xe đã mua. Xe chưa sở hữu cần mua ở Shop.", 11, false, "#8aa3c2")
        );
        HBox.setHgrow(footer.getChildren().get(4), Priority.ALWAYS);
        return footer;
    }

    private void refreshView() {
        garageCoins.setText(String.format("%,d", profile.getCoins()));
        equippedText.setText(profile.getEquippedCar().getDisplayName());
        selectedName.setText(selectedCar.getDisplayName());
        selectedSub.setText(selectedCar.getSubtitle());
        ownedState.setText(profile.ownsCar(selectedCar) ? "Đã mở khóa" : "Chưa sở hữu • Mua trong Shop");
        powerValue.setText(String.valueOf(selectedCar.getPower()));
        handlingValue.setText(String.valueOf(selectedCar.getHandling()));
        boostValue.setText(String.valueOf(selectedCar.getBoost()));
        rewardValue.setText("+" + selectedCar.getBaseRewardBonus() + " coin");
        preview.setImage(loadImage(selectedCar.getImagePath()));

        for (GarageCar car : GarageCar.values()) {
            VBox card = carCards.get(car);
            if (card != null) {
                card.setStyle(baseCardStyle(car == selectedCar));
            }
        }

        if (!profile.ownsCar(selectedCar)) {
            actionButton.setDisable(true);
            actionButton.setText("CHƯA MỞ KHÓA");
        } else if (profile.getEquippedCar() == selectedCar) {
            actionButton.setDisable(true);
            actionButton.setText("ĐANG SỬ DỤNG");
        } else {
            actionButton.setDisable(false);
            actionButton.setText("TRANG BỊ XE NÀY");
        }
    }

    private Button createActionButton() {
        Button button = new Button("TRANG BỊ XE NÀY");
        button.setMinWidth(240);
        button.setMinHeight(50);
        button.setOnAction(e -> {
            profile.equipCar(selectedCar);
            refreshView();
        });
        button.setStyle("""
            -fx-background-radius: 20;
            -fx-font-size: 14px;
            -fx-font-weight: 800;
            -fx-text-fill: white;
            -fx-background-color: #2fd08b;
            -fx-cursor: hand;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 2);
        """);
        button.setOnMouseEntered(e -> button.setStyle(button.getStyle() + " -fx-background-color: #3ee0a0;"));
        button.setOnMouseExited(e -> button.setStyle(button.getStyle().replace("-fx-background-color: #3ee0a0;", "-fx-background-color: #2fd08b;")));
        return button;
    }

    private HBox statRow(String title, Label valueLabel) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12, 14, 12, 14));
        row.setStyle("-fx-background-color: rgba(255,255,255,0.05); -fx-background-radius: 20;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        row.getChildren().addAll(label(title, 13, true, "#f2fbff"), spacer, valueLabel);
        return row;
    }

    private HBox pill(String name, Label value) {
        HBox box = new HBox(8);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(8, 14, 8, 14));
        box.setStyle("""
            -fx-background-color: rgba(255,255,255,0.08);
            -fx-background-radius: 24;
            -fx-border-radius: 24;
            -fx-border-color: rgba(255,255,255,0.1);
        """);
        box.getChildren().addAll(label(name, 10, true, "#88a7c8"), value);
        return box;
    }

    private VBox panelCard() {
        VBox box = new VBox(12);
        box.setPadding(new Insets(18));
        box.setStyle("""
            -fx-background-color: linear-gradient(to bottom, rgba(13,28,52,0.96), rgba(9,16,32,0.96));
            -fx-background-radius: 24;
            -fx-border-radius: 24;
            -fx-border-color: rgba(255,255,255,0.1);
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 15, 0, 0, 4);
        """);
        return box;
    }

    private String baseCardStyle(boolean active) {
        String bg = active ? "linear-gradient(to right, rgba(24,183,255,0.28), rgba(96,139,255,0.22))" : "rgba(255,255,255,0.05)";
        String border = active ? "rgba(120,218,255,0.6)" : "rgba(255,255,255,0.12)";
        return """
            -fx-background-color: %s;
            -fx-background-radius: 20;
            -fx-border-radius: 20;
            -fx-border-color: %s;
            -fx-cursor: hand;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 0, 1);
        """.formatted(bg, border);
    }

    private Button secondaryButton(String text, javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        Button button = new Button(text);
        button.setMinWidth(240);
        button.setMinHeight(46);
        button.setOnAction(handler);
        button.setStyle("""
            -fx-background-radius: 20;
            -fx-font-size: 13px;
            -fx-font-weight: 800;
            -fx-text-fill: white;
            -fx-background-color: rgba(255,255,255,0.1);
            -fx-border-color: rgba(255,255,255,0.15);
            -fx-border-radius: 20;
            -fx-cursor: hand;
        """);
        button.setOnMouseEntered(e -> button.setStyle(button.getStyle().replace("rgba(255,255,255,0.1)", "rgba(255,255,255,0.2)")));
        button.setOnMouseExited(e -> button.setStyle(button.getStyle().replace("rgba(255,255,255,0.2)", "rgba(255,255,255,0.1)")));
        return button;
    }

    private Button navButton(String text, boolean active, javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        String bg = active ? "linear-gradient(to right, #18b7ff, #608bff)" : "rgba(255,255,255,0.08)";
        String border = active ? "rgba(255,255,255,0.0)" : "rgba(255,255,255,0.12)";
        Button button = new Button(text);
        button.setOnAction(handler);
        button.setStyle("""
            -fx-background-radius: 20;
            -fx-border-radius: 20;
            -fx-background-color: %s;
            -fx-border-color: %s;
            -fx-text-fill: white;
            -fx-font-weight: 800;
            -fx-padding: 10 20 10 20;
            -fx-cursor: hand;
        """.formatted(bg, border));
        return button;
    }

    private Image loadImage(String path) {
        return new Image(getClass().getResource(path).toExternalForm(), true);
    }

    private Label label(String text, int size, boolean bold, String color) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setStyle(FONT_FAMILY + "-fx-font-size: " + size + "px; -fx-font-weight: " + (bold ? "800" : "500") + "; -fx-text-fill: " + color + ";");
        return label;
    }
}