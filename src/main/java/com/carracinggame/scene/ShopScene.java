package com.carracinggame.scene;

import com.carracinggame.car.CarDefinition;
import com.carracinggame.core.Game;
import com.carracinggame.core.GameConfig;
import com.carracinggame.core.GameState;
import com.carracinggame.shop.ShopService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class ShopScene implements AppScene {

    private final Game game;
    private final ShopService shopService;
    private final Scene scene;

    private Label messageLabel;
    private Label coinsLabel;
    private VBox listContainer;

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

            Rectangle preview = new Rectangle(90, 56, car.getAccentColor());
            preview.setArcHeight(18);
            preview.setArcWidth(18);

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

    @Override
    public Scene getScene() {
        return scene;
    }

    @Override
    public void onShow() {
        refresh();
        requestFocus();
    }

    @Override
    public void onHide() {
    }
}