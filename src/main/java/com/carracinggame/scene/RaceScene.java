package com.carracinggame.scene;

import com.carracinggame.car.AICar;
import com.carracinggame.car.PlayerCar;
import com.carracinggame.core.Game;
import com.carracinggame.core.GameConfig;
import com.carracinggame.core.GameLoop;
import com.carracinggame.core.GameState;
import com.carracinggame.core.GarageCar;
import com.carracinggame.input.InputHandler;
import com.carracinggame.map.CentralMap;
import com.carracinggame.map.GameMap;
import com.carracinggame.map.NorthMap;
import com.carracinggame.map.SouthMap;
import com.carracinggame.physics.CollisionDetector;
import com.carracinggame.render.GameRenderer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class RaceScene implements AppScene {

    private final Game game;
    private final Scene scene;
    private final GraphicsContext gc;
    private StackPane root = null;

    private final InputHandler input = new InputHandler();
    private final CollisionDetector collision = new CollisionDetector();
    private final GameRenderer renderer = new GameRenderer();
    private final Random random = new Random();

    private final PlayerCar player;
    private final GameMap currentMap;
    private final int targetDistance;
    private final List<AICar> trafficCars = new ArrayList<>();
    private final List<Image> trafficSprites;

    private final VBox pauseOverlay;

    private GameLoop loop;
    private double progressMeters;
    private boolean paused;
    private boolean finishTriggered;
    private int crashFrames;
    private double spawnCooldown;

    public RaceScene(Game game) {
        this.game = game;

        Canvas canvas = new Canvas();
        canvas.widthProperty().bind(root.widthProperty());
        canvas.heightProperty().bind(root.heightProperty());
        this.gc = canvas.getGraphicsContext2D();
        this.root = new StackPane(canvas);
        this.scene = new Scene(root, GameConfig.WIDTH, GameConfig.HEIGHT);

        input.attach(scene);

        this.currentMap = createMap();
        this.targetDistance = switch (game.getSelectedMap()) {
            case NORTH -> 2600;
            case CENTRAL -> 3000;
            case SOUTH -> 3400;
        };

        GarageCar car = game.getPlayerProfile().getEquippedCar();
        Image straight = loadImage(car.getImagePath());
        this.player = new PlayerCar(
                GameConfig.TRACK_X + GameConfig.TRACK_W / 2 - GameConfig.CAR_W / 2,
                455,
                GameConfig.CAR_W,
                GameConfig.CAR_H,
                straight,
                loadImage(car.getLeftImagePath()),
                loadImage(car.getRightImagePath()),
                loadImage(car.getCrashImagePath()),
                input,
                4.2 + car.getPower() / 35.0,
                4.2 + car.getHandling() / 28.0
        );
        this.trafficSprites = List.of(
                loadImage("/images/cars/traffic_1.png"),
                loadImage("/images/cars/traffic_2.png"),
                loadImage("/images/cars/thunder_r.png")
        );

        this.pauseOverlay = buildPauseOverlay();
        this.pauseOverlay.setVisible(false);
        root.getChildren().add(pauseOverlay);

        scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.P || e.getCode() == KeyCode.ESCAPE) {
                togglePause();
                e.consume();
            }
        });
    }

    private GameMap createMap() {
        return switch (game.getSelectedMap()) {
            case NORTH -> new NorthMap(GameConfig.WIDTH, GameConfig.HEIGHT, GameConfig.TRACK_X, GameConfig.TRACK_W, 3);
            case CENTRAL -> new CentralMap(GameConfig.WIDTH, GameConfig.HEIGHT, GameConfig.TRACK_X, GameConfig.TRACK_W, 3);
            case SOUTH -> new SouthMap(GameConfig.WIDTH, GameConfig.HEIGHT, GameConfig.TRACK_X, GameConfig.TRACK_W, 3);
        };
    }

    private Image loadImage(String path) {
        var url = getClass().getResource(path);
        if (url == null) {
            throw new IllegalStateException("Không tìm thấy ảnh: " + path);
        }
        return new Image(url.toExternalForm(), true);
    }

    @Override
    public Scene getScene() {
        return scene;
    }

    @Override
    public void onShow() {
        input.clear();
        trafficCars.clear();
        progressMeters = 0;
        paused = false;
        finishTriggered = false;
        crashFrames = 0;
        spawnCooldown = 18;
        pauseOverlay.setVisible(false);

        player.reset(GameConfig.TRACK_X + GameConfig.TRACK_W / 2 - GameConfig.CAR_W / 2, 455);
        scene.getRoot().requestFocus();

        loop = new GameLoop(new GameLoop.Tick() {
            @Override
            public void update() {
                tickUpdate();
            }

            @Override
            public void render() {
                tickRender();
            }
        });
        loop.start();
    }

    private void tickUpdate() {
        if (paused || finishTriggered) {
            return;
        }

        player.update();
        collision.clampToTrack(player, currentMap.getTrackX(), currentMap.getTrackW(), GameConfig.HEIGHT);
        double roadSpeed = Math.max(2.4, player.getSpeed() * 1.25 + 1.2);
        currentMap.update(roadSpeed);

        if (crashFrames > 0) {
            crashFrames--;
            updateTraffic(roadSpeed * 0.65);
            if (crashFrames == 0) {
                finishTriggered = true;
                game.failRace((int) Math.round(progressMeters));
            }
            return;
        }

        progressMeters += Math.max(0, player.getSpeed()) * 1.7;

        spawnCooldown -= 1;
        if (spawnCooldown <= 0) {
            spawnTraffic();
            spawnCooldown = 28 + random.nextInt(24);
        }

        updateTraffic(roadSpeed);

        if (progressMeters >= targetDistance) {
            finishTriggered = true;
            game.finishRace((int) Math.round(progressMeters));
        }
    }

    private void updateTraffic(double roadSpeed) {
        Iterator<AICar> iterator = trafficCars.iterator();
        while (iterator.hasNext()) {
            AICar traffic = iterator.next();
            traffic.update(roadSpeed);
            if (traffic.isOutOfScreen(GameConfig.HEIGHT)) {
                iterator.remove();
                continue;
            }
            if (!player.isCrashed() && collision.intersects(player, traffic)) {
                player.crash();
                crashFrames = 26;
            }
        }
    }

    private void spawnTraffic() {
        if (trafficCars.size() >= 5) {
            return;
        }

        double laneWidth = currentMap.getTrackW() / currentMap.getLanes();
        int lane = random.nextInt(currentMap.getLanes());
        double x = currentMap.getTrackX() + lane * laneWidth + (laneWidth - GameConfig.CAR_W) / 2.0;
        double y = -100 - random.nextInt(220);
        Image sprite = trafficSprites.get(random.nextInt(trafficSprites.size()));
        double speed = 1.7 + random.nextDouble() * 2.2;

        for (AICar existing : trafficCars) {
            if (Math.abs(existing.getX() - x) < 10 && Math.abs(existing.getY() - y) < 180) {
                return;
            }
        }

        trafficCars.add(new AICar(x, y, GameConfig.CAR_W, GameConfig.CAR_H, sprite, speed));
    }

    private void tickRender() {
        currentMap.render(gc);
        for (AICar traffic : trafficCars) {
            renderer.drawCar(gc, traffic);
        }
        renderer.drawCar(gc, player);
        drawHud();
        drawFinishBar();
    }

    private void drawHud() {
        gc.setFill(Color.rgb(5, 12, 24, 0.78));
        gc.fillRoundRect(18, 18, 275, 122, 20, 20);

        gc.setFill(Color.WHITE);
        gc.fillText("MAP: " + game.getSelectedMapLabel(), 30, 42);
        gc.fillText("XE: " + game.getPlayerProfile().getEquippedCar().getDisplayName(), 30, 64);
        gc.fillText("Coins: " + String.format("%,d", game.getPlayerProfile().getCoins()), 30, 86);
        gc.fillText("Speed: " + (int) Math.round(player.getSpeed() * 24) + " km/h", 30, 108);
        gc.fillText("Progress: " + (int) progressMeters + " / " + targetDistance + "m", 30, 130);

        gc.setFill(Color.rgb(5, 12, 24, 0.72));
        gc.fillRoundRect(550, 18, 232, 56, 18, 18);
        gc.setFill(Color.web("#dff8ff"));
        gc.fillText("Controls: ← → move • ↑ accelerate • ↓ slow • P/ESC pause", 564, 51);
    }

    private void drawFinishBar() {
        double barX = 18;
        double barY = 154;
        double barW = 260;
        double progress = Math.min(1.0, progressMeters / targetDistance);

        gc.setFill(Color.rgb(255, 255, 255, 0.08));
        gc.fillRoundRect(barX, barY, barW, 10, 20, 20);
        gc.setFill(Color.web(player.isCrashed() ? "#ff5f5f" : "#2fd08b"));
        gc.fillRoundRect(barX, barY, barW * progress, 10, 20, 20);
    }

    private VBox buildPauseOverlay() {
        VBox box = new VBox(12);
        box.setAlignment(Pos.CENTER);
        box.setMaxWidth(280);
        box.setPadding(new Insets(22));
        box.setStyle("""
            -fx-background-color: rgba(7,17,34,0.90);
            -fx-background-radius: 26;
            -fx-border-radius: 26;
            -fx-border-color: rgba(110,215,255,0.22);
        """);

        Label title = label("PAUSE", 26, true, "#ffffff");
        Label sub = label("Tạm dừng cuộc đua. Bạn có thể tiếp tục, chơi lại hoặc quay về menu.", 12, false, "#9ab4d0");
        sub.setWrapText(true);

        Button resume = overlayButton("TIẾP TỤC", "#2fd08b", e -> togglePause());
        Button replay = overlayButton("CHƠI LẠI", "#4fa6ff", e -> game.switchState(GameState.RACE));
        Button menu = overlayButton("VỀ MENU", "#8d68ff", e -> game.switchState(GameState.MENU));

        box.getChildren().addAll(title, sub, resume, replay, menu);
        return box;
    }

    private Button overlayButton(String text, String color, EventHandler<ActionEvent> handler) {
        Button button = new Button(text);
        button.setMinWidth(220);
        button.setMinHeight(44);
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

    private void togglePause() {
        if (finishTriggered || crashFrames > 0) {
            return;
        }
        paused = !paused;
        pauseOverlay.setVisible(paused);
        if (!paused) {
            scene.getRoot().requestFocus();
        }
    }

    private Label label(String text, int size, boolean bold, String color) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setStyle("-fx-font-size: " + size + "px; -fx-font-weight: " + (bold ? "800" : "500") + "; -fx-text-fill: " + color + ";");
        return label;
    }

    @Override
    public void onHide() {
        if (loop != null) {
            loop.stop();
            loop = null;
        }
        input.clear();
        trafficCars.clear();
    }
}
