package com.carracinggame.scene;

import com.carracinggame.car.CarDefinition;
import com.carracinggame.car.CarId;
import com.carracinggame.car.CarSkill;
import com.carracinggame.core.Game;
import com.carracinggame.core.GameConfig;
import com.carracinggame.core.GameState;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

public class UpgradeScene implements AppScene {

    private static final String FONT = "Arial";

    private static final int MAX_SKILL_LEVEL = 20;
    private static final int BASE_UPGRADE_COST = 500;
    private static final int COST_STEP = 250;
    private static final double BONUS_PER_LEVEL = 0.1;

    private final Game game;
    private final Scene scene;
    private final Label messageLabel;
    private final Label coinLabel;
    private final VBox contentBox;

    public UpgradeScene(Game game) {
        this.game = game;

        Label title = new Label("NÂNG CẤP SKILL XE");
        title.setFont(Font.font(FONT, FontWeight.EXTRA_BOLD, 34));
        title.setTextFill(Color.WHITE);

        messageLabel = new Label("Mỗi cấp tăng +0.1 giây thời gian skill khi đua.");
        messageLabel.setTextFill(Color.web("#cbd5e1"));
        messageLabel.setFont(Font.font(FONT, 16));
        messageLabel.setWrapText(true);

        coinLabel = new Label();
        coinLabel.setTextFill(Color.web("#facc15"));
        coinLabel.setFont(Font.font(FONT, FontWeight.BOLD, 16));

        VBox topBox = new VBox(8, title, messageLabel, coinLabel);
        topBox.setPadding(new Insets(24, 24, 10, 24));

        contentBox = new VBox(20);
        contentBox.setPadding(new Insets(10, 24, 24, 24));
        contentBox.setFillWidth(true);
        contentBox.setMaxWidth(1300);

        ScrollPane scrollPane = new ScrollPane(contentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setPannable(true);
        scrollPane.setStyle("""
            -fx-background: transparent;
            -fx-background-color: transparent;
        """);

        StackPane centerWrapper = new StackPane(scrollPane);
        centerWrapper.setPadding(new Insets(0, 8, 0, 8));
        StackPane.setAlignment(scrollPane, Pos.TOP_CENTER);

        Button backButton = createGhostButton("Quay lại gara");
        backButton.setOnAction(event -> game.goTo(GameState.GARAGE));

        HBox bottomBox = new HBox(backButton);
        bottomBox.setAlignment(Pos.CENTER_LEFT);
        bottomBox.setPadding(new Insets(0, 0, 20, 24));

        BorderPane root = new BorderPane();
        root.setPrefSize(GameConfig.WIDTH, GameConfig.HEIGHT);
        root.setTop(topBox);
        root.setCenter(centerWrapper);
        root.setBottom(bottomBox);
        root.setStyle("""
            -fx-background-color:
                radial-gradient(center 15% 12%, radius 45%, rgba(59,130,246,0.18), transparent 55%),
                radial-gradient(center 85% 18%, radius 38%, rgba(124,58,237,0.18), transparent 58%),
                linear-gradient(to bottom, #07111f 0%, #0b1220 45%, #09131c 100%);
        """);

        this.scene = new Scene(root, GameConfig.WIDTH, GameConfig.HEIGHT);
        refresh();
    }

    private void refresh() {
        contentBox.getChildren().clear();

        CarDefinition car = game.getUpgradeCarDefinition();
        if (car == null) {
            Label emptyLabel = new Label("Không tìm thấy xe để nâng cấp.");
            emptyLabel.setTextFill(Color.WHITE);
            emptyLabel.setFont(Font.font(FONT, FontWeight.BOLD, 18));
            contentBox.getChildren().add(emptyLabel);
            return;
        }

        UpgradeData data = UpgradeStore.load(getProfileKey(car.getId()));
        CarSkill skill = CarSkill.forCar(car.getId());

        coinLabel.setText("Coin hiện có: " + getCoins());

        double bonusSeconds = data.getSkillLevel() * BONUS_PER_LEVEL;
        double upgradedDuration = skill.activeSeconds() + bonusSeconds;
        int upgradeCost = getSkillUpgradeCost(data.getSkillLevel());

        Node preview = createCarPreview(car.getId());

        Label name = new Label(car.getName());
        name.setTextFill(Color.WHITE);
        name.setFont(Font.font(FONT, FontWeight.EXTRA_BOLD, 28));

        Label skillName = new Label("Skill: " + skill.name() + " • " + skill.shortLabel());
        skillName.setTextFill(Color.web(skill.accentColor()));
        skillName.setFont(Font.font(FONT, FontWeight.EXTRA_BOLD, 19));

        Label skillDesc = new Label(skill.description());
        skillDesc.setWrapText(true);
        skillDesc.setTextFill(Color.web("#dbeafe"));
        skillDesc.setFont(Font.font(FONT, 15));

        Label stats = new Label(
                "Cấp skill: " + data.getSkillLevel() + "/" + MAX_SKILL_LEVEL +
                        "   •   Gốc: " + formatSeconds(skill.activeSeconds()) +
                        "   •   Cộng thêm: +" + formatSeconds(bonusSeconds) +
                        "   •   Khi đua: " + formatSeconds(upgradedDuration)
        );
        stats.setTextFill(Color.web("#dbeafe"));
        stats.setFont(Font.font(FONT, 16));
        stats.setWrapText(true);

        Label note = new Label("Nâng cấp này chỉ tăng thời gian tồn tại của skill. Mỗi cấp +0.1 giây khi dùng skill trong race.");
        note.setWrapText(true);
        note.setTextFill(Color.web("#93c5fd"));
        note.setFont(Font.font(FONT, 15));

        VBox infoBox = new VBox(10, name, skillName, skillDesc, stats, note);
        infoBox.setAlignment(Pos.CENTER_LEFT);
        infoBox.setMaxWidth(760);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        VBox badgeBox = new VBox(12,
                createBadge("Skill Lv", data.getSkillLevel() + "/" + MAX_SKILL_LEVEL),
                createBadge("Bonus", "+" + formatSeconds(bonusSeconds)),
                createBadge("Khi đua", formatSeconds(upgradedDuration))
        );
        badgeBox.setAlignment(Pos.TOP_RIGHT);

        HBox heroCard = new HBox(24, preview, infoBox, spacer, badgeBox);
        heroCard.setAlignment(Pos.CENTER_LEFT);
        heroCard.setPadding(new Insets(22));
        heroCard.setStyle("""
            -fx-background-color: rgba(15,23,42,0.78);
            -fx-background-radius: 24;
            -fx-border-color: rgba(255,255,255,0.10);
            -fx-border-radius: 24;
        """);

        Label upgradeTitle = createSectionTitle("NÂNG CẤP THỜI GIAN SKILL");

        VBox upgradeBox = new VBox(
                createSkillUpgradeCard(
                        skill.name(),
                        "Mỗi lần nâng cấp tăng thêm 0.1 giây cho skill của xe này khi đua.",
                        data.getSkillLevel(),
                        MAX_SKILL_LEVEL,
                        upgradeCost,
                        skill.activeSeconds(),
                        upgradedDuration,
                        () -> buySkillUpgrade(car.getId())
                )
        );

        contentBox.getChildren().addAll(heroCard, upgradeTitle, upgradeBox);
    }

    private VBox createSkillUpgradeCard(String titleText,
                                        String descText,
                                        int currentLevel,
                                        int maxLevel,
                                        int costValue,
                                        double baseDuration,
                                        double upgradedDuration,
                                        Runnable action) {

        Label title = new Label(titleText);
        title.setTextFill(Color.WHITE);
        title.setFont(Font.font(FONT, FontWeight.EXTRA_BOLD, 22));

        Label level = new Label("Cấp hiện tại: " + currentLevel + "/" + maxLevel);
        level.setTextFill(Color.web("#a5f3fc"));
        level.setFont(Font.font(FONT, FontWeight.BOLD, 15));

        Label desc = new Label(descText);
        desc.setWrapText(true);
        desc.setTextFill(Color.web("#cbd5e1"));
        desc.setFont(Font.font(FONT, 14));

        Label baseInfo = new Label("Thời gian gốc: " + formatSeconds(baseDuration));
        baseInfo.setTextFill(Color.web("#cbd5e1"));
        baseInfo.setFont(Font.font(FONT, 14));

        Label upgradeInfo = new Label("Sau nâng cấp hiện tại khi đua: " + formatSeconds(upgradedDuration));
        upgradeInfo.setTextFill(Color.web("#86efac"));
        upgradeInfo.setFont(Font.font(FONT, FontWeight.BOLD, 14));

        Label perLevel = new Label("Mỗi cấp: +0.1 giây");
        perLevel.setTextFill(Color.web("#93c5fd"));
        perLevel.setFont(Font.font(FONT, 13));

        Label cost = new Label(currentLevel >= maxLevel ? "Đã tối đa" : "Chi phí: " + costValue + " coin");
        cost.setTextFill(currentLevel >= maxLevel ? Color.web("#94a3b8") : Color.web("#fcd34d"));
        cost.setFont(Font.font(FONT, FontWeight.BOLD, 15));

        Button upgradeButton = createPrimaryButton(currentLevel >= maxLevel ? "Tối đa" : "Nâng cấp");
        upgradeButton.setDisable(currentLevel >= maxLevel);
        upgradeButton.setOpacity(currentLevel >= maxLevel ? 0.75 : 1.0);
        upgradeButton.setOnAction(event -> action.run());

        VBox box = new VBox(12, title, level, desc, baseInfo, upgradeInfo, perLevel, cost, upgradeButton);
        box.setPrefWidth(420);
        box.setMaxWidth(420);
        box.setPadding(new Insets(20));
        box.setStyle("""
            -fx-background-color: rgba(30,41,59,0.86);
            -fx-background-radius: 20;
            -fx-border-color: rgba(255,255,255,0.08);
            -fx-border-radius: 20;
        """);

        return box;
    }

    private Label createSectionTitle(String text) {
        Label label = new Label(text);
        label.setTextFill(Color.WHITE);
        label.setFont(Font.font(FONT, FontWeight.EXTRA_BOLD, 22));
        return label;
    }

    private HBox createBadge(String labelText, String valueText) {
        Label label = new Label(labelText + ": ");
        label.setTextFill(Color.web("#cbd5e1"));
        label.setFont(Font.font(FONT, FontWeight.BOLD, 13));

        Label value = new Label(valueText);
        value.setTextFill(Color.WHITE);
        value.setFont(Font.font(FONT, FontWeight.EXTRA_BOLD, 13));

        HBox box = new HBox(label, value);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(8, 12, 8, 12));
        box.setStyle("""
            -fx-background-color: rgba(255,255,255,0.06);
            -fx-background-radius: 14;
            -fx-border-color: rgba(255,255,255,0.08);
            -fx-border-radius: 14;
        """);

        return box;
    }

    private Button createPrimaryButton(String text) {
        Button button = new Button(text);
        button.setFont(Font.font(FONT, FontWeight.BOLD, 15));
        button.setTextFill(Color.WHITE);
        button.setPrefWidth(170);
        button.setPrefHeight(40);
        button.setStyle("""
            -fx-background-color: linear-gradient(to right, #7c3aed, #2563eb);
            -fx-background-radius: 14;
            -fx-cursor: hand;
        """);
        return button;
    }

    private Button createGhostButton(String text) {
        Button button = new Button(text);
        button.setFont(Font.font(FONT, FontWeight.BOLD, 16));
        button.setTextFill(Color.WHITE);
        button.setPrefHeight(42);
        button.setStyle("""
            -fx-background-color: rgba(15,23,42,0.85);
            -fx-background-radius: 14;
            -fx-border-color: rgba(255,255,255,0.12);
            -fx-border-radius: 14;
            -fx-cursor: hand;
        """);
        return button;
    }

    private void buySkillUpgrade(CarId carId) {
        UpgradeData data = UpgradeStore.load(getProfileKey(carId));

        int level = data.skillLevel;
        if (level >= MAX_SKILL_LEVEL) {
            messageLabel.setText("Skill đã đạt cấp tối đa.");
            refresh();
            return;
        }

        int cost = getSkillUpgradeCost(level);
        if (!spendCoins(cost)) {
            messageLabel.setText("Không đủ coin để nâng cấp skill.");
            refresh();
            return;
        }

        data.skillLevel++;
        UpgradeStore.save(getProfileKey(carId), data);
        messageLabel.setText("Nâng cấp skill thành công. +" + formatSeconds(BONUS_PER_LEVEL) + " thời gian khi đua.");
        refresh();
    }

    private int getSkillUpgradeCost(int currentLevel) {
        return BASE_UPGRADE_COST + currentLevel * COST_STEP;
    }

    private String getProfileKey(CarId carId) {
        return resolveUsername(game) + "|" + String.valueOf(carId);
    }

    private static String resolveUsername(Game game) {
        try {
            if (game == null) return "guest";

            Object profile = game.getPlayerProfile();
            if (profile == null) return "guest";

            Method getUsername = profile.getClass().getMethod("getUsername");
            Object value = getUsername.invoke(profile);
            return value == null ? "guest" : String.valueOf(value);
        } catch (Exception e) {
            return "guest";
        }
    }

    private int getCoins() {
        try {
            Object profile = game.getPlayerProfile();
            if (profile == null) return 0;

            Method method = profile.getClass().getMethod("getCoins");
            Object value = method.invoke(profile);

            if (value instanceof Number number) {
                return number.intValue();
            }
            return Integer.parseInt(String.valueOf(value));
        } catch (Exception e) {
            return 0;
        }
    }

    private boolean spendCoins(int amount) {
        try {
            Object profile = game.getPlayerProfile();
            if (profile == null) return false;

            int currentCoins = getCoins();
            if (currentCoins < amount) return false;

            for (String setterName : List.of("setCoins", "updateCoins", "setCoin")) {
                try {
                    Method setter = profile.getClass().getMethod(setterName, int.class);
                    setter.invoke(profile, currentCoins - amount);
                    trySyncProfile();
                    return true;
                } catch (NoSuchMethodException ignored) {
                }
            }

            for (String methodName : List.of("spendCoins", "deductCoins", "removeCoins", "decreaseCoins")) {
                try {
                    Method spend = profile.getClass().getMethod(methodName, int.class);
                    Object result = spend.invoke(profile, amount);
                    trySyncProfile();

                    if (result instanceof Boolean ok) {
                        return ok;
                    }
                    return true;
                } catch (NoSuchMethodException ignored) {
                }
            }

            try {
                Method addCoins = profile.getClass().getMethod("addCoins", int.class);
                addCoins.invoke(profile, -amount);
                trySyncProfile();
                return true;
            } catch (NoSuchMethodException ignored) {
            }

            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private void trySyncProfile() {
        for (String methodName : List.of("saveProfile", "savePlayerData", "syncPlayerData", "persistProfile")) {
            try {
                Method method = game.getClass().getMethod(methodName);
                method.invoke(game);
                return;
            } catch (Exception ignored) {
            }
        }
    }

    private Node createCarPreview(CarId carId) {
        StackPane previewBox = new StackPane();
        previewBox.setPrefSize(240, 165);
        previewBox.setMinSize(240, 165);
        previewBox.setMaxSize(240, 165);
        previewBox.setStyle("""
            -fx-background-color: rgba(255,255,255,0.05);
            -fx-background-radius: 20;
            -fx-border-color: rgba(255,255,255,0.10);
            -fx-border-radius: 20;
        """);

        Node carView = CarViewFactory.createShopPreview(carId);
        carView.setScaleX(0.72);
        carView.setScaleY(0.72);

        previewBox.getChildren().add(carView);
        return previewBox;
    }

    private String formatSeconds(double seconds) {
        return String.format("%.1fs", seconds);
    }

    @Override
    public Scene getScene() {
        return scene;
    }

    @Override
    public void onShow() {
        refresh();
        requestFocus();
    }

    public static UpgradeData getUpgradeData(String username, CarId carId) {
        return UpgradeStore.load(username + "|" + String.valueOf(carId));
    }

    public static int getSkillLevel(Game game, CarId carId) {
        return UpgradeStore.load(resolveUsername(game) + "|" + String.valueOf(carId)).getSkillLevel();
    }

    public static double getExtraSkillSeconds(Game game, CarId carId) {
        return getSkillLevel(game, carId) * BONUS_PER_LEVEL;
    }

    public static double getUpgradedSkillDuration(Game game, CarId carId) {
        return CarSkill.forCar(carId).activeSeconds() + getExtraSkillSeconds(game, carId);
    }

    public static final class UpgradeData {
        private int skillLevel;

        public int getSkillLevel() {
            return skillLevel;
        }
    }

    private static final class UpgradeStore {
        private static final Path STORE_PATH =
                Paths.get(System.getProperty("user.home"), ".carracinggame", "car-skill-upgrades.properties");

        static UpgradeData load(String key) {
            Properties properties = readProperties();

            UpgradeData data = new UpgradeData();
            data.skillLevel = parseInt(properties.getProperty(key + ".skillLevel"), 0);
            return data;
        }

        static void save(String key, UpgradeData data) {
            Properties properties = readProperties();
            properties.setProperty(key + ".skillLevel", String.valueOf(data.skillLevel));

            try {
                Files.createDirectories(STORE_PATH.getParent());
                try (OutputStream outputStream = Files.newOutputStream(STORE_PATH)) {
                    properties.store(outputStream, "Car skill upgrade data");
                }
            } catch (IOException ignored) {
            }
        }

        private static Properties readProperties() {
            Properties properties = new Properties();

            if (!Files.exists(STORE_PATH)) {
                return properties;
            }

            try (InputStream inputStream = Files.newInputStream(STORE_PATH)) {
                properties.load(inputStream);
            } catch (IOException ignored) {
            }

            return properties;
        }

        private static int parseInt(String value, int fallback) {
            try {
                return Integer.parseInt(value);
            } catch (Exception e) {
                return fallback;
            }
        }
    }
}