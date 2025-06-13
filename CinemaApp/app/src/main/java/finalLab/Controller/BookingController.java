package finalLab.Controller;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import finalLab.Model.Movie;
import finalLab.Model.Ticket;

public class BookingController extends BaseController {
    private Movie selectedMovie;
    private LocalDate selectedDate;
    private String selectedTime;
    private List<String> selectedSeats;
    private Map<String, Integer> selectedSnacks;

    private Button selectedMovieButton;
    private Button selectedDateButton;
    private Button selectedTimeButton;

    private MainMenuController mainMenuController;

    public BookingController(Stage primaryStage) {
        super(primaryStage);
        this.selectedSeats = new ArrayList<>();
        this.selectedSnacks = new HashMap<>();
    }

    private MainMenuController getMainMenuController() {
        if (mainMenuController == null) {
            mainMenuController = new MainMenuController(primaryStage);
        }
        return mainMenuController;
    }

    @Override
    public void show() {
        showMovieSelectionScene();
    }

    public void showMovieSelectionScene() {
        VBox root = new VBox(25);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background: linear-gradient(to bottom, #0f0c29, #24243e, #302b63);");

        Label titleLabel = new Label("🎬 SELECT YOUR MOVIE");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 0, 3);");

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        GridPane movieGrid = new GridPane();
        movieGrid.setHgap(20);
        movieGrid.setVgap(20);
        movieGrid.setAlignment(Pos.CENTER);
        movieGrid.setPadding(new Insets(20));

        List<Movie> movies = movieManager.getAllMovies();
        selectedMovie = null;
        selectedMovieButton = null;

        int col = 0, row = 0;
        for (Movie movie : movies) {
            Button movieButton = createMovieButton(movie);
            movieGrid.add(movieButton, col, row);

            col++;
            if (col > 2) {
                col = 0;
                row++;
            }
        }

        scrollPane.setContent(movieGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(400);

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        Button nextButton = createStyledButton("➡ NEXT", "#4CAF50", "#45a049");
        Button backButton = createStyledButton("⬅ BACK", "#757575", "#616161");

        nextButton.setOnAction(e -> {
            if (selectedMovie != null) {
                showDateSelectionScene();
            } else {
                showStyledAlert("⚠ No Selection", "Please select a movie first!", Alert.AlertType.WARNING);
            }
        });

        backButton.setOnAction(e -> {
            MainMenuController controller = getMainMenuController();
            controller.setCurrentUser(currentUser);
            controller.show();
        });

        buttonBox.getChildren().addAll(backButton, nextButton);

        root.getChildren().addAll(titleLabel, scrollPane, buttonBox);

        Scene scene = new Scene(root, 450, 600);
        setScene(scene);
    }

    private Button createMovieButton(Movie movie) {
        VBox movieBox = new VBox(8);
        movieBox.setAlignment(Pos.CENTER);
        movieBox.setPadding(new Insets(15));
        movieBox.setStyle("-fx-background-color: #2c3e50; -fx-background-radius: 10; " +
                "-fx-border-color: #34495e; -fx-border-width: 2; -fx-border-radius: 10;");

        Label nameLabel = new Label(movie.getTitle());
        nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white; " +
                "-fx-text-alignment: center;");
        nameLabel.setWrapText(true);

        Label genreLabel = new Label("🎭 " + movie.getGenre());
        genreLabel.setStyle("-fx-text-fill: #ecf0f1; -fx-font-size: 12px;");

        Label priceLabel = new Label("💰 Rp " + String.format("%,d", movie.getPrice()));
        priceLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 13px;");

        movieBox.getChildren().addAll(nameLabel, genreLabel, priceLabel);

        Button movieButton = new Button();
        movieButton.setGraphic(movieBox);
        movieButton.setPrefSize(180, 120);
        movieButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");

        movieButton.setOnMouseEntered(e -> {
            if (selectedMovieButton != movieButton) {
                movieBox.setStyle("-fx-background-color: #34495e; -fx-background-radius: 10; " +
                        "-fx-border-color: #3498db; -fx-border-width: 2; -fx-border-radius: 10; " +
                        "-fx-effect: dropshadow(gaussian, rgba(52, 152, 219, 0.5), 15, 0, 0, 0);");
            }
        });

        movieButton.setOnMouseExited(e -> {
            if (selectedMovieButton != movieButton) {
                movieBox.setStyle("-fx-background-color: #2c3e50; -fx-background-radius: 10; " +
                        "-fx-border-color: #34495e; -fx-border-width: 2; -fx-border-radius: 10;");
            }
        });

        movieButton.setOnAction(e -> {
            if (selectedMovieButton != null) {
                VBox prevBox = (VBox) selectedMovieButton.getGraphic();
                prevBox.setStyle("-fx-background-color: #2c3e50; -fx-background-radius: 10; " +
                        "-fx-border-color: #34495e; -fx-border-width: 2; -fx-border-radius: 10;");
            }

            selectedMovie = movie;
            selectedMovieButton = movieButton;
            movieBox.setStyle("-fx-background-color: #27ae60; -fx-background-radius: 10; " +
                    "-fx-border-color: #2ecc71; -fx-border-width: 3; -fx-border-radius: 10; " +
                    "-fx-effect: dropshadow(gaussian, rgba(46, 204, 113, 0.8), 20, 0, 0, 0);");
        });

        return movieButton;
    }

    public void showDateSelectionScene() {

        VBox root = new VBox(25);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background: linear-gradient(to bottom, #0f0c29, #24243e, #302b63);");

        Label titleLabel = new Label("📅 SELECT DATE");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 0, 3);");

        Label movieLabel = new Label("🎬 " + selectedMovie.getTitle());
        movieLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #fff; -fx-font-weight: bold; " +
                "-fx-background-color: rgba(255,255,255,0.1); -fx-padding: 10; " +
                "-fx-background-radius: 10;");

        VBox dateContainer = new VBox(15);
        dateContainer.setAlignment(Pos.CENTER);
        selectedDate = null;
        selectedDateButton = null;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy");

        for (int i = 0; i < 5; i++) {
            LocalDate date = LocalDate.now().plusDays(i);
            Button dateButton = createDateButton(date, formatter);
            dateContainer.getChildren().add(dateButton);
        }

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        Button nextButton = createStyledButton("➡ NEXT", "#4CAF50", "#45a049");
        Button backButton = createStyledButton("⬅ BACK", "#757575", "#616161");

        nextButton.setOnAction(e -> {
            if (selectedDate != null) {
                showTimeSelectionScene();
            } else {
                showStyledAlert("⚠ No Selection", "Please select a date first!", Alert.AlertType.WARNING);
            }
        });

        backButton.setOnAction(e -> showMovieSelectionScene());

        buttonBox.getChildren().addAll(backButton, nextButton);

        root.getChildren().addAll(titleLabel, movieLabel, dateContainer, buttonBox);

        Scene scene = new Scene(root, 450, 600);
        setScene(scene);
    }

    private Button createDateButton(LocalDate date, DateTimeFormatter formatter) {
        Button dateButton = new Button(date.format(formatter));
        dateButton.setPrefWidth(400);
        dateButton.setPrefHeight(50);
        dateButton.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-font-size: 14px; " +
                "-fx-background-radius: 10; -fx-border-color: #2c3e50; -fx-border-width: 2; " +
                "-fx-border-radius: 10;");

        dateButton.setOnMouseEntered(e -> {
            if (selectedDateButton != dateButton) {
                dateButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; " +
                        "-fx-background-radius: 10; -fx-border-color: #2980b9; -fx-border-width: 2; " +
                        "-fx-border-radius: 10;");
            }
        });

        dateButton.setOnMouseExited(e -> {
            if (selectedDateButton != dateButton) {
                dateButton.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-font-size: 14px; " +
                        "-fx-background-radius: 10; -fx-border-color: #2c3e50; -fx-border-width: 2; " +
                        "-fx-border-radius: 10;");
            }
        });

        dateButton.setOnAction(e -> {
            if (selectedDateButton != null) {
                selectedDateButton
                        .setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-font-size: 14px; " +
                                "-fx-background-radius: 10; -fx-border-color: #2c3e50; -fx-border-width: 2; " +
                                "-fx-border-radius: 10;");
            }

            selectedDate = date;
            selectedDateButton = dateButton;
            dateButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 14px; " +
                    "-fx-background-radius: 10; -fx-border-color: #2ecc71; -fx-border-width: 3; " +
                    "-fx-border-radius: 10; -fx-effect: dropshadow(gaussian, rgba(46, 204, 113, 0.8), 15, 0, 0, 0);");
        });

        return dateButton;
    }

    public void showTimeSelectionScene() {

        VBox root = new VBox(120);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background: linear-gradient(to bottom, #0f0c29, #24243e, #302b63);");

        VBox headerSection = new VBox(20);
        headerSection.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("🕐 SELECT TIME");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 0, 3);");

        Label detailLabel = new Label("🎬 " + selectedMovie.getTitle() + "\n📅 " +
                selectedDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")));
        detailLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #fff; -fx-text-alignment: center; " +
                "-fx-background-color: rgba(255,255,255,0.1); -fx-padding: 15; " +
                "-fx-background-radius: 10;");

        headerSection.getChildren().addAll(titleLabel, detailLabel);

        VBox timeSection = new VBox(30);
        timeSection.setAlignment(Pos.CENTER);

        HBox timeBox = new HBox(20);
        timeBox.setAlignment(Pos.CENTER);

        selectedTime = null;
        selectedTimeButton = null;

        String[] times = { "10:00", "13:00", "16:00", "19:00", "22:00" };

        for (String time : times) {
            Button timeButton = createTimeButton(time);
            timeBox.getChildren().add(timeButton);
        }

        timeSection.getChildren().add(timeBox);

        VBox navigationSection = new VBox(30);
        navigationSection.setAlignment(Pos.CENTER);

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        Button nextButton = createStyledButton("➡ NEXT", "#4CAF50", "#45a049");
        Button backButton = createStyledButton("⬅ BACK", "#757575", "#616161");

        nextButton.setOnAction(e -> {
            if (selectedTime != null) {
                showSeatSelectionScene();
            } else {
                showStyledAlert("⚠ No Selection", "Please select a time first!", Alert.AlertType.WARNING);
            }
        });

        backButton.setOnAction(e -> showDateSelectionScene());

        buttonBox.getChildren().addAll(backButton, nextButton);
        navigationSection.getChildren().add(buttonBox);

        root.getChildren().addAll(headerSection, timeSection, navigationSection);

        Scene scene = new Scene(root, 600, 700);
        setScene(scene);
    }

    private Button createTimeButton(String time) {
        Button timeButton = new Button(time);
        timeButton.setPrefSize(75, 50);
        timeButton.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-font-size: 16px; " +
                "-fx-font-weight: bold; -fx-background-radius: 15; -fx-border-color: #2c3e50; " +
                "-fx-border-width: 2; -fx-border-radius: 15;");

        timeButton.setOnMouseEntered(e -> {
            if (selectedTimeButton != timeButton) {
                timeButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 16px; " +
                        "-fx-font-weight: bold; -fx-background-radius: 15; -fx-border-color: #2980b9; " +
                        "-fx-border-width: 2; -fx-border-radius: 15;");
            }
        });

        timeButton.setOnMouseExited(e -> {
            if (selectedTimeButton != timeButton) {
                timeButton.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-font-size: 16px; " +
                        "-fx-font-weight: bold; -fx-background-radius: 15; -fx-border-color: #2c3e50; " +
                        "-fx-border-width: 2; -fx-border-radius: 15;");
            }
        });

        timeButton.setOnAction(e -> {
            if (selectedTimeButton != null) {
                selectedTimeButton
                        .setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-font-size: 16px; " +
                                "-fx-font-weight: bold; -fx-background-radius: 15; -fx-border-color: #2c3e50; " +
                                "-fx-border-width: 2; -fx-border-radius: 15;");
            }

            selectedTime = time;
            selectedTimeButton = timeButton;
            timeButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 16px; " +
                    "-fx-font-weight: bold; -fx-background-radius: 15; -fx-border-color: #2ecc71; " +
                    "-fx-border-width: 3; -fx-border-radius: 15; " +
                    "-fx-effect: dropshadow(gaussian, rgba(46, 204, 113, 0.8), 15, 0, 0, 0);");
        });

        return timeButton;

    }

    public void showSeatSelectionScene() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background: linear-gradient(to bottom, #0f0c29, #24243e, #302b63);");

        Label titleLabel = new Label("🪑 SELECT YOUR SEATS");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 0, 3);");

        VBox screenContainer = new VBox(5);
        screenContainer.setAlignment(Pos.CENTER);

        Label screenLabel = new Label("🎬 SCREEN 🎬");
        screenLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white; -fx-background-color: #2c3e50; " +
                "-fx-padding: 15; -fx-background-radius: 20; -fx-font-weight: bold; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 0, 3);");
        screenLabel.setMaxWidth(Double.MAX_VALUE);
        screenLabel.setAlignment(Pos.CENTER);

        Rectangle screenEffect = new Rectangle(400, 5);
        screenEffect.setFill(Color.LIGHTBLUE);
        screenEffect.setStyle("-fx-effect: dropshadow(gaussian, lightblue, 15, 0, 0, 0);");

        screenContainer.getChildren().addAll(screenLabel, screenEffect);

        GridPane seatGrid = new GridPane();
        seatGrid.setHgap(8);
        seatGrid.setVgap(8);
        seatGrid.setAlignment(Pos.CENTER);
        seatGrid.setPadding(new Insets(30, 0, 20, 0));

        selectedSeats.clear();
        List<String> bookedSeats = bookingManager.getBookedSeats(selectedMovie.getTitle(), selectedDate, selectedTime);

        for (int row = 0; row < 5; row++) {
            char rowLetter = (char) ('A' + row);

            Label rowLabel = new Label(String.valueOf(rowLetter));
            rowLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
            seatGrid.add(rowLabel, 0, row + 1);

            for (int col = 1; col <= 8; col++) {
                String seatId = rowLetter + String.valueOf(col);
                Button seatButton = new Button(String.valueOf(col));
                seatButton.setMinSize(45, 45);
                seatButton.setMaxSize(45, 45);

                if (bookedSeats.contains(seatId)) {
                    seatButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; " +
                            "-fx-background-radius: 8; -fx-font-weight: bold;");
                    seatButton.setDisable(true);
                } else {
                    seatButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; " +
                            "-fx-background-radius: 8; -fx-font-weight: bold;");

                    seatButton.setOnMouseEntered(e -> {
                        if (!selectedSeats.contains(seatId)) {
                            seatButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; " +
                                    "-fx-background-radius: 8; -fx-font-weight: bold; " +
                                    "-fx-effect: dropshadow(gaussian, rgba(46, 204, 113, 0.8), 10, 0, 0, 0);");
                        }
                    });

                    seatButton.setOnMouseExited(e -> {
                        if (!selectedSeats.contains(seatId)) {
                            seatButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; " +
                                    "-fx-background-radius: 8; -fx-font-weight: bold;");
                        }
                    });

                    seatButton.setOnAction(e -> {
                        if (selectedSeats.contains(seatId)) {
                            selectedSeats.remove(seatId);
                            seatButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; " +
                                    "-fx-background-radius: 8; -fx-font-weight: bold;");
                        } else {
                            selectedSeats.add(seatId);
                            seatButton.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; " +
                                    "-fx-background-radius: 8; -fx-font-weight: bold; " +
                                    "-fx-effect: dropshadow(gaussian, rgba(243, 156, 18, 0.8), 15, 0, 0, 0);");
                        }
                    });
                }

                seatGrid.add(seatButton, col, row + 1);
            }
        }

        HBox legendBox = new HBox(30);
        legendBox.setAlignment(Pos.CENTER);
        legendBox.setPadding(new Insets(20));
        legendBox.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-background-radius: 15;");

        VBox availableBox = createLegendItem("✅ Available", "#27ae60");
        VBox selectedBox = createLegendItem("🎯 Selected", "#f39c12");
        VBox bookedBox = createLegendItem("❌ Booked", "#e74c3c");

        legendBox.getChildren().addAll(availableBox, selectedBox, bookedBox);

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        Button nextButton = createStyledButton("➡ NEXT", "#4CAF50", "#45a049");
        Button backButton = createStyledButton("⬅ BACK", "#757575", "#616161");

        nextButton.setOnAction(e -> {
            if (selectedSeats.isEmpty()) {
                showStyledAlert("⚠ No Selection", "Please select at least one seat!", Alert.AlertType.WARNING);
            } else {
                showSnackSelectionScene();
            }
        });

        backButton.setOnAction(e -> showTimeSelectionScene());

        buttonBox.getChildren().addAll(backButton, nextButton);

        ScrollPane scrollPane = new ScrollPane();
        VBox content = new VBox(20);
        content.getChildren().addAll(titleLabel, screenContainer, seatGrid, legendBox, buttonBox);
        content.setAlignment(Pos.CENTER);
        scrollPane.setContent(content);
        scrollPane.setFitToWidth(true);

        root.getChildren().add(scrollPane);

        Scene scene = new Scene(root, 600, 500);
        setScene(scene);
    }

    private VBox createLegendItem(String text, String color) {
        VBox legendItem = new VBox(5);
        legendItem.setAlignment(Pos.CENTER);

        Button colorButton = new Button();
        colorButton.setPrefSize(30, 30);
        colorButton.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 6;");
        colorButton.setDisable(true);

        Label textLabel = new Label(text);
        textLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold;");

        legendItem.getChildren().addAll(colorButton, textLabel);
        return legendItem;
    }

    public void showSnackSelectionScene() {
        VBox root = new VBox(25);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background: linear-gradient(to bottom, #0f0c29, #24243e, #302b63);");

        Label titleLabel = new Label("🍿 SELECT SNACKS (Optional)");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 0, 3);");

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        VBox snackContainer = new VBox(15);
        snackContainer.setPadding(new Insets(20));
        selectedSnacks.clear();

        String[][] snacks = {
                { "🍿 Popcorn Large", "25000" },
                { "🍿 Popcorn Medium", "20000" },
                { "🥤 Coca Cola", "15000" },
                { "💧 Mineral Water", "8000" },
                { "🌮 Nachos", "30000" },
                { "🌭 Hot Dog", "35000" }
        };

        for (String[] snack : snacks) {
            HBox snackBox = createSnackItem(snack[0], Integer.parseInt(snack[1]));
            snackContainer.getChildren().add(snackBox);
        }

        scrollPane.setContent(snackContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(350);

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        Button nextButton = createStyledButton("➡ NEXT", "#4CAF50", "#45a049");
        Button backButton = createStyledButton("⬅ BACK", "#757575", "#616161");

        nextButton.setOnAction(e -> showPaymentScene());
        backButton.setOnAction(e -> showSeatSelectionScene());

        buttonBox.getChildren().addAll(backButton, nextButton);

        root.getChildren().addAll(titleLabel, scrollPane, buttonBox);

        Scene scene = new Scene(root, 450, 600);
        setScene(scene);
    }

    private HBox createSnackItem(String name, int price) {
        HBox snackBox = new HBox(15);
        snackBox.setAlignment(Pos.CENTER_LEFT);
        snackBox.setPadding(new Insets(15));
        snackBox.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-background-radius: 15; " +
                "-fx-border-color: rgba(255,255,255,0.2); -fx-border-width: 1; -fx-border-radius: 15;");

        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        nameLabel.setPrefWidth(200);

        Label priceLabel = new Label("Rp " + String.format("%,d", price));
        priceLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 14px; -fx-font-weight: bold;");
        priceLabel.setPrefWidth(120);

        Spinner<Integer> quantitySpinner = new Spinner<>(0, 10, 0);
        quantitySpinner.setPrefWidth(100);
        quantitySpinner.setStyle("-fx-background-color: white; -fx-background-radius: 8;");

        String snackKey = name.replaceAll("^[^a-zA-Z]+", "").split(" ")[0] + " " +
                (name.contains("Large") ? "Large"
                        : name.contains("Medium") ? "Medium"
                                : name.replaceAll("^[^a-zA-Z]+", "").split(" ").length > 1
                                        ? name.replaceAll("^[^a-zA-Z]+", "").split(" ")[1]
                                        : "");

        quantitySpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal > 0) {
                selectedSnacks.put(snackKey, newVal);
            } else {
                selectedSnacks.remove(snackKey);
            }
        });

        snackBox.getChildren().addAll(nameLabel, priceLabel, quantitySpinner);
        return snackBox;
    }

    public void showPaymentScene() {
        VBox root = new VBox(25);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background: linear-gradient(to bottom, #0f0c29, #24243e, #302b63);");

        Label titleLabel = new Label("💳 PAYMENT SUMMARY");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 0, 3);");

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        VBox summaryBox = new VBox(15);
        summaryBox.setPadding(new Insets(25));
        summaryBox.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-background-radius: 20; " +
                "-fx-border-color: rgba(255,255,255,0.2); -fx-border-width: 2; -fx-border-radius: 20;");

        VBox movieSection = createSummarySection("🎬 MOVIE DETAILS");
        movieSection.getChildren().addAll(
                createSummaryItem("Movie", selectedMovie.getTitle()),
                createSummaryItem("Date & Time",
                        selectedDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy")) + " at " + selectedTime),
                createSummaryItem("Seats", String.join(", ", selectedSeats)));

        int ticketTotal = selectedMovie.getPrice() * selectedSeats.size();
        Label ticketPriceLabel = new Label("💰 Ticket Total: Rp " + String.format("%,d", ticketTotal));
        ticketPriceLabel.setStyle("-fx-text-fill: #2ecc71; -fx-font-size: 16px; -fx-font-weight: bold;");
        movieSection.getChildren().add(ticketPriceLabel);

        summaryBox.getChildren().add(movieSection);

        int snackTotal = 0;
        if (!selectedSnacks.isEmpty()) {
            VBox snackSection = createSummarySection("🍿 SNACKS");

            Map<String, Integer> snackPrices = Map.of(
                    "Popcorn Large", 25000, "Popcorn Medium", 20000, "Coca Cola", 15000,
                    "Mineral Water", 8000, "Nachos", 30000, "Hot Dog", 35000);

            for (Map.Entry<String, Integer> entry : selectedSnacks.entrySet()) {
                String snackName = entry.getKey();
                int quantity = entry.getValue();
                int price = snackPrices.get(snackName) * quantity;
                snackTotal += price;

                Label snackLabel = new Label(
                        "• " + snackName + " x" + quantity + " = Rp " + String.format("%,d", price));
                snackLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
                snackSection.getChildren().add(snackLabel);
            }

            Label snackTotalLabel = new Label("🍿 Snack Total: Rp " + String.format("%,d", snackTotal));
            snackTotalLabel.setStyle("-fx-text-fill: #f39c12; -fx-font-size: 16px; -fx-font-weight: bold;");
            snackSection.getChildren().add(snackTotalLabel);

            summaryBox.getChildren().add(snackSection);
        }

        int totalAmount = ticketTotal + snackTotal;
        VBox totalSection = new VBox(10);
        totalSection.setStyle("-fx-background-color: rgba(46, 204, 113, 0.2); -fx-background-radius: 15; " +
                "-fx-padding: 20;");

        Label totalLabel = new Label("💎 GRAND TOTAL: Rp " + String.format("%,d", totalAmount));
        totalLabel.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 5, 0, 0, 2);");

        Label balanceLabel = new Label("💰 Your Balance: Rp " + String.format("%,d", currentUser.getBalance()));
        balanceLabel.setStyle("-fx-text-fill: #2ecc71; -fx-font-size: 16px; -fx-font-weight: bold;");

        totalSection.getChildren().addAll(totalLabel, balanceLabel);
        summaryBox.getChildren().add(totalSection);

        scrollPane.setContent(summaryBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(400);

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        Button payButton;
        if (currentUser.getBalance() >= totalAmount) {
            payButton = createStyledButton("💳 PAY NOW", "#27ae60", "#2ecc71");
        } else {
            payButton = createStyledButton("❌ INSUFFICIENT BALANCE", "#e74c3c", "#e74c3c");
            payButton.setDisable(true);
        }

        Button backButton = createStyledButton("⬅ BACK", "#757575", "#616161");

        payButton.setOnAction(e -> {
            if (currentUser.getBalance() >= totalAmount) {
                currentUser.setBalance(currentUser.getBalance() - totalAmount);
                userManager.updateUserBalance(currentUser.getUsername(), currentUser.getBalance());

                Ticket ticket = new Ticket(
                        UUID.randomUUID().toString(),
                        currentUser.getUsername(),
                        selectedMovie.getTitle(),
                        selectedDate,
                        selectedTime,
                        new ArrayList<>(selectedSeats),
                        new HashMap<>(selectedSnacks),
                        totalAmount);

                bookingManager.saveTicket(ticket);
                bookingManager.saveBookedSeats(selectedMovie.getTitle(), selectedDate, selectedTime, selectedSeats);

                showPaymentSuccessScene(ticket);
            }
        });

        backButton.setOnAction(e -> showSnackSelectionScene());

        buttonBox.getChildren().addAll(backButton, payButton);

        root.getChildren().addAll(titleLabel, scrollPane, buttonBox);

        Scene scene = new Scene(root, 450, 600);
        setScene(scene);
    }

    private VBox createSummarySection(String title) {
        VBox section = new VBox(10);
        section.setPadding(new Insets(15));
        section.setStyle("-fx-background-color: rgba(255,255,255,0.05); -fx-background-radius: 10;");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        section.getChildren().add(titleLabel);

        return section;
    }

    private Label createSummaryItem(String label, String value) {
        Label item = new Label(label + ": " + value);
        item.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        return item;
    }

    public void showPaymentSuccessScene(Ticket ticket) {
        VBox root = new VBox(30);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background: linear-gradient(to bottom, #0f0c29, #24243e, #302b63);");

        // Success animation effect
        Label successIcon = new Label("✅");
        successIcon.setStyle("-fx-font-size: 80px;");

        Label successLabel = new Label("PAYMENT SUCCESSFUL!");
        successLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: white; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 0, 3);");

        VBox ticketBox = new VBox(10);
        ticketBox.setAlignment(Pos.CENTER);
        ticketBox.setPadding(new Insets(20));
        ticketBox.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-background-radius: 20; " +
                "-fx-border-color: rgba(255,255,255,0.3); -fx-border-width: 2; -fx-border-radius: 20;");

        Label ticketIdLabel = new Label("🎫 Ticket ID: " + ticket.getTicketId());
        ticketIdLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white; -fx-font-weight: bold;");

        Label thankYouLabel = new Label("Thank you for your purchase!");
        thankYouLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white; -fx-font-style: italic;");

        ticketBox.getChildren().addAll(ticketIdLabel, thankYouLabel);

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        Button viewTicketButton = createStyledButton("🎫 VIEW TICKET", "#3498db", "#2980b9");
        Button backToMainButton = createStyledButton("🏠 MAIN MENU", "#27ae60", "#2ecc71");

        TicketController ticketController = new TicketController(primaryStage);
        viewTicketButton.setOnAction(e -> {
            ticketController.setCurrentUser(currentUser);
            ticketController.showTicketDetail(ticket);
        });

        backToMainButton.setOnAction(e -> {
            MainMenuController controller = getMainMenuController();
            controller.setCurrentUser(currentUser);
            controller.show();
        });

        buttonBox.getChildren().addAll(viewTicketButton, backToMainButton);

        root.getChildren().addAll(successIcon, successLabel, ticketBox, buttonBox);

        Scene scene = new Scene(root, 450, 600);
        primaryStage.setScene(scene);
    }
}