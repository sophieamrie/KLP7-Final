package finalLab.Controller;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class MainMenuController extends BaseController {

    private BookingController bookingController;
    private TicketController ticketController;
    private TopUpController topUpController;
    private AuthController authController;

    public MainMenuController(Stage primaryStage) {
        super(primaryStage);
    }

    @Override
    public void show() {
        showMainScene();
    }

    private BookingController getBookingController() {
        if (bookingController == null) {
            bookingController = new BookingController(primaryStage);
        }
        return bookingController;
    }

    private TicketController getTicketController() {
        if (ticketController == null) {
            ticketController = new TicketController(primaryStage);
        }
        return ticketController;
    }

    private TopUpController getTopUpController() {
        if (topUpController == null) {
            topUpController = new TopUpController(primaryStage);
        }
        return topUpController;
    }

    private AuthController getAuthController() {
        if (authController == null) {
            authController = new AuthController(primaryStage);
        }
        return authController;
    }

    public void showMainScene() {
        VBox root = new VBox(30);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background: linear-gradient(to bottom, #0f0c29, #24243e, #302b63);");

        VBox welcomeBox = new VBox(10);
        welcomeBox.setAlignment(Pos.CENTER);

        Label welcomeLabel = new Label("Welcome back, " + currentUser.getUsername() + "! 👋");
        welcomeLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: white; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 0, 2);");

        Label balanceLabel = new Label("💰 Balance: Rp " + String.format("%,d", currentUser.getBalance()));
        balanceLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #4CAF50; -fx-font-weight: bold; " +
                "-fx-background-color: rgba(255,255,255,0.1); -fx-padding: 10; " +
                "-fx-background-radius: 15;");

        welcomeBox.getChildren().addAll(welcomeLabel, balanceLabel);

        VBox menuContainer = new VBox(20);
        menuContainer.setAlignment(Pos.CENTER);

        Button bookTicketButton = createMenuButton("🎫 Book Ticket", "Book your movie experience", "#4CAF50",
                "#45a049");
        Button viewTicketButton = createMenuButton("📋 View Tickets", "Check your bookings", "#2196F3", "#1976D2");
        Button topUpButton = createMenuButton("💳 Top Up", "Add funds to your account", "#FF9800", "#F57C00");
        Button logoutButton = createMenuButton("🚪 Logout", "Sign out safely", "#f44336", "#d32f2f");

        bookTicketButton.setOnAction(e -> {
            BookingController controller = getBookingController();
            controller.setCurrentUser(currentUser);
            controller.show();
        });

        viewTicketButton.setOnAction(e -> {
            TicketController controller = getTicketController();
            controller.setCurrentUser(currentUser);
            controller.show();
        });

        topUpButton.setOnAction(e -> {
            TopUpController controller = getTopUpController();
            controller.setCurrentUser(currentUser);
            controller.show();
        });

        logoutButton.setOnAction(e -> {
            AuthController controller = getAuthController();
            controller.show();
        });

        menuContainer.getChildren().addAll(bookTicketButton, viewTicketButton, topUpButton, logoutButton);

        root.getChildren().addAll(welcomeBox, menuContainer);

        Scene scene = new Scene(root, 450, 600);
        setScene(scene);
    }
}
