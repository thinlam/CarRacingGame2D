package com.carracinggame.scene;

import com.carracinggame.car.CarDefinition;
import com.carracinggame.car.CarId;
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
import javafx.scene.layout.FlowPane;
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

    private static final int MAX_HANDLING = 5;
    private static final int MAX_SHIELD = 3;
    private static final int MAX_BRAKE = 5;

    private final Game game;
    private final Scene scene;
    private final Label messageLabel;
    private final Label coinLabel;
    private final VBox contentBox;

    public UpgradeScene(Game game) {
        this.game = game;

        Label title = new Label("NÂNG CẤP XE");
        title.setFont(Font.font(FONT, FontWeight.EXTRA_BOLD, 34));
        title.setTextFill(Color.WHITE);

        messageLabel = new Label("Tăng chỉ số để né vật cản tốt hơn, hoặc đổi màu sơn / decal cho xe.");
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
        contentBox.setMaxWidth(1500);

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
        coinLabel.setText("Coin hiện có: " + getCoins());

        Node preview = createCarPreview(car.getId(), data);

        Label name = new Label(car.getName());
        name.setTextFill(Color.WHITE);
        name.setFont(Font.font(FONT, FontWeight.EXTRA_BOLD, 28));

        Label stats = new Label(buildStatText(data));
        stats.setTextFill(Color.web("#dbeafe"));
        stats.setFont(Font.font(FONT, 16));

        Label note = new Label("Xe này phù hợp lối chơi né vật cản với Handling, Shield và Brake / Control.");
        note.setWrapText(true);
        note.setTextFill(Color.web("#93c5fd"));
        note.setFont(Font.font(FONT, 15));

        VBox infoBox = new VBox(10, name, stats, note);
        infoBox.setAlignment(Pos.CENTER_LEFT);
        infoBox.setMaxWidth(720);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        VBox badgeBox = new VBox(12,
                createBadge("Màu sơn", data.paintName),
                createBadge("Decal", data.decalName),
                createBadge("Shield", data.shieldLevel + "/" + MAX_SHIELD)
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

        Label gameplayTitle = createSectionTitle("NÂNG CẤP GAMEPLAY");

        FlowPane gameplayPane = new FlowPane();
        gameplayPane.setHgap(18);
        gameplayPane.setVgap(18);
        gameplayPane.setPrefWrapLength(1100);
        gameplayPane.getChildren().addAll(
                createUpgradeCard(
                        "Handling",
                        "Đổi làn nhanh hơn, vào cua gọn hơn, né chướng ngại mượt hơn.",
                        data.handlingLevel,
                        MAX_HANDLING,
                        500 + data.handlingLevel * 400,
                        "Tăng mạnh khả năng né vật cản.",
                        () -> buyGameplay(car.getId(), "handling")
                ),
                createUpgradeCard(
                        "Shield",
                        "Tạo lớp khiên bảo vệ tạm thời. Va chạm sẽ không bị hạ gục ngay.",
                        data.shieldLevel,
                        MAX_SHIELD,
                        800 + data.shieldLevel * 500,
                        "Cứu thua rất tốt ở đoạn đường khó.",
                        () -> buyGameplay(car.getId(), "shield")
                ),
                createUpgradeCard(
                        "Brake / Control",
                        "Giảm tốc và kiểm soát thân xe tốt hơn khi vào đoạn nhiều vật cản.",
                        data.brakeLevel,
                        MAX_BRAKE,
                        400 + data.brakeLevel * 350,
                        "Hợp lối chơi canh nhịp và né chính xác.",
                        () -> buyGameplay(car.getId(), "brake")
                )
        );

        Label appearanceTitle = createSectionTitle("NGOẠI HÌNH & CÁ NHÂN HÓA");

        FlowPane paintPane = new FlowPane();
        paintPane.setHgap(18);
        paintPane.setVgap(18);
        paintPane.setPrefWrapLength(1100);
        paintPane.getChildren().addAll(
                createStyleCard(
                        "Xanh Neon",
                        "Sơn xanh lạnh nổi bật, hợp vibe tốc độ ban đêm.",
                        250,
                        data.paintName.equals("Xanh Neon"),
                        () -> buyPaint(car.getId(), "Xanh Neon", 250)
                ),
                createStyleCard(
                        "Đỏ Flame",
                        "Sơn đỏ đậm thể thao, cảm giác xe đua hơn.",
                        300,
                        data.paintName.equals("Đỏ Flame"),
                        () -> buyPaint(car.getId(), "Đỏ Flame", 300)
                ),
                createStyleCard(
                        "Vàng Gold",
                        "Sơn vàng ánh kim, nhìn nổi và sang hơn.",
                        450,
                        data.paintName.equals("Vàng Gold"),
                        () -> buyPaint(car.getId(), "Vàng Gold", 450)
                )
        );

        FlowPane decalPane = new FlowPane();
        decalPane.setHgap(18);
        decalPane.setVgap(18);
        decalPane.setPrefWrapLength(1100);
        decalPane.getChildren().addAll(
                createStyleCard(
                        "Decal Tia Chớp",
                        "Sọc chéo tốc độ, nhìn xe sắc và nhanh hơn.",
                        300,
                        data.decalName.equals("Tia Chớp"),
                        () -> buyDecal(car.getId(), "Tia Chớp", 300)
                ),
                createStyleCard(
                        "Decal Lửa",
                        "Họa tiết ngọn lửa đậm chất arcade racing.",
                        400,
                        data.decalName.equals("Lửa"),
                        () -> buyDecal(car.getId(), "Lửa", 400)
                ),
                createStyleCard(
                        "Decal Carbon",
                        "Phong cách carbon tối màu, ngầu và hiện đại.",
                        550,
                        data.decalName.equals("Carbon"),
                        () -> buyDecal(car.getId(), "Carbon", 550)
                )
        );

        contentBox.getChildren().addAll(
                heroCard,
                gameplayTitle,
                gameplayPane,
                appearanceTitle,
                paintPane,
                decalPane
        );
    }

    private VBox createUpgradeCard(String titleText,
                                   String descText,
                                   int currentLevel,
                                   int maxLevel,
                                   int costValue,
                                   String bonusText,
                                   Runnable action) {

        Label title = new Label(titleText);
        title.setTextFill(Color.WHITE);
        title.setFont(Font.font(FONT, FontWeight.EXTRA_BOLD, 20));

        Label level = new Label("Cấp hiện tại: " + currentLevel + "/" + maxLevel);
        level.setTextFill(Color.web("#a5f3fc"));
        level.setFont(Font.font(FONT, FontWeight.BOLD, 14));

        Label desc = new Label(descText);
        desc.setWrapText(true);
        desc.setTextFill(Color.web("#cbd5e1"));
        desc.setFont(Font.font(FONT, 14));

        Label bonus = new Label("Hiệu quả: " + bonusText);
        bonus.setWrapText(true);
        bonus.setTextFill(Color.web("#86efac"));
        bonus.setFont(Font.font(FONT, 13));

        Label cost = new Label(currentLevel >= maxLevel ? "Đã tối đa" : "Chi phí: " + costValue + " coin");
        cost.setTextFill(currentLevel >= maxLevel ? Color.web("#94a3b8") : Color.web("#fcd34d"));
        cost.setFont(Font.font(FONT, FontWeight.BOLD, 15));

        Button upgradeButton = createPrimaryButton(currentLevel >= maxLevel ? "Tối đa" : "Nâng cấp");
        upgradeButton.setDisable(currentLevel >= maxLevel);
        upgradeButton.setOpacity(currentLevel >= maxLevel ? 0.75 : 1.0);
        upgradeButton.setOnAction(event -> action.run());

        VBox box = new VBox(12, title, level, desc, bonus, cost, upgradeButton);
        box.setPrefWidth(320);
        box.setMinWidth(320);
        box.setMaxWidth(320);
        box.setPadding(new Insets(18));
        box.setStyle("""
            -fx-background-color: rgba(30,41,59,0.86);
            -fx-background-radius: 20;
            -fx-border-color: rgba(255,255,255,0.08);
            -fx-border-radius: 20;
        """);

        return box;
    }

    private VBox createStyleCard(String titleText,
                                 String descText,
                                 int costValue,
                                 boolean selected,
                                 Runnable action) {

        Label title = new Label(titleText);
        title.setTextFill(Color.WHITE);
        title.setFont(Font.font(FONT, FontWeight.EXTRA_BOLD, 19));

        Label desc = new Label(descText);
        desc.setWrapText(true);
        desc.setTextFill(Color.web("#cbd5e1"));
        desc.setFont(Font.font(FONT, 14));

        Label cost = new Label(selected ? "Đang sử dụng" : "Mở khóa: " + costValue + " coin");
        cost.setTextFill(selected ? Color.web("#86efac") : Color.web("#fcd34d"));
        cost.setFont(Font.font(FONT, FontWeight.BOLD, 15));

        Button button = createPrimaryButton(selected ? "Đã chọn" : "Mua & áp dụng");
        button.setDisable(selected);
        button.setOpacity(selected ? 0.75 : 1.0);
        button.setOnAction(event -> action.run());

        VBox box = new VBox(12, title, desc, cost, button);
        box.setPrefWidth(320);
        box.setMinWidth(320);
        box.setMaxWidth(320);
        box.setPadding(new Insets(18));
        box.setStyle("""
            -fx-background-color: rgba(22,30,46,0.86);
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

    private void buyGameplay(CarId carId, String type) {
        UpgradeData data = UpgradeStore.load(getProfileKey(carId));

        int level;
        int max;
        int cost;

        switch (type) {
            case "handling" -> {
                level = data.handlingLevel;
                max = MAX_HANDLING;
                cost = 500 + level * 400;
            }
            case "shield" -> {
                level = data.shieldLevel;
                max = MAX_SHIELD;
                cost = 800 + level * 500;
            }
            default -> {
                level = data.brakeLevel;
                max = MAX_BRAKE;
                cost = 400 + level * 350;
            }
        }

        if (level >= max) {
            messageLabel.setText("Hạng mục này đã đạt cấp tối đa.");
            refresh();
            return;
        }

        if (!spendCoins(cost)) {
            messageLabel.setText("Không đủ coin để nâng cấp " + prettyType(type) + ".");
            refresh();
            return;
        }

        switch (type) {
            case "handling" -> data.handlingLevel++;
            case "shield" -> data.shieldLevel++;
            default -> data.brakeLevel++;
        }

        UpgradeStore.save(getProfileKey(carId), data);
        messageLabel.setText("Nâng cấp thành công " + prettyType(type) + ".");
        refresh();
    }

    private void buyPaint(CarId carId, String paintName, int cost) {
        UpgradeData data = UpgradeStore.load(getProfileKey(carId));

        if (paintName.equals(data.paintName)) {
            messageLabel.setText("Màu sơn này đang được sử dụng.");
            refresh();
            return;
        }

        if (!spendCoins(cost)) {
            messageLabel.setText("Không đủ coin để mở khóa màu sơn " + paintName + ".");
            refresh();
            return;
        }

        data.paintName = paintName;
        UpgradeStore.save(getProfileKey(carId), data);
        messageLabel.setText("Đã mở khóa và áp dụng màu sơn " + paintName + ".");
        refresh();
    }

    private void buyDecal(CarId carId, String decalName, int cost) {
        UpgradeData data = UpgradeStore.load(getProfileKey(carId));

        if (decalName.equals(data.decalName)) {
            messageLabel.setText("Decal này đang được sử dụng.");
            refresh();
            return;
        }

        if (!spendCoins(cost)) {
            messageLabel.setText("Không đủ coin để mở khóa decal " + decalName + ".");
            refresh();
            return;
        }

        data.decalName = decalName;
        UpgradeStore.save(getProfileKey(carId), data);
        messageLabel.setText("Đã mở khóa và áp dụng decal " + decalName + ".");
        refresh();
    }

    private String buildStatText(UpgradeData data) {
        return "Handling " + data.handlingLevel + "/" + MAX_HANDLING
                + "   •   Shield " + data.shieldLevel + "/" + MAX_SHIELD
                + "   •   Brake " + data.brakeLevel + "/" + MAX_BRAKE;
    }

    private String prettyType(String type) {
        return switch (type) {
            case "handling" -> "Handling";
            case "shield" -> "Shield";
            default -> "Brake / Control";
        };
    }

    private String getProfileKey(CarId carId) {
        return resolveUsername() + "|" + String.valueOf(carId);
    }

    private String resolveUsername() {
        try {
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

    private Node createCarPreview(CarId carId, UpgradeData data) {
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

        CarPaintUtil.applyPaint(carView, data.paintName);

        Label decalLabel = new Label("Decal: " + data.decalName);
        decalLabel.setTextFill(Color.WHITE);
        decalLabel.setFont(Font.font(FONT, FontWeight.BOLD, 11));
        decalLabel.setStyle("""
            -fx-background-color: rgba(0,0,0,0.35);
            -fx-background-radius: 10;
            -fx-padding: 6 10 6 10;
        """);

        StackPane.setAlignment(decalLabel, Pos.BOTTOM_CENTER);
        StackPane.setMargin(decalLabel, new Insets(0, 0, 10, 0));

        previewBox.getChildren().addAll(carView, decalLabel);
        return previewBox;
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

    public static final class UpgradeData {
        private int handlingLevel;
        private int shieldLevel;
        private int brakeLevel;
        private String paintName = "Mặc định";
        private String decalName = "Mặc định";

        public int getHandlingLevel() {
            return handlingLevel;
        }

        public int getShieldLevel() {
            return shieldLevel;
        }

        public int getBrakeLevel() {
            return brakeLevel;
        }

        public String getPaintName() {
            return paintName;
        }

        public String getDecalName() {
            return decalName;
        }
    }

    private static final class UpgradeStore {
        private static final Path STORE_PATH =
                Paths.get(System.getProperty("user.home"), ".carracinggame", "car-upgrades.properties");

        static UpgradeData load(String key) {
            Properties properties = readProperties();

            UpgradeData data = new UpgradeData();
            data.handlingLevel = parseInt(properties.getProperty(key + ".handling"), 0);
            data.shieldLevel = parseInt(properties.getProperty(key + ".shield"), 0);
            data.brakeLevel = parseInt(properties.getProperty(key + ".brake"), 0);
            data.paintName = properties.getProperty(key + ".paint", "Mặc định");
            data.decalName = properties.getProperty(key + ".decal", "Mặc định");
            return data;
        }

        static void save(String key, UpgradeData data) {
            Properties properties = readProperties();
            properties.setProperty(key + ".handling", String.valueOf(data.handlingLevel));
            properties.setProperty(key + ".shield", String.valueOf(data.shieldLevel));
            properties.setProperty(key + ".brake", String.valueOf(data.brakeLevel));
            properties.setProperty(key + ".paint", data.paintName);
            properties.setProperty(key + ".decal", data.decalName);

            try {
                Files.createDirectories(STORE_PATH.getParent());
                try (OutputStream outputStream = Files.newOutputStream(STORE_PATH)) {
                    properties.store(outputStream, "Car upgrade data");
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