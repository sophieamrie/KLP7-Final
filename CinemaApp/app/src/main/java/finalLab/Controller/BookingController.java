package finalLab.Controller;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import finalLab.Main;
import finalLab.Model.Movie;
import finalLab.Model.SnackItem;
import finalLab.Model.User;
import finalLab.Service.MovieService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class BookingController {
    private static final String[] SEAT_ROWS = {"A", "B", "C", "D", "E"};
    private static final int SEATS_PER_ROW = 8;
    private static final Map<String, Set<String>> BOOKED_SEATS = new HashMap<>();
    
    private static final String PRIMARY_COLOR = "#2c3e50";
    private static final String SECONDARY_COLOR = "#34495e";
    private static final String SUCCESS_COLOR = "#4caf50";
    private static final String NEUTRAL_COLOR = "#95a5a6";
    
    private final Main mainApp;
    private final Stage primaryStage;
    private final User currentUser;
    private final MovieService movieService;
    private SnackController snackController;

    private Movie selectedMovie;
    private String selectedDate;
    private String selectedSchedule;
    private Set<String> selectedSeats = new HashSet<>();
    private List<SnackItem> selectedSnacks = new ArrayList<>();
    
    private String currentTicketId;
    private String currentTicketText;
    private String paymentMethod;
    private boolean paymentCompleted;

    private Label selectedSeatsDisplayLabel;

    public BookingController(Main mainApp, User currentUser) {
        this.mainApp = Objects.requireNonNull(mainApp, "Main app cannot be null");
        this.primaryStage = mainApp.getPrimaryStage();
        this.currentUser = Objects.requireNonNull(currentUser, "Current user cannot be null");
        this.movieService = mainApp.getMovieService();
    }

    public void showMoviesList() {
        try {
            VBox root = createBaseLayout();
            
            HBox header = createHeader("🎬 Sedang Tayang");
            HBox headerBox = new HBox(header);
            headerBox.setAlignment(Pos.CENTER);
            
            ScrollPane scrollPane = createMoviesScrollPane();
            
            Button backBtn = createStyledButton("← BACK TO MENU", NEUTRAL_COLOR);
            backBtn.setOnAction(e -> mainApp.showMainMenu());
            HBox backBox = new HBox(backBtn);
            backBox.setAlignment(Pos.CENTER);
            backBox.setPadding(new Insets(20, 0, 0, 0));

            root.getChildren().addAll(headerBox, scrollPane, backBox);
            setScene(root, 900, 650);
        } catch (Exception e) {
            handleError("Error showing movies list", e);
        }
    }

    public void showMovieDetail(Movie movie) {
        if (movie == null) {
            mainApp.showAlert("Error", "Movie information is not available");
            return;
        }
        try {
            this.selectedMovie = movie;
            
            VBox root = createBaseLayout();
            
            Button backBtn = createStyledButton("← KEMBALI", NEUTRAL_COLOR);
            backBtn.setOnAction(e -> showMoviesList());
            HBox backBox = new HBox(backBtn);
            backBox.setAlignment(Pos.CENTER_LEFT);

            HBox movieHeader = createMovieHeader(movie);
            VBox scheduleSection = createScheduleSection();
            
            root.getChildren().addAll(backBox, movieHeader, scheduleSection);
            setScene(root, 800, 600);
        } catch (Exception e) {
            handleError("Error showing movie details", e);
        }
    }

    public void showSeatSelection() {
        if (!validateSelection()) {
            return;
        }
        try {
            VBox root = createBaseLayout();
            
            Label titleLabel = createStyledLabel("SELECT SEATS", "20px", "white", true);
            
            LocalDate date = LocalDate.parse(selectedDate);
            String displayDate = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            Label infoLabel = createStyledLabel(
                selectedMovie.getTitle() + " - " + displayDate + " - " + selectedSchedule,
                "14px", "white", false
                );
                
            VBox seatBox = createSeatSelectionBox();
                
            VBox headerBox = new VBox(titleLabel, infoLabel);
            headerBox.setAlignment(Pos.CENTER);
            root.getChildren().addAll(headerBox, seatBox);
            setScene(root, 600, 550);
        } catch (Exception e) {
            handleError("Error showing seat selection", e);
        }
    }

    public void confirmSeatBooking() {
        if (selectedMovie != null && selectedDate != null && selectedSchedule != null && !selectedSeats.isEmpty()) {
            String key = getBookingKey(selectedMovie, selectedDate, selectedSchedule);
            BOOKED_SEATS.computeIfAbsent(key, k -> new HashSet<>()).addAll(selectedSeats);
        }
    }

    public Movie getSelectedMovie() { return selectedMovie; }
    public String getSelectedDate() { return selectedDate; }
    public String getSelectedSchedule() { return selectedSchedule; }
    public Set<String> getSelectedSeats() { return new HashSet<>(selectedSeats); }
    public List<SnackItem> getSelectedSnacks() { return new ArrayList<>(selectedSnacks); }
    public User getCurrentUser() { return currentUser; }
    public Stage getPrimaryStage() { return primaryStage; }
    public String getCurrentTicketId() { return currentTicketId; }
    public String getCurrentTicketText() { return currentTicketText; }
    public String getPaymentMethod() { return paymentMethod; }
    public boolean isPaymentCompleted() { return paymentCompleted; }

    public void setSnackController(SnackController snackController) {
        this.snackController = snackController;
    }

    public void setSelectedSeats(Set<String> seats) {
        this.selectedSeats = seats != null ? new HashSet<>(seats) : new HashSet<>();
    }

    public void setSelectedSnacks(List<SnackItem> snacks) {
        this.selectedSnacks = snacks != null ? new ArrayList<>(snacks) : new ArrayList<>();
    }

    public void updateUserBalance(double newBalance) {
        currentUser.setBalance(newBalance);
    }

    public void setCurrentTicketId(String ticketId) { this.currentTicketId = ticketId; }
    public void setCurrentTicketText(String ticketText) { this.currentTicketText = ticketText; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public void setPaymentCompleted(boolean completed) { this.paymentCompleted = completed; }


    private boolean validateSelection() {
        if (selectedMovie == null) {
            mainApp.showAlert("Error", "No movie selected");
            return false;
        }
        if (selectedDate == null) {
            mainApp.showAlert("Error", "No date selected");
            return false;
        }
        if (selectedSchedule == null) {
            mainApp.showAlert("Error", "No schedule selected");
            return false;
        }
        return true;
    }

    private void handleError(String message, Exception e) {
        System.err.println(message + ": " + e.getMessage());
        e.printStackTrace();
        mainApp.showAlert("Error", message + ". Please try again.");
    }

    private String getBookingKey(Movie movie, String date, String schedule) {
        return movie.getTitle() + "_" + date + "_" + schedule;
    }

    private Set<String> getBookedSeats(Movie movie, String date, String schedule) {
        String key = getBookingKey(movie, date, schedule);
        return BOOKED_SEATS.getOrDefault(key, new HashSet<>());
    }

    private VBox createBaseLayout() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle(String.format("-fx-background-color: linear-gradient(to bottom, %s, %s);", PRIMARY_COLOR, SECONDARY_COLOR));
        return root;
    }

    private HBox createHeader(String title) {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(15, 20, 15, 20));
        header.setSpacing(20);

        Label logoLabel = new Label(title);
        logoLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: rgb(253, 253, 253);");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(logoLabel, spacer);
        return header;
    }

    private ScrollPane createMoviesScrollPane() {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(false);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        HBox moviesContainer = new HBox(20);
        moviesContainer.setPadding(new Insets(20));
        moviesContainer.setAlignment(Pos.CENTER_LEFT);

        try {
            List<Movie> movies = movieService.loadMovies();
            if (movies == null || movies.isEmpty()) {
                Label noMoviesLabel = createStyledLabel("No movies available", "16px", "white", false);
                moviesContainer.getChildren().add(noMoviesLabel);
            } else {
                movies.forEach(movie -> moviesContainer.getChildren().add(createMovieCard(movie)));
            }
        } catch (Exception e) {
            handleError("Error loading movies", e);
            Label errorLabel = createStyledLabel("Error loading movies", "16px", "white", false);
            moviesContainer.getChildren().add(errorLabel);
        }

        scrollPane.setContent(moviesContainer);
        return scrollPane;
    }

    private VBox createMovieCard(Movie movie) {
        VBox card = new VBox();
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 20; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 3);");
        card.setPrefWidth(250); 
        card.setMaxWidth(250);

        VBox posterArea = createPosterArea(movie);
        VBox infoArea = createMovieInfoArea(movie);
        
        card.getChildren().addAll(posterArea, infoArea);
        addHoverEffect(card);
        
        return card;
    }

    private VBox createPosterArea(Movie movie) {
        VBox posterArea = new VBox();
        posterArea.setPrefHeight(400);
        posterArea.setAlignment(Pos.CENTER);
        posterArea.setStyle("-fx-background-color: white; -fx-background-radius: 15 15 0 0;");

        ImageView posterImage = loadMoviePoster(movie.getTitle());

        if (posterImage != null) {
            StackPane posterStack = new StackPane();
            posterStack.getChildren().add(posterImage);

            HBox badgeArea = createAgeRatingBadge(movie);
            StackPane.setAlignment(badgeArea, Pos.TOP_LEFT);
            posterStack.getChildren().add(badgeArea);

            posterArea.getChildren().add(posterStack);
        } else {
            createFallbackPoster(posterArea, movie);
        }

        return posterArea;
    }

    private void createFallbackPoster(VBox posterArea, Movie movie) {
        posterArea.setStyle("-fx-background-color: #ff6b6b; -fx-background-radius: 12;");
        
        HBox badgeArea = createAgeRatingBadge(movie);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        badgeArea.getChildren().add(spacer);

        VBox titleArea = new VBox();
        titleArea.setAlignment(Pos.CENTER);
        titleArea.setPadding(new Insets(15));

        Label movieTitle = new Label(movie.getTitle());
        movieTitle.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-text-alignment: center;");
        movieTitle.setWrapText(true);
        titleArea.getChildren().add(movieTitle);

        Region spacer2 = new Region();
        VBox.setVgrow(spacer2, Priority.ALWAYS);

        posterArea.getChildren().addAll(badgeArea, spacer2, titleArea);
    }

    private HBox createAgeRatingBadge(Movie movie) {
        HBox badgeArea = new HBox();
        badgeArea.setAlignment(Pos.TOP_LEFT);
        badgeArea.setPadding(new Insets(10));

        Label ageRating = new Label(getAgeRating(movie));
        ageRating.setStyle("-fx-background-color: rgba(0,0,0,0.8); -fx-text-fill: white; " +
                "-fx-padding: 4 8; -fx-background-radius: 10; -fx-font-size: 10px; -fx-font-weight: bold;");

        badgeArea.getChildren().add(ageRating);
        return badgeArea;
    }
    
    private ImageView loadMoviePoster(String movieTitle) {
        try {
            String posterFileName = getPosterFileName(movieTitle);
            InputStream imageStream = getClass().getResourceAsStream("/images/posters/" + posterFileName);
            
            if (imageStream != null) {
                Image image = new Image(imageStream);
                ImageView imageView = new ImageView(image);
                
                imageView.setFitWidth(250);
                imageView.setFitHeight(335);
                imageView.setPreserveRatio(false);
                imageView.setSmooth(true);
                
                Rectangle clip = new Rectangle(250, 335);
                clip.setArcWidth(30);
                clip.setArcHeight(30);
                imageView.setClip(clip);
                
                return imageView;
            }
        } catch (Exception e) {
            System.out.println("Error loading poster for: " + movieTitle + " - " + e.getMessage());
        }
        
        return null;
    }

    private String getPosterFileName(String movieTitle) {
        Map<String, String> posterMap = new HashMap<>();
        posterMap.put("Avengers: Endgame", "avengers_endgame.jpg");
        posterMap.put("Spider-Man: No Way Home", "spiderman_no_way_home.jpg");
        posterMap.put("The Batman", "the_batman.jpg");
        posterMap.put("Top Gun: Maverick", "top_gun_maverick.jpg");
        return posterMap.getOrDefault(movieTitle, "default_poster.jpg");
    }

    private VBox createMovieInfoArea(Movie movie) {
        VBox infoArea = new VBox(8);
        infoArea.setPadding(new Insets(15));
        infoArea.setAlignment(Pos.CENTER);

        Label titleLabel = new Label(movie.getTitle());
        titleLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #333;");
        titleLabel.setWrapText(true);

        Button buyTicketBtn = createStyledButton("BELI TIKET", "#4fc3f7");
        buyTicketBtn.setStyle(buyTicketBtn.getStyle() + " -fx-padding: 8 20; -fx-font-size: 11px;");
        buyTicketBtn.setOnAction(e -> showMovieDetail(movie));

        infoArea.getChildren().addAll(titleLabel, buyTicketBtn);
        return infoArea;
    }

    private void addHoverEffect(VBox card) {
        String normalStyle = "-fx-background-color: white; -fx-background-radius: 15; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 3); " +
                        "-fx-scale-x: 1.0; -fx-scale-y: 1.0;";
        String hoverStyle = "-fx-background-color: white; -fx-background-radius: 15; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 15, 0, 0, 8); " +
                        "-fx-scale-x: 1.03; -fx-scale-y: 1.03;";
        
        card.setOnMouseEntered(e -> card.setStyle(hoverStyle));
        card.setOnMouseExited(e -> card.setStyle(normalStyle));
    }

    private HBox createMovieHeader(Movie movie) {
        HBox movieHeader = new HBox(20);
        movieHeader.setAlignment(Pos.CENTER_LEFT);
        movieHeader.setPadding(new Insets(20));
        movieHeader.setStyle("-fx-background-color: white; -fx-background-radius: 20;");

        VBox miniPoster = createMiniPoster(movie);
        VBox movieDetails = createMovieDetails(movie);
        
        movieHeader.getChildren().addAll(miniPoster, movieDetails);
        return movieHeader;
    }

    private VBox createMiniPoster(Movie movie) {
        VBox miniPoster = new VBox();
        miniPoster.setPrefSize(120, 160);
        miniPoster.setAlignment(Pos.CENTER);
        
        ImageView miniPosterImage = loadMoviePoster(movie.getTitle());
        if (miniPosterImage != null) {
            miniPosterImage.setFitWidth(120);
            miniPosterImage.setFitHeight(160);
            
            Rectangle miniClip = new Rectangle(120, 160);
            miniClip.setArcWidth(25);
            miniClip.setArcHeight(25);
            miniPosterImage.setClip(miniClip);
            
            miniPoster.getChildren().add(miniPosterImage);
        } else {
            miniPoster.setStyle("-fx-background-color: #ff6b6b; -fx-background-radius: 12;");
            
            Label posterTitle = new Label(movie.getTitle());
            posterTitle.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px;");
            posterTitle.setWrapText(true);
            miniPoster.getChildren().add(posterTitle);
        }

        return miniPoster;
    }

    private VBox createMovieDetails(Movie movie) {
        VBox movieDetails = new VBox(15);
        Label titleLabel = new Label(movie.getTitle());
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #333;");
        
        movieDetails.getChildren().addAll(
            titleLabel,
            createInfoRow("Genre", movie.getGenre()),
            createInfoRow("Durasi", movie.getDuration() + " menit"),
            createInfoRow("Rating Usia", getAgeRating(movie))
        );
        
        return movieDetails;
    }

    private VBox createScheduleSection() {
        VBox scheduleSection = new VBox(15);
        scheduleSection.setPadding(new Insets(20));
        scheduleSection.setStyle("-fx-background-color: white; -fx-background-radius: 20;");
        
        Label scheduleTitle = createStyledLabel("📅 JADWAL", "18px", "#333", true);
        HBox scheduleHeader = new HBox(scheduleTitle);
        scheduleHeader.setAlignment(Pos.CENTER);

        HBox dateSelection = createDateSelection();
        VBox timeSection = createTimeSelection();
        HBox proceedBox = createProceedButton();

        scheduleSection.getChildren().addAll(scheduleHeader, dateSelection, timeSection, proceedBox);
        return scheduleSection;
    }

    private HBox createProceedButton() {
        Button proceedBtn = createStyledButton("LANJUT: PILIH KURSI", SUCCESS_COLOR);
        proceedBtn.setStyle(proceedBtn.getStyle() + " -fx-padding: 10 20; -fx-font-size: 12px;");
        proceedBtn.setOnAction(e -> {
            if (selectedDate != null && selectedSchedule != null) {
                showSeatSelection();
            } else {
                mainApp.showAlert("Error", "Pilih tanggal dan waktu terlebih dahulu");
            }
        });
        
        HBox proceedBox = new HBox(proceedBtn);
        proceedBox.setAlignment(Pos.CENTER);
        proceedBox.setPadding(new Insets(20, 0, 0, 0));
        
        return proceedBox;
    }

    private HBox createDateSelection() {
        HBox dateSelection = new HBox(15);
        dateSelection.setAlignment(Pos.CENTER);
        
        for (int i = 0; i < 5; i++) {
            LocalDate date = LocalDate.now().plusDays(i);
            VBox dateCard = createDateCard(date, i);
            dateSelection.getChildren().add(dateCard);
        }
        
        return dateSelection;
    }

    private VBox createDateCard(LocalDate date, int dayIndex) {
        VBox card = new VBox(5);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(12, 18, 12, 18));
        card.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 15; -fx-cursor: hand;");
        
        String dayText = getDayText(dayIndex, date);
        
        Label dayLabel = new Label(dayText);
        dayLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        
        Label dateLabel = new Label(date.format(DateTimeFormatter.ofPattern("dd MMM")));
        dateLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");
        
        card.getChildren().addAll(dayLabel, dateLabel);
        card.setOnMouseClicked(e -> selectDate(card, date));
        
        return card;
    }

    private String getDayText(int dayIndex, LocalDate date) {
        return switch (dayIndex) {
            case 0 -> "HARI INI";
            case 1 -> "BESOK";
            default -> date.format(DateTimeFormatter.ofPattern("EEE")).toUpperCase();
        };
    }

    private void selectDate(VBox selectedCard, LocalDate date) {
        HBox parent = (HBox) selectedCard.getParent();
        
        // Reset all date cards
        parent.getChildren().forEach(node -> {
            if (node instanceof VBox vbox) {
                resetDateCardStyle(vbox);
            }
        });
        
        highlightSelectedDateCard(selectedCard);
        
        selectedDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    private void resetDateCardStyle(VBox card) {
        card.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 15; -fx-cursor: hand;");
        card.getChildren().forEach(child -> {
            if (child instanceof Label label) {
                String style = label.getStyle();
                style = style.replace("white", "#666").replace("white", "#333");
                label.setStyle(style);
            }
        });
    }

    private void highlightSelectedDateCard(VBox card) {
        card.setStyle("-fx-background-color: #1976d2; -fx-background-radius: 15; -fx-cursor: hand;");
        card.getChildren().forEach(child -> {
            if (child instanceof Label label) {
                String style = label.getStyle();
                style = style.replace("#666", "white").replace("#333", "white");
                label.setStyle(style);
            }
        });
    }

    private VBox createTimeSelection() {
        VBox timeSection = new VBox(15);
        
        Label timeLabel = createStyledLabel("Pilih Waktu:", "16px", "#333", true);
        HBox timeHeader = new HBox(timeLabel);
        timeHeader.setAlignment(Pos.CENTER);

        HBox timeSlots = new HBox(10);
        timeSlots.setAlignment(Pos.CENTER);
        
        if (selectedMovie != null && selectedMovie.getSchedules() != null) {
            for (String schedule : selectedMovie.getSchedules()) {
                Button timeBtn = createTimeButton(schedule, timeSlots);
                timeSlots.getChildren().add(timeBtn);
            }
        }
        
        timeSection.getChildren().addAll(timeHeader, timeSlots);
        return timeSection;
    }

    private Button createTimeButton(String schedule, HBox timeSlots) {
        Button timeBtn = new Button(schedule);
        timeBtn.setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: #333; " +
                    "-fx-background-radius: 10; -fx-padding: 10 15;");
        timeBtn.setOnAction(e -> selectTime(timeSlots, timeBtn, schedule));
        return timeBtn;
    }

    private void selectTime(HBox timeSlots, Button selectedBtn, String schedule) {
        selectedSchedule = schedule;
        
        // Reset all buttons
        timeSlots.getChildren().forEach(node -> {
            if (node instanceof Button btn) {
                btn.setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: #333; " +
                        "-fx-background-radius: 10; -fx-padding: 10 15;");
            }
        });
        
        selectedBtn.setStyle("-fx-background-color: #1976d2; -fx-text-fill: white; " +
                        "-fx-background-radius: 10; -fx-padding: 10 15;");
    }

    private VBox createSeatSelectionBox() {
        VBox seatBox = new VBox(10);
        seatBox.setAlignment(Pos.CENTER);
        seatBox.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 10;");

        Label screenLabel = new Label("🎬 SCREEN 🎬");
        screenLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; " +
                        "-fx-background-color: #34495e; -fx-text-fill: white; " +
                        "-fx-padding: 10; -fx-background-radius: 5;");

        HBox legendBox = createSeatLegend();
        GridPane seatGrid = createSeatGrid();
        
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
        GridPane seatGrid = new GridPane();
        seatGrid.setAlignment(Pos.CENTER);
        seatGrid.setHgap(8);
        seatGrid.setVgap(8);
        seatGrid.setPadding(new Insets(15));
        seatGrid.setStyle("-fx-background-color: #ecf0f1; -fx-background-radius: 10;");

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

        return seatGrid;
    }

    private Button createSeatButton(String seatId, int seatNumber, Set<String> bookedSeats) {
    Button seatBtn = new Button(String.valueOf(seatNumber));
    seatBtn.setUserData(seatId);
    seatBtn.setPrefSize(35, 35);
    seatBtn.setMinSize(35, 35);
    seatBtn.setMaxSize(35, 35);

    if (bookedSeats.contains(seatId)) {
        seatBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; " +
                    "-fx-font-weight: bold; -fx-background-radius: 5; " +
                    "-fx-border-color: #c0392b; -fx-border-width: 2; -fx-border-radius: 5;");
        seatBtn.setDisable(true);
        seatBtn.setText("X");
    } else if (selectedSeats.contains(seatId)) {
        seatBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; " +
                    "-fx-font-weight: bold; -fx-background-radius: 5; " +
                    "-fx-border-color: #2980b9; -fx-border-width: 2; -fx-border-radius: 5;");
        seatBtn.setOnAction(e -> toggleSeat(seatBtn, seatId, bookedSeats));
    } else {
        seatBtn.setStyle("-fx-background-color: #ecf0f1; -fx-text-fill: #2c3e50; " +
                    "-fx-font-weight: bold; -fx-background-radius: 5; " +
                    "-fx-border-color: #bdc3c7; -fx-border-width: 2; -fx-border-radius: 5;");
        seatBtn.setOnAction(e -> toggleSeat(seatBtn, seatId, bookedSeats));
    }

    return seatBtn;
}

private void toggleSeat(Button seatBtn, String seatId, Set<String> bookedSeats) {
    if (bookedSeats.contains(seatId)) {
        return;
    }
    
    if (selectedSeats.contains(seatId)) {
        selectedSeats.remove(seatId);
        seatBtn.setStyle("-fx-background-color: #ecf0f1; -fx-text-fill: #2c3e50; " +
                    "-fx-font-weight: bold; -fx-background-radius: 5; " +
                    "-fx-border-color: #bdc3c7; -fx-border-width: 2; -fx-border-radius: 5;");
    } else {
        selectedSeats.add(seatId);
        seatBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; " +
                    "-fx-font-weight: bold; -fx-background-radius: 5; " +
                    "-fx-border-color: #2980b9; -fx-border-width: 2; -fx-border-radius: 5;");
    }
    
    updateSelectedSeatsDisplay();
}

    private void updateSelectedSeatsDisplay() {
        System.out.println("Selected seats: " + 
            (selectedSeats.isEmpty() ? "None" : String.join(", ", selectedSeats)));
    }

    private HBox createSeatNavigationButtons() {
        Button nextBtn = createStyledButton("NEXT: SELECT SNACKS", "#3498db");
        nextBtn.setPrefWidth(180);
        nextBtn.setOnAction(e -> {
            if (!selectedSeats.isEmpty()) {
                if (snackController != null) {
                    snackController.showSnackSelection();
                } else {
                    mainApp.showAlert("Error", "SnackController not set!");
                }
            } else {
                mainApp.showAlert("Error", "Please select at least one seat");
            }
        });

        Button backBtn = createStyledButton("← BACK", "#95a5a6");
        backBtn.setPrefWidth(180);
        backBtn.setOnAction(e -> showMovieDetail(selectedMovie));

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(backBtn, nextBtn);
        
        return buttonBox;
    }

    private HBox createInfoRow(String label, String value) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        
        Label labelLbl = new Label(label);
        labelLbl.setStyle("-fx-text-fill: #666; -fx-font-size: 14px;");
        labelLbl.setPrefWidth(100);
        
        Label valueLbl = new Label(value);
        valueLbl.setStyle("-fx-text-fill: #333; -fx-font-weight: bold; -fx-font-size: 14px;");
        
        row.getChildren().addAll(labelLbl, valueLbl);
        return row;
    }

    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle(String.format("-fx-background-color: %s; -fx-text-fill: white; " +
                                    "-fx-font-weight: bold; -fx-background-radius: 25;", color));
        return button;
    }

    private Label createStyledLabel(String text, String fontSize, String color, boolean bold) {
        Label label = new Label(text);
        String style = String.format("-fx-font-size: %s; -fx-text-fill: %s;", fontSize, color);
        if (bold) style += " -fx-font-weight: bold;";
        label.setStyle(style);
        return label;
    }

    private String getAgeRating(Movie movie) {
        String genre = movie.getGenre().toLowerCase();
        if (genre.contains("horror")) return "17+";
        if (genre.contains("action")) return "13+";
        return "SU";
    }

    private void setScene(VBox root, int width, int height) {
        Scene scene = new Scene(root, width, height);
        primaryStage.setScene(scene);
    }
}