package com.carracinggame.scene;

import com.carracinggame.car.CarDefinition;
import com.carracinggame.car.CarId;
import com.carracinggame.core.Game;
import com.carracinggame.core.GameConfig;
import com.carracinggame.core.GameState;
import com.carracinggame.shop.ShopService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import com.carracinggame.scene.CarViewFactory;

public class ShopScene implements AppScene {

    private final Game game;
    private final ShopService shopService;
    private final Scene scene;

    private final Label messageLabel;
    private final Label coinsLabel;
    private final VBox listContainer;

    public ShopScene(Game game) {
        this.game = game;
        this.shopService = game.getShopService();

        BorderPane root = new BorderPane();
        root.setPrefSize(GameConfig.WIDTH, GameConfig.HEIGHT);
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #0f172a, #111827 55%, #1d4ed8);");

        Label title = new Label("SHOP XE");
        title.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 32));
        title.setTextFill(Color.WHITE);

        coinsLabel = new Label();
        coinsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        coinsLabel.setTextFill(Color.web("#fde68a"));

        messageLabel = new Label("Chọn xe để mua.");
        messageLabel.setTextFill(Color.web("#dbeafe"));
        messageLabel.setFont(Font.font(16));

        VBox topBox = new VBox(8, title, coinsLabel, messageLabel);
        topBox.setPadding(new Insets(24));
        root.setTop(topBox);

        listContainer = new VBox(14);
        listContainer.setPadding(new Insets(16));

        ScrollPane scrollPane = new ScrollPane(listContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        root.setCenter(scrollPane);

        Button backButton = new Button("Quay lại menu");
        backButton.setOnAction(event -> game.switchState(GameState.MENU));
        backButton.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        backButton.setTextFill(Color.WHITE);
        backButton.setStyle("-fx-background-color: #1f2937; -fx-background-radius: 14;");

        HBox bottomBox = new HBox(backButton);
        bottomBox.setAlignment(Pos.CENTER_LEFT);
        bottomBox.setPadding(new Insets(0, 0, 20, 24));
        root.setBottom(bottomBox);

        this.scene = new Scene(root, GameConfig.WIDTH, GameConfig.HEIGHT);
        refresh();
    }

    private void refresh() {
        coinsLabel.setText("Coin hiện có: " + game.getPlayerProfile().getCoins());
        listContainer.getChildren().clear();

        for (CarDefinition car : shopService.getCars()) {
            boolean owned = game.getGarageService().ownsCar(car.getId());

            Node preview = createCarPreview(car);

            Label name = new Label(car.getName());
            name.setTextFill(Color.WHITE);
            name.setFont(Font.font("Arial", FontWeight.BOLD, 22));

            Label price = new Label(
                    car.getPrice() == 0
                            ? "Miễn phí / xe mặc định"
                            : "Giá: " + car.getPrice() + " coin"
            );
            price.setTextFill(Color.web("#fde68a"));
            price.setFont(Font.font(16));

            Label stats = new Label(car.getStats().toPrettyText());
            stats.setTextFill(Color.web("#e5e7eb"));

            Label status = new Label(owned ? "Đã sở hữu" : "Chưa sở hữu");
            status.setTextFill(owned ? Color.web("#86efac") : Color.web("#fca5a5"));
            status.setFont(Font.font("Arial", FontWeight.BOLD, 15));

            VBox infoBox = new VBox(6, name, price, stats, status);

            Button actionButton = new Button(owned ? "Đã mua" : "Mua");
            actionButton.setDisable(owned);
            actionButton.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            actionButton.setTextFill(Color.WHITE);
            actionButton.setStyle(
                    "-fx-background-color: " + (owned ? "#4b5563" : "#f59e0b") + "; -fx-background-radius: 14;"
            );

            actionButton.setOnAction(event -> {
                ShopService.BuyResult result = shopService.buyCar(
                        car.getId(),
                        game.getPlayerProfile(),
                        game.getGarageService()
                );
                messageLabel.setText(result.message());
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

    private Node createCarPreview(CarDefinition car) {
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

        Node carView = CarViewFactory.createShopPreview(car.getId());

        // thu nhỏ xe trong khung
        carView.setScaleX(0.6);
        carView.setScaleY(0.6);

        StackPane.setAlignment(carView, Pos.CENTER);
        previewBox.getChildren().add(carView);

        return previewBox;
    }

    private Node buildTopViewCar(CarDefinition car) {
        Pane sprite = new Pane();
        sprite.setPrefSize(48, 86);
        sprite.setScaleX(1.9);
        sprite.setScaleY(1.9);

        Color bodyColor = getBodyColor(car.getId());
        Color centerColor = getCenterColor(car.getId());
        Color wingColor = bodyColor.darker();
        Color tireColor = Color.web("#2b2b2b");
        Color lightColor = Color.web("#f8fafc");

        Rectangle rearWing = rect(16, 4, wingColor, 16, 2, 3, 3);
        Rectangle rearBody = rect(12, 8, bodyColor, 18, 7, 4, 4);
        Rectangle body = rect(18, 38, bodyColor, 15, 15, 6, 6);
        Rectangle cockpit = rect(10, 18, centerColor, 19, 24, 4, 4);
        Rectangle nose = rect(10, 10, lightColor, 19, 54, 4, 4);
        Rectangle frontWing = rect(18, 4, lightColor, 15, 67, 3, 3);

        Rectangle tireLT = rect(4, 10, tireColor, 11, 18, 2, 2);
        Rectangle tireRT = rect(4, 10, tireColor, 33, 18, 2, 2);
        Rectangle tireLB = rect(4, 10, tireColor, 11, 46, 2, 2);
        Rectangle tireRB = rect(4, 10, tireColor, 33, 46, 2, 2);

        Rectangle sideLeftTop = rect(3, 8, wingColor, 13, 28, 2, 2);
        Rectangle sideRightTop = rect(3, 8, wingColor, 32, 28, 2, 2);
        Rectangle sideLeftBottom = rect(3, 8, wingColor, 13, 40, 2, 2);
        Rectangle sideRightBottom = rect(3, 8, wingColor, 32, 40, 2, 2);

        Rectangle rearLight = rect(8, 3, lightColor, 20, 10, 2, 2);
        Rectangle frontLight = rect(8, 3, Color.web("#fde68a"), 20, 61, 2, 2);

        sprite.getChildren().addAll(
                rearWing, rearBody, body, cockpit, nose, frontWing,
                tireLT, tireRT, tireLB, tireRB,
                sideLeftTop, sideRightTop, sideLeftBottom, sideRightBottom,
                rearLight, frontLight
        );

        return sprite;
    }

    private Rectangle rect(double w, double h, Color color,
                           double x, double y, double arcW, double arcH) {
        Rectangle r = new Rectangle(w, h, color);
        r.setX(x);
        r.setY(y);
        r.setArcWidth(arcW);
        r.setArcHeight(arcH);
        return r;
    }

    private Color getBodyColor(CarId carId) {
        return switch (carId) {
            case RED_RACER -> Color.web("#ef4444");
            case BLUE_STORM -> Color.web("#2563eb");
            case GREEN_SHADOW -> Color.web("#111827");
            default -> Color.web("#9ca3af");
        };
    }

    private Color getCenterColor(CarId carId) {
        return switch (carId) {
            case RED_RACER -> Color.web("#facc15");
            case BLUE_STORM -> Color.web("#93c5fd");
            case GREEN_SHADOW -> Color.web("#a78bfa");
            default -> Color.web("#e5e7eb");
        };
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