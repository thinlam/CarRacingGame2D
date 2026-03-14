package com.carracinggame;

import com.carracinggame.core.Game;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        Game game = new Game(stage);
        game.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
    