package com.carracinggame.scene;

import com.carracinggame.core.Game;
import com.carracinggame.database.LeaderboardDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardScene implements AppScene {

    private final Game game;
    private final Scene scene;
    private final VBox listBox;
    private final LeaderboardDAO leaderboardDAO = new LeaderboardDAO();

    public LeaderboardScene(Game game) {
        this.game = game;

        BorderPane root = new BorderPane();
        root.setStyle("""
            -fx-background-color:
                linear-gradient(to bottom, #02050c 0%, #071120 38%, #09192b 72%, #050913 100%);
        """);

        VBox header = new VBox(6);
        header.setPadding(new Insets(20, 20, 12, 20));

        Label title = new Label("BẢNG XẾP HẠNG TOÀN SERVER");
        title.setStyle("""
            -fx-font-size: 26px;
            -fx-font-weight: 900;
            -fx-text-fill: white;
        """);

        Label sub = new Label("Top 10 người chơi có quãng đường cao nhất");
        sub.setStyle("""
            -fx-font-size: 12px;
            -fx-font-weight: 700;
            -fx-text-fill: #8bb7d8;
        """);

        header.getChildren().addAll(title, sub);

        HBox topActions = new HBox(10);
        topActions.setAlignment(Pos.CENTER_LEFT);
        topActions.setPadding(new Insets(0, 20, 10, 20));

        Button refreshBtn = new Button("Làm mới");
        refreshBtn.setStyle("""
            -fx-background-color: linear-gradient(to bottom, #3aa7ff, #226dd8);
            -fx-text-fill: white;
            -fx-font-weight: 900;
            -fx-background-radius: 14;
            -fx-padding: 10 18 10 18;
        """);

        Button backBtn = new Button("Quay lại menu");
        backBtn.setStyle("""
            -fx-background-color: linear-gradient(to bottom, #19c37d, #0d8f5a);
            -fx-text-fill: white;
            -fx-font-weight: 900;
            -fx-background-radius: 14;
            -fx-padding: 10 18 10 18;
        """);

        refreshBtn.setOnAction(e -> refreshLeaderboard());
        backBtn.setOnAction(e -> game.showMenu());

        topActions.getChildren().addAll(refreshBtn, backBtn);

        listBox = new VBox(12);
        listBox.setPadding(new Insets(20));

        ScrollPane scrollPane = new ScrollPane(listBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("""
            -fx-background: transparent;
            -fx-background-color: transparent;
            -fx-border-color: transparent;
        """);

        root.setTop(new VBox(header, topActions));
        root.setCenter(scrollPane);

        this.scene = new Scene(root, 1280, 720);
        refreshLeaderboard();
    }

    private HBox createRankRow(int rankNumber, LeaderboardDAO.Entry entry) {
        HBox row = new HBox(16);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(14));
        row.setStyle("""
            -fx-background-color:
                linear-gradient(to bottom, rgba(10,21,39,0.96), rgba(7,16,30,0.96));
            -fx-background-radius: 18;
            -fx-border-color: rgba(255,255,255,0.08);
            -fx-border-radius: 18;
        """);

        Label rank = new Label("#" + rankNumber);
        rank.setMinWidth(60);
        rank.setStyle("""
            -fx-font-size: 22px;
            -fx-font-weight: 900;
            -fx-text-fill: #ffd54a;
        """);

        VBox info = new VBox(4);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label username = new Label(entry.username());
        username.setStyle("""
            -fx-font-size: 18px;
            -fx-font-weight: 900;
            -fx-text-fill: white;
        """);

        Label sub = new Label("Người chơi");
        sub.setStyle("""
            -fx-font-size: 11px;
            -fx-font-weight: 700;
            -fx-text-fill: #87a4bf;
        """);

        info.getChildren().addAll(username, sub);

        Label km = new Label((int) entry.distanceKm() + " km");
        km.setStyle("""
            -fx-font-size: 18px;
            -fx-font-weight: 900;
            -fx-text-fill: #4dd8ff;
        """);

        row.getChildren().addAll(rank, info, km);
        return row;
    }

    private HBox createEmptyRankRow(int rankNumber) {
        HBox row = new HBox(16);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(14));
        row.setStyle("""
            -fx-background-color:
                linear-gradient(to bottom, rgba(10,21,39,0.78), rgba(7,16,30,0.78));
            -fx-background-radius: 18;
            -fx-border-color: rgba(255,255,255,0.06);
            -fx-border-radius: 18;
        """);

        Label rank = new Label("#" + rankNumber);
        rank.setMinWidth(60);
        rank.setStyle("""
            -fx-font-size: 22px;
            -fx-font-weight: 900;
            -fx-text-fill: #5e7b95;
        """);

        VBox info = new VBox(4);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label username = new Label("---");
        username.setStyle("""
            -fx-font-size: 18px;
            -fx-font-weight: 900;
            -fx-text-fill: #7f97b2;
        """);

        Label sub = new Label("Chưa có người chơi");
        sub.setStyle("""
            -fx-font-size: 11px;
            -fx-font-weight: 700;
            -fx-text-fill: #5f7890;
        """);

        info.getChildren().addAll(username, sub);

        Label km = new Label("0 km");
        km.setStyle("""
            -fx-font-size: 18px;
            -fx-font-weight: 900;
            -fx-text-fill: #4f6d86;
        """);

        row.getChildren().addAll(rank, info, km);
        return row;
    }

    private void refreshLeaderboard() {
        listBox.getChildren().clear();

        List<LeaderboardDAO.Entry> entries;
        try {
            entries = leaderboardDAO.getTop10();
        } catch (Exception e) {
            entries = new ArrayList<>();
        }

        for (int i = 0; i < 10; i++) {
            if (i < entries.size()) {
                listBox.getChildren().add(createRankRow(i + 1, entries.get(i)));
            } else {
                listBox.getChildren().add(createEmptyRankRow(i + 1));
            }
        }
    }

    @Override
    public Scene getScene() {
        return scene;
    }

    @Override
    public void onShow() {
        refreshLeaderboard();
        scene.getRoot().requestFocus();
    }

    @Override
    public void onHide() {
    }
}