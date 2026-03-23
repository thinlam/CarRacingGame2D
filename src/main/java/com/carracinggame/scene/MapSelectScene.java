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
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class MapSelectScene implements AppScene {

    private final Game game;
    private final Scene scene;
    private static final String FONT_FAMILY = "-fx-font-family: 'Inter', 'Segoe UI', 'Poppins', sans-serif;";

    public MapSelectScene(Game game) {
        this.game = game;

        BorderPane root = new BorderPane();
        root.setPrefSize(GameConfig.WIDTH, GameConfig.HEIGHT);
        root.setStyle("""
            -fx-background-color:
                radial-gradient(center 20% 15%, radius 40%, rgba(0,180,255,0.08), transparent 65%),
                radial-gradient(center 88% 25%, radius 35%, rgba(143,104,255,0.08), transparent 70%),
                linear-gradient(to bottom, #050b16 0%, #0a1324 55%, #07101d 100%);
        """);

        VBox top = new VBox(8);
        top.setPadding(new Insets(24, 28, 12, 28));
        Label title = new Label("CHỌN BẢN ĐỒ");
        title.setStyle(FONT_FAMILY + "-fx-font-size: 28px; -fx-font-weight: 900; -fx-text-fill: #ffffff;");
        Label sub = new Label("Xe hiện tại: " + game.getPlayerProfile().getEquippedCar().getDisplayName() + "  •  Chọn map để bắt đầu đua");
        sub.setStyle(FONT_FAMILY + "-fx-font-size: 13px; -fx-font-weight: 600; -fx-text-fill: #9bb4d0;");
        top.getChildren().addAll(title, sub);
        root.setTop(top);

        HBox cards = new HBox(20);
        cards.setAlignment(Pos.CENTER);
        cards.setPadding(new Insets(20, 28, 28, 28));

        cards.getChildren().addAll(
                mapCard("MIỀN BẮC", "Hồ Gươm • Hạ Long • Sa Pa", MapId.NORTH, "#4aa0ff"),
                mapCard("MIỀN TRUNG", "Huế • Hội An • Hải Vân", MapId.CENTRAL, "#ffb347"),
                mapCard("MIỀN NAM", "Bến Thành • Củ Chi • Cái Răng", MapId.SOUTH, "#4cd964")
        );

        root.setCenter(cards);

        HBox bottom = new HBox();
        bottom.setPadding(new Insets(0, 28, 28, 28));
        bottom.setAlignment(Pos.CENTER_LEFT);

        Button back = new Button("← Quay lại");
        back.setStyle("""
            -fx-background-radius: 20;
            -fx-background-color: rgba(255,255,255,0.12);
            -fx-text-fill: white;
            -fx-font-weight: 800;
            -fx-padding: 8 18 8 18;
            -fx-cursor: hand;
        """);
        back.setOnAction(e -> game.switchState(GameState.MENU));
        back.setOnMouseEntered(e -> back.setStyle(back.getStyle().replace("rgba(255,255,255,0.12)", "rgba(255,255,255,0.2)")));
        back.setOnMouseExited(e -> back.setStyle(back.getStyle().replace("rgba(255,255,255,0.2)", "rgba(255,255,255,0.12)")));
        bottom.getChildren().add(back);

        root.setBottom(bottom);

        this.scene = new Scene(root, GameConfig.WIDTH, GameConfig.HEIGHT);
    }

    private VBox mapCard(String name, String places, MapId id, String color) {
        VBox card = new VBox(12);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPadding(new Insets(18));
        card.setPrefWidth(240);
        card.setStyle("""
            -fx-background-color: rgba(20,30,50,0.8);
            -fx-background-radius: 24;
            -fx-border-radius: 24;
            -fx-border-color: rgba(255,255,255,0.12);
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 20, 0, 0, 6);
            -fx-cursor: hand;
        """);
        card.setOnMouseEntered(e -> card.setStyle(card.getStyle().replace("rgba(20,30,50,0.8)", "rgba(30,40,60,0.9)")));
        card.setOnMouseExited(e -> card.setStyle(card.getStyle().replace("rgba(30,40,60,0.9)", "rgba(20,30,50,0.8)")));

        Label t = new Label(name);
        t.setStyle(FONT_FAMILY + "-fx-font-size: 18px; -fx-font-weight: 800; -fx-text-fill: white;");
        Label p = new Label(places);
        p.setStyle(FONT_FAMILY + "-fx-font-size: 12px; -fx-font-weight: 500; -fx-text-fill: #b0c4de;");
        p.setWrapText(true);

        Canvas preview = new Canvas(200, 120);
        drawPreview(preview.getGraphicsContext2D(), id, color);

        Button choose = new Button("Chọn & Đua");
        choose.setMinWidth(180);
        choose.setStyle("""
            -fx-background-radius: 20;
            -fx-font-size: 13px;
            -fx-font-weight: 800;
            -fx-text-fill: white;
            -fx-background-color: %s;
            -fx-cursor: hand;
        """.formatted(color));
        choose.setOnAction(e -> {
            game.setSelectedMap(id);
            game.switchState(GameState.RACE);
        });
        choose.setOnMouseEntered(ev -> choose.setStyle(choose.getStyle().replace(color, color + "aa")));
        choose.setOnMouseExited(ev -> choose.setStyle(choose.getStyle().replace(color + "aa", color)));

        card.getChildren().addAll(t, p, preview, choose);
        return card;
    }

    private void drawPreview(GraphicsContext gc, MapId id, String color) {
        int w = 200, h = 120;
        double trackX = 60, trackW = 80;

        gc.setFill(Color.web("#111822"));
        gc.fillRect(0, 0, w, h);

        gc.setFill(Color.web(color));
        gc.fillRect(trackX, 0, trackW, h);
        gc.setFill(Color.rgb(255, 255, 255, 0.2));
        gc.fillRect(trackX + 5, 5, trackW - 10, h - 10);

        gc.setStroke(Color.web(color, 0.8));
        gc.setLineWidth(2);
        gc.strokeLine(trackX, 0, trackX, h);
        gc.strokeLine(trackX + trackW, 0, trackX + trackW, h);

        gc.setFill(Color.WHITE);
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
    public Scene getScene() { return scene; }
    @Override
    public void onShow() { scene.getRoot().requestFocus(); }
    @Override
    public void onHide() {}
}