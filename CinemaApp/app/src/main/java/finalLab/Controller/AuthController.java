package finalLab.Controller;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import finalLab.Model.User;

public class AuthController extends BaseController {
    private MainMenuController mainMenuController;

    public AuthController(Stage primaryStage) {
        super(primaryStage);
        this.mainMenuController = new MainMenuController(primaryStage);
    }

    @Override
    public void show() {
        showLoginScene();
    }

    public void showLoginScene() {
        VBox root = new VBox(25);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background: linear-gradient(to bottom, #0f0c29, #24243e, #302b63);");

        Label titleLabel = new Label("🎬 CINEMA BOOKING");
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: white; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 0, 3);");

        Label subtitleLabel = new Label("Your Movie Experience Starts Here");
        subtitleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #b8b8b8; -fx-font-style: italic;");

        VBox inputContainer = createStyledContainer();

        TextField usernameField = createStyledTextField("👤 Username");
        PasswordField passwordField = createStyledPasswordField("🔒 Password");

        Button loginButton = createFixedStyledButton("🚀 LOGIN", "#4CAF50", "#45a049");

        Label registerLabel = new Label("Don't have an account? Register here");
        registerLabel.setStyle("-fx-text-fill: #64b5f6; -fx-cursor: hand; -fx-underline: true; " +
                "-fx-font-size: 12px;");
        registerLabel.setOnMouseClicked(e -> showRegisterScene());
        registerLabel.setOnMouseEntered(e -> registerLabel.setStyle("-fx-text-fill: #90caf9; -fx-cursor: hand; " +
                "-fx-underline: true; -fx-font-size: 12px;"));
        registerLabel.setOnMouseExited(e -> registerLabel.setStyle("-fx-text-fill: #64b5f6; -fx-cursor: hand; " +
                "-fx-underline: true; -fx-font-size: 12px;"));

        Label messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: #ff6b6b; -fx-font-size: 12px;");

        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                showMessage(messageLabel, "⚠ Please fill all fields", "#ff6b6b");
                return;
            }

            User user = userManager.login(username, password);
            if (user != null) {
                currentUser = user;
                mainMenuController.setCurrentUser(user);
                mainMenuController.show();
            } else {
                showMessage(messageLabel, "❌ Invalid username or password", "#ff6b6b");
            }
        });

        inputContainer.getChildren().addAll(usernameField, passwordField, loginButton);
        root.getChildren().addAll(titleLabel, subtitleLabel, inputContainer, registerLabel, messageLabel);

        Scene scene = new Scene(root, 450, 600);
        setScene(scene);

        if (!primaryStage.isShowing()) {
            initializeWindow();
        }

        primaryStage.show();
    }

    public void showRegisterScene() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background: linear-gradient(to bottom, #0f0c29, #24243e, #302b63);");

        Label titleLabel = new Label("📝 CREATE ACCOUNT");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 0, 3);");

        VBox inputContainer = createStyledContainer();

        TextField usernameField = createStyledTextField("👤 Username");
        PasswordField passwordField = createStyledPasswordField("🔒 Password (min 8 chars, letters+numbers)");
        PasswordField confirmPasswordField = createStyledPasswordField("🔒 Confirm Password");

        Button registerButton = createFixedStyledButton("✅ REGISTER", "#2196F3", "#1976D2");
        Button backButton = createFixedStyledButton("⬅ BACK TO LOGIN", "#757575", "#616161");

        Label messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: #ff6b6b; -fx-font-size: 12px;");

        registerButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                showMessage(messageLabel, "⚠ Please fill all fields", "#ff6b6b");
                return;
            }

            if (!password.equals(confirmPassword)) {
                showMessage(messageLabel, "❌ Passwords don't match", "#ff6b6b");
                return;
            }

            if (!isValidPassword(password)) {
                showMessage(messageLabel, "❌ Password must be at least 8 characters with letters and numbers",
                        "#ff6b6b");
                return;
            }

            if (userManager.register(username, password)) {
                showMessage(messageLabel, "✅ Registration successful! Redirecting to login...", "#4CAF50");
                new Thread(() -> {
                    try {
                        Thread.sleep(2000);
                        javafx.application.Platform.runLater(() -> showLoginScene());
                    } catch (InterruptedException ex) {
                    }
                }).start();
            } else {
                showMessage(messageLabel, "❌ Username already exists", "#ff6b6b");
            }
        });

        backButton.setOnAction(e -> showLoginScene());

        inputContainer.getChildren().addAll(usernameField, passwordField, confirmPasswordField, registerButton,
                backButton);
        root.getChildren().addAll(titleLabel, inputContainer, messageLabel);

        Scene scene = new Scene(root, 450, 600);
        setScene(scene);
    }

    private boolean isValidPassword(String password) {
        if (password.length() < 8)
            return false;
        boolean hasLetter = false;
        boolean hasDigit = false;
        for (char c : password.toCharArray()) {
            if (Character.isLetter(c))
                hasLetter = true;
            if (Character.isDigit(c))
                hasDigit = true;
        }
        return hasLetter && hasDigit;
    }
}