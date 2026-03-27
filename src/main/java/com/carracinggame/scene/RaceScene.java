package com.carracinggame.scene;

import com.carracinggame.car.CarId;
import com.carracinggame.core.Game;
import com.carracinggame.core.GameState;
import com.carracinggame.map.MapId;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
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
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class RaceScene implements AppScene {

    private static final double VIEW_W = 520;
    private static final double VIEW_H = 760;

    private static final double ROAD_W = 300;
    private static final double ROAD_X = (VIEW_W - ROAD_W) / 2.0;
    private static final int LANE_COUNT = 4;

    private static final double PLAYER_Y = 620;

    // Độ dài từng miền
    private static final double NORTH_TRACK_LENGTH = 15000;
    private static final double CENTRAL_TRACK_LENGTH = 18000;
    private static final double SOUTH_TRACK_LENGTH = 20000;

    private static final double LINE_SCREEN_OFFSET = 95;
    private static final double START_LINE_DISTANCE = 0;

    private static final double PLAYER_ACTOR_W = 64;
    private static final double PLAYER_ACTOR_H = 98;
    private static final double AI_ACTOR_W = 64;
    private static final double AI_ACTOR_H = 98;

    private static final double AI_MIN_GAP = 130;
    private static final double AI_RESPAWN_BASE_Y = -180;
    private static final double AI_RESPAWN_EXTRA_RANDOM = 120;
    private static final double AI_DESPAWN_Y = VIEW_H + 140;

    private static final int MAX_HITS = 2;
    private static final double PLAYER_INVULNERABLE_TIME = 1.25;
    private static final double SAFE_RESPAWN_Y_GAP = 140;

    private static final String DEFAULT_TIP_TEXT =
            "↑/W tăng tốc   •   ↓/S phanh   •   ← → / A D để né xe";

    private final Game game;
    private final Scene scene;

    private final Canvas canvas;
    private final Pane actorLayer;
    private final Canvas miniMapCanvas;

    private final Label titleLabel;
    private final Label speedLabel;
    private final Label distanceLabel;
    private final Label mapLabel;
    private final Label crashLabel;
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
    private double currentTrackLength;

    private double roadScroll = 0;
    private boolean finished = false;
    private boolean finishDialogShown = false;

    private double countdownTime = 4.0;
    private boolean raceStarted = false;

    private int hitCount = 0;
    private double invulnerableTime = 0;

    public RaceScene(Game game) {
        this.game = game;
        this.currentTrackLength = resolveTrackLength();

        this.canvas = new Canvas(VIEW_W, VIEW_H);

        this.actorLayer = new Pane();
        this.actorLayer.setPrefSize(VIEW_W, VIEW_H);
        this.actorLayer.setMinSize(VIEW_W, VIEW_H);
        this.actorLayer.setMaxSize(VIEW_W, VIEW_H);
        this.actorLayer.setMouseTransparent(true);

        Rectangle actorClip = new Rectangle(VIEW_W, VIEW_H);
        actorLayer.setClip(actorClip);

        this.miniMapCanvas = new Canvas(120, 160);

        this.titleLabel = new Label("RACE - MAP " + mapDisplayName().toUpperCase());
        this.speedLabel = new Label();
        this.distanceLabel = new Label();
        this.mapLabel = new Label();
        this.crashLabel = new Label();
        this.tipLabel = new Label(DEFAULT_TIP_TEXT);

        this.countdownLabel = new Label("3");
        this.countdownLabel.setStyle("""
            -fx-font-size: 56px;
            -fx-font-weight: 900;
            -fx-text-fill: white;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.35), 16, 0.3, 0, 4);
        """);

        this.playerActor = createPlayerActor(getEquippedCarId(), 1.35, PLAYER_ACTOR_W, PLAYER_ACTOR_H);

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

        Rectangle frameClip = new Rectangle(VIEW_W, VIEW_H);
        frameClip.setArcWidth(28);
        frameClip.setArcHeight(28);
        raceFrame.setClip(frameClip);

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
        updateHud();
        redraw();
    }

    @Override
    public Scene getScene() {
        return scene;
    }

    @Override
    public void onShow() {
        scene.getRoot().requestFocus();
        resetRace();
        timer.start();
    }

    @Override
    public void onHide() {
        if (timer != null) {
            timer.stop();
        }
        pressedKeys.clear();
    }

    private void resetRace() {
        lastFrame = 0L;
        finished = false;
        finishDialogShown = false;
        raceStarted = false;
        countdownTime = 4.0;

        currentTrackLength = resolveTrackLength();
        titleLabel.setText("RACE - MAP " + mapDisplayName().toUpperCase());

        playerSpeed = 0;
        playerDistance = 0;
        roadScroll = 0;
        playerX = laneCenter(1);

        hitCount = 0;
        invulnerableTime = 0;
        playerActor.setOpacity(1.0);
        tipLabel.setText(DEFAULT_TIP_TEXT);

        pressedKeys.clear();
        initActors();
        updateHud();
        redraw();
    }

    private Node buildTopBar() {
        HBox top = new HBox(16);
        top.setAlignment(Pos.CENTER_LEFT);
        top.setPadding(new Insets(14, 18, 12, 18));
        top.setStyle("""
            -fx-background-color: rgba(255,255,255,0.46);
            -fx-background-radius: 0 0 22 22;
        """);

        titleLabel.setStyle("""
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

        crashLabel.setStyle("""
            -fx-font-size: 14px;
            -fx-font-weight: 900;
            -fx-text-fill: #b22222;
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
        backBtn.setOnAction(e -> {
            if (timer != null) {
                timer.stop();
            }
            tryGoMenu();
        });

        top.getChildren().addAll(titleLabel, speedLabel, distanceLabel, mapLabel, crashLabel, spacer, backBtn);
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
        aiCars.clear();

        actorLayer.getChildren().add(playerActor);
        playerActor.toBack();

        for (int i = 0; i < 6; i++) {
            AiCar aiCar = new AiCar(
                    randomAiCarId(),
                    laneCenter(i % LANE_COUNT),
                    VIEW_H + 220 + i * 110,
                    180 + random.nextInt(40)
            );
            aiCar.actor.setVisible(false);
            aiCars.add(aiCar);
            actorLayer.getChildren().add(aiCar.actor);
            aiCar.actor.toFront();
        }
    }

    private void releaseAiCars() {
        List<Integer> laneOrder = new ArrayList<>(List.of(0, 1, 2, 3));
        Collections.shuffle(laneOrder, random);

        for (int i = 0; i < aiCars.size(); i++) {
            if (i > 0 && i % laneOrder.size() == 0) {
                Collections.shuffle(laneOrder, random);
            }

            AiCar aiCar = aiCars.get(i);
            int lane = laneOrder.get(i % laneOrder.size());

            aiCar.lane = lane;
            aiCar.x = laneCenter(lane);
            aiCar.screenY = -140 - i * (AI_MIN_GAP + 30);
            aiCar.speed = 160 + random.nextInt(110);
            aiCar.actor.setVisible(true);
            aiCar.resetCar(randomAiCarId());
            aiCar.actor.toFront();
        }

        playerActor.toBack();
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
            updateInvulnerability(dt);
        } else {
            playerSpeed = 0;
            updateInvulnerability(dt);
        }

        updateHud();

        if (playerDistance >= currentTrackLength) {
            playerDistance = currentTrackLength;
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

            if (!raceStarted) {
                raceStarted = true;
                releaseAiCars();
            }
        }
    }

    private void updateInvulnerability(double dt) {
        if (invulnerableTime > 0) {
            invulnerableTime -= dt;

            if (invulnerableTime > 0) {
                boolean blink = ((int) (invulnerableTime * 10)) % 2 == 0;
                playerActor.setOpacity(blink ? 0.42 : 1.0);
            } else {
                invulnerableTime = 0;
                playerActor.setOpacity(1.0);
            }
        } else {
            playerActor.setOpacity(1.0);
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
        if (playerDistance > currentTrackLength) {
            playerDistance = currentTrackLength;
        }
        roadScroll += playerSpeed * dt;
    }

    private void updateAiCars(double dt) {
        for (int lane = 0; lane < LANE_COUNT; lane++) {
            List<AiCar> laneCars = new ArrayList<>();

            for (AiCar aiCar : aiCars) {
                if (aiCar.lane == lane) {
                    laneCars.add(aiCar);
                }
            }

            laneCars.sort((a, b) -> Double.compare(a.screenY, b.screenY));

            for (int i = 0; i < laneCars.size(); i++) {
                AiCar aiCar = laneCars.get(i);

                double nextY = aiCar.screenY + (aiCar.speed + playerSpeed * 0.35) * dt;

                if (i < laneCars.size() - 1) {
                    AiCar frontCar = laneCars.get(i + 1);
                    double maxAllowedY = frontCar.screenY - AI_MIN_GAP;

                    if (nextY > maxAllowedY) {
                        nextY = maxAllowedY;
                    }
                }

                aiCar.screenY = nextY;
            }
        }

        for (AiCar aiCar : aiCars) {
            if (aiCar.screenY > AI_DESPAWN_Y) {
                respawnAiCar(aiCar);
            }
        }

        if (invulnerableTime > 0) {
            return;
        }

        for (AiCar aiCar : aiCars) {
            boolean collideX = Math.abs(aiCar.x - playerX) < 34;
            boolean collideY = Math.abs(aiCar.screenY - PLAYER_Y) < 48;

            if (collideX && collideY) {
                handlePlayerCollision(aiCar);
                break;
            }
        }
    }

    private void handlePlayerCollision(AiCar hitCar) {
        hitCount++;

        if (hitCount >= MAX_HITS) {
            gameOver();
            return;
        }

        removeHitObstacle(hitCar);

        playerSpeed = 0;
        playerX = laneCenter(findBestSafeLane());
        invulnerableTime = PLAYER_INVULNERABLE_TIME;

        playerActor.toBack();
        for (AiCar aiCar : aiCars) {
            aiCar.actor.toFront();
        }

        tipLabel.setText("Bạn đã va chạm 1 lần! Va chạm thêm 1 lần nữa sẽ GAME OVER.");
    }

    private void removeHitObstacle(AiCar hitCar) {
        hitCar.actor.setVisible(false);
        respawnAiCar(hitCar);
    }

    private int findBestSafeLane() {
        int currentLane = nearestLane(playerX);
        double currentSafety = laneSafetyScore(currentLane);

        if (currentSafety >= SAFE_RESPAWN_Y_GAP) {
            return currentLane;
        }

        int bestLane = currentLane;
        double bestSafety = currentSafety;

        for (int lane = 0; lane < LANE_COUNT; lane++) {
            double safety = laneSafetyScore(lane);
            if (safety > bestSafety) {
                bestSafety = safety;
                bestLane = lane;
            }
        }

        return bestLane;
    }

    private double laneSafetyScore(int lane) {
        double minGap = Double.MAX_VALUE;

        for (AiCar aiCar : aiCars) {
            if (aiCar.lane != lane) {
                continue;
            }

            double gap = Math.abs(aiCar.screenY - PLAYER_Y);
            if (gap < minGap) {
                minGap = gap;
            }
        }

        return minGap == Double.MAX_VALUE ? 99999 : minGap;
    }

    private int nearestLane(double x) {
        int bestLane = 0;
        double bestDistance = Double.MAX_VALUE;

        for (int lane = 0; lane < LANE_COUNT; lane++) {
            double dist = Math.abs(laneCenter(lane) - x);
            if (dist < bestDistance) {
                bestDistance = dist;
                bestLane = lane;
            }
        }

        return bestLane;
    }

    private void gameOver() {
        if (finished || finishDialogShown) {
            return;
        }

        finished = true;
        finishDialogShown = true;

        if (timer != null) {
            timer.stop();
        }

        playerSpeed = 0;
        playerActor.setOpacity(1.0);

        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText("Bạn đã va chạm 2 lần!");
            alert.setContentText("Game Over. Hãy thử lại nhé!");
            alert.showAndWait();
            tryGoMenu();
        });
    }

    private void respawnAiCar(AiCar aiCar) {
        int bestLane = 0;
        double bestSpawnY = -99999;

        for (int lane = 0; lane < LANE_COUNT; lane++) {
            double candidateY = computeSpawnYForLane(aiCar, lane);

            if (candidateY > bestSpawnY) {
                bestSpawnY = candidateY;
                bestLane = lane;
            } else if (Math.abs(candidateY - bestSpawnY) < 0.001 && random.nextBoolean()) {
                bestSpawnY = candidateY;
                bestLane = lane;
            }
        }

        aiCar.lane = bestLane;
        aiCar.x = laneCenter(bestLane);
        aiCar.screenY = bestSpawnY;
        aiCar.speed = 160 + random.nextInt(110);
        aiCar.resetCar(randomAiCarId());
        aiCar.actor.setVisible(true);
        aiCar.actor.toFront();
    }

    private double computeSpawnYForLane(AiCar self, int lane) {
        double topMostY = Double.POSITIVE_INFINITY;

        for (AiCar other : aiCars) {
            if (other == self) {
                continue;
            }
            if (other.lane != lane) {
                continue;
            }
            if (other.screenY < topMostY) {
                topMostY = other.screenY;
            }
        }

        double baseSpawn = AI_RESPAWN_BASE_Y - random.nextInt((int) AI_RESPAWN_EXTRA_RANDOM);

        if (topMostY == Double.POSITIVE_INFINITY) {
            return baseSpawn;
        }

        double safeSpawn = topMostY - AI_MIN_GAP - random.nextInt(35);
        return Math.min(baseSpawn, safeSpawn);
    }

    private void redraw() {
        drawWorld();
        positionActors();
        drawMiniMap();
    }

    private void drawWorld() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, VIEW_W, VIEW_H);

        MapId mapId = getSafeMapId();

        switch (mapId) {
            case NORTH -> {
                gc.setFill(Color.web("#9fd7ff"));
                gc.fillRect(0, 0, VIEW_W, VIEW_H);
                gc.setFill(Color.rgb(255, 255, 255, 0.20));
                gc.fillRect(0, 0, ROAD_X, VIEW_H);
                gc.fillRect(ROAD_X + ROAD_W, 0, VIEW_W - (ROAD_X + ROAD_W), VIEW_H);
            }
            case CENTRAL -> {
                gc.setFill(Color.web("#ffd59e"));
                gc.fillRect(0, 0, VIEW_W, VIEW_H);
                gc.setFill(Color.rgb(255, 248, 220, 0.22));
                gc.fillRect(0, 0, ROAD_X, VIEW_H);
                gc.fillRect(ROAD_X + ROAD_W, 0, VIEW_W - (ROAD_X + ROAD_W), VIEW_H);
            }
            case SOUTH -> {
                gc.setFill(Color.web("#8fe0b4"));
                gc.fillRect(0, 0, VIEW_W, VIEW_H);
                gc.setFill(Color.rgb(255, 255, 255, 0.16));
                gc.fillRect(0, 0, ROAD_X, VIEW_H);
                gc.fillRect(ROAD_X + ROAD_W, 0, VIEW_W - (ROAD_X + ROAD_W), VIEW_H);
            }
        }

        drawRegionDecor(gc, mapId);

        drawRoadBarrier(gc, ROAD_X - 18, 0, 18, VIEW_H);
        drawRoadBarrier(gc, ROAD_X + ROAD_W, 0, 18, VIEW_H);

        gc.setFill(Color.web("#4b4b4b"));
        gc.fillRect(ROAD_X, 0, ROAD_W, VIEW_H);

        gc.setStroke(Color.web("#d8d8d8"));
        gc.setLineWidth(3);
        gc.strokeLine(ROAD_X + 4, 0, ROAD_X + 4, VIEW_H);
        gc.strokeLine(ROAD_X + ROAD_W - 4, 0, ROAD_X + ROAD_W - 4, VIEW_H);

        double laneW = ROAD_W / (double) LANE_COUNT;
        for (int i = 1; i < LANE_COUNT; i++) {
            double x = ROAD_X + laneW * i;
            drawDashedVertical(gc, x, roadScroll * 1.2, 38, 26);
        }

        gc.setFill(Color.rgb(255, 255, 255, 0.03));
        gc.fillRect(ROAD_X + ROAD_W * 0.25, 0, ROAD_W * 0.5, VIEW_H);

        drawStartAndFinishLines(gc);
    }

    private void drawRegionDecor(GraphicsContext gc, MapId mapId) {
        switch (mapId) {
            case NORTH -> drawNorthDecor(gc);
            case CENTRAL -> drawCentralDecor(gc);
            case SOUTH -> drawSouthDecor(gc);
        }
    }

    private void drawNorthDecor(GraphicsContext gc) {
        // Hồ / núi / sương
        gc.setFill(Color.rgb(0, 80, 120, 0.30));
        gc.fillRoundRect(20, 110, ROAD_X - 40, 220, 40, 40);

        gc.setFill(Color.rgb(230, 230, 230, 0.85));
        gc.fillRoundRect(78, 210, 56, 68, 10, 10);
        gc.setFill(Color.rgb(120, 120, 120, 0.85));
        gc.fillRect(78, 205, 56, 10);

        gc.setFill(Color.rgb(80, 120, 90, 0.55));
        gc.fillPolygon(
                new double[]{30, 170, 310},
                new double[]{520, 260, 520},
                3
        );
        gc.fillPolygon(
                new double[]{VIEW_W - 30, VIEW_W - 170, VIEW_W - 310},
                new double[]{520, 260, 520},
                3
        );

        gc.setFill(Color.rgb(255, 255, 255, 0.16));
        gc.fillRect(0, 0, VIEW_W, VIEW_H);
    }

    private void drawCentralDecor(GraphicsContext gc) {
        // Nắng - biển - đồi cát
        gc.setFill(Color.rgb(0, 110, 170, 0.28));
        gc.fillRect(0, 0, ROAD_X, VIEW_H);
        gc.fillRect(ROAD_X + ROAD_W, 0, VIEW_W - (ROAD_X + ROAD_W), VIEW_H);

        gc.setFill(Color.rgb(255, 220, 140, 0.70));
        gc.fillOval(25, 420, 150, 90);
        gc.fillOval(VIEW_W - 170, 380, 145, 100);

        gc.setFill(Color.rgb(200, 140, 70, 0.55));
        gc.fillOval(70, 170, 80, 140);
        gc.fillOval(VIEW_W - 150, 150, 82, 145);

        gc.setFill(Color.rgb(255, 245, 210, 0.30));
        gc.fillOval(55, 70, 50, 50);
    }

    private void drawSouthDecor(GraphicsContext gc) {
        // Sông nước - cây dừa - đồng bằng
        gc.setFill(Color.rgb(0, 120, 160, 0.25));
        gc.fillRoundRect(18, 150, ROAD_X - 36, 160, 30, 30);
        gc.fillRoundRect(ROAD_X + ROAD_W + 18, 220, VIEW_W - (ROAD_X + ROAD_W) - 36, 170, 30, 30);

        gc.setStroke(Color.rgb(90, 55, 25, 0.85));
        gc.setLineWidth(6);

        gc.strokeLine(60, 520, 85, 420);
        gc.setFill(Color.rgb(30, 150, 70, 0.85));
        gc.fillOval(72, 388, 26, 54);
        gc.fillOval(55, 395, 42, 18);
        gc.fillOval(70, 400, 45, 16);

        gc.strokeLine(VIEW_W - 60, 520, VIEW_W - 85, 420);
        gc.setFill(Color.rgb(30, 150, 70, 0.85));
        gc.fillOval(VIEW_W - 98, 388, 26, 54);
        gc.fillOval(VIEW_W - 110, 395, 42, 18);
        gc.fillOval(VIEW_W - 100, 400, 45, 16);

        gc.setFill(Color.rgb(80, 170, 90, 0.35));
        gc.fillOval(35, 95, 120, 70);
        gc.fillOval(VIEW_W - 155, 110, 120, 70);
    }

    private void drawStartAndFinishLines(GraphicsContext gc) {
        double startY = PLAYER_Y - LINE_SCREEN_OFFSET - (START_LINE_DISTANCE - playerDistance);
        double finishY = PLAYER_Y + LINE_SCREEN_OFFSET - (currentTrackLength - playerDistance);

        drawCheckLine(gc, startY, Color.WHITE, Color.web("#d9d9d9"));
        drawCheckLine(gc, finishY, Color.WHITE, Color.BLACK);

        if (startY >= -20 && startY <= VIEW_H + 20) {
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font(14));
            gc.fillText("START", ROAD_X + ROAD_W + 14, startY + 10);
        }

        if (finishY >= -20 && finishY <= VIEW_H + 20) {
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font(14));
            gc.fillText("FINISH", ROAD_X + ROAD_W + 14, finishY + 10);
        }
    }

    private void drawCheckLine(GraphicsContext gc, double y, Color colorA, Color colorB) {
        if (y < -20 || y > VIEW_H + 20) {
            return;
        }

        double blockW = ROAD_W / 6.0;
        double lineH = 12;

        for (int i = 0; i < 6; i++) {
            double x = ROAD_X + i * blockW;
            gc.setFill(i % 2 == 0 ? colorA : colorB);
            gc.fillRect(x, y, blockW, lineH);
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
        setActorPosition(playerActor, playerX, PLAYER_Y, PLAYER_ACTOR_W, PLAYER_ACTOR_H, 1.0);
        playerActor.toBack();

        for (AiCar aiCar : aiCars) {
            boolean visible = raceStarted && aiCar.screenY > -160 && aiCar.screenY < VIEW_H + 110;
            aiCar.actor.setVisible(visible);

            if (visible) {
                setActorPosition(aiCar.actor, aiCar.x, aiCar.screenY, AI_ACTOR_W, AI_ACTOR_H, 1.0);
                aiCar.actor.toFront();
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
            if (!raceStarted) {
                continue;
            }
            double aiDistance = clamp(playerDistance + (PLAYER_Y - aiCar.screenY) * 6.0, 0, currentTrackLength);
            drawMiniDot(gc, w / 2.0, miniY(aiDistance, h), Color.web("#7dd3fc"), 4.5);
        }

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font(11));
        gc.fillText("START", 10, h - 8);
        gc.fillText("FINISH", 62, 14);
    }

    private double miniY(double distance, double height) {
        double t = clamp(distance, 0, currentTrackLength) / currentTrackLength;
        return (height - 14) - t * (height - 28);
    }

    private void updateHud() {
        speedLabel.setText("Tốc độ: " + (int) playerSpeed + " km/h");
        distanceLabel.setText("Quãng đường: " + (int) playerDistance + " / " + (int) currentTrackLength + " km");
        mapLabel.setText("Map: " + mapDisplayName());
        crashLabel.setText("Va chạm: " + hitCount + " / " + MAX_HITS);
    }

    private void finishRace() {
        if (finished || finishDialogShown) {
            return;
        }

        finished = true;
        finishDialogShown = true;

        if (timer != null) {
            timer.stop();
        }

        try {
            game.getPlayerProfile().addCoins(300);
        } catch (Exception ignored) {
        }

        try {
            game.saveProgress();
        } catch (Exception ignored) {
        }

        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Về đích");
            alert.setHeaderText("Bạn đã hoàn thành chặng đua " + mapDisplayName() + "!");
            alert.setContentText("Thưởng: +300 coin");
            alert.showAndWait();
            tryGoMenu();
        });
    }

    private StackPane createPlayerActor(CarId carId, double scale, double width, double height) {
        Node carNode = CarViewFactory.createPlayerRaceCar(carId);
        carNode.setScaleX(scale);
        carNode.setScaleY(scale);

        StackPane wrapper = new StackPane(carNode);
        wrapper.setPrefSize(width, height);
        wrapper.setMinSize(width, height);
        wrapper.setMaxSize(width, height);
        wrapper.setMouseTransparent(true);
        return wrapper;
    }

    private StackPane createAiActor(CarId carId, double scale, double width, double height) {
        Node carNode = CarViewFactory.createObstacleRaceCar(carId);
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
        double laneW = ROAD_W / (double) LANE_COUNT;
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
        return switch (getSafeMapId()) {
            case NORTH -> "Miền Bắc";
            case CENTRAL -> "Miền Trung";
            case SOUTH -> "Miền Nam";
        };
    }

    private MapId getSafeMapId() {
        try {
            MapId mapId = game.getSelectedMap();
            return mapId == null ? MapId.NORTH : mapId;
        } catch (Exception e) {
            return MapId.NORTH;
        }
    }

    private double resolveTrackLength() {
        return switch (getSafeMapId()) {
            case NORTH -> NORTH_TRACK_LENGTH;
            case CENTRAL -> CENTRAL_TRACK_LENGTH;
            case SOUTH -> SOUTH_TRACK_LENGTH;
        };
    }

    private CarId getEquippedCarId() {
        try {
            if (game.getGarageService() != null
                    && game.getGarageService().getEquippedCar() != null
                    && game.getGarageService().getEquippedCar().getId() != null) {
                return game.getGarageService().getEquippedCar().getId();
            }
        } catch (Exception ignored) {
        }
        return CarId.BLUE_STORM;
    }

    private CarId randomAiCarId() {
        CarId[] ids = {
                CarId.RED_RACER,
                CarId.BLUE_STORM,
                CarId.GREEN_SHADOW
        };
        return ids[random.nextInt(ids.length)];
    }

    private void tryGoMenu() {
        try {
            game.goTo(GameState.MENU);
            return;
        } catch (Exception ignored) {
        }

        try {
            game.switchState(GameState.MENU);
        } catch (Exception ignored) {
        }
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private final class AiCar {
        private int lane;
        private double x;
        private double screenY;
        private double speed;
        private final StackPane actor;

        private AiCar(CarId carId, double x, double screenY, double speed) {
            this.lane = 0;
            this.x = x;
            this.screenY = screenY;
            this.speed = speed;
            this.actor = createAiActor(carId, 1.3, AI_ACTOR_W, AI_ACTOR_H);
        }

        private void resetCar(CarId carId) {
            actor.getChildren().clear();
            Node carNode = CarViewFactory.createObstacleRaceCar(carId);
            carNode.setScaleX(1.3);
            carNode.setScaleY(1.3);
            actor.getChildren().add(carNode);
            actor.toFront();
        }
    }
}