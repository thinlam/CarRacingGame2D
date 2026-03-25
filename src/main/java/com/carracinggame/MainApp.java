package com.carracinggame;

import com.carracinggame.core.Game;
import com.carracinggame.database.MongoDBConnection;
import com.carracinggame.scene.LoginScene;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class MainApp extends Application {

    public static final double APP_WIDTH = 1280;
    public static final double APP_HEIGHT = 720;

    @Override
    public void start(Stage stage) {
        try {
            MongoDBConnection.init();

            stage.setTitle("Car Racing 2D - Login");
            stage.setWidth(APP_WIDTH);
            stage.setHeight(APP_HEIGHT);

            // Cho phép bấm -, ô vuông, X
            stage.setResizable(true);
            stage.setMinWidth(1100);
            stage.setMinHeight(720);

            stage.setOnCloseRequest(event -> {
                Platform.exit();
                System.exit(0);
            });

            stage.setScene(LoginScene.create(stage, () -> {
                Game game = new Game(stage);
                game.start();
            }));

            stage.setMaximized(true);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi khởi động");
            alert.setHeaderText(null);
            alert.setContentText("Không thể khởi động game: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @Override
    public void stop() {
        System.out.println("Game closed.");
        MongoDBConnection.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}