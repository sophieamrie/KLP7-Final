package finalLab.Controller;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import finalLab.Model.User;
import finalLab.Service.BookingManager;
import finalLab.Service.MovieManager;
import finalLab.Service.UserManager;

public abstract class BaseController {
    protected Stage primaryStage;
    protected UserManager userManager;
    protected MovieManager movieManager;
    protected BookingManager bookingManager;
    protected User currentUser;

    private static boolean globalMaximized = false;
    private static double globalX = -1;
    private static double globalY = -1;
    private static double globalWidth = 450;
    private static double globalHeight = 600;
    private static boolean stateInitialized = false;

    public BaseController(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.userManager = new UserManager();
        this.movieManager = new MovieManager();
        this.bookingManager = new BookingManager();
    }

    protected void setupStage() {
        primaryStage.setResizable(true);
        primaryStage.setMinWidth(450);
        primaryStage.setMinHeight(600);
        primaryStage.setTitle("Cinema Booking System");
    }

    protected void setScene(Scene scene) {
        saveCurrentState();

        primaryStage.setScene(scene);
        setupStage();

        javafx.application.Platform.runLater(() -> {
            restoreWindowState();
        });
    }

    private void saveCurrentState() {
        if (primaryStage.isShowing()) {
            globalMaximized = primaryStage.isMaximized();

            if (!globalMaximized) {
                globalX = primaryStage.getX();
                globalY = primaryStage.getY();
                globalWidth = primaryStage.getWidth();
                globalHeight = primaryStage.getHeight();
            }
            stateInitialized = true;
        }
    }

    private void restoreWindowState() {
        if (stateInitialized) {
            if (globalMaximized) {
                primaryStage.setMaximized(true);
            } else {
                primaryStage.setMaximized(false);
                if (globalX >= 0 && globalY >= 0) {
                    primaryStage.setX(globalX);
                    primaryStage.setY(globalY);
                }
                primaryStage.setWidth(globalWidth);
                primaryStage.setHeight(globalHeight);
            }
        } else {
            primaryStage.centerOnScreen();
        }
    }

    protected void initializeWindow() {
        if (!stateInitialized) {
            primaryStage.centerOnScreen();
            stateInitialized = true;
            globalX = primaryStage.getX();
            globalY = primaryStage.getY();
            globalWidth = primaryStage.getWidth();
            globalHeight = primaryStage.getHeight();
        }
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    protected VBox createStyledContainer() {
        VBox container = new VBox(15);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(20));
        container.setMaxWidth(300);
        container.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-background-radius: 20; " +
                "-fx-border-color: rgba(255,255,255,0.2); -fx-border-width: 1; -fx-border-radius: 20;");
        return container;
    }

    protected TextField createStyledTextField(String promptText) {
        TextField textField = new TextField();
        textField.setPromptText(promptText);
        textField.setPrefHeight(45);
        textField.setPrefWidth(250);
        textField.setMaxWidth(250);
        textField.setStyle("-fx-background-color: rgba(255,255,255,0.9); -fx-background-radius: 10; " +
                "-fx-border-color: #3498db; -fx-border-width: 2; -fx-border-radius: 10; " +
                "-fx-font-size: 14px; -fx-padding: 10;");

        textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                textField.setStyle("-fx-background-color: rgba(255,255,255,1); -fx-background-radius: 10; " +
                        "-fx-border-color: #2ecc71; -fx-border-width: 2; -fx-border-radius: 10; " +
                        "-fx-font-size: 14px; -fx-padding: 10; " +
                        "-fx-effect: dropshadow(gaussian, rgba(46, 204, 113, 0.5), 10, 0, 0, 0);");
            } else {
                textField.setStyle("-fx-background-color: rgba(255,255,255,0.9); -fx-background-radius: 10; " +
                        "-fx-border-color: #3498db; -fx-border-width: 2; -fx-border-radius: 10; " +
                        "-fx-font-size: 14px; -fx-padding: 10;");
            }
        });
        return textField;
    }

    protected PasswordField createStyledPasswordField(String promptText) {
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText(promptText);
        passwordField.setPrefHeight(45);
        passwordField.setPrefWidth(250);
        passwordField.setMaxWidth(250); // FIXED: Tidak melebar saat maximize
        passwordField.setStyle("-fx-background-color: rgba(255,255,255,0.9); -fx-background-radius: 10; " +
                "-fx-border-color: #3498db; -fx-border-width: 2; -fx-border-radius: 10; " +
                "-fx-font-size: 14px; -fx-padding: 10;");

        passwordField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                passwordField.setStyle("-fx-background-color: rgba(255,255,255,1); -fx-background-radius: 10; " +
                        "-fx-border-color: #2ecc71; -fx-border-width: 2; -fx-border-radius: 10; " +
                        "-fx-font-size: 14px; -fx-padding: 10; " +
                        "-fx-effect: dropshadow(gaussian, rgba(46, 204, 113, 0.5), 10, 0, 0, 0);");
            } else {
                passwordField.setStyle("-fx-background-color: rgba(255,255,255,0.9); -fx-background-radius: 10; " +
                        "-fx-border-color: #3498db; -fx-border-width: 2; -fx-border-radius: 10; " +
                        "-fx-font-size: 14px; -fx-padding: 10;");
            }
        });
        return passwordField;
    }

    protected Button createStyledButton(String text, String bgColor, String hoverColor) {
        Button button = new Button(text);
        button.setPrefHeight(45);
        button.setPrefWidth(200);
        button.setStyle("-fx-background-color: " + bgColor + "; -fx-text-fill: white; " +
                "-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 10; " +
                "-fx-border-radius: 10; -fx-cursor: hand;");

        button.setOnMouseEntered(e -> {
            button.setStyle("-fx-background-color: " + hoverColor + "; -fx-text-fill: white; " +
                    "-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 10; " +
                    "-fx-border-radius: 10; -fx-cursor: hand; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 3);");
        });

        button.setOnMouseExited(e -> {
            button.setStyle("-fx-background-color: " + bgColor + "; -fx-text-fill: white; " +
                    "-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 10; " +
                    "-fx-border-radius: 10; -fx-cursor: hand;");
        });
        return button;
    }

    protected Button createFixedStyledButton(String text, String bgColor, String hoverColor) {
        Button button = new Button(text);
        button.setPrefHeight(45);
        button.setPrefWidth(250);
        button.setMaxWidth(250);
        button.setStyle("-fx-background-color: " + bgColor + "; -fx-text-fill: white; " +
                "-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 10; " +
                "-fx-border-radius: 10; -fx-cursor: hand;");

        button.setOnMouseEntered(e -> {
            button.setStyle("-fx-background-color: " + hoverColor + "; -fx-text-fill: white; " +
                    "-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 10; " +
                    "-fx-border-radius: 10; -fx-cursor: hand; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 3);");
        });

        button.setOnMouseExited(e -> {
            button.setStyle("-fx-background-color: " + bgColor + "; -fx-text-fill: white; " +
                    "-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 10; " +
                    "-fx-border-radius: 10; -fx-cursor: hand;");
        });
        return button;
    }

    protected Button createMenuButton(String title, String subtitle, String bgColor, String hoverColor) {
        VBox buttonContent = new VBox(5);
        buttonContent.setAlignment(Pos.CENTER);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.setStyle("-fx-text-fill: rgba(255,255,255,0.8); -fx-font-size: 12px;");

        buttonContent.getChildren().addAll(titleLabel, subtitleLabel);

        Button button = new Button();
        button.setGraphic(buttonContent);
        button.setPrefWidth(300);
        button.setPrefHeight(70);
        button.setStyle("-fx-background-color: " + bgColor + "; -fx-background-radius: 15; " +
                "-fx-border-radius: 15; -fx-cursor: hand;");

        button.setOnMouseEntered(e -> {
            button.setStyle("-fx-background-color: " + hoverColor + "; -fx-background-radius: 15; " +
                    "-fx-border-radius: 15; -fx-cursor: hand; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 15, 0, 0, 5);");
        });

        button.setOnMouseExited(e -> {
            button.setStyle("-fx-background-color: " + bgColor + "; -fx-background-radius: 15; " +
                    "-fx-border-radius: 15; -fx-cursor: hand;");
        });
        return button;
    }

    protected void showMessage(Label messageLabel, String message, String color) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 12px;");
    }

    protected void showStyledAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #2c3e50;");
        dialogPane.lookup(".content.label").setStyle("-fx-text-fill: white;");

        alert.showAndWait();
    }

    public abstract void show();
}