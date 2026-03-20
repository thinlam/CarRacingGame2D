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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class MapSelectScene implements AppScene {

    private final Game game;
    private final Scene scene;

    public MapSelectScene(Game game) {
        this.game = game;

        BorderPane root = new BorderPane();
        root.setPrefSize(GameConfig.WIDTH, GameConfig.HEIGHT);
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #7fd2ff, #e7f7ff);");

        // Top title
        VBox top = new VBox(6);
        top.setPadding(new Insets(18));
        Label title = new Label("CHỌN BẢN ĐỒ (BẮC • TRUNG • NAM)");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: 900; -fx-text-fill: #0d2b4f;");
        Label sub = new Label("Chọn 1 map để vào đua");
        sub.setStyle("-fx-font-size: 13px; -fx-font-weight: 700; -fx-text-fill: #204a73;");
        top.getChildren().addAll(title, sub);
        root.setTop(top);

        // 3 cards
        HBox cards = new HBox(14);
        cards.setAlignment(Pos.CENTER);
        cards.setPadding(new Insets(10, 18, 18, 18));

        cards.getChildren().addAll(
                mapCard("MIỀN BẮC", "Hồ Gươm • Hạ Long • Sa Pa", MapId.NORTH),
                mapCard("MIỀN TRUNG", "Huế • Hội An • Hải Vân", MapId.CENTRAL),
                mapCard("MIỀN NAM", "Bến Thành • Củ Chi • Cái Răng", MapId.SOUTH)
        );

        root.setCenter(cards);

        // Back
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

        root.setBottom(bottom);

        this.scene = new Scene(root, GameConfig.WIDTH, GameConfig.HEIGHT);
    }

    private VBox mapCard(String name, String places, MapId id) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPadding(new Insets(14));
        card.setPrefWidth(230);
        card.setStyle("""
            -fx-background-color: rgba(255,255,255,0.65);
            -fx-background-radius: 18;
            -fx-border-radius: 18;
            -fx-border-color: rgba(255,255,255,0.8);
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.14), 12, 0, 0, 6);
        """);

        Label t = new Label(name);
        t.setStyle("-fx-font-size: 16px; -fx-font-weight: 900; -fx-text-fill: #0d2b4f;");
        Label p = new Label(places);
        p.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #204a73;");
        p.setWrapText(true);

        Canvas preview = new Canvas(200, 120);
        drawPreview(preview.getGraphicsContext2D(), id);

        Button choose = new Button("Chọn & Đua");
        choose.setMinWidth(180);
        choose.setStyle("""
            -fx-background-radius: 16;
            -fx-font-size: 13px;
            -fx-font-weight: 900;
            -fx-text-fill: white;
            -fx-background-color: #35c88b;
        """);
        choose.setOnAction(e -> {
            game.setSelectedMap(id);
            game.switchState(GameState.RACE);
        });

        card.getChildren().addAll(t, p, preview, choose);
        return card;
    }

    // Preview tạm bằng màu (chưa cần asset)
    private void drawPreview(GraphicsContext gc, MapId id) {
        int w = 200, h = 120;
        double trackX = 60, trackW = 80;

        // background theo miền
        if (id == MapId.NORTH) gc.setFill(Color.web("#9fd7ff"));
        else if (id == MapId.CENTRAL) gc.setFill(Color.web("#ffd7a6"));
        else gc.setFill(Color.web("#9fe2c1"));
        gc.fillRect(0, 0, w, h);

        // track
        gc.setFill(Color.web("#6b6b6b"));
        gc.fillRect(trackX, 0, trackW, h);

        // border
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);
        gc.strokeLine(trackX, 0, trackX, h);
        gc.strokeLine(trackX + trackW, 0, trackX + trackW, h);

        // decor đơn giản
        gc.setFill(Color.rgb(255,255,255,0.25));
        if (id == MapId.NORTH) {
            gc.fillOval(10, 25, 40, 30); // “hồ”
            gc.fillOval(145, 70, 40, 30); // “đảo”
        } else if (id == MapId.CENTRAL) {
            gc.fillRoundRect(10, 65, 45, 30, 8, 8); // “cổng”
            gc.fillOval(150, 15, 10, 15); // “đèn”
            gc.fillOval(165, 25, 10, 15);
        } else {
            gc.fillRect(10, 70, 45, 25); // “chợ”
            gc.fillOval(150, 65, 35, 20); // “thuyền”
        }
    }

    @Override public Scene getScene() { return scene; }
    @Override public void onShow() { scene.getRoot().requestFocus(); }
    @Override public void onHide() {}
}
