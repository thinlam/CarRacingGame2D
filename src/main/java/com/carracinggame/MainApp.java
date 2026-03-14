package com.carracinggame;

import com.carracinggame.core.Game;
import com.carracinggame.database.MongoDBConnection;
import com.carracinggame.scene.LoginScene;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        try {
            MongoDBConnection.init();

            stage.setTitle("Car Racing Game 2D");

            stage.setScene(LoginScene.create(stage, () -> {
                Game game = new Game(stage);
                game.start();
            }));

            stage.show();

        } catch (Exception e) {
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("MongoDB Atlas Error");
            alert.setHeaderText("Không kết nối được MongoDB Atlas");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @Override
    public void stop() {
        MongoDBConnection.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}