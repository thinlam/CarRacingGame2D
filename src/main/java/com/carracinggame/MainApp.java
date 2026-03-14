package com.carracinggame;

import com.carracinggame.core.Game;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class MainApp extends Application {

    private Game game;

    @Override
    public void start(Stage stage) {
        try {
            stage.setTitle("Car Racing 2D - Shop & Garage");
            stage.setMinWidth(1100);
            stage.setMinHeight(720);
            stage.centerOnScreen();

            stage.setOnCloseRequest(event -> {
                Platform.exit();
                System.exit(0);
            });

            game = new Game(stage);
            game.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        System.out.println("Game closed.");
    }

    public static void main(String[] args) {
        launch(args);
    }
}