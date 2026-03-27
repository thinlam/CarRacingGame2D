package com.carracinggame.scene;

import com.carracinggame.car.CarDefinition;
import com.carracinggame.car.CarId;
import com.carracinggame.core.Game;
import com.carracinggame.core.GameConfig;
import com.carracinggame.core.GameState;
import com.carracinggame.garage.GarageService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.lang.reflect.Method;

public class GarageScene implements AppScene {

    private final Game game;
    private final GarageService garageService;
    private final Scene scene;

    private final Label messageLabel;
    private final VBox listContainer;

    public GarageScene(Game game) {
        this.game = game;
        this.garageService = game.getGarageService();

        Label title = new Label("GARA XE");
        title.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 32));
        title.setTextFill(Color.WHITE);

        messageLabel = new Label("Chọn xe đang dùng cho Race hoặc vào trang nâng cấp xe.");
        messageLabel.setTextFill(Color.web("#dbeafe"));
        messageLabel.setFont(Font.font(16));

        VBox topBox = new VBox(8, title, messageLabel);
        topBox.setPadding(new Insets(24));

        listContainer = new VBox(14);
        listContainer.setPadding(new Insets(16));

        ScrollPane scrollPane = new ScrollPane(listContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        Button backButton = new Button("Quay lại menu");
        backButton.setOnAction(event -> game.goTo(GameState.MENU));
        backButton.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        backButton.setTextFill(Color.WHITE);
        backButton.setStyle("-fx-background-color: #1f2937; -fx-background-radius: 14;");
        backButton.setPrefHeight(42);

        HBox bottomBox = new HBox(backButton);
        bottomBox.setAlignment(Pos.CENTER_LEFT);
        bottomBox.setPadding(new Insets(0, 0, 20, 24));

        BorderPane root = new BorderPane();
        root.setPrefSize(GameConfig.WIDTH, GameConfig.HEIGHT);
        root.setTop(topBox);
        root.setCenter(scrollPane);
        root.setBottom(bottomBox);
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #111827, #0f172a 55%, #0f766e);");

        this.scene = new Scene(root, GameConfig.WIDTH, GameConfig.HEIGHT);
        refresh();
    }

    private void refresh() {
        listContainer.getChildren().clear();

        String username = resolveUsername();

        for (CarDefinition car : garageService.getOwnedCarDefinitions()) {
            boolean equipped = garageService.getEquippedCar() != null
                    && garageService.getEquippedCar().getId() == car.getId();

            UpgradeScene.UpgradeData upgradeData =
                    UpgradeScene.getUpgradeData(username, car.getId());

            Node preview = createCarPreview(car.getId(), upgradeData);

            Label name = new Label(car.getName());
            name.setTextFill(Color.WHITE);
            name.setFont(Font.font("Arial", FontWeight.BOLD, 22));

            Label stats = new Label(car.getStats().toPrettyText());
            stats.setTextFill(Color.web("#e5e7eb"));

            Label status = new Label(equipped ? "Đang sử dụng" : "Đã sở hữu");
            status.setTextFill(equipped ? Color.web("#86efac") : Color.web("#cbd5e1"));
            status.setFont(Font.font("Arial", FontWeight.BOLD, 15));

            Label paintInfo = new Label("Màu sơn: " + upgradeData.getPaintName());
            paintInfo.setTextFill(Color.web("#93c5fd"));
            paintInfo.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));

            Label decalInfo = new Label("Decal: " + upgradeData.getDecalName());
            decalInfo.setTextFill(Color.web("#f9a8d4"));
            decalInfo.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));

            VBox infoBox = new VBox(6, name, stats, status, paintInfo, decalInfo);
            infoBox.setAlignment(Pos.CENTER_LEFT);

            Button upgradeButton = new Button("Nâng cấp xe");
            upgradeButton.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            upgradeButton.setTextFill(Color.WHITE);
            upgradeButton.setMinWidth(150);
            upgradeButton.setPrefHeight(42);
            upgradeButton.setStyle("-fx-background-color: #2563eb; -fx-background-radius: 14;");
            upgradeButton.setOnAction(event -> game.openUpgradeScene(car.getId()));

            Button actionButton = new Button(equipped ? "Đã chọn" : "Chọn xe");
            actionButton.setDisable(equipped);
            actionButton.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            actionButton.setTextFill(Color.WHITE);
            actionButton.setMinWidth(150);
            actionButton.setPrefHeight(42);
            actionButton.setStyle(
                    "-fx-background-color: " + (equipped ? "#4b5563" : "#10b981") + "; -fx-background-radius: 14;"
            );

            actionButton.setOnAction(event -> {
                garageService.equipCar(car.getId());
                game.saveProgress();
                messageLabel.setText("Đã chọn " + car.getName() + " làm xe hiện tại.");
                refresh();
            });

            VBox buttonBox = new VBox(10, upgradeButton, actionButton);
            buttonBox.setAlignment(Pos.CENTER_RIGHT);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            HBox card = new HBox(18, preview, infoBox, spacer, buttonBox);
            card.setAlignment(Pos.CENTER_LEFT);
            card.setPadding(new Insets(18));
            card.setStyle("-fx-background-color: rgba(15,23,42,0.55); -fx-background-radius: 18;");

            listContainer.getChildren().add(card);
        }
    }

    private Node createCarPreview(CarId carId, UpgradeScene.UpgradeData upgradeData) {
        StackPane previewBox = new StackPane();
        previewBox.setPrefSize(190, 110);
        previewBox.setMinSize(190, 110);
        previewBox.setMaxSize(190, 110);
        previewBox.setStyle("""
            -fx-background-color: rgba(255,255,255,0.05);
            -fx-background-radius: 18;
            -fx-border-color: rgba(255,255,255,0.10);
            -fx-border-radius: 18;
        """);

        Node carView = CarViewFactory.createShopPreview(carId);
        carView.setScaleX(0.6);
        carView.setScaleY(0.6);

        CarPaintUtil.applyPaint(carView, upgradeData.getPaintName());

        previewBox.getChildren().add(carView);
        return previewBox;
    }

    private String resolveUsername() {
        try {
            Object profile = game.getPlayerProfile();
            if (profile == null) return "guest";

            for (String methodName : new String[]{"getUsername", "getName", "getPlayerName", "getDisplayName"}) {
                try {
                    Method method = profile.getClass().getMethod(methodName);
                    Object value = method.invoke(profile);
                    if (value != null && !String.valueOf(value).isBlank()) {
                        return String.valueOf(value);
                    }
                } catch (NoSuchMethodException ignored) {
                }
            }

            return "guest";
        } catch (Exception e) {
            return "guest";
        }
    }

    @Override
    public Scene getScene() {
        return scene;
    }

    @Override
    public void onShow() {
        refresh();
        requestFocus();
    }
}