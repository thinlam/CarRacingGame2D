package com.carracinggame.scene;

import com.carracinggame.core.UserSession;
import com.carracinggame.database.PlayerDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.net.URI;
import java.net.URL;

public class LoginScene {

    private final Stage stage;
    private final Runnable onLoginSuccess;
    private final PlayerDAO playerDAO = new PlayerDAO();

    private TextField usernameField;
    private PasswordField passwordField;

    private Label messageLabel;
    private Button actionButton;
    private Label forgotKeyLabel;

    public LoginScene(Stage stage, Runnable onLoginSuccess) {
        this.stage = stage;
        this.onLoginSuccess = onLoginSuccess;
    }

    public static Scene create(Stage stage, Runnable onLoginSuccess) {
        return new LoginScene(stage, onLoginSuccess).build();
    }

    private Scene build() {
        StackPane root = new StackPane();
        root.getStyleClass().add("login-root");

        URL bgUrl = getClass().getResource("/images/cars/ui/login-bg.jpg");
        if (bgUrl != null) {
            ImageView bgView = new ImageView(new Image(bgUrl.toExternalForm()));
            bgView.setPreserveRatio(false);
            bgView.fitWidthProperty().bind(root.widthProperty());
            bgView.fitHeightProperty().bind(root.heightProperty());
            bgView.setOpacity(0.42);
            bgView.setEffect(new GaussianBlur(18));
            root.getChildren().add(bgView);
        }

        Region overlay = new Region();
        overlay.getStyleClass().add("background-overlay");
        overlay.prefWidthProperty().bind(root.widthProperty());
        overlay.prefHeightProperty().bind(root.heightProperty());

        BorderPane hudLayer = new BorderPane();
        hudLayer.setMouseTransparent(true);

        ProgressBar syncBar = new ProgressBar(0.72);
        syncBar.setPrefWidth(90);
        syncBar.getStyleClass().add("sync-bar");

        Label syncLabel = new Label("SYNCING DATA...");
        syncLabel.getStyleClass().add("hud-label");

        HBox topHud = new HBox(8, syncBar, syncLabel);
        topHud.setAlignment(Pos.TOP_RIGHT);
        BorderPane.setMargin(topHud, new Insets(20, 24, 0, 0));
        hudLayer.setTop(topHud);

        VBox bottomHud = new VBox(
                4,
                hudText("SYSTEM STATUS: OPTIMAL"),
                hudText("CONNECTION: LATENCY 14MS"),
                hudText("VER: 2.04.11_NEON")
        );
        bottomHud.setAlignment(Pos.BOTTOM_LEFT);
        BorderPane.setMargin(bottomHud, new Insets(0, 0, 20, 24));
        hudLayer.setBottom(bottomHud);

        VBox card = new VBox(8);
        card.getStyleClass().add("glass-card");
        card.setAlignment(Pos.TOP_CENTER);
        card.setFillWidth(true);

        card.setMinWidth(320);
        card.setPrefWidth(320);
        card.setMaxWidth(320);

        Text titleWhite = new Text("RACE ");
        titleWhite.getStyleClass().add("title-white");

        Text titleBlue = new Text("LOGIN");
        titleBlue.getStyleClass().add("title-blue");

        TextFlow titleFlow = new TextFlow(titleWhite, titleBlue);
        titleFlow.setTextAlignment(TextAlignment.CENTER);
        titleFlow.setMaxWidth(Double.MAX_VALUE);

        Label subtitle = new Label("GLOBAL CIRCUIT ACCESS");
        subtitle.getStyleClass().add("subtitle-label");

        Label userLabel = sectionLabel("PILOT CREDENTIALS");

        usernameField = new TextField();
        usernameField.setPromptText("Username");
        HBox usernameBox = createInputShell("◉", usernameField);

        Label passLabel = sectionLabel("ACCESS CODE");
        forgotKeyLabel = new Label("FORGOT KEY?");
        forgotKeyLabel.getStyleClass().add("forgot-link");

        Region passSpacer = new Region();
        HBox.setHgrow(passSpacer, Priority.ALWAYS);

        HBox passwordHeader = new HBox(passLabel, passSpacer, forgotKeyLabel);
        passwordHeader.setAlignment(Pos.CENTER_LEFT);

        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        HBox passwordBox = createInputShell("◈", passwordField);

        CheckBox rememberBox = new CheckBox("STAY AUTHENTICATED");
        rememberBox.getStyleClass().add("remember-box");

        actionButton = new Button("⚡ START ENGINE");
        actionButton.getStyleClass().add("primary-btn");
        actionButton.setMaxWidth(Double.MAX_VALUE);

        messageLabel = new Label();
        messageLabel.getStyleClass().add("message-label");
        messageLabel.setWrapText(true);

        HBox divider = createDivider("EXTERNAL LINKUPS");

        Button facebookBtn = socialButton("/images/cars/ui/github.png");
        facebookBtn.setOnAction(e ->
                openLink("https://web.facebook.com/profile.php?id=100054360498935")
        );

        Button tiktokBtn = socialButton("/images/cars/ui/google.png");
        tiktokBtn.setOnAction(e ->
                openLink("https://www.tiktok.com/@dane.ee_17?_r=1&_t=ZS-94fjFVJqcUy")
        );

        Button discordBtn = socialButton("/images/cars/ui/discord.png");
        discordBtn.setOnAction(e ->
                openLink("https://discord.gg/j5TVgbuaZh")
        );

        HBox socialRow = new HBox(10, facebookBtn, tiktokBtn, discordBtn);
        socialRow.setAlignment(Pos.CENTER);

        HBox footerRow = new HBox(
                16,
                footerText("SOCIAL"),
                footerText("PRIVACY"),
                footerText("SUPPORT")
        );
        footerRow.setAlignment(Pos.CENTER);

        Label helperLabel = new Label("Chưa có tài khoản?");
        helperLabel.getStyleClass().add("helper-label");

        Hyperlink switchLink = new Hyperlink("Đăng ký");
        switchLink.getStyleClass().add("switch-link");
        switchLink.setBorder(Border.EMPTY);
        switchLink.setPadding(Insets.EMPTY);
        switchLink.setOnAction(e ->
                stage.setScene(RegisterScene.create(stage, () ->
                        stage.setScene(LoginScene.create(stage, onLoginSuccess)), onLoginSuccess))
        );

        HBox switchBox = new HBox(5, helperLabel, switchLink);
        switchBox.setAlignment(Pos.CENTER);

        actionButton.setOnAction(e -> handleLogin());
        usernameField.setOnAction(e -> handleLogin());
        passwordField.setOnAction(e -> handleLogin());

        card.getChildren().addAll(
                titleFlow,
                subtitle,
                spacer(2),
                userLabel,
                usernameBox,
                passwordHeader,
                passwordBox,
                rememberBox,
                spacer(2),
                actionButton,
                messageLabel,
                spacer(3),
                divider,
                socialRow,
                footerRow,
                spacer(2),
                switchBox
        );

        VBox centerBox = new VBox(card);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setFillWidth(false);
        centerBox.setPickOnBounds(false);
        centerBox.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        root.getChildren().addAll(overlay, hudLayer, centerBox);

        Scene scene = new Scene(root, 1366, 768);
        scene.getStylesheets().add(
                getClass().getResource("/css/login.css").toExternalForm()
        );

        return scene;
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty()) {
            showMessage("Vui lòng nhập username.", "#ff7b7b");
            return;
        }

        if (password.isEmpty()) {
            showMessage("Vui lòng nhập password.", "#ff7b7b");
            return;
        }

        boolean valid = playerDAO.validateLogin(username, password);
        if (valid) {
            UserSession.setUsername(username);
            showMessage("Đăng nhập thành công!", "#57ff9a");
            onLoginSuccess.run();
        } else {
            showMessage("Sai username hoặc password.", "#ff7b7b");
        }
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

    private HBox createDivider(String text) {
        Region left = new Region();
        left.getStyleClass().add("divider-line");
        HBox.setHgrow(left, Priority.ALWAYS);

        Label center = new Label(text);
        center.getStyleClass().add("divider-text");

        Region right = new Region();
        right.getStyleClass().add("divider-line");
        HBox.setHgrow(right, Priority.ALWAYS);

        HBox box = new HBox(8, left, center, right);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    private Button socialButton(String iconPath) {
        Image img = new Image(getClass().getResourceAsStream(iconPath));
        ImageView icon = new ImageView(img);
        icon.setFitWidth(18);
        icon.setFitHeight(18);
        icon.setPreserveRatio(true);
        icon.setSmooth(true);

        Button btn = new Button();
        btn.setGraphic(icon);
        btn.getStyleClass().add("social-btn");
        btn.setFocusTraversable(false);
        btn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        return btn;
    }

    private void openLink(String url) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                showMessage("Máy không hỗ trợ mở trình duyệt.", "#ff7b7b");
            }
        } catch (Exception e) {
            showMessage("Không mở được link.", "#ff7b7b");
            e.printStackTrace();
        }
    }

    private Label footerText(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("footer-text");
        return label;
    }

    private Label sectionLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("section-label");
        return label;
    }

    private Label hudText(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("hud-label");
        return label;
    }

    private Region spacer(double height) {
        Region region = new Region();
        region.setMinHeight(height);
        region.setPrefHeight(height);
        region.setMaxHeight(height);
        return region;
    }

    private void showMessage(String text, String color) {
        messageLabel.setText(text);
        messageLabel.setStyle("-fx-text-fill: " + color + ";");
    }
}