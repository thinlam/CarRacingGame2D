package com.carracinggame.scene;

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

public class RaceScene implements AppScene {

    private final Game game;
    private final Scene scene;
    private final GraphicsContext gc;

    private GameLoop loop;

    private final InputHandler input = new InputHandler();
    private final CollisionDetector collision = new CollisionDetector();
    private final GameRenderer renderer = new GameRenderer();

    private final PlayerCar player;

    public RaceScene(Game game) {
        this.game = game;

        Canvas canvas = new Canvas(GameConfig.WIDTH, GameConfig.HEIGHT);
        this.gc = canvas.getGraphicsContext2D();

        this.scene = new Scene(new StackPane(canvas), GameConfig.WIDTH, GameConfig.HEIGHT);

        input.attach(scene);

        // ESC về Menu (không đè input)
        scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                game.switchState(GameState.MENU);
                e.consume();
            }
        });

        Image carSprite = loadImage("/images/cars/red_car.png");
        this.player = new PlayerCar(380, 450, GameConfig.CAR_W, GameConfig.CAR_H, carSprite, input);
    }

    private Image loadImage(String path) {
        var url = getClass().getResource(path);
        if (url == null) {
            throw new IllegalStateException("Không tìm thấy ảnh: " + path + " (đặt trong src/main/resources)");
        }
        return new Image(url.toExternalForm());
    }

    @Override
    public Scene getScene() {
        return scene;
    }

    @Override
    public void onShow() {
        input.clear();
        scene.getRoot().requestFocus(); // cực quan trọng để nhận phím

        loop = new GameLoop(new GameLoop.Tick() {
            @Override
            public void update() {
                player.update();
                collision.clampToTrack(player, GameConfig.TRACK_X, GameConfig.TRACK_W, GameConfig.HEIGHT);
            }

            @Override
            public void render() {
                renderer.drawBackground(gc, GameConfig.WIDTH, GameConfig.HEIGHT);
                renderer.drawTrack(gc, GameConfig.TRACK_X, GameConfig.TRACK_W, GameConfig.HEIGHT);
                renderer.drawCar(gc, player);
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
        input.clear();
    }
}
