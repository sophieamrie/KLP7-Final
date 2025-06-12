package finalLab.Controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

import finalLab.Main;
import finalLab.Model.Movie;
import finalLab.Model.User;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class SeatBookingController {
    private static final String[] SEAT_ROWS = {"A", "B", "C", "D", "E"};
    private static final int SEATS_PER_ROW = 8;
    public static final Map<String, Set<String>> PERMANENTLY_BOOKED_SEATS = new HashMap<>();

    private static final String PRIMARY_COLOR = "#2c3e50";
    private static final String SECONDARY_COLOR = "#34495e";
    private static final String SUCCESS_COLOR = "#4caf50";
    private static final String NEUTRAL_COLOR = "#95a5a6";
    private static final String BOOKED_COLOR = "#e74c3c";
    private static final String SELECTED_COLOR = "#3498db";
    private static final String AVAILABLE_COLOR = "#ecf0f1";
    
    private final Main mainApp;
    private final Stage primaryStage;
    private final User currentUser;
    private final BookingController bookingController;
    private SnackController snackController;

    private Movie selectedMovie;
    private String selectedDate;
    private String selectedSchedule;
    private Set<String> selectedSeats = new HashSet<>();
    
    private Label selectedSeatsDisplayLabel;
    private GridPane seatGrid;

    public SeatBookingController(Main mainApp, User currentUser, BookingController bookingController) {
        this.mainApp = Objects.requireNonNull(mainApp, "Main app cannot be null");
        this.primaryStage = mainApp.getPrimaryStage();
        this.currentUser = Objects.requireNonNull(currentUser, "Current user cannot be null");
        this.bookingController = Objects.requireNonNull(bookingController, "Booking controller cannot be null");
        System.out.println("SeatBookingController initialized for user: " + currentUser.getUsername());
    }

    public void setSnackController(SnackController snackController) {
        this.snackController = snackController;
        System.out.println("SnackController set: " + (snackController != null));
    }

    private String getBookingKey(Movie movie, String date, String schedule) {
        return movie.getTitle() + "_" + date + "_" + schedule;
    }

    private Set<String> getBookedSeats(String bookingKey) {
        return PERMANENTLY_BOOKED_SEATS.getOrDefault(bookingKey, new HashSet<>());
    }

    private Set<String> getBookedSeats(Movie movie, String date, String schedule) {
        String key = movie.getId() + "_" + selectedDate + "_" + selectedSchedule;

        return getBookedSeats(key);
    }

    public void showSeatSelection(Movie movie, String date, String schedule) {
    System.out.println("=== SHOWING SEAT SELECTION ===");
    System.out.println("Movie: " + (movie != null ? movie.getTitle() : "NULL"));
    System.out.println("Date: " + date);
    System.out.println("Schedule: " + schedule);

    this.selectedMovie = movie;
    this.selectedDate = date;
    this.selectedSchedule = schedule;
    this.selectedSeats.clear();

    if (!validateSelection()) {
        System.err.println("Validation failed!");
        return;
    }

    try {
        Platform.runLater(() -> {
            VBox root = createBaseLayout();

            Label titleLabel = createStyledLabel("SELECT SEATS", "20px", "white", true);

            String displayDate = formatDisplayDate(selectedDate);
            Label infoLabel = createStyledLabel(
                selectedMovie.getTitle() + " - " + displayDate + " - " + selectedSchedule,
                "14px", "white", false
            );

            GridPane seatGrid = new GridPane();
            seatGrid.setAlignment(Pos.CENTER);
            seatGrid.setHgap(8);
            seatGrid.setVgap(8);
            seatGrid.setPadding(new Insets(15));
            seatGrid.setStyle("-fx-background-color: #ecf0f1; -fx-background-radius: 10;");

            String key = movie.getId() + "_" + selectedDate + "_" + selectedSchedule;
            Set<String> bookedSeats = PERMANENTLY_BOOKED_SEATS.getOrDefault(key, new HashSet<>());

            for (int row = 0; row < SEAT_ROWS.length; row++) {
                Label rowLabel = new Label(SEAT_ROWS[row]);
                rowLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #2c3e50;");
                seatGrid.add(rowLabel, 0, row);

                for (int col = 1; col <= SEATS_PER_ROW; col++) {
                    String seatId = SEAT_ROWS[row] + col;
                    Button seatButton = new Button(seatId);
                    seatButton.setPrefSize(35, 35);

                    if (bookedSeats.contains(seatId)) {
                        seatButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
                        seatButton.setDisable(true);
                        seatButton.setText("X");
                    } else {
                        seatButton.setStyle("-fx-background-color: #ecf0f1; -fx-font-weight: bold;");
                        seatButton.setOnAction(e -> {
                            if (selectedSeats.contains(seatId)) {
                                selectedSeats.remove(seatId);
                                seatButton.setStyle("-fx-background-color: #ecf0f1; -fx-font-weight: bold;");
                            } else {
                                selectedSeats.add(seatId);
                                seatButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
                            }
                            updateSelectedSeatsDisplay();
                        });
                    }

                    seatGrid.add(seatButton, col, row);
                }
            }

            selectedSeatsDisplayLabel = createStyledLabel("Selected seats: None", "14px", PRIMARY_COLOR, true);
            HBox buttonBox = createSeatNavigationButtons();

            VBox seatBox = new VBox(15);
            seatBox.setAlignment(Pos.CENTER);
            seatBox.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 10;");
            seatBox.getChildren().addAll(
                new Label("🎬 SCREEN 🎬"),
                createSeatLegend(),
                seatGrid,
                selectedSeatsDisplayLabel,
                buttonBox
            );

            VBox headerBox = new VBox(10);
            headerBox.setAlignment(Pos.CENTER);
            headerBox.getChildren().addAll(titleLabel, infoLabel);

            root.getChildren().addAll(headerBox, seatBox);
            setScene(root, 600, 550);

            System.out.println("Seat selection UI created successfully");
        });
    } catch (Exception e) {
        handleError("Error showing seat selection", e);
    }
}
    
    private String formatDisplayDate(String dateStr) {
        try {
            LocalDate dateObj = LocalDate.parse(dateStr);
            return dateObj.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (DateTimeParseException e) {
            System.err.println("Error parsing date: " + dateStr + ", using as-is");
            return dateStr;
        }
    }

    public void confirmSeatBooking() {
        System.out.println("=== CONFIRMING SEAT BOOKING ===");
        
        if (selectedMovie == null || selectedDate == null || selectedSchedule == null || selectedSeats.isEmpty()) {
            System.err.println("Cannot confirm booking: incomplete selection data");
            return;
            
        }
        String key = selectedMovie.getId() + "_" + selectedDate + "_" + selectedSchedule;
    Set<String> booked = PERMANENTLY_BOOKED_SEATS.computeIfAbsent(key, k -> new HashSet<>());
    booked.addAll(selectedSeats);
    System.out.println("Confirmed seats for " + key + ": " + booked);
        String bookingKey = getBookingKey(selectedMovie, selectedDate, selectedSchedule);
        Set<String> currentlyBooked = PERMANENTLY_BOOKED_SEATS.computeIfAbsent(bookingKey, k -> new HashSet<>());
        
        for (String seat : selectedSeats) {
            if (currentlyBooked.contains(seat)) {
                Platform.runLater(() -> {
                    mainApp.showAlert("Booking Error", 
                        "Seat " + seat + " has been booked by another user. Please select different seats.");
                });
                return;
            }
        }
        
        currentlyBooked.addAll(selectedSeats);
        
        System.out.println("Seats confirmed as booked: " + selectedSeats + 
                        " for " + selectedMovie.getTitle() + " on " + selectedDate + " at " + selectedSchedule);
        
        selectedSeats.clear();
        
        return;
    }

    public Set<String> getSelectedSeats() {
        return new HashSet<>(selectedSeats);
    }

    public void setSelectedSeats(Set<String> seats) {
        System.out.println("Setting selected seats: " + seats);
        
        if (seats == null) {
            this.selectedSeats = new HashSet<>();
            return;
        }

        if (selectedMovie != null && selectedDate != null && selectedSchedule != null) {
            String bookingKey = getBookingKey(selectedMovie, selectedDate, selectedSchedule);
            Set<String> bookedSeats = getBookedSeats(bookingKey);
            
            Set<String> validSeats = new HashSet<>();
            for (String seat : seats) {
                if (!bookedSeats.contains(seat)) {
                    validSeats.add(seat);
                }
            }
            
            this.selectedSeats = validSeats;
        } else {
            this.selectedSeats = new HashSet<>(seats);
        }
        
        Platform.runLater(() -> {
            if (selectedSeatsDisplayLabel != null) {
                updateSelectedSeatsDisplay();
            }
            if (seatGrid != null) {
                refreshSeatGrid();
            }
        });
    }

    public void clearSelectedSeats() {
        this.selectedSeats.clear();
        Platform.runLater(() -> {
            if (selectedSeatsDisplayLabel != null) {
                updateSelectedSeatsDisplay();
            }
            if (seatGrid != null) {
                refreshSeatGrid();
            }
        });
    }


    public boolean isSeatAvailable(String seatId) {
        if (selectedMovie == null || selectedDate == null || selectedSchedule == null) {
            return false;
        }
        
        String bookingKey = getBookingKey(selectedMovie, selectedDate, selectedSchedule);
        Set<String> bookedSeats = getBookedSeats(bookingKey);
        return !bookedSeats.contains(seatId);
    }

    private boolean validateSelection() {
        if (selectedMovie == null) {
            Platform.runLater(() -> mainApp.showAlert("Error", "No movie selected"));
            return false;
        }
        if (selectedDate == null || selectedDate.trim().isEmpty()) {
            Platform.runLater(() -> mainApp.showAlert("Error", "No date selected"));
            return false;
        }
        if (selectedSchedule == null || selectedSchedule.trim().isEmpty()) {
            Platform.runLater(() -> mainApp.showAlert("Error", "No schedule selected"));
            return false;
        }
        return true;
    }

    private void handleError(String message, Exception e) {
        System.err.println(message + ": " + e.getMessage());
        e.printStackTrace();
        Platform.runLater(() -> {
            mainApp.showAlert("Error", message + ". Please try again.");
        });
    }

    private VBox createBaseLayout() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle(String.format("-fx-background-color: linear-gradient(to bottom, %s, %s);", PRIMARY_COLOR, SECONDARY_COLOR));
        return root;
    }

    private VBox createSeatSelectionBox() {
        VBox seatBox = new VBox(15);
        seatBox.setAlignment(Pos.CENTER);
        seatBox.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 10;");

        Label screenLabel = new Label("🎬 SCREEN 🎬");
        screenLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; " +
                        "-fx-background-color: #34495e; -fx-text-fill: white; " +
                        "-fx-padding: 10; -fx-background-radius: 5;");

        HBox legendBox = createSeatLegend();
        seatGrid = createSeatGrid();
        
        selectedSeatsDisplayLabel = createStyledLabel("Selected seats: None", "14px", PRIMARY_COLOR, true);
        
        HBox buttonBox = createSeatNavigationButtons();

        seatBox.getChildren().addAll(screenLabel, legendBox, seatGrid, selectedSeatsDisplayLabel, buttonBox);
        return seatBox;
    }

    private HBox createSeatLegend() {
        HBox legendBox = new HBox(20);
        legendBox.setAlignment(Pos.CENTER);

        String legendStyle = "-fx-font-size: 12px; -fx-text-fill: #333;";
        
        Label availableLabel = new Label("⬜ Available");
        Label selectedLabel = new Label("🟦 Selected");
        Label bookedLabel = new Label("🟥 Booked");
        
        availableLabel.setStyle(legendStyle);
        selectedLabel.setStyle(legendStyle);
        bookedLabel.setStyle(legendStyle);

        legendBox.getChildren().addAll(availableLabel, selectedLabel, bookedLabel);
        return legendBox;
    }

    private GridPane createSeatGrid() {
        System.out.println("Creating seat grid...");
        
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(8);
        grid.setVgap(8);
        grid.setPadding(new Insets(15));
        grid.setStyle("-fx-background-color: #ecf0f1; -fx-background-radius: 10;");

        Set<String> bookedSeats = getBookedSeats(selectedMovie, selectedDate, selectedSchedule);
        System.out.println("Booked seats for this screening: " + bookedSeats);

        for (int i = 0; i < SEAT_ROWS.length; i++) {
            Label rowLabel = new Label(SEAT_ROWS[i]);
            rowLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #2c3e50;");
            grid.add(rowLabel, 0, i);

            for (int j = 1; j <= SEATS_PER_ROW; j++) {
                String seatId = SEAT_ROWS[i] + j;
                Button seatBtn = createSeatButton(seatId, j, bookedSeats);
                grid.add(seatBtn, j, i);
            }
        }

        System.out.println("Seat grid created with " + (SEAT_ROWS.length * SEATS_PER_ROW) + " seats");
        return grid;
    }

    private Button createSeatButton(String seatId, int seatNumber, Set<String> bookedSeats) {
        Button seatBtn = new Button(String.valueOf(seatNumber));
        seatBtn.setUserData(seatId);
        seatBtn.setPrefSize(35, 35);
        seatBtn.setMinSize(35, 35);
        seatBtn.setMaxSize(35, 35);

        if (bookedSeats.contains(seatId)) {
    seatBtn.setStyle("-fx-background-color: " + BOOKED_COLOR);
    seatBtn.setDisable(true);
    seatBtn.setText("X");
    seatBtn.setOnAction(null); 
} else if (selectedSeats.contains(seatId)) {
            seatBtn.setStyle(String.format("-fx-background-color: %s; -fx-text-fill: white; " +
                        "-fx-font-weight: bold; -fx-background-radius: 5; " +
                        "-fx-border-color: #2980b9; -fx-border-width: 2; -fx-border-radius: 5;", SELECTED_COLOR));
            seatBtn.setOnAction(e -> handleSeatClick(seatBtn, seatId));
        } else {
            seatBtn.setStyle(String.format("-fx-background-color: %s; -fx-text-fill: #2c3e50; " +
                        "-fx-font-weight: bold; -fx-background-radius: 5; " +
                        "-fx-border-color: #bdc3c7; -fx-border-width: 2; -fx-border-radius: 5;", AVAILABLE_COLOR));
            seatBtn.setOnAction(e -> handleSeatClick(seatBtn, seatId));
        }

        return seatBtn;
    }

    private void handleSeatClick(Button seatBtn, String seatId) {
        System.out.println("Seat clicked: " + seatId);
        
        String bookingKey = getBookingKey(selectedMovie, selectedDate, selectedSchedule);
        Set<String> currentlyBooked = getBookedSeats(bookingKey);
        
        if (currentlyBooked.contains(seatId)) {
            Platform.runLater(() -> {
                mainApp.showAlert("Seat Unavailable", "This seat has been booked by another user.");
                refreshSeatGrid();
            });
            return;
        }
        
        toggleSeat(seatBtn, seatId);
    }

    private void toggleSeat(Button seatBtn, String seatId) {
        if (selectedSeats.contains(seatId)) {
            selectedSeats.remove(seatId);
            seatBtn.setStyle(String.format("-fx-background-color: %s; -fx-text-fill: #2c3e50; " +
                        "-fx-font-weight: bold; -fx-background-radius: 5; " +
                        "-fx-border-color: #bdc3c7; -fx-border-width: 2; -fx-border-radius: 5;", AVAILABLE_COLOR));
            System.out.println("Seat " + seatId + " unselected");
        } else {
            selectedSeats.add(seatId);
            seatBtn.setStyle(String.format("-fx-background-color: %s; -fx-text-fill: white; " +
                        "-fx-font-weight: bold; -fx-background-radius: 5; " +
                        "-fx-border-color: #2980b9; -fx-border-width: 2; -fx-border-radius: 5;", SELECTED_COLOR));
            System.out.println("Seat " + seatId + " selected");
        }
        
        updateSelectedSeatsDisplay();
    }

    private void refreshSeatGrid() {
        if (seatGrid != null) {
            System.out.println("Refreshing seat grid...");
            seatGrid.getChildren().clear();
            
            Set<String> bookedSeats = getBookedSeats(selectedMovie, selectedDate, selectedSchedule);

            for (int i = 0; i < SEAT_ROWS.length; i++) {
                Label rowLabel = new Label(SEAT_ROWS[i]);
                rowLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #2c3e50;");
                seatGrid.add(rowLabel, 0, i);

                for (int j = 1; j <= SEATS_PER_ROW; j++) {
                    String seatId = SEAT_ROWS[i] + j;
                    Button seatBtn = createSeatButton(seatId, j, bookedSeats);
                    seatGrid.add(seatBtn, j, i);
                }
            }
            System.out.println("Seat grid refreshed");
        }
    }

    private void updateSelectedSeatsDisplay() {
        if (selectedSeatsDisplayLabel != null) {
            String displayText = selectedSeats.isEmpty() ? "Selected seats: None" : 
                               "Selected seats: " + String.join(", ", selectedSeats);
            selectedSeatsDisplayLabel.setText(displayText);
        }
        
        System.out.println("Selected seats updated: " + 
            (selectedSeats.isEmpty() ? "None" : String.join(", ", selectedSeats)));
    }

    private HBox createSeatNavigationButtons() {
        Button nextBtn = createStyledButton("NEXT: SELECT SNACKS", SUCCESS_COLOR);
        nextBtn.setPrefWidth(180);
        nextBtn.setOnAction(e -> {
            System.out.println("Next button clicked, selected seats: " + selectedSeats);
            
            if (!selectedSeats.isEmpty()) {
                String bookingKey = getBookingKey(selectedMovie, selectedDate, selectedSchedule);
                Set<String> currentlyBooked = getBookedSeats(bookingKey);

                boolean hasConflict = false;
                for (String seat : selectedSeats) {
                    if (currentlyBooked.contains(seat)) {
                        hasConflict = true;
                        break;
                    }
                }

                if (hasConflict) {
                    mainApp.showAlert("Seat Conflict",
                            "Some selected seats are no longer available. Please refresh and select again.");
                    refreshSeatGrid();
                    return;
                }

                    try {
                        bookingController.setSelectedSeats(selectedSeats);
                        System.out.println("Selected seats passed to BookingController");
                    } catch (Exception ex) {
                        System.err.println("Error setting selected seats to BookingController: " + ex.getMessage());
                        ex.printStackTrace();
                    }

                    if (snackController != null) {
                        try {
                            snackController.showSnackSelection();
                            System.out.println("Navigated to snack selection");
                        } catch (Exception ex) {
                            System.err.println("Error showing snack selection: " + ex.getMessage());
                            ex.printStackTrace();
                            mainApp.showAlert("Error", "Failed to show snack selection: " + ex.getMessage());
                        }
                    } else {
                        System.err.println("SnackController is null!");
                        mainApp.showAlert("Error", "SnackController not set!");
                    }
                } else {
                    mainApp.showAlert("Error", "Please select at least one seat");
                }
        });

        Button backBtn = createStyledButton("← BACK", NEUTRAL_COLOR);
        backBtn.setPrefWidth(180);
        backBtn.setOnAction(e -> {
            System.out.println("Back button clicked");
            try {
                bookingController.showMovieDetail(selectedMovie);
            } catch (Exception ex) {
                System.err.println("Error going back: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(backBtn, nextBtn);
        
        return buttonBox;
    }

    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle(String.format("-fx-background-color: %s; -fx-text-fill: white; " +
                                    "-fx-font-weight: bold; -fx-background-radius: 25; " +
                                    "-fx-cursor: hand;", color));
        return button;
    }

    private Label createStyledLabel(String text, String fontSize, String color, boolean bold) {
        Label label = new Label(text);
        String style = String.format("-fx-font-size: %s; -fx-text-fill: %s;", fontSize, color);
        if (bold) style += " -fx-font-weight: bold;";
        label.setStyle(style);
        return label;
    }

    private void setScene(VBox root, int width, int height) {
        Scene scene = new Scene(root, width, height);
        primaryStage.setScene(scene);
        System.out.println("Scene set successfully");
    }

    public Map<String, Set<String>> getAllBookedSeats() {
        Map<String, Set<String>> copy = new HashMap<>();
        for (Map.Entry<String, Set<String>> entry : PERMANENTLY_BOOKED_SEATS.entrySet()) {
            copy.put(entry.getKey(), new HashSet<>(entry.getValue()));
        }
        return copy;
    }


    public void addBookedSeats(Movie movie, String date, String schedule, Set<String> seats) {
        if (movie == null || date == null || schedule == null || seats == null) {
            System.err.println("Cannot add booked seats: invalid parameters");
            return;
        }
        
String key = movie.getId() + "_" + selectedDate + "_" + selectedSchedule;
Set<String> bookedSeats = PERMANENTLY_BOOKED_SEATS.getOrDefault(key, new HashSet<>());
bookedSeats.addAll(selectedSeats);
PERMANENTLY_BOOKED_SEATS.put(key, bookedSeats);

        if (Objects.equals(this.selectedMovie, movie) && 
            Objects.equals(this.selectedDate, date) && 
            Objects.equals(this.selectedSchedule, schedule) && 
            seatGrid != null) {
            Platform.runLater(() -> refreshSeatGrid());
        }
    }

    public Set<String> getAvailableSeats(Movie movie, String date, String schedule) {
        Set<String> allSeats = new HashSet<>();
        for (String row : SEAT_ROWS) {
            for (int i = 1; i <= SEATS_PER_ROW; i++) {
                allSeats.add(row + i);
            }
        }
        
        Set<String> bookedSeats = getBookedSeats(movie, date, schedule);
        allSeats.removeAll(bookedSeats);
        
        return allSeats;
    }

    public void printBookingStats() {
        System.out.println("=== BOOKING STATISTICS ===");
        if (PERMANENTLY_BOOKED_SEATS.isEmpty()) {
            System.out.println("No bookings yet");
        } else {
            for (Map.Entry<String, Set<String>> entry : PERMANENTLY_BOOKED_SEATS.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue().size() + " seats booked");
                System.out.println("  Booked seats: " + entry.getValue());
            }
        }
        System.out.println("========================");
    }
}