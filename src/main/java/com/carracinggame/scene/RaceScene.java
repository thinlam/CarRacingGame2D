package com.carracinggame.scene;

import com.carracinggame.car.CarId;
import com.carracinggame.car.CarSkill;
import com.carracinggame.core.Game;
import com.carracinggame.core.GameState;
import com.carracinggame.map.CityRoadBlock;
import com.carracinggame.map.CityRoadBlockType;
import com.carracinggame.map.CityRoadGenerator;
import com.carracinggame.map.MapId;
import com.carracinggame.map.TrafficLightState;
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
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class RaceScene implements AppScene {

    private static final double VIEW_W = 520;
    private static final double VIEW_H = 760;

    private static final double ROAD_W = 320;
    private static final double ROAD_X = (VIEW_W - ROAD_W) / 2.0;
    private static final int LANE_COUNT = 4;

    private static final double PLAYER_Y = 620;
    private static final double PLAYER_ACTOR_W = 64;
    private static final double PLAYER_ACTOR_H = 98;
    private static final double AI_ACTOR_W = 64;
    private static final double AI_ACTOR_H = 98;
    private static final double POLICE_ACTOR_W = 66;
    private static final double POLICE_ACTOR_H = 102;

    private static final double NORTH_TRACK_LENGTH = 15000;
    private static final double CENTRAL_TRACK_LENGTH = 18000;
    private static final double SOUTH_TRACK_LENGTH = 20000;

    private static final double BLOCK_LOOK_BEHIND = 280;
    private static final double BLOCK_LOOK_AHEAD = 2200;

    private static final double SIDEWALK_W = 18;
    private static final double OUTER_GREEN_W = 12;
    private static final double HOUSE_TOP_BOTTOM_MARGIN = 12;
    private static final double JUNCTION_CLEAR_MARGIN = 22;

    private static final double LINE_SCREEN_OFFSET = 95;
    private static final double START_LINE_DISTANCE = 0;

    private static final double PLAYER_MAX_SPEED = 520;
    private static final double PLAYER_ACCEL = 250;
    private static final double PLAYER_DRAG = 75;
    private static final double PLAYER_BRAKE = 320;

    private static final double SAME_DIR_SPAWN_MIN = 2.0;
    private static final double SAME_DIR_SPAWN_MAX = 3.0;
    private static final double OPPOSITE_DIR_SPAWN_MIN = 2.8;
    private static final double OPPOSITE_DIR_SPAWN_MAX = 4.2;

    private static final double SAME_DIR_MIN_SPEED = 110;
    private static final double SAME_DIR_MAX_SPEED = 260;
    private static final double OPPOSITE_DIR_MIN_SPEED = 180;
    private static final double OPPOSITE_DIR_MAX_SPEED = 320;

    private static final double AI_MIN_GAP = 118;
    private static final double AI_DESPAWN_BOTTOM = VIEW_H + 140;
    private static final double AI_DESPAWN_TOP = -180;

    private static final double PLAYER_INVULNERABLE_TIME = 1.15;
    private static final int MAX_HITS = 2;

    private static final double PEDESTRIAN_SPAWN_INTERVAL = 10.0;
    private static final double PEDESTRIAN_COLLISION_X = 26;
    private static final double PEDESTRIAN_COLLISION_Y = 24;

    private static final double POLICE_CHASE_TIME = 5.0;
    private static final int POLICE_CRASH_FINE = 30;

    private static final int MAX_SKILL_USES = 2;
    private static final double EMP_RANGE = 270;
    private static final double EMP_PULSE_INTERVAL = 0.18;
    private static final double PHANTOM_SPEED_BOOST = 85;

    private static final String DEFAULT_TIP_TEXT =
            "↑/W tăng tốc   •   ↓/S phanh   •   ← → / A D chuyển làn   •   SPACE dùng skill";

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
    private final Label skillLabel;
    private final Label skillStateLabel;
    private final Label tipLabel;
    private final Label countdownLabel;

    private final Set<KeyCode> pressedKeys = new HashSet<>();
    private final Random random = new Random();

    private final List<AiCar> aiCars = new ArrayList<>();
    private final List<Pedestrian> pedestrians = new ArrayList<>();

    private final StackPane playerActor;
    private final StackPane policeActor;

    private AnimationTimer timer;
    private long lastFrame = 0L;

    private double playerSpeed = 0;
    private double playerDistance = 0;
    private double currentTrackLength;
    private double roadScroll = 0;

    private int currentLane = 2;
    private int targetLane = 2;
    private double playerX = laneCenter(2);

    private boolean leftLaneLatch = false;
    private boolean rightLaneLatch = false;

    private int turnSignalDirection = 0;
    private double turnSignalBlinkTimer = 0;

    private boolean finished = false;
    private boolean finishDialogShown = false;

    private boolean raceStarted = false;
    private double countdownTime = 4.0;

    private int hitCount = 0;
    private double invulnerableTime = 0;

    private CityRoadGenerator cityRoadGenerator;
    private double raceElapsed = 0;

    private double sameDirSpawnTimer = 1.0;
    private double oppositeDirSpawnTimer = 1.8;
    private double pedestrianSpawnTimer = PEDESTRIAN_SPAWN_INTERVAL;

    private CityRoadBlock monitoredTrafficBlock;
    private boolean redStopSatisfied = false;
    private boolean redViolationTriggered = false;

    private boolean policeActive = false;
    private double policeChaseRemaining = 0;
    private double policeX = laneCenter(2);
    private double policeY = VIEW_H + 70;
    private double policeSirenTimer = 0;

    private CarId equippedCarId;
    private CarSkill currentSkill;
    private double upgradedSkillDuration = 0;
    private int skillUsesRemaining = MAX_SKILL_USES;
    private double skillCooldownRemaining = 0;
    private double skillActiveRemaining = 0;
    private boolean phantomDashActive = false;
    private boolean empActive = false;
    private double empPulseTimer = 0;
    private boolean skillKeyLatch = false;

    public RaceScene(Game game) {
        this.game = game;
        this.currentTrackLength = resolveTrackLength();
        this.cityRoadGenerator = new CityRoadGenerator(getSafeMapId());

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
        this.skillLabel = new Label();
        this.skillStateLabel = new Label();
        this.tipLabel = new Label(DEFAULT_TIP_TEXT);

        this.countdownLabel = new Label("3");
        this.countdownLabel.setStyle("""
            -fx-font-size: 56px;
            -fx-font-weight: 900;
            -fx-text-fill: white;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.35), 16, 0.3, 0, 4);
        """);

        this.playerActor = createPlayerActor(getEquippedCarId(), 1.35, PLAYER_ACTOR_W, PLAYER_ACTOR_H);
        this.policeActor = createPoliceActor(1.30, POLICE_ACTOR_W, POLICE_ACTOR_H);

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
        loadEquippedSkillData();
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
        cityRoadGenerator = new CityRoadGenerator(getSafeMapId());
        raceElapsed = 0;
        roadScroll = 0;

        playerSpeed = 0;
        playerDistance = 0;
        currentLane = 2;
        targetLane = 2;
        playerX = laneCenter(2);

        leftLaneLatch = false;
        rightLaneLatch = false;
        turnSignalDirection = 0;
        turnSignalBlinkTimer = 0;

        hitCount = 0;
        invulnerableTime = 0;
        playerActor.setOpacity(1.0);

        sameDirSpawnTimer = 1.0;
        oppositeDirSpawnTimer = 1.8;
        pedestrianSpawnTimer = PEDESTRIAN_SPAWN_INTERVAL;

        monitoredTrafficBlock = null;
        redStopSatisfied = false;
        redViolationTriggered = false;

        policeActive = false;
        policeChaseRemaining = 0;
        policeX = playerX;
        policeY = VIEW_H + 70;
        policeSirenTimer = 0;
        policeActor.setVisible(false);

        loadEquippedSkillData();
        resetSkillState();
        resetPlayerActorCar(equippedCarId);

        pedestrians.clear();
        tipLabel.setText(DEFAULT_TIP_TEXT);
        initActors();
        updateHud();
        redraw();
    }

    private void loadEquippedSkillData() {
        equippedCarId = getEquippedCarId();
        currentSkill = CarSkill.forCar(equippedCarId);

        try {
            upgradedSkillDuration = UpgradeScene.getUpgradedSkillDuration(game, equippedCarId);
        } catch (Exception e) {
            upgradedSkillDuration = defaultSkillDuration();
        }
    }

    private void resetSkillState() {
        skillUsesRemaining = MAX_SKILL_USES;
        skillCooldownRemaining = 0;
        skillActiveRemaining = 0;
        phantomDashActive = false;
        empActive = false;
        empPulseTimer = 0;
        skillKeyLatch = false;
        updateSkillVisualState();
    }

    private double defaultSkillDuration() {
        return isEmpSkill() ? 2.8 : 3.8;
    }

    private double defaultSkillCooldown() {
        return isEmpSkill() ? 8.0 : 7.0;
    }

    private boolean isEmpSkill() {
        return equippedCarId == CarId.BLUE_STORM;
    }

    private String currentSkillDisplayName() {
        return isEmpSkill() ? "EMP Pulse" : "Phantom Dash";
    }

    private void resetPlayerActorCar(CarId carId) {
        playerActor.getChildren().clear();
        Node carNode = CarViewFactory.createPlayerRaceCar(carId);
        carNode.setScaleX(1.35);
        carNode.setScaleY(1.35);
        playerActor.getChildren().add(carNode);
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

        skillLabel.setStyle("""
            -fx-font-size: 14px;
            -fx-font-weight: 900;
            -fx-text-fill: #214d76;
        """);

        skillStateLabel.setStyle("""
            -fx-font-size: 13px;
            -fx-font-weight: 800;
            -fx-text-fill: #5b21b6;
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

        top.getChildren().addAll(
                titleLabel, speedLabel, distanceLabel, mapLabel, crashLabel,
                skillLabel, skillStateLabel, spacer, backBtn
        );
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
        actorLayer.getChildren().add(policeActor);

        playerActor.toBack();
        policeActor.toBack();
        policeActor.setVisible(false);

        for (int i = 0; i < 12; i++) {
            AiCar aiCar = new AiCar(randomAiCarId());
            aiCar.active = false;
            aiCar.actor.setVisible(false);
            aiCars.add(aiCar);
            actorLayer.getChildren().add(aiCar.actor);
        }
    }

    private void releaseInitialTraffic() {
        spawnAiCar(true);
        spawnAiCar(true);
        spawnAiCar(false);
        spawnAiCar(false);
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
            raceElapsed += dt;
            updatePlayerInput(dt);
            updateTrafficLightRule(dt);
            updatePlayerState(dt);
            updateTrafficSpawners(dt);
            updatePedestrians(dt);
            updateSkill(dt);
            updateAiCars(dt);
            updatePolice(dt);
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
                releaseInitialTraffic();
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
            playerSpeed += PLAYER_ACCEL * dt;
        } else {
            playerSpeed -= PLAYER_DRAG * dt;
        }

        if (isPressed(KeyCode.DOWN, KeyCode.S)) {
            playerSpeed -= PLAYER_BRAKE * dt;
        }

        double maxSpeed = PLAYER_MAX_SPEED + (phantomDashActive ? PHANTOM_SPEED_BOOST : 0);
        playerSpeed = clamp(playerSpeed, 0, maxSpeed);

        boolean leftPressed = isPressed(KeyCode.LEFT, KeyCode.A);
        boolean rightPressed = isPressed(KeyCode.RIGHT, KeyCode.D);

        if (leftPressed && !leftLaneLatch) {
            if (targetLane > 0) {
                targetLane--;
                turnSignalDirection = -1;
                turnSignalBlinkTimer = 0;
            }
        }
        if (rightPressed && !rightLaneLatch) {
            if (targetLane < LANE_COUNT - 1) {
                targetLane++;
                turnSignalDirection = 1;
                turnSignalBlinkTimer = 0;
            }
        }

        leftLaneLatch = leftPressed;
        rightLaneLatch = rightPressed;

        double targetX = laneCenter(targetLane);
        double lerp = clamp(dt * 8.5, 0, 1);
        playerX += (targetX - playerX) * lerp;

        if (Math.abs(playerX - targetX) < 1.8) {
            playerX = targetX;
            currentLane = targetLane;
            turnSignalDirection = 0;
        }

        turnSignalBlinkTimer += dt;
    }

    private void updateTrafficLightRule(double dt) {
        CityRoadBlock upcomingLightBlock = findUpcomingTrafficLightBlock(650);

        if (upcomingLightBlock == null) {
            monitoredTrafficBlock = null;
            redStopSatisfied = false;
            redViolationTriggered = false;
            if (!tipLabel.getText().startsWith("Cảnh sát") && !tipLabel.getText().startsWith("Đèn đỏ")) {
                tipLabel.setText(DEFAULT_TIP_TEXT);
            }
            return;
        }

        TrafficLightState state = cityRoadGenerator.getTrafficLightState(raceElapsed, upcomingLightBlock.getIndex());
        double stopLine = getMainRoadStopWorld(upcomingLightBlock, true);
        double delta = stopLine - playerDistance;

        if (state != TrafficLightState.RED) {
            if (monitoredTrafficBlock == upcomingLightBlock) {
                monitoredTrafficBlock = null;
                redStopSatisfied = false;
                redViolationTriggered = false;
            }
            if (!policeActive) {
                tipLabel.setText(DEFAULT_TIP_TEXT);
            }
            return;
        }

        if (monitoredTrafficBlock == null || monitoredTrafficBlock.getIndex() != upcomingLightBlock.getIndex()) {
            monitoredTrafficBlock = upcomingLightBlock;
            redStopSatisfied = false;
            redViolationTriggered = false;
        }

        if (delta <= 180 && delta >= 0) {
            tipLabel.setText("Đèn đỏ phía trước: phanh và dừng hẳn trước vạch.");
        }

        if (delta > 24 && delta < 115 && playerSpeed > 0) {
            playerSpeed -= 260 * dt;
            if (playerSpeed < 0) {
                playerSpeed = 0;
            }
        }

        if (delta > 24 && delta < 120 && playerSpeed <= 4) {
            redStopSatisfied = true;
        }

        if (playerDistance >= stopLine + 6 && !redStopSatisfied && !redViolationTriggered) {
            redViolationTriggered = true;
            startPoliceChase();
        }
    }

    private void updatePlayerState(double dt) {
        playerDistance += playerSpeed * dt;
        if (playerDistance > currentTrackLength) {
            playerDistance = currentTrackLength;
        }
        roadScroll += playerSpeed * dt;
    }

    private void updateTrafficSpawners(double dt) {
        sameDirSpawnTimer -= dt;
        oppositeDirSpawnTimer -= dt;

        if (sameDirSpawnTimer <= 0) {
            spawnAiCar(true);
            sameDirSpawnTimer = randomRange(SAME_DIR_SPAWN_MIN, SAME_DIR_SPAWN_MAX);
        }

        if (oppositeDirSpawnTimer <= 0) {
            spawnAiCar(false);
            oppositeDirSpawnTimer = randomRange(OPPOSITE_DIR_SPAWN_MIN, OPPOSITE_DIR_SPAWN_MAX);
        }
    }

    private void updatePedestrians(double dt) {
        pedestrianSpawnTimer -= dt;

        if (pedestrianSpawnTimer <= 0) {
            spawnPedestrianAtUpcomingCrosswalk();
            pedestrianSpawnTimer = PEDESTRIAN_SPAWN_INTERVAL;
        }

        Iterator<Pedestrian> iterator = pedestrians.iterator();
        while (iterator.hasNext()) {
            Pedestrian pedestrian = iterator.next();
            pedestrian.progress += pedestrian.speed * dt;

            if (pedestrian.progress > 1.15) {
                iterator.remove();
                continue;
            }

            double px = pedestrian.getScreenX();
            double py = pedestrian.getScreenY(playerDistance);

            if (!phantomDashActive && invulnerableTime <= 0) {
                boolean collideX = Math.abs(px - playerX) < PEDESTRIAN_COLLISION_X;
                boolean collideY = Math.abs(py - PLAYER_Y) < PEDESTRIAN_COLLISION_Y;

                if (collideX && collideY) {
                    iterator.remove();
                    handleGenericCollision("Bạn đã tông vào người đi bộ!");
                    break;
                }
            }
        }
    }

    private void updateSkill(double dt) {
        if (skillCooldownRemaining > 0) {
            skillCooldownRemaining -= dt;
            if (skillCooldownRemaining < 0) {
                skillCooldownRemaining = 0;
            }
        }

        boolean spacePressed = pressedKeys.contains(KeyCode.SPACE);
        if (spacePressed && !skillKeyLatch) {
            activateSkill();
        }
        skillKeyLatch = spacePressed;

        if (skillActiveRemaining > 0) {
            skillActiveRemaining -= dt;

            if (phantomDashActive) {
                playerSpeed = Math.min(playerSpeed + 55 * dt, PLAYER_MAX_SPEED + PHANTOM_SPEED_BOOST);
            }

            if (empActive) {
                empPulseTimer -= dt;
                while (empPulseTimer <= 0) {
                    empPulseTimer += EMP_PULSE_INTERVAL;
                    destroyAiAheadByEmp();
                }
            }

            if (skillActiveRemaining <= 0) {
                skillActiveRemaining = 0;
                phantomDashActive = false;
                empActive = false;
                tipLabel.setText(DEFAULT_TIP_TEXT);
            }
        }

        updateSkillVisualState();
    }

    private void activateSkill() {
        if (skillUsesRemaining <= 0) {
            tipLabel.setText("Skill đã hết lượt dùng trong vòng này.");
            return;
        }
        if (skillCooldownRemaining > 0 || skillActiveRemaining > 0) {
            return;
        }

        skillUsesRemaining--;

        double duration = upgradedSkillDuration > 0 ? upgradedSkillDuration : defaultSkillDuration();
        skillActiveRemaining = duration;
        skillCooldownRemaining = currentSkill != null ? currentSkill.cooldownSeconds() : defaultSkillCooldown();

        if (isEmpSkill()) {
            empActive = true;
            phantomDashActive = false;
            empPulseTimer = 0;
            tipLabel.setText("EMP Pulse kích hoạt: vô hiệu hóa mọi xe phía trước trong tầm.");
        } else {
            phantomDashActive = true;
            empActive = false;
            tipLabel.setText("Phantom Dash kích hoạt: xe mờ đi, tăng tốc và xuyên vật cản.");
        }

        updateSkillVisualState();
    }

    private void updateSkillVisualState() {
        if (phantomDashActive) {
            playerActor.setStyle("""
                -fx-opacity: 0.62;
                -fx-effect: dropshadow(gaussian, rgba(255,255,255,0.95), 28, 0.45, 0, 0);
            """);
        } else if (empActive) {
            playerActor.setStyle("""
                -fx-effect: dropshadow(gaussian, rgba(80,180,255,0.95), 30, 0.45, 0, 0);
            """);
        } else {
            playerActor.setStyle("");
        }
    }

    private void destroyAiAheadByEmp() {
        for (AiCar aiCar : aiCars) {
            if (!aiCar.active) {
                continue;
            }
            if (aiCar.screenY >= PLAYER_Y) {
                continue;
            }
            if (PLAYER_Y - aiCar.screenY > EMP_RANGE) {
                continue;
            }
            if (Math.abs(aiCar.x - playerX) > 155) {
                continue;
            }

            aiCar.active = false;
            aiCar.actor.setVisible(false);
        }
    }

    private Double findNearestRedStopYForAi(AiCar aiCar) {
        List<CityRoadBlock> blocks = cityRoadGenerator.getVisibleBlocks(playerDistance, 0, BLOCK_LOOK_AHEAD);

        double currentWorld = screenYToWorldDistance(aiCar.screenY);
        Double bestStopWorld = null;
        double bestGap = Double.MAX_VALUE;

        for (CityRoadBlock block : blocks) {
            if (!block.hasTrafficLight()) {
                continue;
            }

            TrafficLightState lightState = cityRoadGenerator.getTrafficLightState(raceElapsed, block.getIndex());
            if (lightState != TrafficLightState.RED) {
                continue;
            }

            double stopWorld = getMainRoadStopWorld(block, aiCar.sameDirection);

            if (aiCar.sameDirection) {
                double gap = stopWorld - currentWorld;
                if (gap >= -6 && gap < bestGap) {
                    bestGap = gap;
                    bestStopWorld = stopWorld;
                }
            } else {
                double gap = currentWorld - stopWorld;
                if (gap >= -6 && gap < bestGap) {
                    bestGap = gap;
                    bestStopWorld = stopWorld;
                }
            }
        }

        return bestStopWorld == null ? null : worldToScreenY(bestStopWorld);
    }

    private double clampAiAtRedLight(AiCar aiCar, double nextY) {
        Double stopY = findNearestRedStopYForAi(aiCar);
        if (stopY == null) {
            return nextY;
        }

        double stopWorld = screenYToWorldDistance(stopY);
        double currentWorld = screenYToWorldDistance(aiCar.screenY);
        double nextWorld = screenYToWorldDistance(nextY);

        double stopMargin = 52.0;

        if (aiCar.sameDirection) {
            double maxWorld = stopWorld - stopMargin;

            if (currentWorld <= maxWorld && nextWorld > maxWorld) {
                return worldToScreenY(maxWorld);
            }

            if (currentWorld > maxWorld && currentWorld < stopWorld + 14) {
                return worldToScreenY(maxWorld);
            }
        } else {
            double minWorld = stopWorld + stopMargin;

            if (currentWorld >= minWorld && nextWorld < minWorld) {
                return worldToScreenY(minWorld);
            }

            if (currentWorld < minWorld && currentWorld > stopWorld - 14) {
                return worldToScreenY(minWorld);
            }
        }

        return nextY;
    }

    private void updateAiCars(double dt) {
        List<AiCar> sameDirCars = new ArrayList<>();
        List<AiCar> oppositeCars = new ArrayList<>();

        for (AiCar aiCar : aiCars) {
            if (!aiCar.active) {
                continue;
            }
            if (aiCar.sameDirection) {
                sameDirCars.add(aiCar);
            } else {
                oppositeCars.add(aiCar);
            }
        }

        sameDirCars.sort(Comparator.comparingDouble(a -> a.screenY));
        for (int i = 0; i < sameDirCars.size(); i++) {
            AiCar aiCar = sameDirCars.get(i);

            double relative = playerSpeed - aiCar.speed;
            double nextY = aiCar.screenY + relative * dt;

            nextY = clampAiAtRedLight(aiCar, nextY);

            if (i < sameDirCars.size() - 1) {
                AiCar frontCar = sameDirCars.get(i + 1);
                double maxAllowedY = frontCar.screenY - AI_MIN_GAP;
                if (nextY > maxAllowedY) {
                    nextY = maxAllowedY;
                }
            }

            aiCar.screenY = nextY;
        }

        oppositeCars.sort(Comparator.comparingDouble(a -> a.screenY));
        for (int i = 0; i < oppositeCars.size(); i++) {
            AiCar aiCar = oppositeCars.get(i);

            double relative = playerSpeed + aiCar.speed;
            double nextY = aiCar.screenY + relative * dt;

            nextY = clampAiAtRedLight(aiCar, nextY);

            if (i < oppositeCars.size() - 1) {
                AiCar frontCar = oppositeCars.get(i + 1);
                double maxAllowedY = frontCar.screenY - AI_MIN_GAP;
                if (nextY > maxAllowedY) {
                    nextY = maxAllowedY;
                }
            }

            aiCar.screenY = nextY;
        }

        for (AiCar aiCar : aiCars) {
            if (!aiCar.active) {
                continue;
            }

            if (aiCar.sameDirection) {
                if (aiCar.screenY > AI_DESPAWN_BOTTOM || aiCar.screenY < AI_DESPAWN_TOP) {
                    aiCar.active = false;
                    aiCar.actor.setVisible(false);
                    continue;
                }
            } else {
                if (aiCar.screenY > AI_DESPAWN_BOTTOM) {
                    aiCar.active = false;
                    aiCar.actor.setVisible(false);
                    continue;
                }
            }

            if (phantomDashActive || invulnerableTime > 0) {
                continue;
            }

            boolean collideX = Math.abs(aiCar.x - playerX) < 34;
            boolean collideY = Math.abs(aiCar.screenY - PLAYER_Y) < 48;

            if (collideX && collideY) {
                aiCar.active = false;
                aiCar.actor.setVisible(false);
                handleGenericCollision("Bạn đã va chạm với xe!");
                break;
            }
        }
    }

    private void updatePolice(double dt) {
        if (!policeActive) {
            policeActor.setVisible(false);
            return;
        }

        policeChaseRemaining -= dt;
        policeSirenTimer += dt;

        double targetX = playerX;
        double targetY = PLAYER_Y + 108;

        policeX += (targetX - policeX) * clamp(dt * 4.5, 0, 1);
        policeY += (targetY - policeY) * clamp(dt * 3.2, 0, 1);

        policeActor.setVisible(true);

        if (policeChaseRemaining <= 0) {
            policeActive = false;
            policeActor.setVisible(false);
            tipLabel.setText("Bạn đã thoát truy đuổi an toàn. Cảnh sát bỏ cuộc.");
        }
    }

    private void handleGenericCollision(String message) {
        hitCount++;

        if (policeActive) {
            applyPoliceCrashPenalty();
        }

        if (hitCount >= MAX_HITS) {
            gameOver();
            return;
        }

        playerSpeed = 0;
        invulnerableTime = PLAYER_INVULNERABLE_TIME;
        tipLabel.setText(message + " Cẩn thận, thêm 1 lần nữa sẽ GAME OVER.");
    }

    private void applyPoliceCrashPenalty() {
        try {
            game.getPlayerProfile().addCoins(-POLICE_CRASH_FINE);
        } catch (Exception ignored) {
        }
        try {
            game.saveProgress();
        } catch (Exception ignored) {
        }

        tipLabel.setText("Bạn gây tai nạn khi đang bỏ chạy! Bị phạt -" + POLICE_CRASH_FINE + " coins.");
    }

    private void startPoliceChase() {
        policeActive = true;
        policeChaseRemaining = POLICE_CHASE_TIME;
        policeX = playerX;
        policeY = VIEW_H + 80;
        policeSirenTimer = 0;
        policeActor.setVisible(true);
        tipLabel.setText("Cảnh sát đang truy đuổi vì vượt đèn đỏ! Sống sót 5 giây để thoát.");
    }

    private void spawnAiCar(boolean sameDirection) {
        AiCar slot = findInactiveAiCar();
        if (slot == null) {
            return;
        }

        slot.active = true;
        slot.sameDirection = sameDirection;
        slot.resetCar(sameDirection ? randomSameDirectionCarId() : randomOppositeDirectionCarId());

        if (sameDirection) {
            if (playerSpeed < 120) {
                slot.active = false;
                slot.actor.setVisible(false);
                return;
            }

            slot.lane = random.nextBoolean() ? 2 : 3;
            slot.x = laneCenter(slot.lane);

            double slowerThanPlayer = playerSpeed - randomRange(70, 150);
            slot.speed = clamp(slowerThanPlayer, SAME_DIR_MIN_SPEED, SAME_DIR_MAX_SPEED);

            slot.screenY = 90 + random.nextInt(130);
            ensureSameDirectionGap(slot);
        } else {
            slot.lane = random.nextBoolean() ? 0 : 1;
            slot.x = laneCenter(slot.lane);
            slot.speed = randomRange(OPPOSITE_DIR_MIN_SPEED, OPPOSITE_DIR_MAX_SPEED);
            slot.screenY = -130 - random.nextInt(120);
            ensureOppositeGap(slot);
        }

        Double stopY = findNearestRedStopYForAi(slot);
        if (stopY != null) {
            double stopWorld = screenYToWorldDistance(stopY);
            double stopMargin = 60.0;

            if (slot.sameDirection) {
                double maxWorld = stopWorld - stopMargin;
                double slotWorld = screenYToWorldDistance(slot.screenY);

                if (slotWorld > maxWorld) {
                    slot.screenY = worldToScreenY(maxWorld - 10 - random.nextInt(16));
                }
            } else {
                double minWorld = stopWorld + stopMargin;
                double slotWorld = screenYToWorldDistance(slot.screenY);

                if (slotWorld < minWorld) {
                    slot.screenY = worldToScreenY(minWorld + 10 + random.nextInt(16));
                }
            }
        }

        slot.actor.setVisible(true);
    }

    private void ensureSameDirectionGap(AiCar spawned) {
        for (AiCar aiCar : aiCars) {
            if (aiCar == spawned || !aiCar.active || !aiCar.sameDirection || aiCar.lane != spawned.lane) {
                continue;
            }
            if (Math.abs(aiCar.screenY - spawned.screenY) < AI_MIN_GAP) {
                spawned.screenY = aiCar.screenY - AI_MIN_GAP;
            }
        }
    }

    private void ensureOppositeGap(AiCar spawned) {
        for (AiCar aiCar : aiCars) {
            if (aiCar == spawned || !aiCar.active || aiCar.sameDirection || aiCar.lane != spawned.lane) {
                continue;
            }
            if (Math.abs(aiCar.screenY - spawned.screenY) < AI_MIN_GAP) {
                spawned.screenY = aiCar.screenY - AI_MIN_GAP;
            }
        }
    }

    private AiCar findInactiveAiCar() {
        for (AiCar aiCar : aiCars) {
            if (!aiCar.active) {
                return aiCar;
            }
        }
        return null;
    }

    private void spawnPedestrianAtUpcomingCrosswalk() {
        CityRoadBlock crosswalkBlock = findUpcomingCrosswalkBlock(800);
        if (crosswalkBlock == null) {
            return;
        }

        Pedestrian pedestrian = new Pedestrian();
        pedestrian.crosswalkDistance = crosswalkBlock.getStartDistance() + crosswalkBlock.getLength() * 0.55 + 8;
        pedestrian.fromLeft = random.nextBoolean();
        pedestrian.progress = 0;
        pedestrian.speed = 0.28 + random.nextDouble() * 0.08;
        pedestrians.add(pedestrian);
    }

    private CityRoadBlock findUpcomingCrosswalkBlock(double maxAheadDistance) {
        List<CityRoadBlock> blocks = cityRoadGenerator.getVisibleBlocks(playerDistance, 0, maxAheadDistance);
        CityRoadBlock best = null;
        double bestDelta = Double.MAX_VALUE;

        for (CityRoadBlock block : blocks) {
            if (!block.hasCrosswalk()) {
                continue;
            }
            double delta = block.getStartDistance() - playerDistance;
            if (delta < 80 || delta > maxAheadDistance) {
                continue;
            }
            if (delta < bestDelta) {
                bestDelta = delta;
                best = block;
            }
        }

        return best;
    }

    private CityRoadBlock findUpcomingTrafficLightBlock(double maxAheadDistance) {
        List<CityRoadBlock> blocks = cityRoadGenerator.getVisibleBlocks(playerDistance, 0, maxAheadDistance);
        CityRoadBlock best = null;
        double bestDelta = Double.MAX_VALUE;

        for (CityRoadBlock block : blocks) {
            if (!block.hasTrafficLight()) {
                continue;
            }
            double delta = block.getStartDistance() - playerDistance;
            if (delta < -40 || delta > maxAheadDistance) {
                continue;
            }
            if (delta < bestDelta) {
                bestDelta = delta;
                best = block;
            }
        }

        return best;
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
        double laneW = ROAD_W / (double) LANE_COUNT;

        drawCityBackground(gc, mapId);
        drawCitySideDecor(gc, mapId);

        drawRoadBarrier(gc, ROAD_X - 18, 0, 18, VIEW_H);
        drawRoadBarrier(gc, ROAD_X + ROAD_W, 0, 18, VIEW_H);

        gc.setFill(Color.web("#484848"));
        gc.fillRect(ROAD_X, 0, ROAD_W, VIEW_H);

        gc.setFill(Color.rgb(255, 255, 255, 0.03));
        gc.fillRect(ROAD_X, 0, ROAD_W * 0.48, VIEW_H);
        gc.fillRect(ROAD_X + ROAD_W * 0.52, 0, ROAD_W * 0.48, VIEW_H);

        gc.setStroke(Color.web("#f5f5f5"));
        gc.setLineWidth(3);
        gc.strokeLine(ROAD_X + 4, 0, ROAD_X + 4, VIEW_H);
        gc.strokeLine(ROAD_X + ROAD_W - 4, 0, ROAD_X + ROAD_W - 4, VIEW_H);

        double split1 = ROAD_X + laneW;
        double split3 = ROAD_X + laneW * 3;
        gc.setStroke(Color.rgb(240, 240, 240, 0.85));
        gc.setLineWidth(2.5);
        drawDashedVertical(gc, split1, roadScroll * 1.2, 34, 22);
        drawDashedVertical(gc, split3, roadScroll * 1.2, 34, 22);

        double centerLeft = ROAD_X + laneW * 2 - 5;
        double centerRight = ROAD_X + laneW * 2 + 5;
        gc.setStroke(Color.web("#ffd84d"));
        gc.setLineWidth(3);
        gc.strokeLine(centerLeft, 0, centerLeft, VIEW_H);
        gc.strokeLine(centerRight, 0, centerRight, VIEW_H);

        drawCityRoadFeatures(gc);
        drawDirectionalHints(gc, laneW);
        drawPedestrianActors(gc);
        drawPlayerTurnSignals(gc);
        drawPoliceSirenEffect(gc);
        drawStartAndFinishLines(gc);
    }

    private void drawCityBackground(GraphicsContext gc, MapId mapId) {
        switch (mapId) {
            case NORTH -> {
                gc.setFill(Color.web("#9fd7ff"));
                gc.fillRect(0, 0, VIEW_W, VIEW_H);
                gc.setFill(Color.rgb(235, 244, 255, 0.85));
                gc.fillRect(0, 0, ROAD_X, VIEW_H);
                gc.fillRect(ROAD_X + ROAD_W, 0, VIEW_W - (ROAD_X + ROAD_W), VIEW_H);
            }
            case CENTRAL -> {
                gc.setFill(Color.web("#ffd59e"));
                gc.fillRect(0, 0, VIEW_W, VIEW_H);
                gc.setFill(Color.rgb(255, 244, 226, 0.90));
                gc.fillRect(0, 0, ROAD_X, VIEW_H);
                gc.fillRect(ROAD_X + ROAD_W, 0, VIEW_W - (ROAD_X + ROAD_W), VIEW_H);
            }
            case SOUTH -> {
                gc.setFill(Color.web("#8fe0b4"));
                gc.fillRect(0, 0, VIEW_W, VIEW_H);
                gc.setFill(Color.rgb(230, 255, 240, 0.85));
                gc.fillRect(0, 0, ROAD_X, VIEW_H);
                gc.fillRect(ROAD_X + ROAD_W, 0, VIEW_W - (ROAD_X + ROAD_W), VIEW_H);
            }
        }
    }

    private void drawCitySideDecor(GraphicsContext gc, MapId mapId) {
        List<CityRoadBlock> blocks = cityRoadGenerator.getVisibleBlocks(playerDistance, BLOCK_LOOK_BEHIND, BLOCK_LOOK_AHEAD);

        for (CityRoadBlock block : blocks) {
            double yStart = worldToScreenY(block.getStartDistance());
            double yEnd = worldToScreenY(block.getEndDistance());
            double top = Math.min(yStart, yEnd);
            double bottom = Math.max(yStart, yEnd);
            double height = bottom - top;

            if (bottom < -140 || top > VIEW_H + 140) {
                continue;
            }

            drawDistrictPanels(gc, top, height, mapId);
            drawSidewalkStrip(gc, top, height);
            drawHouseRow(gc, block, top, height, true, mapId);
            drawHouseRow(gc, block, top, height, false, mapId);
        }
    }

    private void drawCityRoadFeatures(GraphicsContext gc) {
        List<CityRoadBlock> blocks = cityRoadGenerator.getVisibleBlocks(playerDistance, BLOCK_LOOK_BEHIND, BLOCK_LOOK_AHEAD);

        for (CityRoadBlock block : blocks) {
            double yStart = worldToScreenY(block.getStartDistance());
            double yEnd = worldToScreenY(block.getEndDistance());
            double top = Math.min(yStart, yEnd);
            double bottom = Math.max(yStart, yEnd);

            if (bottom < -140 || top > VIEW_H + 140) {
                continue;
            }

            drawRoadBranchAndCrossing(gc, block, top, bottom);
        }
    }

    private void drawSidewalkStrip(GraphicsContext gc, double top, double height) {
        gc.setFill(Color.web("#cfd6dd"));
        gc.fillRect(ROAD_X - SIDEWALK_W, top, SIDEWALK_W, height);
        gc.fillRect(ROAD_X + ROAD_W, top, SIDEWALK_W, height);

        gc.setStroke(Color.rgb(255, 255, 255, 0.30));
        gc.setLineWidth(1.2);
        gc.strokeLine(ROAD_X - 1, top, ROAD_X - 1, top + height);
        gc.strokeLine(ROAD_X + ROAD_W + 1, top, ROAD_X + ROAD_W + 1, top + height);

        gc.setFill(Color.rgb(255, 255, 255, 0.24));
        for (double y = top + 8; y < top + height; y += 22) {
            gc.fillRect(ROAD_X - SIDEWALK_W + 3, y, SIDEWALK_W - 6, 6);
            gc.fillRect(ROAD_X + ROAD_W + 3, y, SIDEWALK_W - 6, 6);
        }
    }

    private void drawHouseRow(GraphicsContext gc, CityRoadBlock block, double top, double height, boolean leftSide, MapId mapId) {
        long baseSeed = leftSide ? block.getLeftHouseSeed() : block.getRightHouseSeed();
        double clearHalf = getJunctionClearHalf(block);
        double middleY = top + height / 2.0;

        if (clearHalf <= 0) {
            drawHouseSegment(gc, baseSeed, top + HOUSE_TOP_BOTTOM_MARGIN, height - HOUSE_TOP_BOTTOM_MARGIN * 2, leftSide, mapId);
            return;
        }

        double firstY = top + HOUSE_TOP_BOTTOM_MARGIN;
        double firstH = (middleY - clearHalf) - firstY;

        double secondY = middleY + clearHalf;
        double secondH = (top + height - HOUSE_TOP_BOTTOM_MARGIN) - secondY;

        drawHouseSegment(gc, baseSeed + 11, firstY, firstH, leftSide, mapId);
        drawHouseSegment(gc, baseSeed + 47, secondY, secondH, leftSide, mapId);
    }

    private Color pickHouseColor(MapId mapId, Random localRandom) {
        return switch (mapId) {
            case NORTH -> switch (localRandom.nextInt(4)) {
                case 0 -> Color.web("#cfd8e6");
                case 1 -> Color.web("#e3edf8");
                case 2 -> Color.web("#d7e0cf");
                default -> Color.web("#e9d7d7");
            };
            case CENTRAL -> switch (localRandom.nextInt(4)) {
                case 0 -> Color.web("#f1d1a7");
                case 1 -> Color.web("#f4dfc6");
                case 2 -> Color.web("#e7c692");
                default -> Color.web("#f8ead6");
            };
            case SOUTH -> switch (localRandom.nextInt(4)) {
                case 0 -> Color.web("#cfe8db");
                case 1 -> Color.web("#d9f2cc");
                case 2 -> Color.web("#f3e2c7");
                default -> Color.web("#dce8f6");
            };
        };
    }

    private void drawDistrictPanels(GraphicsContext gc, double top, double height, MapId mapId) {
        double leftW = ROAD_X - SIDEWALK_W;
        double rightX = ROAD_X + ROAD_W + SIDEWALK_W;
        double rightW = VIEW_W - rightX;

        gc.setFill(pickDistrictPanelColor(mapId));
        gc.fillRoundRect(0, top, leftW, height, 16, 16);
        gc.fillRoundRect(rightX, top, rightW, height, 16, 16);

        gc.setFill(pickGreenEdgeColor(mapId));
        gc.fillRect(0, top, OUTER_GREEN_W, height);
        gc.fillRect(VIEW_W - OUTER_GREEN_W, top, OUTER_GREEN_W, height);

        gc.setFill(Color.rgb(255, 255, 255, 0.16));
        gc.fillRect(0, top, leftW, 5);
        gc.fillRect(rightX, top, rightW, 5);
    }

    private Color pickDistrictPanelColor(MapId mapId) {
        return switch (mapId) {
            case NORTH -> Color.web("#dbe6ef");
            case CENTRAL -> Color.web("#f1dfc8");
            case SOUTH -> Color.web("#d8eadf");
        };
    }

    private Color pickGreenEdgeColor(MapId mapId) {
        return switch (mapId) {
            case NORTH -> Color.web("#2faa52");
            case CENTRAL -> Color.web("#53a35b");
            case SOUTH -> Color.web("#2fb56b");
        };
    }

    private double getJunctionRoadHalf(CityRoadBlock block) {
        return switch (block.getType()) {
            case CROSSROAD, T_JUNCTION_LEFT, T_JUNCTION_RIGHT -> 36.0;
            case ALLEY_LEFT, ALLEY_RIGHT -> 26.0;
            case STRAIGHT -> (block.hasLeftAlley() || block.hasRightAlley()) ? 24.0 : 0.0;
        };
    }

    private double getJunctionClearHalf(CityRoadBlock block) {
        double roadHalf = getJunctionRoadHalf(block);
        return roadHalf > 0 ? roadHalf + JUNCTION_CLEAR_MARGIN : 0.0;
    }

    private boolean hasLeftConnection(CityRoadBlock block) {
        return block.getType() == CityRoadBlockType.ALLEY_LEFT
                || block.getType() == CityRoadBlockType.T_JUNCTION_LEFT
                || block.getType() == CityRoadBlockType.CROSSROAD
                || block.hasLeftAlley();
    }

    private boolean hasRightConnection(CityRoadBlock block) {
        return block.getType() == CityRoadBlockType.ALLEY_RIGHT
                || block.getType() == CityRoadBlockType.T_JUNCTION_RIGHT
                || block.getType() == CityRoadBlockType.CROSSROAD
                || block.hasRightAlley();
    }

    private void drawHouseSegment(GraphicsContext gc, long seed, double yStart, double availableH, boolean leftSide, MapId mapId) {
        if (availableH < 42) {
            return;
        }

        Random localRandom = new Random(seed);
        double baseX = leftSide ? 14 : ROAD_X + ROAD_W + 30;
        double maxWidth = leftSide ? ROAD_X - 40 : VIEW_W - (ROAD_X + ROAD_W) - 44;
        double y = yStart;

        while (y < yStart + availableH - 34) {
            double houseW = Math.max(36, Math.min(maxWidth, 40 + localRandom.nextInt(14)));
            double houseH = 46 + localRandom.nextInt(24);

            if (y + houseH > yStart + availableH) {
                break;
            }

            Color body = pickHouseColor(mapId, localRandom);
            Color roof = body.darker();

            gc.setFill(body);
            gc.fillRoundRect(baseX, y, houseW, houseH, 10, 10);

            gc.setFill(roof);
            gc.fillRoundRect(baseX - 2, y - 6, houseW + 4, 9, 6, 6);

            gc.setFill(Color.rgb(255, 244, 190, 0.78));
            for (int row = 0; row < 2; row++) {
                for (int col = 0; col < 2; col++) {
                    double wx = baseX + 8 + col * Math.max(12, houseW / 2.4);
                    double wy = y + 10 + row * 16;
                    gc.fillRoundRect(wx, wy, 7, 9, 3, 3);
                }
            }

            gc.setFill(Color.rgb(98, 70, 45, 0.78));
            gc.fillRoundRect(baseX + houseW / 2 - 5, y + houseH - 14, 10, 14, 4, 4);

            double treeX = leftSide ? baseX + houseW + 9 : baseX - 9;
            double treeY = y + houseH - 12;
            drawTree(gc, treeX, treeY, 0.78 + localRandom.nextDouble() * 0.12);

            y += houseH + 12 + localRandom.nextInt(8);
        }
    }

    private void drawIntersectionCrosswalks(GraphicsContext gc, double middleY, double roadHalf,
                                            boolean leftConnection, boolean rightConnection) {
        drawHorizontalZebra(gc, ROAD_X + 8, middleY - roadHalf - 18, ROAD_W - 16, 20);
        drawHorizontalZebra(gc, ROAD_X + 8, middleY + roadHalf - 2, ROAD_W - 16, 20);

        if (leftConnection) {
            drawVerticalZebra(gc, ROAD_X - 18, middleY - roadHalf + 6, 20, roadHalf * 2.0 - 12);
        }
        if (rightConnection) {
            drawVerticalZebra(gc, ROAD_X + ROAD_W - 2, middleY - roadHalf + 6, 20, roadHalf * 2.0 - 12);
        }
    }

    private void drawHorizontalZebra(GraphicsContext gc, double x, double y, double width, double height) {
        gc.setFill(Color.rgb(250, 250, 250, 0.97));

        double barW = 10;
        double gap = 4;

        for (double xx = x; xx < x + width; xx += barW + gap) {
            gc.fillRect(xx, y, barW, height);
        }

        gc.setStroke(Color.rgb(255, 255, 255, 0.52));
        gc.setLineWidth(1.4);
        gc.strokeLine(x - 2, y - 3, x + width + 2, y - 3);
        gc.strokeLine(x - 2, y + height + 3, x + width + 2, y + height + 3);
    }

    private void drawVerticalZebra(GraphicsContext gc, double x, double y, double width, double height) {
        gc.setFill(Color.rgb(250, 250, 250, 0.97));

        double barH = 10;
        double gap = 4;

        for (double yy = y; yy < y + height; yy += barH + gap) {
            gc.fillRect(x, yy, width, barH);
        }

        gc.setStroke(Color.rgb(255, 255, 255, 0.52));
        gc.setLineWidth(1.4);
        gc.strokeLine(x - 3, y - 2, x - 3, y + height + 2);
        gc.strokeLine(x + width + 3, y - 2, x + width + 3, y + height + 2);
    }

    private void drawRoadBranchAndCrossing(GraphicsContext gc, CityRoadBlock block, double top, double bottom) {
        double middleY = (top + bottom) / 2.0;
        double roadHalf = getJunctionRoadHalf(block);

        boolean leftConnection = hasLeftConnection(block);
        boolean rightConnection = hasRightConnection(block);

        if (leftConnection && roadHalf > 0) {
            drawWideSideStreet(gc, true, middleY - roadHalf, roadHalf * 2.0);
        }
        if (rightConnection && roadHalf > 0) {
            drawWideSideStreet(gc, false, middleY - roadHalf, roadHalf * 2.0);
        }

        if (block.hasCrosswalk()) {
            if (roadHalf > 0) {
                drawIntersectionCrosswalks(gc, middleY, roadHalf, leftConnection, rightConnection);
            } else {
                drawCrosswalk(gc, middleY + 8);
            }
        }

        if (block.hasTrafficLight()) {
            TrafficLightState mainRoadState = cityRoadGenerator.getTrafficLightState(raceElapsed, block.getIndex());
            TrafficLightState sideRoadState = getSideRoadLightState(mainRoadState);

            if (roadHalf > 0) {
                drawIntersectionTrafficLights(
                        gc,
                        middleY,
                        roadHalf,
                        leftConnection,
                        rightConnection,
                        mainRoadState,
                        sideRoadState
                );
            } else if (block.hasCrosswalk()) {
                drawTrafficLights(gc, middleY - 12, mainRoadState);
            }
        }
    }

    private void drawWideSideStreet(GraphicsContext gc, boolean left, double y, double height) {
        double x = left ? 0 : ROAD_X + ROAD_W;
        double width = left ? ROAD_X + SIDEWALK_W : VIEW_W - (ROAD_X + ROAD_W);

        gc.setFill(Color.web("#595b62"));
        gc.fillRect(x, y, width, height);

        gc.setFill(Color.web("#cfd6dd"));
        gc.fillRect(x, y - 8, width, 8);
        gc.fillRect(x, y + height, width, 8);

        gc.setStroke(Color.rgb(255, 255, 255, 0.76));
        gc.setLineWidth(2.0);
        gc.setLineDashes(16, 14);
        gc.strokeLine(x + 14, y + height / 2.0, x + width - 14, y + height / 2.0);
        gc.setLineDashes();

        gc.setStroke(Color.rgb(255, 255, 255, 0.38));
        gc.setLineWidth(1.2);
        gc.strokeLine(x, y + 8, x + width, y + 8);
        gc.strokeLine(x, y + height - 8, x + width, y + height - 8);

        double arrowX = left ? Math.max(26, width * 0.35) : x + width * 0.65;
        drawHorizontalArrow(gc, arrowX, y + height / 2.0, left);
    }

    private void drawCrosswalk(GraphicsContext gc, double y) {
        double stripeW = 24;
        double gap = 5;
        double stripeH = 18;

        gc.setFill(Color.rgb(250, 250, 250, 0.97));

        double x = ROAD_X + 10;
        while (x < ROAD_X + ROAD_W - 10) {
            gc.fillRect(x, y, stripeW, stripeH);
            x += stripeW + gap;
        }

        gc.setStroke(Color.rgb(255, 255, 255, 0.55));
        gc.setLineWidth(1.5);
        gc.strokeLine(ROAD_X, y - 4, ROAD_X + ROAD_W, y - 4);
        gc.strokeLine(ROAD_X, y + stripeH + 4, ROAD_X + ROAD_W, y + stripeH + 4);
    }

    private void drawIntersectionTrafficLights(GraphicsContext gc,
                                               double middleY,
                                               double roadHalf,
                                               boolean leftConnection,
                                               boolean rightConnection,
                                               TrafficLightState mainRoadState,
                                               TrafficLightState sideRoadState) {

        double oppositeStopY = middleY - roadHalf - 10;
        double sameStopY = middleY + roadHalf + 10;

        drawMainRoadStopLine(gc, oppositeStopY);
        drawMainRoadStopLine(gc, sameStopY);

        double oppositeLightX = ROAD_X - 16;
        double oppositeLightY = oppositeStopY - 6;
        drawSingleVerticalTrafficLight(gc, oppositeLightX, oppositeLightY, mainRoadState);

        double sameLightX = ROAD_X + ROAD_W + 16;
        double sameLightY = sameStopY + 6;
        drawSingleVerticalTrafficLight(gc, sameLightX, sameLightY, mainRoadState);

        if (leftConnection) {
            double leftStopX = ROAD_X - 8;
            drawSideRoadStopLine(gc, leftStopX, middleY, true);

            double leftSideLightX = ROAD_X - 28;
            double leftSideLightY = middleY;
            drawSingleSideRoadTrafficLight(gc, leftSideLightX, leftSideLightY, sideRoadState, true);
        }

        if (rightConnection) {
            double rightStopX = ROAD_X + ROAD_W + 8;
            drawSideRoadStopLine(gc, rightStopX, middleY, false);

            double rightSideLightX = ROAD_X + ROAD_W + 28;
            double rightSideLightY = middleY;
            drawSingleSideRoadTrafficLight(gc, rightSideLightX, rightSideLightY, sideRoadState, false);
        }
    }

    private void drawTrafficLights(GraphicsContext gc, double y, TrafficLightState state) {
        double stopY = y + 28;
        double leftLightX = ROAD_X - 16;
        double rightLightX = ROAD_X + ROAD_W + 16;

        drawMainRoadStopLine(gc, stopY);
        drawSingleVerticalTrafficLight(gc, leftLightX, stopY + 6, state);
        drawSingleVerticalTrafficLight(gc, rightLightX, stopY + 6, state);
    }

    private TrafficLightState getSideRoadLightState(TrafficLightState mainRoadState) {
        return switch (mainRoadState) {
            case GREEN, YELLOW -> TrafficLightState.RED;
            case RED -> TrafficLightState.GREEN;
        };
    }

    private void drawMainRoadStopLine(GraphicsContext gc, double y) {
        gc.setStroke(Color.rgb(255, 255, 255, 0.98));
        gc.setLineWidth(4.5);
        gc.strokeLine(ROAD_X + 10, y, ROAD_X + ROAD_W - 10, y);

        gc.setStroke(Color.rgb(0, 0, 0, 0.22));
        gc.setLineWidth(1.0);
        gc.strokeLine(ROAD_X + 10, y + 3, ROAD_X + ROAD_W - 10, y + 3);
    }

    private void drawSideRoadStopLine(GraphicsContext gc, double x, double middleY, boolean fromLeft) {
        gc.setStroke(Color.rgb(255, 255, 255, 0.98));
        gc.setLineWidth(4.0);

        double y1 = middleY - 22;
        double y2 = middleY + 22;

        gc.strokeLine(x, y1, x, y2);

        gc.setStroke(Color.rgb(0, 0, 0, 0.20));
        gc.setLineWidth(1.0);
        gc.strokeLine(x + (fromLeft ? 3 : -3), y1, x + (fromLeft ? 3 : -3), y2);
    }

    private void drawSingleVerticalTrafficLight(GraphicsContext gc,
                                                double centerX,
                                                double centerY,
                                                TrafficLightState state) {
        double poleTopY = centerY - 26;
        double poleBottomY = centerY + 18;

        double headW = 14;
        double headH = 30;
        double headX = centerX - headW / 2.0;
        double headY = centerY - headH / 2.0;

        gc.setStroke(Color.web("#56606b"));
        gc.setLineWidth(3.0);
        gc.strokeLine(centerX, poleTopY, centerX, poleBottomY);

        gc.setFill(Color.web("#1b1f24"));
        gc.fillRoundRect(headX, headY, headW, headH, 5, 5);

        gc.setFill(state == TrafficLightState.RED ? Color.web("#ff4d4f") : Color.rgb(120, 32, 32, 0.35));
        gc.fillOval(headX + 3.5, headY + 3, 7, 7);

        gc.setFill(state == TrafficLightState.YELLOW ? Color.web("#ffd84d") : Color.rgb(110, 90, 20, 0.35));
        gc.fillOval(headX + 3.5, headY + 11, 7, 7);

        gc.setFill(state == TrafficLightState.GREEN ? Color.web("#4ade80") : Color.rgb(30, 120, 50, 0.35));
        gc.fillOval(headX + 3.5, headY + 19, 7, 7);
    }

    private void drawSingleSideRoadTrafficLight(GraphicsContext gc,
                                                double centerX,
                                                double centerY,
                                                TrafficLightState state,
                                                boolean fromLeft) {
        double poleTopY = centerY - 18;
        double poleBottomY = centerY + 18;

        double headW = 14;
        double headH = 30;
        double headX = fromLeft ? centerX + 2 : centerX - 16;
        double headY = centerY - headH / 2.0;

        gc.setStroke(Color.web("#56606b"));
        gc.setLineWidth(3.0);
        gc.strokeLine(centerX, poleTopY, centerX, poleBottomY);

        gc.setFill(Color.web("#1b1f24"));
        gc.fillRoundRect(headX, headY, headW, headH, 5, 5);

        gc.setFill(state == TrafficLightState.RED ? Color.web("#ff4d4f") : Color.rgb(120, 32, 32, 0.35));
        gc.fillOval(headX + 3.5, headY + 3, 7, 7);

        gc.setFill(state == TrafficLightState.YELLOW ? Color.web("#ffd84d") : Color.rgb(110, 90, 20, 0.35));
        gc.fillOval(headX + 3.5, headY + 11, 7, 7);

        gc.setFill(state == TrafficLightState.GREEN ? Color.web("#4ade80") : Color.rgb(30, 120, 50, 0.35));
        gc.fillOval(headX + 3.5, headY + 19, 7, 7);
    }

    private void drawDirectionalHints(GraphicsContext gc, double laneW) {
        for (int lane = 0; lane < LANE_COUNT; lane++) {
            double x = laneCenter(lane);
            boolean sameDirectionLane = lane >= 2;

            for (double y = 110 - (roadScroll % 180); y < VIEW_H; y += 180) {
                drawLaneArrow(gc, x, y, sameDirectionLane);
            }
        }
    }

    private void drawLaneArrow(GraphicsContext gc, double x, double y, boolean upward) {
        gc.setStroke(Color.rgb(255, 255, 255, 0.14));
        gc.setLineWidth(3);

        if (upward) {
            gc.strokeLine(x, y + 18, x, y - 16);
            gc.strokeLine(x, y - 16, x - 7, y - 6);
            gc.strokeLine(x, y - 16, x + 7, y - 6);
        } else {
            gc.strokeLine(x, y - 18, x, y + 16);
            gc.strokeLine(x, y + 16, x - 7, y + 6);
            gc.strokeLine(x, y + 16, x + 7, y + 6);
        }
    }

    private void drawPedestrianActors(GraphicsContext gc) {
        for (Pedestrian pedestrian : pedestrians) {
            double x = pedestrian.getScreenX();
            double y = pedestrian.getScreenY(playerDistance);
            drawPedestrian(gc, x, y, 0.94);
        }
    }

    private void drawPedestrian(GraphicsContext gc, double x, double y, double scale) {
        gc.setFill(Color.web("#222222"));
        gc.fillOval(x - 3 * scale, y - 10 * scale, 6 * scale, 6 * scale);

        gc.setStroke(Color.web("#2d3748"));
        gc.setLineWidth(2 * scale);
        gc.strokeLine(x, y - 4 * scale, x, y + 6 * scale);
        gc.strokeLine(x, y - 1 * scale, x - 5 * scale, y + 2 * scale);
        gc.strokeLine(x, y - 1 * scale, x + 5 * scale, y + 2 * scale);
        gc.strokeLine(x, y + 6 * scale, x - 4 * scale, y + 12 * scale);
        gc.strokeLine(x, y + 6 * scale, x + 4 * scale, y + 12 * scale);
    }

    private void drawPlayerTurnSignals(GraphicsContext gc) {
        if (turnSignalDirection == 0) {
            return;
        }

        boolean blinkOn = ((int) (turnSignalBlinkTimer * 5)) % 2 == 0;
        if (!blinkOn) {
            return;
        }

        gc.setFill(Color.web("#ffb020"));

        double leftX = playerX - 28;
        double rightX = playerX + 20;
        double y = PLAYER_Y - 6;

        if (turnSignalDirection < 0) {
            gc.fillRoundRect(leftX, y, 10, 6, 4, 4);
        } else {
            gc.fillRoundRect(rightX, y, 10, 6, 4, 4);
        }
    }

    private void drawPoliceSirenEffect(GraphicsContext gc) {
        if (!policeActive || !policeActor.isVisible()) {
            return;
        }

        boolean blink = ((int) (policeSirenTimer * 8)) % 2 == 0;
        double baseY = policeY - 28;

        gc.setFill(blink ? Color.web("#ff4d4f") : Color.rgb(255, 77, 79, 0.35));
        gc.fillOval(policeX - 14, baseY, 9, 6);

        gc.setFill(blink ? Color.web("#4da6ff") : Color.rgb(77, 166, 255, 0.35));
        gc.fillOval(policeX + 5, baseY, 9, 6);
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
        playerActor.toFront();

        if (policeActive) {
            setActorPosition(policeActor, policeX, policeY, POLICE_ACTOR_W, POLICE_ACTOR_H, 1.0);
            policeActor.toFront();
        }

        for (AiCar aiCar : aiCars) {
            aiCar.actor.setVisible(aiCar.active && aiCar.screenY > -180 && aiCar.screenY < VIEW_H + 150);

            if (aiCar.actor.isVisible()) {
                setActorPosition(aiCar.actor, aiCar.x, aiCar.screenY, AI_ACTOR_W, AI_ACTOR_H, 1.0);
                aiCar.actor.toFront();
            }
        }

        if (policeActive) {
            policeActor.toFront();
            playerActor.toFront();
        } else {
            playerActor.toFront();
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
            if (!aiCar.active) {
                continue;
            }
            double aiDistance = clamp(playerDistance + (PLAYER_Y - aiCar.screenY) * 6.0, 0, currentTrackLength);
            drawMiniDot(gc, w / 2.0, miniY(aiDistance, h), aiCar.sameDirection ? Color.web("#7dd3fc") : Color.web("#fbbf24"), 4.2);
        }

        if (policeActive) {
            double policeDistance = clamp(playerDistance + (PLAYER_Y - policeY) * 6.0, 0, currentTrackLength);
            drawMiniDot(gc, w / 2.0, miniY(policeDistance, h), Color.web("#ff4d4f"), 5);
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

        skillLabel.setText("Skill: " + currentSkillDisplayName());

        String stateText;
        if (skillActiveRemaining > 0) {
            stateText = "Đang dùng " + formatSeconds(skillActiveRemaining) + " • còn " + skillUsesRemaining + " lượt";
        } else if (skillCooldownRemaining > 0) {
            stateText = "Hồi chiêu " + formatSeconds(skillCooldownRemaining) + " • còn " + skillUsesRemaining + " lượt";
        } else {
            stateText = "Sẵn sàng • còn " + skillUsesRemaining + " lượt";
        }
        skillStateLabel.setText(stateText);
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
        playerActor.setStyle("");

        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText("Bạn đã va chạm quá số lần cho phép!");
            alert.setContentText("Hãy thử lại nhé!");
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

    private StackPane createPoliceActor(double scale, double width, double height) {
        Node carNode = CarViewFactory.createObstacleRaceCar(CarId.RED_RACER);
        carNode.setScaleX(scale);
        carNode.setScaleY(scale);

        StackPane wrapper = new StackPane(carNode);
        wrapper.setPrefSize(width, height);
        wrapper.setMinSize(width, height);
        wrapper.setMaxSize(width, height);
        wrapper.setMouseTransparent(true);
        wrapper.setStyle("""
            -fx-effect: dropshadow(gaussian, rgba(255,255,255,0.35), 20, 0.3, 0, 0);
        """);
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

    private double worldToScreenY(double worldDistance) {
        return PLAYER_Y - LINE_SCREEN_OFFSET - (worldDistance - playerDistance);
    }

    private double screenYToWorldDistance(double screenY) {
        return playerDistance + (PLAYER_Y - LINE_SCREEN_OFFSET - screenY);
    }

    private double getMainRoadStopWorld(CityRoadBlock block, boolean fromBottomToTop) {
        double roadHalf = getJunctionRoadHalf(block);
        double middle = block.getStartDistance() + block.getLength() * 0.5;

        if (roadHalf > 0) {
            if (fromBottomToTop) {
                return middle + roadHalf + 10.0;
            }
            return middle - roadHalf - 10.0;
        }

        return block.getStartDistance() + block.getLength() * 0.55;
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
        CarId[] ids = {CarId.RED_RACER, CarId.BLUE_STORM, CarId.GREEN_SHADOW};
        return ids[random.nextInt(ids.length)];
    }

    private CarId randomSameDirectionCarId() {
        return randomAiCarId();
    }

    private CarId randomOppositeDirectionCarId() {
        return randomAiCarId();
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

    private double randomRange(double min, double max) {
        return min + random.nextDouble() * (max - min);
    }

    private String formatSeconds(double seconds) {
        return String.format("%.1fs", seconds);
    }

    private void drawHorizontalArrow(GraphicsContext gc, double x, double y, boolean pointRight) {
        gc.setStroke(Color.rgb(255, 255, 255, 0.24));
        gc.setLineWidth(3.0);

        double len = 22;

        if (pointRight) {
            gc.strokeLine(x - len, y, x, y);
            gc.strokeLine(x, y, x - 7, y - 5);
            gc.strokeLine(x, y, x - 7, y + 5);
        } else {
            gc.strokeLine(x + len, y, x, y);
            gc.strokeLine(x, y, x + 7, y - 5);
            gc.strokeLine(x, y, x + 7, y + 5);
        }
    }

    private void drawTree(GraphicsContext gc, double x, double y, double scale) {
        gc.setStroke(Color.rgb(110, 72, 40, 0.9));
        gc.setLineWidth(4 * scale);
        gc.strokeLine(x, y, x, y + 14 * scale);

        gc.setFill(Color.rgb(42, 152, 78, 0.85));
        gc.fillOval(x - 10 * scale, y - 12 * scale, 20 * scale, 18 * scale);
        gc.fillOval(x - 14 * scale, y - 4 * scale, 28 * scale, 16 * scale);
    }

    private final class AiCar {
        private int lane;
        private boolean sameDirection;
        private boolean active;
        private double x;
        private double screenY;
        private double speed;
        private final StackPane actor;

        private AiCar(CarId carId) {
            this.actor = createAiActor(carId, 1.28, AI_ACTOR_W, AI_ACTOR_H);
        }

        private void resetCar(CarId carId) {
            actor.getChildren().clear();
            Node carNode = CarViewFactory.createObstacleRaceCar(carId);
            carNode.setScaleX(1.28);
            carNode.setScaleY(1.28);

            if (!sameDirection) {
                carNode.setRotate(180);
            } else {
                carNode.setRotate(0);
            }

            actor.getChildren().add(carNode);
            actor.toFront();
        }
    }

    private final class Pedestrian {
        private double crosswalkDistance;
        private boolean fromLeft;
        private double progress;
        private double speed;

        private double getScreenY(double playerDistanceNow) {
            return PLAYER_Y - LINE_SCREEN_OFFSET - (crosswalkDistance - playerDistanceNow);
        }

        private double getScreenX() {
            double padding = 16;
            if (fromLeft) {
                return ROAD_X + padding + progress * (ROAD_W - padding * 2);
            }
            return ROAD_X + ROAD_W - padding - progress * (ROAD_W - padding * 2);
        }
    }
}