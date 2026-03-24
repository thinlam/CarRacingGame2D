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

        messageLabel = new Label("Chọn xe đang dùng cho Race.");
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

        for (CarDefinition car : garageService.getOwnedCarDefinitions()) {
            boolean equipped = garageService.getEquippedCar() != null
                    && garageService.getEquippedCar().getId() == car.getId();

            Node preview = createCarPreview(car.getId());

            Label name = new Label(car.getName());
            name.setTextFill(Color.WHITE);
            name.setFont(Font.font("Arial", FontWeight.BOLD, 22));

            Label stats = new Label(car.getStats().toPrettyText());
            stats.setTextFill(Color.web("#e5e7eb"));

            Label status = new Label(equipped ? "Đang sử dụng" : "Đã sở hữu");
            status.setTextFill(equipped ? Color.web("#86efac") : Color.web("#cbd5e1"));
            status.setFont(Font.font("Arial", FontWeight.BOLD, 15));

            VBox infoBox = new VBox(6, name, stats, status);

            Button actionButton = new Button(equipped ? "Đã chọn" : "Chọn xe");
            actionButton.setDisable(equipped);
            actionButton.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            actionButton.setTextFill(Color.WHITE);
            actionButton.setStyle(
                    "-fx-background-color: " + (equipped ? "#4b5563" : "#10b981") + "; -fx-background-radius: 14;"
            );

            actionButton.setOnAction(event -> {
                garageService.equipCar(car.getId());
                messageLabel.setText("Đã chọn " + car.getName() + " làm xe hiện tại.");
                refresh();
            });

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            HBox card = new HBox(18, preview, infoBox, spacer, actionButton);
            card.setAlignment(Pos.CENTER_LEFT);
            card.setPadding(new Insets(18));
            card.setStyle("-fx-background-color: rgba(15,23,42,0.55); -fx-background-radius: 18;");

            listContainer.getChildren().add(card);
        }
    }

    private Node createCarPreview(CarId carId) {
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
        previewBox.getChildren().add(carView);

        return previewBox;
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