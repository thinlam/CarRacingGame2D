package com.carracinggame;

import com.carracinggame.core.Game;
import com.carracinggame.database.MongoDBConnection;
import com.carracinggame.scene.LoginScene;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class MainApp extends Application {

    private Game game;
    public static final double APP_WIDTH = 1280;
    public static final double APP_HEIGHT = 720;

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
            MongoDBConnection.init();

            stage.setTitle("Car Racing Game 2D");

            // Kích thước khởi tạo ban đầu
            stage.setWidth(APP_WIDTH);
            stage.setHeight(APP_HEIGHT);

            // Cho phép resize để dùng được nút -, ô vuông, X
            stage.setResizable(true);

            // Kích thước nhỏ nhất để layout không bể
            stage.setMinWidth(1000);
            stage.setMinHeight(650);

            stage.setScene(LoginScene.create(stage, () -> {
                Game game = new Game(stage);
                game.start();
            }));

            // Mở ra ở trạng thái phóng to cửa sổ
            // vẫn còn thanh tiêu đề, nút -, ô vuông, X
            stage.setMaximized(true);

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
        System.out.println("Game closed.");
        MongoDBConnection.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}