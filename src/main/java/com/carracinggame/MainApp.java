package com.carracinggame;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.HashSet;
import java.util.Set;

public class MainApp extends Application {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    private double carX = 380;
    private double carY = 450;
    private double speed = 4;

    private final Set<KeyCode> keysPressed = new HashSet<>();

    @Override
    public void start(Stage stage) {
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        Scene scene = new Scene(new StackPane(canvas));
        stage.setTitle("Car Racing Game 2D");
        stage.setScene(scene);
        stage.show();

        scene.setOnKeyPressed(e -> keysPressed.add(e.getCode()));
        scene.setOnKeyReleased(e -> keysPressed.remove(e.getCode()));

        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
                render(gc);
            }
        };
        gameLoop.start();
    }

    private void update() {
        if (keysPressed.contains(KeyCode.LEFT)) {
            carX -= speed;
        }
        if (keysPressed.contains(KeyCode.RIGHT)) {
            carX += speed;
        }
        if (keysPressed.contains(KeyCode.UP)) {
            carY -= speed;
        }
        if (keysPressed.contains(KeyCode.DOWN)) {
            carY += speed;
        }
    }

    private void render(GraphicsContext gc) {
        gc.setFill(Color.DARKGRAY);
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        // Đường đua
        gc.setFill(Color.GRAY);
        gc.fillRect(200, 0, 400, HEIGHT);

        // Xe
        gc.setFill(Color.RED);
        gc.fillRect(carX, carY, 40, 70);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
