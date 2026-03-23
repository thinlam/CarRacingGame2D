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

import java.util.EnumMap;
import java.util.Map;

public class ShopScene implements AppScene {

    private final Game game;
    private final PlayerProfile profile;
    private final Scene scene;
    private GarageCar selectedCar;

    private final Map<GarageCar, VBox> carCards = new EnumMap<>(GarageCar.class);

    private Label coinsLabel;
    private Label ownedCountLabel;
    private Label selectedName;
    private Label selectedSub;
    private Label selectedPrice;
    private Label ownership;
    private Label infoLine;
    private ImageView preview;
    private Button buyButton;
    private Button equipButton;

    private static final String FONT_FAMILY = "-fx-font-family: 'Inter', 'Segoe UI', 'Poppins', sans-serif;";

    public ShopScene(Game game) {
        this.game = game;
        this.profile = game.getPlayerProfile();
        this.selectedCar = GarageCar.values()[0];

        BorderPane root = new BorderPane();
        root.setPrefSize(GameConfig.WIDTH, GameConfig.HEIGHT);
        root.setStyle("""
            -fx-background-color:
                radial-gradient(center 12% 16%, radius 40%, rgba(255,152,68,0.08), transparent 60%),
                radial-gradient(center 86% 18%, radius 35%, rgba(0,180,255,0.08), transparent 65%),
                linear-gradient(to bottom, #050b16 0%, #0a1324 52%, #07101d 100%);
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
        refreshView();
        scene.getRoot().requestFocus();
    }

    private Node buildHeader() {
        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(6);
        titleBox.getChildren().addAll(
                label("SHOP", 28, true, "#ffffff"),
                label("Mua xe mới, nâng cấp bộ sưu tập", 12, false, "#9bb4d0")
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        coinsLabel = label("", 14, true, "#ffffff");
        ownedCountLabel = label("", 14, true, "#ffffff");
        header.getChildren().addAll(titleBox, spacer, pill("COINS", coinsLabel), pill("OWNED", ownedCountLabel));
        return header;
    }

    private Node buildMainContent() {
        HBox main = new HBox(24);
        main.setAlignment(Pos.CENTER_LEFT);

        VBox listPanel = panelCard();
        listPanel.setPrefWidth(270);
        listPanel.getChildren().add(label("CATALOG", 14, true, "#dff8ff"));
        for (GarageCar car : GarageCar.values()) {
            VBox card = catalogCard(car);
            carCards.put(car, card);
            listPanel.getChildren().add(card);
        }

        StackPane previewPanel = new StackPane();
        previewPanel.setPrefSize(260, 370);
        previewPanel.setStyle("""
            -fx-background-color: linear-gradient(to bottom, rgba(22,31,49,0.96), rgba(10,16,29,0.96));
            -fx-background-radius: 28;
            -fx-border-radius: 28;
            -fx-border-color: rgba(255,184,120,0.25);
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 20, 0, 0, 8);
        """);
        preview = new ImageView();
        preview.setFitHeight(200);
        preview.setPreserveRatio(true);
        preview.setRotate(-5);
        previewPanel.getChildren().add(preview);

        VBox detailPanel = panelCard();
        detailPanel.setPrefWidth(280);
        selectedName = label("", 26, true, "#ffffff");
        selectedSub = label("", 12, false, "#9ab5d4");
        selectedPrice = label("", 14, true, "#ffcf97");
        ownership = label("", 11, true, "#ffffff");
        ownership.setPadding(new Insets(6, 12, 6, 12));
        ownership.setStyle("-fx-background-color: rgba(255,255,255,0.08); -fx-background-radius: 999;");
        infoLine = label("", 12, false, "#dff8ff");

        buyButton = primaryButton("MUA XE", "#ff8d4d", e -> {
            profile.buyCar(selectedCar);
            refreshView();
        });
        equipButton = primaryButton("TRANG BỊ", "#2fd08b", e -> {
            profile.equipCar(selectedCar);
            refreshView();
        });

        detailPanel.getChildren().addAll(
                selectedName,
                selectedSub,
                selectedPrice,
                ownership,
                infoLine,
                statLine("⚡ Power", selectedCar.getPower()),
                statLine("🔄 Handling", selectedCar.getHandling()),
                statLine("🚀 Boost", selectedCar.getBoost()),
                buyButton,
                equipButton
        );

        main.getChildren().addAll(listPanel, previewPanel, detailPanel);
        return main;
    }

    private VBox catalogCard(GarageCar car) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(12, 14, 12, 14));
        card.setStyle(cardStyle(false));
        card.getChildren().addAll(
                label(car.getDisplayName(), 14, true, "#ffffff"),
                label(car.getPriceText(), 11, true, "#ffd29b"),
                label(car.getSubtitle(), 11, false, "#9ab5d4")
        );
        card.setOnMouseClicked(e -> {
            selectedCar = car;
            refreshView();
        });
        return card;
    }

    private Node buildFooter() {
        HBox footer = new HBox(12);
        footer.setAlignment(Pos.CENTER_LEFT);
        footer.getChildren().addAll(
                navButton("MENU", false, e -> game.switchState(GameState.MENU)),
                navButton("GARAGE", false, e -> game.switchState(GameState.GARAGE)),
                navButton("SHOP", true, e -> game.switchState(GameState.SHOP)),
                navButton("MAP", false, e -> game.switchState(GameState.MAP_SELECT)),
                new Region(),
                label("Mua xe ở đây, sau đó qua Garage để xem danh sách xe đã sở hữu.", 11, false, "#8aa3c2")
        );
        HBox.setHgrow(footer.getChildren().get(4), Priority.ALWAYS);
        return footer;
    }

    private void refreshView() {
        coinsLabel.setText(String.format("%,d", profile.getCoins()));
        ownedCountLabel.setText(String.valueOf(profile.getOwnedCarCount()));
        selectedName.setText(selectedCar.getDisplayName());
        selectedSub.setText(selectedCar.getSubtitle());
        selectedPrice.setText("💰 Giá: " + selectedCar.getPriceText());
        ownership.setText(profile.ownsCar(selectedCar) ? "✓ Đã sở hữu" : "✗ Chưa sở hữu");
        infoLine.setText(selectedCar.getStatLine() + "  •  Reward +" + selectedCar.getBaseRewardBonus());
        preview.setImage(loadImage(selectedCar.getImagePath()));

        for (GarageCar car : GarageCar.values()) {
            VBox card = carCards.get(car);
            if (card != null) {
                card.setStyle(cardStyle(car == selectedCar));
            }
        }

        boolean owned = profile.ownsCar(selectedCar);
        buyButton.setDisable(owned || !profile.canBuy(selectedCar));
        if (owned) {
            buyButton.setText("ĐÃ MUA");
        } else if (!profile.canBuy(selectedCar)) {
            buyButton.setText("KHÔNG ĐỦ COIN");
        } else {
            buyButton.setText("MUA XE");
        }

        boolean equipped = profile.getEquippedCar() == selectedCar;
        equipButton.setDisable(!owned || equipped);
        equipButton.setText(equipped ? "ĐANG TRANG BỊ" : "TRANG BỊ");
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

    private HBox statLine(String title, int value) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12, 14, 12, 14));
        row.setStyle("-fx-background-color: rgba(255,255,255,0.05); -fx-background-radius: 20;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        row.getChildren().addAll(label(title, 13, true, "#f2fbff"), spacer, label(String.valueOf(value), 13, true, "#ffffff"));
        return row;
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

    private String cardStyle(boolean active) {
        String bg = active ? "linear-gradient(to right, rgba(255,141,77,0.25), rgba(255,207,151,0.15))" : "rgba(255,255,255,0.05)";
        String border = active ? "rgba(255,205,150,0.6)" : "rgba(255,255,255,0.12)";
        return """
            -fx-background-color: %s;
            -fx-background-radius: 20;
            -fx-border-radius: 20;
            -fx-border-color: %s;
            -fx-cursor: hand;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 0, 1);
        """.formatted(bg, border);
    }

    private Button primaryButton(String text, String color, javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        Button button = new Button(text);
        button.setMinWidth(240);
        button.setMinHeight(50);
        button.setOnAction(handler);
        button.setStyle("""
            -fx-background-radius: 24;
            -fx-font-size: 14px;
            -fx-font-weight: 800;
            -fx-text-fill: white;
            -fx-background-color: %s;
            -fx-cursor: hand;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 2);
        """.formatted(color));
        button.setOnMouseEntered(e -> button.setStyle(button.getStyle().replace(color, color + "aa")));
        button.setOnMouseExited(e -> button.setStyle(button.getStyle().replace(color + "aa", color)));
        return button;
    }

    private Button navButton(String text, boolean active, javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        String bg = active ? "linear-gradient(to right, #ff8d4d, #ffc27a)" : "rgba(255,255,255,0.08)";
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