package finalLab.Controller;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class TopUpController extends BaseController {
    private MainMenuController mainMenuController;

    public TopUpController(Stage primaryStage) {
        super(primaryStage);
        this.mainMenuController = new MainMenuController(primaryStage);
    }

    @Override
    public void show() {
        showTopUpScene();
    }

    public void showTopUpScene() {
        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background: linear-gradient(to bottom, #0f0c29, #24243e, #302b63);");

        Label titleLabel = new Label("💳 TOP UP BALANCE");
        titleLabel.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: white; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 0, 3); " +
                "-fx-background-color: rgba(255,255,255,0.05); " +
                "-fx-background-radius: 15; -fx-padding: 15 25;");

        VBox contentBox = new VBox(0);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new Insets(30));
        contentBox.setStyle("-fx-background-color: rgba(255,255,255,0.12); -fx-background-radius: 20; " +
                "-fx-border-color: rgba(255,255,255,0.2); -fx-border-width: 2; -fx-border-radius: 20; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 15, 0, 0, 5);");

        VBox balanceBox = new VBox(5);
        balanceBox.setAlignment(Pos.CENTER);

        Label balanceTitle = new Label("Current Balance");
        balanceTitle.setStyle("-fx-text-fill: rgba(255,255,255,0.8); -fx-font-size: 14px;");

        Label currentBalanceLabel = new Label(
                "Rp " + String.format("%,d", currentUser.getBalance()));
        currentBalanceLabel.setStyle("-fx-font-size: 28px; -fx-text-fill: #4CAF50; -fx-font-weight: bold; " +
                "-fx-background-color: rgba(76,175,80,0.15); -fx-padding: 20; " +
                "-fx-background-radius: 15; -fx-border-color: rgba(76,175,80,0.3); " +
                "-fx-border-width: 1; -fx-border-radius: 15;");

        balanceBox.getChildren().addAll(balanceTitle, currentBalanceLabel);

        Label methodLabel = new Label("Select Payment Method:");
        methodLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        VBox methodContainer = new VBox(10);
        methodContainer.setAlignment(Pos.CENTER);
        methodContainer.setStyle("-fx-background-color: rgba(255,255,255,0.08); " +
                "-fx-background-radius: 15; -fx-padding: 15;");

        ToggleGroup methodGroup = new ToggleGroup();

        String[] methods = { "🏦 Bank Transfer", "💚 E-Wallet (GoPay)", "💙 E-Wallet (OVO)", "🔵 E-Wallet (DANA)",
                "💳 Credit Card" };

        for (String method : methods) {
            RadioButton methodRadio = new RadioButton(method);
            methodRadio.setUserData(method.substring(2)); // Remove emoji
            methodRadio.setToggleGroup(methodGroup);
            methodRadio.setStyle("-fx-text-fill: white; -fx-font-size: 14px; " +
                    "-fx-background-color: rgba(255,255,255,0.05); " +
                    "-fx-background-radius: 8; -fx-padding: 10;");
            methodContainer.getChildren().add(methodRadio);
        }

        TextField amountField = createStyledTextField("💰 Enter amount (minimum Rp 10,000)");
        amountField.setPrefWidth(300);
        amountField.setStyle("-fx-font-size: 15px; -fx-padding: 12; " +
                "-fx-background-color: rgba(255,255,255,0.15); " +
                "-fx-background-radius: 12; " +
                "-fx-border-color: rgba(255,255,255,0.3); " +
                "-fx-border-width: 1; -fx-border-radius: 12; " +
                "-fx-text-fill: white; -fx-prompt-text-fill: rgba(255,255,255,0.6);");

        Label messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: #ff6b6b; -fx-font-size: 12px; -fx-font-weight: bold;");

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        Button topUpButton = createModernButton("💳 TOP UP", "#4CAF50", "#45a049");
        Button backButton = createModernButton("⬅ BACK", "#757575", "#616161");

        topUpButton.setOnAction(e -> {
            RadioButton selectedMethod = (RadioButton) methodGroup.getSelectedToggle();
            if (selectedMethod == null) {
                showMessage(messageLabel, "⚠ Please select a payment method", "#ff6b6b");
                return;
            }

            try {
                String amountText = amountField.getText().replaceAll("[^0-9]", "");
                if (amountText.isEmpty()) {
                    showMessage(messageLabel, "⚠ Please enter a valid amount", "#ff6b6b");
                    return;
                }

                int amount = Integer.parseInt(amountText);
                if (amount < 10000) {
                    showMessage(messageLabel, "⚠ Minimum top up amount is Rp 10,000", "#ff6b6b");
                    return;
                }

                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmAlert.setTitle("Confirm Top Up");
                confirmAlert.setHeaderText("Top Up Confirmation");
                confirmAlert.setContentText("Are you sure you want to top up Rp " + String.format("%,d", amount) +
                        " using " + selectedMethod.getUserData() + "?");

                DialogPane dialogPane = confirmAlert.getDialogPane();
                dialogPane.setStyle("-fx-background-color: #2c3e50;");
                dialogPane.lookup(".content.label").setStyle("-fx-text-fill: white;");

                confirmAlert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        currentUser.setBalance(currentUser.getBalance() + amount);
                        userManager.updateUserBalance(currentUser.getUsername(), currentUser.getBalance());

                        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                        successAlert.setTitle("Success");
                        successAlert.setHeaderText("Top Up Successful!");
                        successAlert.setContentText("Your balance has been updated to Rp " +
                                String.format("%,d", currentUser.getBalance()));

                        DialogPane successPane = successAlert.getDialogPane();
                        successPane.setStyle("-fx-background-color: #27ae60;");
                        successPane.lookup(".content.label").setStyle("-fx-text-fill: white;");

                        successAlert.showAndWait();

                        mainMenuController.setCurrentUser(currentUser);
                        mainMenuController.show();
                    }
                });

            } catch (NumberFormatException ex) {
                showMessage(messageLabel, "⚠ Please enter a valid amount", "#ff6b6b");
            }
        });

        backButton.setOnAction(e -> {
            mainMenuController.setCurrentUser(currentUser);
            mainMenuController.show();
        });

        buttonBox.getChildren().addAll(backButton, topUpButton);

        contentBox.getChildren().addAll(balanceBox, methodLabel, methodContainer, amountField, messageLabel,
                buttonBox);

        root.getChildren().addAll(titleLabel, contentBox);

        Scene scene = new Scene(root, 450, 600);
        setScene(scene);
    }

    private Button createModernButton(String text, String baseColor, String hoverColor) {
        Button button = new Button(text);
        button.setPrefWidth(130);
        button.setPrefHeight(45);
        button.setStyle("-fx-background-color: " + baseColor + "; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 14px; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 20; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 0, 2);");

        button.setOnMouseEntered(e -> {
            button.setStyle("-fx-background-color: " + hoverColor + "; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-size: 14px; " +
                    "-fx-font-weight: bold; " +
                    "-fx-background-radius: 20; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 8, 0, 0, 3);");
        });

        button.setOnMouseExited(e -> {
            button.setStyle("-fx-background-color: " + baseColor + "; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-size: 14px; " +
                    "-fx-font-weight: bold; " +
                    "-fx-background-radius: 20; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 0, 2);");
        });

        return button;
    }
}