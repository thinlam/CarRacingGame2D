package com.carracinggame.scene;

import com.carracinggame.car.CarId;
import com.carracinggame.core.Game;
import com.carracinggame.core.GameState;
import com.carracinggame.map.MapId;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class RaceScene implements AppScene {

    private static final double VIEW_W = 520;
    private static final double VIEW_H = 760;

    private static final double ROAD_W = 300;
    private static final double ROAD_X = (VIEW_W - ROAD_W) / 2.0;

    private static final double PLAYER_Y = 625;
    private static final double TRACK_LENGTH = 7000;

    private final Game game;
    private final Scene scene;

    private final Canvas canvas;
    private final Pane actorLayer;
    private final Canvas miniMapCanvas;

    private final Label speedLabel;
    private final Label distanceLabel;
    private final Label mapLabel;
    private final Label tipLabel;
    private final Label countdownLabel;

    private final Set<KeyCode> pressedKeys = new HashSet<>();
    private final List<AiCar> aiCars = new ArrayList<>();
    private final Random random = new Random();

    private final StackPane playerActor;
    private AnimationTimer timer;
    private long lastFrame = 0L;

    private double playerSpeed = 0;
    private double playerDistance = 0;
    private double playerX = laneCenter(1);

    private double roadScroll = 0;
    private boolean finished = false;

    private double countdownTime = 4.0;
    private boolean raceStarted = false;

    public RaceScene(Game game) {
        this.game = game;

        this.canvas = new Canvas(VIEW_W, VIEW_H);
        this.actorLayer = new Pane();
        this.actorLayer.setPrefSize(VIEW_W, VIEW_H);
        this.actorLayer.setMinSize(VIEW_W, VIEW_H);
        this.actorLayer.setMaxSize(VIEW_W, VIEW_H);
        this.actorLayer.setMouseTransparent(true);

        this.miniMapCanvas = new Canvas(120, 160);

        this.speedLabel = new Label();
        this.distanceLabel = new Label();
        this.mapLabel = new Label();
        this.tipLabel = new Label("↑/W tăng tốc   •   ↓/S phanh   •   ← → / A D để né xe");

        this.countdownLabel = new Label("3");
        this.countdownLabel.setStyle("""
            -fx-font-size: 56px;
            -fx-font-weight: 900;
            -fx-text-fill: white;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.35), 16, 0.3, 0, 4);
        """);

        this.playerActor = createActor(
                game.getGarageService().getEquippedCar().getId(),
                2.15,
                92,
                138
        );

        BorderPane root = new BorderPane();
        root.setStyle("""
            -fx-background-color:
                linear-gradient(to bottom, #8ed4ff 0%, #bce8ff 48%, #dff5ff 100%);
        """);

        root.setTop(buildTopBar());

        StackPane raceRoot = new StackPane();
        raceRoot.setPadding(new Insets(16));

        StackPane raceFrame = new StackPane();
        raceFrame.setPrefSize(VIEW_W, VIEW_H);
        raceFrame.setMinSize(VIEW_W, VIEW_H);
        raceFrame.setMaxSize(VIEW_W, VIEW_H);
        raceFrame.setStyle("""
            -fx-background-color: rgba(255,255,255,0.20);
            -fx-background-radius: 28;
            -fx-border-color: rgba(255,255,255,0.46);
            -fx-border-radius: 28;
        """);

        raceFrame.getChildren().addAll(canvas, actorLayer, countdownLabel);
        raceRoot.getChildren().add(raceFrame);

        VBox miniBox = buildMiniMapBox();
        raceRoot.getChildren().add(miniBox);
        StackPane.setAlignment(miniBox, Pos.TOP_RIGHT);
        StackPane.setMargin(miniBox, new Insets(26, 26, 0, 0));

        root.setCenter(raceRoot);
        root.setBottom(buildBottomBar());

        this.scene = new Scene(root);

        configureInput();
        initActors();
        initTimer();
        redraw();
    }

    @Override
    public Scene getScene() {
        return scene;
    }

    @Override
    public void onShow() {
        scene.getRoot().requestFocus();
        lastFrame = 0L;
        finished = false;
        raceStarted = false;
        countdownTime = 4.0;
        playerSpeed = 0;
        playerDistance = 0;
        roadScroll = 0;
        playerX = laneCenter(1);
        initActors();
        timer.start();
    }

    @Override
    public void onHide() {
        if (timer != null) {
            timer.stop();
        }
        pressedKeys.clear();
    }

    private Node buildTopBar() {
        HBox top = new HBox(16);
        top.setAlignment(Pos.CENTER_LEFT);
        top.setPadding(new Insets(14, 18, 12, 18));
        top.setStyle("""
            -fx-background-color: rgba(255,255,255,0.46);
            -fx-background-radius: 0 0 22 22;
        """);

        Label title = new Label("RACE - MAP MIỀN BẮC");
        title.setStyle("""
            -fx-font-size: 22px;
            -fx-font-weight: 900;
            -fx-text-fill: #123b62;
        """);

        speedLabel.setStyle("""
            -fx-font-size: 14px;
            -fx-font-weight: 800;
            -fx-text-fill: #123b62;
        """);

        distanceLabel.setStyle("""
            -fx-font-size: 14px;
            -fx-font-weight: 800;
            -fx-text-fill: #123b62;
        """);

        mapLabel.setStyle("""
            -fx-font-size: 14px;
            -fx-font-weight: 800;
            -fx-text-fill: #123b62;
        """);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button backBtn = new Button("Quay lại");
        backBtn.setStyle("""
            -fx-background-radius: 16;
            -fx-background-color: #1f4e79;
            -fx-text-fill: white;
            -fx-font-weight: 900;
        """);
        backBtn.setOnAction(e -> game.goTo(GameState.MENU));

        top.getChildren().addAll(title, speedLabel, distanceLabel, mapLabel, spacer, backBtn);
        return top;
    }

    private VBox buildMiniMapBox() {
        VBox miniBox = new VBox(8);
        miniBox.setAlignment(Pos.TOP_CENTER);
        miniBox.setPadding(new Insets(10));
        miniBox.setMaxSize(140, 200);
        miniBox.setStyle("""
            -fx-background-color: rgba(8,25,43,0.80);
            -fx-background-radius: 20;
            -fx-border-color: rgba(255,255,255,0.16);
            -fx-border-radius: 20;
        """);

        Label miniTitle = new Label("MINI MAP");
        miniTitle.setStyle("""
            -fx-font-size: 12px;
            -fx-font-weight: 900;
            -fx-text-fill: white;
        """);

        miniBox.getChildren().addAll(miniTitle, miniMapCanvas);
        return miniBox;
    }

    private Node buildBottomBar() {
        HBox bottom = new HBox();
        bottom.setAlignment(Pos.CENTER);
        bottom.setPadding(new Insets(0, 14, 16, 14));

        tipLabel.setStyle("""
            -fx-font-size: 13px;
            -fx-font-weight: 800;
            -fx-text-fill: #214d76;
        """);

        bottom.getChildren().add(tipLabel);
        return bottom;
    }

    private void configureInput() {
        scene.setOnKeyPressed(e -> pressedKeys.add(e.getCode()));
        scene.setOnKeyReleased(e -> pressedKeys.remove(e.getCode()));
    }

    private void initActors() {
        actorLayer.getChildren().clear();
        actorLayer.getChildren().add(playerActor);

        aiCars.clear();

        // 6 xe AI xếp ở vạch xuất phát
        aiCars.add(new AiCar(CarId.BLUE_STORM, laneCenter(0), PLAYER_Y - 40, 190));
        aiCars.add(new AiCar(CarId.BLACK_SHADOW, laneCenter(2), PLAYER_Y - 70, 205));
        aiCars.add(new AiCar(CarId.RED_RACER, laneCenter(3), PLAYER_Y - 120, 180));
        aiCars.add(new AiCar(CarId.BLUE_STORM, laneCenter(1), PLAYER_Y - 170, 210));
        aiCars.add(new AiCar(CarId.BLACK_SHADOW, laneCenter(0), PLAYER_Y - 240, 195));
        aiCars.add(new AiCar(CarId.RED_RACER, laneCenter(2), PLAYER_Y - 300, 185));

        for (AiCar aiCar : aiCars) {
            actorLayer.getChildren().add(aiCar.actor);
        }
    }

    private void initTimer() {
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastFrame == 0L) {
                    lastFrame = now;
                    redraw();
                    return;
                }

                double dt = (now - lastFrame) / 1_000_000_000.0;
                lastFrame = now;

                update(dt);
                redraw();
            }
        };
    }

    private void update(double dt) {
        if (finished) {
            return;
        }

        updateCountdown(dt);

        if (raceStarted) {
            updatePlayerInput(dt);
            updatePlayerState(dt);
            updateAiCars(dt);
        } else {
            playerSpeed = 0;
        }

        updateHud();

        if (playerDistance >= TRACK_LENGTH) {
            finishRace();
        }
    }

    private void updateCountdown(double dt) {
        countdownTime -= dt;

        if (countdownTime > 3) {
            countdownLabel.setText("3");
            countdownLabel.setVisible(true);
        } else if (countdownTime > 2) {
            countdownLabel.setText("2");
            countdownLabel.setVisible(true);
        } else if (countdownTime > 1) {
            countdownLabel.setText("1");
            countdownLabel.setVisible(true);
        } else if (countdownTime > 0) {
            countdownLabel.setText("LET'S GO!");
            countdownLabel.setVisible(true);
        } else {
            countdownLabel.setVisible(false);
            raceStarted = true;
        }
    }

    private void updatePlayerInput(double dt) {
        if (isPressed(KeyCode.UP, KeyCode.W)) {
            playerSpeed += 250 * dt;
        } else {
            playerSpeed -= 70 * dt;
        }

        if (isPressed(KeyCode.DOWN, KeyCode.S)) {
            playerSpeed -= 300 * dt;
        }

        playerSpeed = clamp(playerSpeed, 0, 520);

        double steer = 240 * dt;

        if (isPressed(KeyCode.LEFT, KeyCode.A)) {
            playerX -= steer;
        }
        if (isPressed(KeyCode.RIGHT, KeyCode.D)) {
            playerX += steer;
        }

        double minX = ROAD_X + 30;
        double maxX = ROAD_X + ROAD_W - 30;
        playerX = clamp(playerX, minX, maxX);
    }

    private void updatePlayerState(double dt) {
        playerDistance += playerSpeed * dt;
        roadScroll += playerSpeed * dt;
    }

    private void updateAiCars(double dt) {
        for (AiCar aiCar : aiCars) {
            aiCar.screenY += (playerSpeed - aiCar.speed) * dt;

            if (aiCar.screenY > VIEW_H + 120) {
                aiCar.screenY = -140 - random.nextInt(260);
                aiCar.x = laneCenter(random.nextInt(4));
                aiCar.speed = 160 + random.nextInt(110);
            }

            boolean collideX = Math.abs(aiCar.x - playerX) < 38;
            boolean collideY = Math.abs(aiCar.screenY - PLAYER_Y) < 56;

            if (collideX && collideY) {
                playerSpeed *= 0.80;
                playerX += aiCar.x >= playerX ? -24 : 24;
            }
        }
    }

    private void redraw() {
        drawWorld();
        positionActors();
        drawMiniMap();
    }

    private void drawWorld() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, VIEW_W, VIEW_H);

        gc.setFill(Color.web("#2e9d34"));
        gc.fillRect(0, 0, VIEW_W, VIEW_H);

        drawGrassPattern(gc, 0, 0, ROAD_X, VIEW_H);
        drawGrassPattern(gc, ROAD_X + ROAD_W, 0, VIEW_W - (ROAD_X + ROAD_W), VIEW_H);

        drawRoadBarrier(gc, ROAD_X - 18, 0, 18, VIEW_H);
        drawRoadBarrier(gc, ROAD_X + ROAD_W, 0, 18, VIEW_H);

        gc.setFill(Color.web("#4b4b4b"));
        gc.fillRect(ROAD_X, 0, ROAD_W, VIEW_H);

        gc.setStroke(Color.web("#d8d8d8"));
        gc.setLineWidth(3);
        gc.strokeLine(ROAD_X + 4, 0, ROAD_X + 4, VIEW_H);
        gc.strokeLine(ROAD_X + ROAD_W - 4, 0, ROAD_X + ROAD_W - 4, VIEW_H);

        double laneW = ROAD_W / 4.0;
        for (int i = 1; i < 4; i++) {
            double x = ROAD_X + laneW * i;
            drawDashedVertical(gc, x, roadScroll * 1.2, 38, 26);
        }

        gc.setFill(Color.rgb(255, 255, 255, 0.03));
        gc.fillRect(ROAD_X + ROAD_W * 0.25, 0, ROAD_W * 0.5, VIEW_H);

        drawStartLine(gc);
    }

    private void drawStartLine(GraphicsContext gc) {
        double lineY = PLAYER_Y + 70 + (roadScroll % 1600);
        if (lineY > -40 && lineY < VIEW_H + 40) {
            gc.setFill(Color.WHITE);
            for (int i = 0; i < 10; i++) {
                double x = ROAD_X + i * (ROAD_W / 10.0);
                if (i % 2 == 0) {
                    gc.fillRect(x, lineY, ROAD_W / 10.0, 10);
                }
            }
        }
    }

    private void drawGrassPattern(GraphicsContext gc, double x, double y, double w, double h) {
        gc.setFill(Color.web("#219c34"));
        gc.fillRect(x, y, w, h);

        for (int row = 0; row < 30; row++) {
            for (int col = 0; col < 5; col++) {
                double cx = x + 16 + col * 34 + ((row % 2) * 8);
                double cy = y + 10 + row * 30 - (roadScroll * 0.7 % 30);

                gc.setFill(Color.web("#7bd447"));
                gc.fillOval(cx, cy, 18, 18);

                gc.setFill(Color.web("#5bb730"));
                gc.fillOval(cx + 5, cy + 5, 8, 8);
            }
        }
    }

    private void drawRoadBarrier(GraphicsContext gc, double x, double y, double w, double h) {
        gc.setFill(Color.web("#4b4f56"));
        gc.fillRect(x, y, w, h);

        for (int i = 0; i < 42; i++) {
            double yy = i * 24 - (roadScroll % 24);
            gc.setStroke(Color.web("#7b7f86"));
            gc.setLineWidth(3);
            gc.strokeLine(x + 2, yy, x + w - 2, yy + 14);
            gc.strokeLine(x + w - 2, yy, x + 2, yy + 14);
        }
    }

    private void drawDashedVertical(GraphicsContext gc, double x, double offset, double dash, double gap) {
        double cycle = dash + gap;
        double start = -(offset % cycle);

        for (double y = start; y < VIEW_H; y += cycle) {
            gc.strokeLine(x, y, x, y + dash);
        }
    }

    private void positionActors() {
        setActorPosition(playerActor, playerX, PLAYER_Y, 92, 138, 1.0);
        playerActor.toFront();

        for (AiCar aiCar : aiCars) {
            boolean visible = aiCar.screenY > -120 && aiCar.screenY < VIEW_H + 80;
            aiCar.actor.setVisible(visible);

            if (visible) {
                setActorPosition(aiCar.actor, aiCar.x, aiCar.screenY, 92, 138, 1.0);
            }
        }
    }

    private void setActorPosition(StackPane actor, double centerX, double centerY,
                                  double width, double height, double scale) {
        actor.setLayoutX(centerX - width / 2.0);
        actor.setLayoutY(centerY - height / 2.0);
        actor.setScaleX(scale);
        actor.setScaleY(scale);
    }

    private void drawMiniMap() {
        GraphicsContext gc = miniMapCanvas.getGraphicsContext2D();
        double w = miniMapCanvas.getWidth();
        double h = miniMapCanvas.getHeight();

        gc.clearRect(0, 0, w, h);

        gc.setFill(Color.rgb(8, 28, 47, 0.96));
        gc.fillRoundRect(0, 0, w, h, 18, 18);

        gc.setStroke(Color.rgb(255, 255, 255, 0.15));
        gc.setLineWidth(1);
        gc.strokeRoundRect(1, 1, w - 2, h - 2, 18, 18);

        double roadMiniX = w / 2.0 - 14;
        gc.setFill(Color.web("#355a7a"));
        gc.fillRoundRect(roadMiniX, 12, 28, h - 24, 10, 10);

        drawMiniDot(gc, w / 2.0, miniY(playerDistance, h), Color.web("#ff5f57"), 6);

        for (AiCar aiCar : aiCars) {
            double aiDistance = clamp(playerDistance + (PLAYER_Y - aiCar.screenY) * 6.0, 0, TRACK_LENGTH);
            drawMiniDot(gc, w / 2.0, miniY(aiDistance, h), Color.web("#7dd3fc"), 4.5);
        }

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font(11));
        gc.fillText("START", 10, h - 8);
        gc.fillText("FINISH", 62, 14);
    }

    private double miniY(double distance, double height) {
        double t = clamp(distance, 0, TRACK_LENGTH) / TRACK_LENGTH;
        return (height - 14) - t * (height - 28);
    }

    private void updateHud() {
        speedLabel.setText("Tốc độ: " + (int) playerSpeed + " km/h");
        distanceLabel.setText("Quãng đường: " + (int) playerDistance + " / " + (int) TRACK_LENGTH);
        mapLabel.setText("Map: " + mapDisplayName());
    }

    private void finishRace() {
        finished = true;
        timer.stop();

        game.getPlayerProfile().addCoins(300);
        try {
            game.saveProgress();
        } catch (Exception ignored) {
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Về đích");
        alert.setHeaderText("Bạn đã hoàn thành chặng đua Miền Bắc!");
        alert.setContentText("Thưởng: +300 coin");
        alert.showAndWait();

        game.goTo(GameState.MENU);
    }

    private StackPane createActor(CarId carId, double scale, double width, double height) {
        Node carNode = CarViewFactory.createRaceCar(carId);
        carNode.setRotate(180); // đầu xe hướng lên trên

        carNode.setScaleX(scale);
        carNode.setScaleY(scale);

        StackPane wrapper = new StackPane(carNode);
        wrapper.setPrefSize(width, height);
        wrapper.setMinSize(width, height);
        wrapper.setMaxSize(width, height);
        wrapper.setMouseTransparent(true);
        return wrapper;
    }

    private double laneCenter(int lane) {
        double laneW = ROAD_W / 4.0;
        return ROAD_X + laneW * lane + laneW / 2.0;
    }

    private boolean isPressed(KeyCode... keys) {
        for (KeyCode key : keys) {
            if (pressedKeys.contains(key)) {
                return true;
            }
        }
        return false;
    }

    private void drawMiniDot(GraphicsContext gc, double x, double y, Color color, double size) {
        gc.setFill(color);
        gc.fillOval(x - size / 2, y - size / 2, size, size);
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(1);
        gc.strokeOval(x - size / 2, y - size / 2, size, size);
    }

    private String mapDisplayName() {
        MapId mapId = game.getSelectedMap();
        if (mapId == null) {
            return "Miền Bắc";
        }

        return switch (mapId) {
            case NORTH -> "Miền Bắc";
            case CENTRAL -> "Miền Trung";
            case SOUTH -> "Miền Nam";
        };
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private final class AiCar {
        private double x;
        private double screenY;
        private double speed;
        private final StackPane actor;

        private AiCar(CarId carId, double x, double screenY, double speed) {
            this.x = x;
            this.screenY = screenY;
            this.speed = speed;
            this.actor = createActor(carId, 1.95, 92, 138);
        }
    }
}