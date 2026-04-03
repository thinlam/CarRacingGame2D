package com.carracinggame.scene;

import com.carracinggame.car.CarDefinition;
import com.carracinggame.car.CarSkill;
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
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

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
        root.setStyle("""
                -fx-background-color:
                    radial-gradient(center 18% 12%, radius 45%, rgba(59,130,246,0.18), transparent 60%),
                    radial-gradient(center 86% 18%, radius 35%, rgba(14,165,233,0.12), transparent 62%),
                    linear-gradient(to bottom, #0b1120 0%, #0f172a 45%, #111827 100%);
                """);

        Label title = new Label("SHOP XE & SKILL");
        title.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 32));
        title.setTextFill(Color.WHITE);

        coinsLabel = new Label();
        coinsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        coinsLabel.setTextFill(Color.web("#fde68a"));

        messageLabel = new Label("Mỗi xe có kỹ năng riêng. Mua xe là mở luôn skill của xe đó.");
        messageLabel.setTextFill(Color.web("#dbeafe"));
        messageLabel.setFont(Font.font(16));

        VBox topBox = new VBox(8, title, coinsLabel, messageLabel);
        topBox.setPadding(new Insets(24, 24, 18, 24));
        root.setTop(topBox);

        listContainer = new VBox(16);
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
        backButton.setStyle("""
                -fx-background-color: linear-gradient(to right, #1f2937, #334155);
                -fx-background-radius: 14;
                -fx-padding: 10 18 10 18;
                -fx-cursor: hand;
                """);

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
            CarSkill skill = car.getSkill();

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
            stats.setWrapText(true);

            Label status = new Label(owned ? "Đã sở hữu" : "Chưa sở hữu");
            status.setTextFill(owned ? Color.web("#86efac") : Color.web("#fca5a5"));
            status.setFont(Font.font("Arial", FontWeight.BOLD, 15));

            Label skillBadge = new Label("SKILL ĐỘC QUYỀN");
            skillBadge.setTextFill(Color.WHITE);
            skillBadge.setStyle("""
                    -fx-background-color: rgba(255,255,255,0.12);
                    -fx-background-radius: 999;
                    -fx-padding: 4 10 4 10;
                    -fx-font-size: 11px;
                    -fx-font-weight: 900;
                    """);

            Label skillName = new Label(skill.name() + " • " + skill.shortLabel());
            skillName.setTextFill(Color.web(skill.accentColor()));
            skillName.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 17));

            Label skillDesc = new Label(skill.description());
            skillDesc.setTextFill(Color.web("#dbeafe"));
            skillDesc.setWrapText(true);
            skillDesc.setFont(Font.font(14));

            Label skillMeta = new Label(
                    skill.durationText() + "   |   " +
                            skill.cooldownText() + "   |   Dùng khi đua: SPACE"
            );
            skillMeta.setTextFill(Color.web("#93c5fd"));
            skillMeta.setFont(Font.font(13));

            VBox skillBox = new VBox(5, skillBadge, skillName, skillDesc, skillMeta);
            skillBox.setPadding(new Insets(10, 12, 10, 12));
            skillBox.setStyle("""
                    -fx-background-color: rgba(15,23,42,0.74);
                    -fx-background-radius: 14;
                    -fx-border-color: rgba(255,255,255,0.10);
                    -fx-border-radius: 14;
                    """);

            VBox infoBox = new VBox(8, name, price, stats, status, skillBox);
            infoBox.setPrefWidth(520);

            Button actionButton = new Button(owned ? "Đã mua" : "Mua");
            actionButton.setDisable(owned);
            actionButton.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            actionButton.setTextFill(Color.WHITE);
            actionButton.setStyle("""
                    -fx-background-color: %s;
                    -fx-background-radius: 14;
                    -fx-padding: 12 18 12 18;
                    -fx-cursor: hand;
                    """.formatted(owned ? "#4b5563" : "#f59e0b"));

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
            card.setStyle("""
                    -fx-background-color: rgba(15,23,42,0.58);
                    -fx-background-radius: 18;
                    -fx-border-color: rgba(255,255,255,0.08);
                    -fx-border-radius: 18;
                    """);

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

        carView.setScaleX(0.6);
        carView.setScaleY(0.6);

        StackPane.setAlignment(carView, Pos.CENTER);
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