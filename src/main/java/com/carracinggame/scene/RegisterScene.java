package com.carracinggame.scene;

import com.carracinggame.database.PlayerDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RegisterScene {

    private final Stage stage;
    private final Runnable goToLoginScene;
    private final Runnable onLoginSuccess;
    private final PlayerDAO playerDAO = new PlayerDAO();

    private TextField usernameField;
    private TextField emailField;
    private PasswordField passwordField;
    private PasswordField confirmField;
    private Label messageLabel;

    private final List<AvatarOption> avatarOptions = new ArrayList<>();
    private String selectedAvatarId = "helmet_a";

    private VBox selectedAvatarPreviewBox;
    private Label selectedAvatarNameLabel;

    public RegisterScene(Stage stage, Runnable goToLoginScene, Runnable onLoginSuccess) {
        this.stage = stage;
        this.goToLoginScene = goToLoginScene;
        this.onLoginSuccess = onLoginSuccess;
        initAvatarOptions();
    }

    public static Scene create(Stage stage, Runnable goToLoginScene, Runnable onLoginSuccess) {
        return new RegisterScene(stage, goToLoginScene, onLoginSuccess).build();
    }

    private void initAvatarOptions() {
        avatarOptions.add(new AvatarOption("helmet_a", "Lightning McQueen", "/images/cars/ui/avatar-helmet-a.png", true, null));
        avatarOptions.add(new AvatarOption("helmet_b", "Francesco Bernoulli", "/images/cars/ui/avatar-helmet-b.png", true, null));
        avatarOptions.add(new AvatarOption("car_red", "Carla Veloso", "/images/cars/ui/avatar-car-red.png", true, null));
        avatarOptions.add(new AvatarOption("car_blue", "Raoul Çaroule", "/images/cars/ui/avatar-car-blue.png", true, null));
        avatarOptions.add(new AvatarOption("racer_01", "Nigel Gearsley", "/images/cars/ui/avatar-racer-01.png", true, null));

        avatarOptions.add(new AvatarOption("racer_02", "Jeff Gorvette", "/images/cars/ui/avatar-racer-02.png",true, null ));
        avatarOptions.add(new AvatarOption("cyber_driver", "Lewis Hamilton", "/images/cars/ui/avatar-cyber-driver.png", true, null));
        avatarOptions.add(new AvatarOption("custom", "Miguel Camino", "/images/cars/ui/avatar-custom.png", true, null));
    }

    private Scene build() {
        StackPane root = new StackPane();
        root.getStyleClass().add("register-root");

        URL bgUrl = getClass().getResource("/images/cars/ui/login-bg.jpg");
        if (bgUrl != null) {
            ImageView bgView = new ImageView(new Image(bgUrl.toExternalForm()));
            bgView.setPreserveRatio(false);
            bgView.fitWidthProperty().bind(root.widthProperty());
            bgView.fitHeightProperty().bind(root.heightProperty());
            bgView.setOpacity(0.26);
            bgView.setEffect(new GaussianBlur(18));
            root.getChildren().add(bgView);
        }

        Region overlay = new Region();
        overlay.getStyleClass().add("background-overlay");
        overlay.prefWidthProperty().bind(root.widthProperty());
        overlay.prefHeightProperty().bind(root.heightProperty());

        BorderPane shell = new BorderPane();
        shell.setPadding(new Insets(18, 28, 18, 28));

        shell.setTop(buildTopBar());

        VBox centerWrap = new VBox(22);
        centerWrap.setAlignment(Pos.TOP_CENTER);

        HBox content = new HBox(26);
        content.setAlignment(Pos.TOP_CENTER);
        content.setMaxWidth(920);

        VBox leftPanel = buildRegisterPanel();
        VBox rightPanel = buildAvatarPanel();

        content.getChildren().addAll(leftPanel, rightPanel);

        Label footer = new Label("HYPER ENGINE V4.2     •     GLOBAL SERVERS ONLINE");
        footer.getStyleClass().add("hud-label");

        centerWrap.getChildren().addAll(buildHeroBanner(), content, footer);
        shell.setCenter(centerWrap);

        root.getChildren().addAll(overlay, shell);

        Scene scene = new Scene(root, 1366, 768);
        scene.getStylesheets().add(
                getClass().getResource("/css/login.css").toExternalForm()
        );
        return scene;
    }

    private HBox buildTopBar() {
        Label logo = new Label("RACE GRID");
        logo.getStyleClass().add("brand-label");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label help = new Label("HELP");
        help.getStyleClass().add("top-link");

        Button settings = new Button("⚙");
        settings.getStyleClass().add("top-icon-btn");

        HBox bar = new HBox(16, logo, spacer, help, settings);
        bar.setAlignment(Pos.CENTER_LEFT);
        return bar;
    }

    private StackPane buildHeroBanner() {
        StackPane banner = new StackPane();
        banner.getStyleClass().add("hero-banner");
        banner.setPrefWidth(920);
        banner.setMinHeight(180);
        banner.setMaxWidth(920);

        Rectangle clip = new Rectangle(920, 180);
        clip.setArcWidth(24);
        clip.setArcHeight(24);
        banner.setClip(clip);

        URL bannerUrl = getClass().getResource("/images/cars/ui/register-banner.jpg");
        if (bannerUrl != null) {
            ImageView bannerImage = new ImageView(new Image(bannerUrl.toExternalForm()));
            bannerImage.setFitWidth(920);
            bannerImage.setFitHeight(180);
            bannerImage.setPreserveRatio(false);
            bannerImage.setOpacity(0.88);
            banner.getChildren().add(bannerImage);
        }

        Region darkOverlay = new Region();
        darkOverlay.setPrefSize(920, 180);
        darkOverlay.setStyle("""
            -fx-background-color:
                linear-gradient(to right, rgba(4,14,24,0.90), rgba(4,14,24,0.38));
            -fx-background-radius: 12;
        """);

        VBox textBox = new VBox(10);
        textBox.setAlignment(Pos.CENTER_LEFT);
        textBox.setPadding(new Insets(28, 34, 28, 34));

        Label season = new Label("SEASON ONE");
        season.getStyleClass().add("hero-kicker");

        Label title = new Label("NEW PILOT REGISTRATION");
        title.getStyleClass().add("hero-title");

        Label desc = new Label("Secure your position on the starting line and claim your legacy.");
        desc.getStyleClass().add("hero-desc");
        desc.setWrapText(true);
        desc.setMaxWidth(430);

        textBox.getChildren().addAll(season, title, desc);

        banner.getChildren().addAll(darkOverlay, textBox);
        StackPane.setAlignment(textBox, Pos.CENTER_LEFT);

        return banner;
    }

    private VBox buildRegisterPanel() {
        VBox panel = new VBox(14);
        panel.getStyleClass().add("register-panel");
        panel.setPrefWidth(540);

        Label sectionTitle = new Label("DRIVER IDENTIFICATION");
        sectionTitle.getStyleClass().add("panel-title");

        usernameField = new TextField();
        usernameField.setPromptText("Nickname");
        VBox userBox = fieldGroup("NICKNAME", createInputShell("@", usernameField));

        emailField = new TextField();
        emailField.setPromptText("Email address");
        VBox emailBox = fieldGroup("EMAIL ADDRESS", createInputShell("✉", emailField));

        passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        confirmField = new PasswordField();
        confirmField.setPromptText("Confirm password");

        VBox passBox = fieldGroup("PASSWORD", createInputShell("🔒", passwordField));
        VBox confirmBox = fieldGroup("CONFIRM", createInputShell("🛡", confirmField));

        HBox.setHgrow(passBox, Priority.ALWAYS);
        HBox.setHgrow(confirmBox, Priority.ALWAYS);
        passBox.setMaxWidth(Double.MAX_VALUE);
        confirmBox.setMaxWidth(Double.MAX_VALUE);

        HBox passRow = new HBox(16, passBox, confirmBox);
        passRow.setAlignment(Pos.CENTER);

        CheckBox acceptBox = new CheckBox("I accept the Rules of Engagement and Privacy Policy.");
        acceptBox.getStyleClass().add("remember-box");

        Button registerButton = new Button("JOIN THE GRID  »");
        registerButton.getStyleClass().add("register-btn");
        registerButton.setMaxWidth(Double.MAX_VALUE);

        messageLabel = new Label();
        messageLabel.getStyleClass().add("message-label");
        messageLabel.setWrapText(true);

        HBox loginSwitch = new HBox();
        loginSwitch.setAlignment(Pos.CENTER);

        Label text = new Label("Already a pilot? ");
        text.getStyleClass().add("helper-label");

        Hyperlink backLogin = new Hyperlink("Back to Pits ➜");
        backLogin.getStyleClass().add("switch-link");
        backLogin.setBorder(Border.EMPTY);
        backLogin.setPadding(Insets.EMPTY);
        backLogin.setOnAction(e -> goToLoginScene.run());

        loginSwitch.getChildren().addAll(text, backLogin);

        registerButton.setOnAction(e -> handleRegister(acceptBox));
        usernameField.setOnAction(e -> handleRegister(acceptBox));
        emailField.setOnAction(e -> handleRegister(acceptBox));
        passwordField.setOnAction(e -> handleRegister(acceptBox));
        confirmField.setOnAction(e -> handleRegister(acceptBox));

        panel.getChildren().addAll(
                sectionTitle,
                userBox,
                emailBox,
                passRow,
                acceptBox,
                registerButton,
                messageLabel,
                loginSwitch
        );

        return panel;
    }

    private VBox buildAvatarPanel() {
        VBox panel = new VBox(14);
        panel.getStyleClass().add("avatar-panel");
        panel.setPrefWidth(320);

        HBox titleRow = new HBox();
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("CHOOSE AVATAR");
        title.getStyleClass().add("panel-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button allButton = new Button("TẤT CẢ");
        allButton.getStyleClass().add("all-avatar-btn");
        allButton.setOnAction(e -> openAvatarPicker());

        titleRow.getChildren().addAll(title, spacer, allButton);

        selectedAvatarPreviewBox = new VBox(10);
        selectedAvatarPreviewBox.getStyleClass().add("selected-avatar-box");
        selectedAvatarPreviewBox.setAlignment(Pos.CENTER);

        selectedAvatarNameLabel = new Label();
        selectedAvatarNameLabel.getStyleClass().add("selected-avatar-name");

        updateSelectedAvatarPreview();

        VBox statsBox = new VBox(10);
        statsBox.getStyleClass().add("stats-box");

        Label statsTitle = new Label("DRIVER STATS PREVIEW");
        statsTitle.getStyleClass().add("stats-title");

        statsBox.getChildren().addAll(
                statsTitle,
                statRow("Reaction", 0.86),
                statRow("Technical", 0.42)
        );

        panel.getChildren().addAll(titleRow, selectedAvatarPreviewBox, selectedAvatarNameLabel, statsBox);
        return panel;
    }

    private void updateSelectedAvatarPreview() {
        selectedAvatarPreviewBox.getChildren().clear();

        AvatarOption selected = findAvatarById(selectedAvatarId);
        if (selected == null) {
            selectedAvatarNameLabel.setText("Chưa chọn pilot");
            return;
        }

        URL imageUrl = getClass().getResource(selected.imagePath());
        if (imageUrl != null) {
            ImageView imageView = new ImageView(new Image(imageUrl.toExternalForm()));
            imageView.setFitWidth(210);
            imageView.setFitHeight(160);
            imageView.setPreserveRatio(false);

            StackPane wrapper = new StackPane(imageView);
            wrapper.setPrefSize(210, 160);
            wrapper.getStyleClass().add("selected-avatar-image-wrap");
            selectedAvatarPreviewBox.getChildren().add(wrapper);
        } else {
            Label fallback = new Label(selected.displayName());
            fallback.getStyleClass().add("avatar-fallback-text");
            fallback.setPrefSize(210, 160);
            fallback.setAlignment(Pos.CENTER);
            selectedAvatarPreviewBox.getChildren().add(fallback);
        }

        selectedAvatarNameLabel.setText("Pilot active: " + selected.displayName());
    }

    private void openAvatarPicker() {
        Stage dialog = new Stage();
        dialog.initOwner(stage);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Select Your Pilot");
        dialog.setResizable(false);

        BorderPane root = new BorderPane();
        root.getStyleClass().add("pilot-dialog-root");
        root.setPadding(new Insets(18, 18, 18, 18));

        VBox headerBox = new VBox(6);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Label moduleLabel = new Label("MODULE  //  SELECTION");
        moduleLabel.getStyleClass().add("pilot-module-label");

        Label titleLabel = new Label("SELECT YOUR PILOT");
        titleLabel.getStyleClass().add("pilot-dialog-title");

        Region titleLine = new Region();
        titleLine.getStyleClass().add("pilot-title-line");
        titleLine.setPrefWidth(82);
        titleLine.setPrefHeight(3);

        headerBox.getChildren().addAll(moduleLabel, titleLabel, titleLine);

        ToggleGroup pickerGroup = new ToggleGroup();
        GridPane grid = new GridPane();
        grid.setHgap(14);
        grid.setVgap(14);

        int col = 0;
        int row = 0;

        for (AvatarOption option : avatarOptions) {
            ToggleButton card = createPilotCard(option);
            card.setToggleGroup(pickerGroup);

            if (!option.unlocked()) {
                card.setDisable(true);
            }

            if (option.id().equals(selectedAvatarId) && option.unlocked()) {
                card.setSelected(true);
            }

            grid.add(card, col, row);

            col++;
            if (col == 4) {
                col = 0;
                row++;
            }
        }

        VBox centerBox = new VBox(grid);
        centerBox.setPadding(new Insets(18, 0, 18, 0));

        Button cancelBtn = new Button("CANCEL");
        cancelBtn.getStyleClass().add("pilot-cancel-btn");
        cancelBtn.setOnAction(e -> dialog.close());

        Button confirmBtn = new Button("CONFIRM");
        confirmBtn.getStyleClass().add("pilot-confirm-btn");
        confirmBtn.setOnAction(e -> {
            Toggle selectedToggle = pickerGroup.getSelectedToggle();
            if (selectedToggle != null && selectedToggle.getUserData() instanceof String avatarId) {
                selectedAvatarId = avatarId;
                updateSelectedAvatarPreview();
            }
            dialog.close();
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox bottomBar = new HBox(12, cancelBtn, spacer, confirmBtn);
        bottomBar.setAlignment(Pos.CENTER);

        root.setTop(headerBox);
        root.setCenter(centerBox);
        root.setBottom(bottomBar);

        Scene scene = new Scene(root, 860, 560);
        scene.getStylesheets().add(
                getClass().getResource("/css/login.css").toExternalForm()
        );

        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private ToggleButton createPilotCard(AvatarOption option) {
        ToggleButton btn = new ToggleButton();
        btn.getStyleClass().add("pilot-card");
        btn.setPrefSize(124, 142);
        btn.setMinSize(124, 142);
        btn.setMaxSize(124, 142);
        btn.setUserData(option.id());
        btn.setFocusTraversable(false);

        StackPane root = new StackPane();

        VBox content = new VBox(8);
        content.setAlignment(Pos.TOP_LEFT);
        content.setPadding(new Insets(8));

        StackPane imageWrap = new StackPane();
        imageWrap.getStyleClass().add("pilot-image-wrap");
        imageWrap.setPrefSize(108, 92);
        imageWrap.setMinSize(108, 92);
        imageWrap.setMaxSize(108, 92);

        URL imageUrl = getClass().getResource(option.imagePath());
        if (imageUrl != null) {
            ImageView imageView = new ImageView(new Image(imageUrl.toExternalForm()));
            imageView.setFitWidth(108);
            imageView.setFitHeight(92);
            imageView.setPreserveRatio(false);
            imageWrap.getChildren().add(imageView);
        } else {
            Label fallback = new Label(option.displayName());
            fallback.getStyleClass().add("pilot-fallback-text");
            fallback.setWrapText(true);
            fallback.setAlignment(Pos.CENTER);
            fallback.setMaxWidth(90);
            imageWrap.getChildren().add(fallback);
        }

        Label codeLabel = new Label("CODE: " + option.displayName());
        codeLabel.getStyleClass().add("pilot-code-label");

        content.getChildren().addAll(imageWrap, codeLabel);
        root.getChildren().add(content);

        if (option.unlocked()) {
            if (option.id().equals(selectedAvatarId)) {
                Label activeBadge = new Label("ACTIVE");
                activeBadge.getStyleClass().add("pilot-active-badge");
                StackPane.setAlignment(activeBadge, Pos.TOP_LEFT);
                StackPane.setMargin(activeBadge, new Insets(98, 0, 0, 12));
                root.getChildren().add(activeBadge);
            }
        } else {
            VBox lockedBox = new VBox(8);
            lockedBox.setAlignment(Pos.CENTER);

            Label lockIcon = new Label("🔒");
            lockIcon.getStyleClass().add("pilot-lock-icon");

            Label reqLabel = new Label(option.requirementText() == null ? "LOCKED" : option.requirementText());
            reqLabel.getStyleClass().add("pilot-lock-text");
            reqLabel.setWrapText(true);
            reqLabel.setAlignment(Pos.CENTER);

            lockedBox.getChildren().addAll(lockIcon, reqLabel);

            StackPane overlay = new StackPane(lockedBox);
            overlay.getStyleClass().add("pilot-locked-overlay");

            root.getChildren().add(overlay);
        }

        btn.setGraphic(root);
        btn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

        return btn;
    }

    private AvatarOption findAvatarById(String id) {
        for (AvatarOption option : avatarOptions) {
            if (option.id().equals(id)) {
                return option;
            }
        }
        return null;
    }

    private HBox statRow(String name, double value) {
        Label label = new Label(name);
        label.getStyleClass().add("stat-label");

        ProgressBar bar = new ProgressBar(value);
        bar.getStyleClass().add("stat-bar");
        bar.setPrefWidth(110);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox row = new HBox(10, label, spacer, bar);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private VBox fieldGroup(String labelText, HBox inputShell) {
        Label label = new Label(labelText);
        label.getStyleClass().add("section-label");

        VBox box = new VBox(6, label, inputShell);
        box.setFillWidth(true);
        return box;
    }

    private HBox createInputShell(String iconText, TextInputControl field) {
        field.getStyleClass().add("input-field-inner");

        Label icon = new Label(iconText);
        icon.getStyleClass().add("input-icon");

        HBox shell = new HBox(8, icon, field);
        shell.getStyleClass().add("input-shell");
        shell.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(field, Priority.ALWAYS);

        return shell;
    }

    private void handleRegister(CheckBox acceptBox) {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirm = confirmField.getText();

        if (username.isEmpty()) {
            showMessage("Vui lòng nhập nickname.", "#ff7b7b");
            return;
        }

        if (username.length() < 3) {
            showMessage("Username phải có ít nhất 3 ký tự.", "#ff7b7b");
            return;
        }

        if (email.isEmpty()) {
            showMessage("Vui lòng nhập email.", "#ff7b7b");
            return;
        }

        if (password.isEmpty()) {
            showMessage("Vui lòng nhập password.", "#ff7b7b");
            return;
        }

        if (password.length() < 6) {
            showMessage("Password phải có ít nhất 6 ký tự.", "#ff7b7b");
            return;
        }

        if (!password.equals(confirm)) {
            showMessage("Mật khẩu xác nhận không khớp.", "#ff7b7b");
            return;
        }

        if (!acceptBox.isSelected()) {
            showMessage("Bạn cần đồng ý điều khoản để đăng ký.", "#ff7b7b");
            return;
        }

        boolean created = playerDAO.registerAccount(username, password, email, selectedAvatarId);

        if (created) {
            showMessage("Đăng ký thành công. Avatar đã được lưu.", "#57ff9a");
            goToLoginScene.run();
        } else {
            showMessage("Username đã tồn tại hoặc không thể tạo tài khoản.", "#ff7b7b");
        }
    }

    private void showMessage(String text, String color) {
        messageLabel.setText(text);
        messageLabel.setStyle("-fx-text-fill: " + color + ";");
    }

    private record AvatarOption(
            String id,
            String displayName,
            String imagePath,
            boolean unlocked,
            String requirementText
    ) {
    }
}