package com.carracinggame.scene;

import com.carracinggame.car.CarDefinition;
import com.carracinggame.car.PlayerCar;
import com.carracinggame.core.Game;
import com.carracinggame.core.GameConfig;
import com.carracinggame.core.GameLoop;
import com.carracinggame.core.GameState;
import com.carracinggame.input.InputHandler;
import com.carracinggame.physics.CollisionDetector;
import com.carracinggame.render.GameRenderer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class RaceScene implements AppScene {

    private final Game game;
    private final Scene scene;
    private final GraphicsContext gc;

    private GameLoop loop;

    private final InputHandler input = new InputHandler();
    private final CollisionDetector collision = new CollisionDetector();
    private final GameRenderer renderer = new GameRenderer();

    private final PlayerCar player;

    private double scrollOffset = 0;

    public RaceScene(Game game) {
        this.game = game;

        Canvas canvas = new Canvas(GameConfig.WIDTH, GameConfig.HEIGHT);
        this.gc = canvas.getGraphicsContext2D();

        StackPane root = new StackPane(canvas);
        this.scene = new Scene(root, GameConfig.WIDTH, GameConfig.HEIGHT);

        input.attach(scene);

        scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                game.switchState(GameState.MENU);
                e.consume();
                return;
            }

            if (e.getCode() == KeyCode.R) {
                resetPlayer();
                e.consume();
            }
        });

        Image carSprite = resolveCarSprite();
        this.player = new PlayerCar(
                GameConfig.PLAYER_START_X,
                GameConfig.PLAYER_START_Y,
                GameConfig.CAR_W,
                GameConfig.CAR_H,
                carSprite,
                input
        );
    }

    private Image resolveCarSprite() {
        try {
            if (game.getGarageService() != null && game.getGarageService().getEquippedCar() != null) {
                CarDefinition equippedCar = game.getGarageService().getEquippedCar();
                String path = equippedCar.getSpritePath();
                if (path != null && !path.isBlank()) {
                    return loadImageOrNull(path);
                }
            }
        } catch (Exception ignored) {
        }

        return loadImageOrNull("/images/cars/red_car.png");
    }

    private Image loadImageOrNull(String path) {
        try {
            var url = getClass().getResource(path);
            if (url == null) {
                return null;
            }
            return new Image(url.toExternalForm());
        } catch (Exception e) {
            return null;
        }
    }

    private void resetPlayer() {
        player.setX(GameConfig.PLAYER_START_X);
        player.setY(GameConfig.PLAYER_START_Y);
        player.setAngleDeg(0);
        player.setSpeed(0);
        scrollOffset = 0;
    }

    @Override
    public Scene getScene() {
        return scene;
    }

    @Override
    public void onShow() {
        input.clear();
        requestFocus();

        loop = new GameLoop(new GameLoop.Tick() {
            @Override
            public void update(double deltaTime) {
                player.update();
                collision.clampToTrack(player, GameConfig.TRACK_X, GameConfig.TRACK_W, GameConfig.HEIGHT);

                scrollOffset += player.getSpeed();
            }

            @Override
            public void render() {
                renderer.clear(gc, GameConfig.WIDTH, GameConfig.HEIGHT);
                renderer.drawBackground(gc, GameConfig.WIDTH, GameConfig.HEIGHT);
                renderer.drawTrack(gc, GameConfig.TRACK_X, GameConfig.TRACK_W, GameConfig.HEIGHT);
                renderer.drawCar(gc, player);
                drawHud();
            }
        });

        loop.start();
    }

    @Override
    public void onHide() {
        if (loop != null) {
            loop.stop();
            loop = null;
        }
        input.detach();
        input.clear();
    }

    private void drawHud() {
        renderer.drawHudBox(gc, 16, 16, 250, 92);

        renderer.drawText(gc,
                "Map: " + game.getSelectedMap(),
                28, 40, 16, Color.WHITE);

        renderer.drawText(gc,
                "Speed: " + String.format("%.2f", player.getSpeed()),
                28, 62, 14, Color.WHITE);

        renderer.drawText(gc,
                "ESC: Menu | R: Reset",
                28, 84, 13, Color.WHITE);
    }
}