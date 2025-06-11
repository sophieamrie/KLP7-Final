package finalLab;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.*;

import finalLab.Controller.AuthController;
import finalLab.Controller.BookingController;
import finalLab.Controller.PaymentController;
import finalLab.Controller.SnackController;
import finalLab.Controller.TicketController;
import finalLab.Model.User;
import finalLab.Service.MovieService;

public class Main extends Application {
    public Stage primaryStage;
    private User currentUser;
    private AuthController authController;
    private BookingController bookingController;
    private SnackController snackController;
    private TicketController ticketController;
    private PaymentController paymentController;
    private MovieService movieService;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Cinema Booking System");
        primaryStage.setResizable(false);

        movieService = new MovieService();
        authController = new AuthController(this);

        initializeDataFiles();
        authController.showLoginScreen();
    }

    public void onLoginSuccess(User user) {
        this.currentUser = user;

        bookingController = new BookingController(this, currentUser);
        snackController = new SnackController(this);
        paymentController = new PaymentController(this, currentUser, bookingController);
        ticketController = new TicketController(this, currentUser);

        bookingController.setSnackController(snackController);
        snackController.setBookingController(bookingController);
        snackController.setPaymentController(paymentController);
        paymentController.setSnackController(snackController);
        showMainMenu();
    }

    public void showMainMenu() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #2c3e50;");

        Label welcomeLabel = new Label("Welcome, " + currentUser.getFullName() + "!");
        welcomeLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label balanceLabel = new Label("💰 Balance: Rp " + String.format("%,.0f", currentUser.getBalance()));
        balanceLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #f1c40f;");

        VBox menuBox = new VBox(15);
        menuBox.setAlignment(Pos.CENTER);
        menuBox.setStyle("-fx-background-color: white; -fx-padding: 30; -fx-background-radius: 10;");

        Button moviesBtn = new Button("🎬 VIEW MOVIES");
        moviesBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-pref-width: 200; -fx-pref-height: 50;");

        Button ticketsBtn = new Button("🎟 MY TICKETS");
        ticketsBtn.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-font-weight: bold; -fx-pref-width: 200; -fx-pref-height: 50;");

        Button topupBtn = new Button("💳 TOP UP BALANCE");
        topupBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-weight: bold; -fx-pref-width: 200; -fx-pref-height: 50;");

        Button logoutBtn = new Button("🚪 LOGOUT");
        logoutBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-pref-width: 200; -fx-pref-height: 50;");

        moviesBtn.setOnAction(e -> bookingController.showMoviesList());
        ticketsBtn.setOnAction(e -> ticketController.showUserTickets());
        topupBtn.setOnAction(e -> paymentController.showTopUpScreen());
        logoutBtn.setOnAction(e -> {
            currentUser = null;
            authController.showLoginScreen();
        });

        menuBox.getChildren().addAll(moviesBtn, ticketsBtn, topupBtn, logoutBtn);
        root.getChildren().addAll(welcomeLabel, balanceLabel, menuBox);

        Scene scene = new Scene(root, 400, 500);
        primaryStage.setScene(scene);
    }

    public void initializeDataFiles() {
        File moviesFile = new File("movies.txt");
        if (!moviesFile.exists()) {
            try (PrintWriter writer = new PrintWriter(moviesFile)) {
                writer.println("Kimetsu no Yaiba: Infinity Castle|Fantasy|192|10:00,13:30,17:00,20:30");
                writer.println("Avengers: Endgame|Action|181|10:00,13:00,16:00,19:00");
                writer.println("20th Century Girl|Romance|150|12.00,15:30,19:30,23:30");
                writer.println("Spider-Man: No Way Home|Action|148|11:00,14:00,17:00,20:00");
                writer.println("Pengabdi Setan 2|Horor|175|08.00,12:00,15:00,22:00");
                writer.println("The Batman|Action|176|09:00,12:00,15:00,18:00");
                writer.println("Pengepungan di Bukit Duri|Thriller|161|11:30,14:30,17:30,20:30");
                writer.println("Top Gun: Maverick|Action|130|10:30,13:30,16:30,19:30");
            } catch (IOException e) {
                showAlert("Error", "Failed to initialize movies data");
            }
        }

        File snacksFile = new File("snacks.txt");
        if (!snacksFile.exists()) {
            try (PrintWriter writer = new PrintWriter(snacksFile)) {
                writer.println("Popcorn Large|25000");
                writer.println("Popcorn Medium|20000");
                writer.println("Coca Cola|15000");
                writer.println("Pepsi|15000");
                writer.println("Nachos|30000");
                writer.println("Hot Dog|25000");
            } catch (IOException e) {
                showAlert("Error", "Failed to initialize snacks data");
            }
        }
    }

    public void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public MovieService getMovieService() {
        return movieService;
    }

    public BookingController getBookingController() {
        return bookingController;
    }

    public SnackController getSnackController() {
        return snackController;
    }

    public TicketController getTicketController() {
        return ticketController;
    }

    public PaymentController getPaymentController() {
        return paymentController;
    }

    public static void main(String[] args) {
        launch(args);
    }
}