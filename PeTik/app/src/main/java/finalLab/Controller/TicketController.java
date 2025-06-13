package finalLab.Controller;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import finalLab.Model.Ticket;

public class TicketController extends BaseController {
    private MainMenuController mainMenuController;

    public TicketController(Stage primaryStage) {
        super(primaryStage);
        this.mainMenuController = new MainMenuController(primaryStage);
    }

    @Override
    public void show() {
        showViewTicketsScene();
    }

    public void showViewTicketsScene() {
        VBox root = new VBox(25);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background: linear-gradient(to bottom, #0f0c29, #24243e, #302b63);");

        Label titleLabel = new Label("🎫 YOUR TICKETS");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 0, 3);");

        List<Ticket> userTickets = bookingManager.getUserTickets(currentUser.getUsername());

        if (userTickets.isEmpty()) {
            VBox emptyBox = new VBox(20);
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPadding(new Insets(50));
            emptyBox.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-background-radius: 20;");

            Label emptyIcon = new Label("🎫");
            emptyIcon.setStyle("-fx-font-size: 60px;");

            Label noTicketsLabel = new Label("No tickets found");
            noTicketsLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #bdc3c7;");

            Label suggestionLabel = new Label("Start by booking your first movie!");
            suggestionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #95a5a6; -fx-font-style: italic;");

            emptyBox.getChildren().addAll(emptyIcon, noTicketsLabel, suggestionLabel);

            Button backButton = createStyledButton("⬅ BACK TO MAIN", "#757575", "#616161");
            backButton.setOnAction(e -> {
                mainMenuController.setCurrentUser(currentUser);
                mainMenuController.show();
            });

            root.getChildren().addAll(titleLabel, emptyBox, backButton);
        } else {
            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

            VBox ticketContainer = new VBox(20);
            ticketContainer.setPadding(new Insets(10));

            for (Ticket ticket : userTickets) {
                HBox ticketCard = createTicketCard(ticket);
                ticketContainer.getChildren().add(ticketCard);
            }

            scrollPane.setContent(ticketContainer);
            scrollPane.setFitToWidth(true);
            scrollPane.setPrefHeight(400);

            Button backButton = createStyledButton("⬅ BACK TO MAIN", "#757575", "#616161");
            backButton.setOnAction(e -> {
                mainMenuController.setCurrentUser(currentUser);
                mainMenuController.show();
            });

            root.getChildren().addAll(titleLabel, scrollPane, backButton);
        }

        Scene scene = new Scene(root, 450, 650);
        setScene(scene);
    }

    private HBox createTicketCard(Ticket ticket) {
        HBox ticketCard = new HBox(20);
        ticketCard.setPadding(new Insets(20));
        ticketCard.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-background-radius: 15; " +
                "-fx-border-color: rgba(255,255,255,0.2); -fx-border-width: 1; -fx-border-radius: 15;");

        VBox leftSection = new VBox(8);
        leftSection.setAlignment(Pos.TOP_LEFT);

        Label movieLabel = new Label("🎬 " + ticket.getMovieTitle());
        movieLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        Label dateLabel = new Label("📅 " + ticket.getShowDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy")) +
                " at " + ticket.getShowTime());
        dateLabel.setStyle("-fx-text-fill: #bdc3c7; -fx-font-size: 13px;");

        Label seatsLabel = new Label("🪑 " + String.join(", ", ticket.getSeats()));
        seatsLabel.setStyle("-fx-text-fill: #bdc3c7; -fx-font-size: 13px;");

        Label totalLabel = new Label("💰 Rp " + String.format("%,d", ticket.getTotalAmount()));
        totalLabel.setStyle("-fx-text-fill: #2ecc71; -fx-font-size: 14px; -fx-font-weight: bold;");

        leftSection.getChildren().addAll(movieLabel, dateLabel, seatsLabel, totalLabel);

        VBox rightSection = new VBox(10);
        rightSection.setAlignment(Pos.CENTER);

        Button viewDetailButton = createStyledButton("📋 DETAILS", "#3498db", "#2980b9");
        viewDetailButton.setPrefWidth(120);
        viewDetailButton.setOnAction(e -> showTicketDetail(ticket));

        rightSection.getChildren().add(viewDetailButton);

        ticketCard.getChildren().addAll(leftSection, rightSection);
        HBox.setHgrow(leftSection, Priority.ALWAYS);

        return ticketCard;
    }

    public void showTicketDetail(Ticket ticket) {
        VBox root = new VBox(25);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background: linear-gradient(to bottom, #0f0c29, #24243e, #302b63);");

        Label titleLabel = new Label("🎫 TICKET DETAILS");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 0, 3);");

        VBox ticketDetail = new VBox(20);
        ticketDetail.setPadding(new Insets(30));
        ticketDetail.setStyle("-fx-background-color: white; -fx-background-radius: 20; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 20, 0, 0, 5);");

        VBox headerSection = new VBox(5);
        headerSection.setAlignment(Pos.CENTER);
        headerSection.setPadding(new Insets(0, 0, 15, 0));
        headerSection.setStyle("-fx-border-color: #ecf0f1; -fx-border-width: 0 0 2 0;");

        Label cinemaLabel = new Label("🎬 CINEMA BOOKING SYSTEM");
        cinemaLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label ticketIdLabel = new Label("Ticket ID: " + ticket.getTicketId());
        ticketIdLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12px;");

        headerSection.getChildren().addAll(cinemaLabel, ticketIdLabel);

        VBox contentSection = new VBox(15);
        contentSection.setPadding(new Insets(15, 0, 0, 0));

        Label customerLabel = createTicketDetailItem("👤 Customer", ticket.getCustomerName());
        Label movieLabel = createTicketDetailItem("🎬 Movie", ticket.getMovieTitle());
        Label dateTimeLabel = createTicketDetailItem("📅 Date & Time",
                ticket.getShowDate().format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy")) + " at "
                        + ticket.getShowTime());
        Label seatsLabel = createTicketDetailItem("🪑 Seats", String.join(", ", ticket.getSeats()));

        contentSection.getChildren().addAll(customerLabel, movieLabel, dateTimeLabel, seatsLabel);

        if (!ticket.getSnacks().isEmpty()) {
            Label snackTitleLabel = new Label("🍿 SNACKS");
            snackTitleLabel.setStyle("-fx-text-fill: #2c3e50; -fx-font-size: 14px; -fx-font-weight: bold; " +
                    "-fx-padding: 10 0 5 0;");
            contentSection.getChildren().add(snackTitleLabel);

            for (Map.Entry<String, Integer> entry : ticket.getSnacks().entrySet()) {
                Label snackLabel = new Label("• " + entry.getKey() + " x" + entry.getValue());
                snackLabel.setStyle("-fx-text-fill: #34495e; -fx-font-size: 13px; -fx-padding: 0 0 0 20;");
                contentSection.getChildren().add(snackLabel);
            }
        }

        VBox totalSection = new VBox(5);
        totalSection.setAlignment(Pos.CENTER);
        totalSection.setPadding(new Insets(15, 0, 0, 0));
        totalSection.setStyle("-fx-border-color: #ecf0f1; -fx-border-width: 2 0 0 0;");

        Label totalLabel = new Label("💎 TOTAL AMOUNT: Rp " + String.format("%,d", ticket.getTotalAmount()));
        totalLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 18px; -fx-font-weight: bold;");

        Label thankLabel = new Label("Thank you for choosing our cinema!");
        thankLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12px; -fx-font-style: italic;");

        totalSection.getChildren().addAll(totalLabel, thankLabel);

        ticketDetail.getChildren().addAll(headerSection, contentSection, totalSection);

        Button backButton = createStyledButton("⬅ BACK", "#757575", "#616161");
        backButton.setOnAction(e -> showViewTicketsScene());

        root.getChildren().addAll(titleLabel, ticketDetail, backButton);

        Scene scene = new Scene(root, 400, 650);
        setScene(scene);
    }

    private Label createTicketDetailItem(String label, String value) {
        Label item = new Label(label + ": " + value);
        item.setStyle("-fx-text-fill: #2c3e50; -fx-font-size: 14px; -fx-padding: 5;");
        item.setWrapText(true);
        return item;
    }
}
