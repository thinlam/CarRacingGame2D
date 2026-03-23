package com.carracinggame.scene;

import com.carracinggame.core.Game;
import com.carracinggame.core.GameConfig;
import com.carracinggame.core.GameState;
import com.carracinggame.core.RaceSummary;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class ResultScene implements AppScene {
    private final Scene scene;

    public ResultScene(Game game) {
        RaceSummary summary = game.getLastRaceSummary();

        BorderPane root = new BorderPane();
        root.setPrefSize(GameConfig.WIDTH, GameConfig.HEIGHT);
        root.setStyle("""
            -fx-background-color:
                linear-gradient(to bottom, #07111f 0%, #0a1630 50%, #09101d 100%);
        """);

        VBox center = new VBox(16);
        center.setMaxWidth(520);
        center.setPadding(new Insets(24));
        center.setAlignment(Pos.CENTER_LEFT);
        center.setStyle("""
            -fx-background-color: rgba(7,17,34,0.82);
            -fx-background-radius: 28;
            -fx-border-radius: 28;
            -fx-border-color: rgba(110,215,255,0.20);
        """);

        center.getChildren().addAll(
                title(summary.getHeadline()),
                sub(summary.getSubline()),
                sub("Map: " + summary.getMapName() + "  •  Xe: " + summary.getCarName()),
                stat("Quãng đường", summary.getDistanceMeters() + "m"),
                stat("Thưởng race", "+" + summary.getBaseReward() + " coin"),
                stat("Thưởng nhiệm vụ", "+" + summary.getMissionReward() + " coin"),
                stat("Tổng nhận", "+" + summary.getTotalReward() + " coin"),
                stat("Coins hiện tại", String.format("%,d", game.getPlayerProfile().getCoins())),
                missionBox(summary)
        );

        HBox actions = new HBox(10);
        actions.getChildren().addAll(
                actionButton("VỀ MENU", "#8d68ff", e -> game.switchState(GameState.MENU)),
                actionButton("GARAGE", "#4fa6ff", e -> game.switchState(GameState.GARAGE)),
                actionButton("CHƠI LẠI", "#2fd08b", e -> game.switchState(GameState.RACE))
        );
        center.getChildren().add(actions);

        root.setCenter(center);
        this.scene = new Scene(root, GameConfig.WIDTH, GameConfig.HEIGHT);
    }

    private Node missionBox(RaceSummary summary) {
        VBox box = new VBox(6);
        box.setPadding(new Insets(12));
        box.setStyle("""
            -fx-background-color: rgba(255,255,255,0.05);
            -fx-background-radius: 18;
            -fx-border-radius: 18;
            -fx-border-color: rgba(255,255,255,0.08);
        """);
        box.getChildren().add(titleSmall(summary.isVictory() ? "NHIỆM VỤ VỪA HOÀN THÀNH" : "TRẠNG THÁI"));
        if (!summary.isVictory()) {
            box.getChildren().add(sub("Va chạm traffic sẽ kết thúc lượt đua. Bạn có thể đổi xe mạnh hơn ở Garage hoặc Shop rồi thử lại."));
        } else if (summary.getClaimedMissions().isEmpty()) {
            box.getChildren().add(sub("Chưa có nhiệm vụ nào hoàn thành trong lượt này."));
        } else {
            for (String mission : summary.getClaimedMissions()) {
                box.getChildren().add(sub("• " + mission));
            }
        }
        return box;
    }

    private Label title(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 28px; -fx-font-weight: 900; -fx-text-fill: white;");
        return label;
    }

    private Label titleSmall(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 14px; -fx-font-weight: 800; -fx-text-fill: #dff7ff;");
        return label;
    }

    private Label sub(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setStyle("-fx-font-size: 12px; -fx-font-weight: 500; -fx-text-fill: #9ab4d0;");
        return label;
    }

    private Node stat(String name, String value) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 12, 10, 12));
        row.setStyle("-fx-background-color: rgba(255,255,255,0.04); -fx-background-radius: 16;");
        Label left = new Label(name);
        left.setStyle("-fx-font-size: 13px; -fx-font-weight: 800; -fx-text-fill: #f6fbff;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label right = new Label(value);
        right.setStyle("-fx-font-size: 13px; -fx-font-weight: 800; -fx-text-fill: #7bd9a8;");
        row.getChildren().addAll(left, spacer, right);
        return row;
    }

    private Button actionButton(String text, String color, javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        Button button = new Button(text);
        button.setMinWidth(150);
        button.setMinHeight(46);
        button.setOnAction(handler);
        button.setStyle("""
            -fx-background-radius: 16;
            -fx-font-size: 13px;
            -fx-font-weight: 900;
            -fx-text-fill: white;
            -fx-background-color: %s;
            -fx-cursor: hand;
        """.formatted(color));
        return button;
    }

    @Override
    public Scene getScene() {
        return scene;
    }
}
