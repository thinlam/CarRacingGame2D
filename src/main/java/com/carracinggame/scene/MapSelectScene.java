package com.carracinggame.scene;

import com.carracinggame.core.Game;
import com.carracinggame.core.GameConfig;
import com.carracinggame.core.GameState;
import com.carracinggame.map.MapId;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class MapSelectScene implements AppScene {

    private final Game game;
    private final Scene scene;

    private Label selectedMapLabel;

    public MapSelectScene(Game game) {
        this.game = game;

        BorderPane root = new BorderPane();
        root.setPrefSize(GameConfig.WIDTH, GameConfig.HEIGHT);
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #7fd2ff, #e7f7ff);");

        VBox top = buildTop();
        HBox centerCards = buildCenterCards();
        HBox bottom = buildBottom();

        root.setTop(top);
        root.setCenter(centerCards);
        root.setBottom(bottom);

        this.scene = new Scene(root, GameConfig.WIDTH, GameConfig.HEIGHT);
    }

    private VBox buildTop() {
        VBox top = new VBox(6);
        top.setPadding(new Insets(18));

        Label title = new Label("CHỌN BẢN ĐỒ (BẮC • TRUNG • NAM)");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: 900; -fx-text-fill: #0d2b4f;");

        Label sub = new Label("Chọn 1 map để vào đua hoặc đặt làm map hiện tại");
        sub.setStyle("-fx-font-size: 13px; -fx-font-weight: 700; -fx-text-fill: #204a73;");

        selectedMapLabel = new Label();
        selectedMapLabel.setStyle("""
                -fx-font-size: 13px;
                -fx-font-weight: 900;
                -fx-text-fill: white;
                -fx-background-color: rgba(13,43,79,0.75);
                -fx-background-radius: 12;
                -fx-padding: 8 12 8 12;
                """);
        refreshSelectedMapLabel();

        top.getChildren().addAll(title, sub, selectedMapLabel);
        return top;
    }

    private HBox buildCenterCards() {
        HBox cards = new HBox(14);
        cards.setAlignment(Pos.CENTER);
        cards.setPadding(new Insets(10, 18, 18, 18));

        cards.getChildren().addAll(
                mapCard("MIỀN BẮC", "Hồ Gươm • Hạ Long • Sa Pa", MapId.NORTH),
                mapCard("MIỀN TRUNG", "Huế • Hội An • Hải Vân", MapId.CENTRAL),
                mapCard("MIỀN NAM", "Bến Thành • Củ Chi • Cái Răng", MapId.SOUTH)
        );

        return cards;
    }

    private HBox buildBottom() {
        HBox bottom = new HBox();
        bottom.setPadding(new Insets(0, 18, 18, 18));
        bottom.setAlignment(Pos.CENTER_LEFT);

        Button back = new Button("← Quay lại");
        back.setStyle("""
                -fx-background-radius: 14;
                -fx-background-color: rgba(255,255,255,0.75);
                -fx-text-fill: #0d2b4f;
                -fx-font-weight: 900;
                """);
        back.setOnAction(e -> game.switchState(GameState.MENU));

        bottom.getChildren().add(back);
        return bottom;
    }

    private VBox mapCard(String name, String places, MapId id) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPadding(new Insets(14));
        card.setPrefWidth(230);

        boolean selected = game.getSelectedMap() == id;
        String borderColor = selected ? "#35c88b" : "rgba(255,255,255,0.8)";
        String borderWidth = selected ? "3" : "1.2";

        card.setStyle("""
                -fx-background-color: rgba(255,255,255,0.65);
                -fx-background-radius: 18;
                -fx-border-radius: 18;
                -fx-border-color: %s;
                -fx-border-width: %s;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.14), 12, 0, 0, 6);
                """.formatted(borderColor, borderWidth));

        Label t = new Label(name);
        t.setStyle("-fx-font-size: 16px; -fx-font-weight: 900; -fx-text-fill: #0d2b4f;");

        Label p = new Label(places);
        p.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #204a73;");
        p.setWrapText(true);

        Label currentBadge = new Label(selected ? "Đang chọn" : "Chưa chọn");
        currentBadge.setStyle("""
                -fx-font-size: 11px;
                -fx-font-weight: 900;
                -fx-text-fill: white;
                -fx-background-radius: 10;
                -fx-padding: 5 10 5 10;
                -fx-background-color: %s;
                """.formatted(selected ? "#35c88b" : "#94a3b8"));

        Canvas preview = new Canvas(200, 120);
        drawPreview(preview.getGraphicsContext2D(), id);

        Button selectButton = new Button("Chọn map");
        selectButton.setMinWidth(180);
        selectButton.setStyle("""
                -fx-background-radius: 16;
                -fx-font-size: 13px;
                -fx-font-weight: 900;
                -fx-text-fill: white;
                -fx-background-color: #2563eb;
                """);
        selectButton.setOnAction(e -> {
            game.setSelectedMap(id);
            game.switchState(GameState.MAP_SELECT);
        });

        Button chooseAndRace = new Button("Chọn & Đua");
        chooseAndRace.setMinWidth(180);
        chooseAndRace.setStyle("""
                -fx-background-radius: 16;
                -fx-font-size: 13px;
                -fx-font-weight: 900;
                -fx-text-fill: white;
                -fx-background-color: #35c88b;
                """);
        chooseAndRace.setOnAction(e -> startRaceWithMap(id));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        card.getChildren().addAll(t, p, currentBadge, preview, spacer, selectButton, chooseAndRace);
        return card;
    }

    private void startRaceWithMap(MapId id) {
        try {
            game.startRace(id);
        } catch (Exception ex) {
            game.setSelectedMap(id);
            game.switchState(GameState.RACE);
        }
    }

    private void refreshSelectedMapLabel() {
        selectedMapLabel.setText("Map hiện tại: " + toDisplayName(game.getSelectedMap()));
    }

    private String toDisplayName(MapId id) {
        return switch (id) {
            case NORTH -> "Miền Bắc";
            case CENTRAL -> "Miền Trung";
            case SOUTH -> "Miền Nam";
        };
    }

    private void drawPreview(GraphicsContext gc, MapId id) {
        int w = 200;
        int h = 120;
        double trackX = 60;
        double trackW = 80;

        gc.clearRect(0, 0, w, h);

        if (id == MapId.NORTH) {
            gc.setFill(Color.web("#9fd7ff"));
        } else if (id == MapId.CENTRAL) {
            gc.setFill(Color.web("#ffd7a6"));
        } else {
            gc.setFill(Color.web("#9fe2c1"));
        }
        gc.fillRect(0, 0, w, h);

        gc.setFill(Color.web("#6b6b6b"));
        gc.fillRect(trackX, 0, trackW, h);

        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);
        gc.strokeLine(trackX, 0, trackX, h);
        gc.strokeLine(trackX + trackW, 0, trackX + trackW, h);

        gc.setFill(Color.rgb(255, 255, 255, 0.85));
        for (int i = 0; i < 4; i++) {
            double y = 8 + i * 30;
            gc.fillRect(trackX + trackW / 2.0 - 2, y, 4, 18);
        }

        gc.setFill(Color.rgb(255, 255, 255, 0.25));
        if (id == MapId.NORTH) {
            gc.fillOval(10, 25, 40, 30);
            gc.fillOval(145, 70, 40, 30);
        } else if (id == MapId.CENTRAL) {
            gc.fillRoundRect(10, 65, 45, 30, 8, 8);
            gc.fillOval(150, 15, 10, 15);
            gc.fillOval(165, 25, 10, 15);
        } else {
            gc.fillRect(10, 70, 45, 25);
            gc.fillOval(150, 65, 35, 20);
        }
    }

    @Override
    public Scene getScene() {
        return scene;
    }

    @Override
    public void onShow() {
        refreshSelectedMapLabel();
        requestFocus();
    }

    @Override
    public void onHide() {
    }
}