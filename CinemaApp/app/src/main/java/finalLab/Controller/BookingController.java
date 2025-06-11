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
import finalLab.Service.playTrailer;
import java.net.URL;

public class BookingController {
    private static final String PRIMARY_COLOR = "#2c3e50";
    private static final String SECONDARY_COLOR = "#34495e";
    private static final String SUCCESS_COLOR = "#4caf50";
    private static final String NEUTRAL_COLOR = "#95a5a6";
    
    private final Main mainApp;
    private final Stage primaryStage;
    private final User currentUser;
    private final MovieService movieService;
    private SnackController snackController;
    private SeatBookingController seatBookingController;

    private Movie selectedMovie;
    private String selectedDate;
    private String selectedSchedule;
    private Set<String> selectedSeats = new HashSet<>();
    private List<SnackItem> selectedSnacks = new ArrayList<>();
    
    private String currentTicketId;
    private String currentTicketText;
    private String paymentMethod;
    private boolean paymentCompleted;

    public BookingController(Main mainApp, User currentUser) {
        this.mainApp = Objects.requireNonNull(mainApp, "Main app cannot be null");
        this.primaryStage = mainApp.getPrimaryStage();
        this.currentUser = Objects.requireNonNull(currentUser, "Current user cannot be null");
        this.movieService = mainApp.getMovieService();
        
        // Initialize SeatBookingController
        this.seatBookingController = new SeatBookingController(mainApp, currentUser, this);
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
        // Delegate to SeatBookingController
        seatBookingController.showSeatSelection(selectedMovie, selectedDate, selectedSchedule);
    }

    // Getters
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

    // Setters
    public void setSnackController(SnackController snackController) {
        this.snackController = snackController;
        // Also set it for SeatBookingController
        if (seatBookingController != null) {
            seatBookingController.setSnackController(snackController);
        }
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

    // Public method for SeatBookingController to access
    public void confirmSeatBooking() {
        // This method can be called by SeatBookingController when seats are confirmed
        // Implementation moved to SeatBookingController but can be called from here if needed
        if (seatBookingController != null) {
            seatBookingController.confirmSeatBooking();
        }
    }

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

            // Tombol trailer di bagian bawah poster (overlay)
            String trailerPath = getTrailerPath(movie.getTitle());
            if (trailerPath != null) {
                Button trailerBtn = new Button("▶ Trailer");
                trailerBtn.setStyle("-fx-background-color: rgba(0,0,0,0.8); -fx-text-fill: white; " +
                                  "-fx-font-weight: bold; -fx-background-radius: 15; " +
                                  "-fx-padding: 6 12; -fx-font-size: 10px; -fx-cursor: hand;");
                
                trailerBtn.setOnMouseEntered(e -> 
                    trailerBtn.setStyle("-fx-background-color: rgba(0,0,0,0.9); -fx-text-fill: white; " +
                                      "-fx-font-weight: bold; -fx-background-radius: 15; " +
                                      "-fx-padding: 6 12; -fx-font-size: 10px; -fx-cursor: hand;"));
                
                trailerBtn.setOnMouseExited(e -> 
                    trailerBtn.setStyle("-fx-background-color: rgba(0,0,0,0.8); -fx-text-fill: white; " +
                                      "-fx-font-weight: bold; -fx-background-radius: 15; " +
                                      "-fx-padding: 6 12; -fx-font-size: 10px; -fx-cursor: hand;"));
                
                trailerBtn.setOnAction(e -> {
                    try {
                        System.out.println("Playing trailer: " + trailerPath);
                        finalLab.Service.playTrailer.show(trailerPath);
                    } catch (Exception ex) {
                        System.err.println("Error playing trailer: " + ex.getMessage());
                        ex.printStackTrace();
                        mainApp.showAlert("Error", "Tidak dapat memutar trailer. File mungkin tidak ditemukan.");
                    }
                });
                
                VBox trailerContainer = new VBox(trailerBtn);
                trailerContainer.setAlignment(Pos.CENTER);
                trailerContainer.setPadding(new Insets(0, 0, 10, 0));
                StackPane.setAlignment(trailerContainer, Pos.BOTTOM_CENTER);
                posterStack.getChildren().add(trailerContainer);
            }

            posterArea.getChildren().add(posterStack);
        } else {
            createFallbackPoster(posterArea, movie);
        }

        return posterArea;
    }

    private void createFallbackPoster(VBox posterArea, Movie movie) {
        posterArea.setStyle("-fx-background-color: #ff6b6b; -fx-background-radius: 12;");

        // BAGIAN: Badge Usia
        HBox badgeArea = createAgeRatingBadge(movie);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // BAGIAN: Judul Film
        VBox titleArea = new VBox();
        titleArea.setAlignment(Pos.CENTER);
        titleArea.setPadding(new Insets(15));
        
        Label movieTitle = new Label(movie.getTitle());
        movieTitle.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-text-alignment: center;");
        movieTitle.setWrapText(true);
        titleArea.getChildren().add(movieTitle);

        String trailerPath = getTrailerPath(movie.getTitle());
        VBox trailerArea = new VBox();
        trailerArea.setAlignment(Pos.CENTER);
        trailerArea.setPadding(new Insets(0, 10, 15, 10));
        
        if (trailerPath != null) {
            Button trailerBtn = new Button("Lihat Trailer");
            trailerBtn.setStyle("-fx-background-color: white; -fx-text-fill: #ff6b6b; " +
                              "-fx-font-weight: bold; -fx-background-radius: 15; " +
                              "-fx-padding: 8 16; -fx-font-size: 11px; -fx-cursor: hand;");
            
            // Hover effect untuk tombol trailer
            trailerBtn.setOnMouseEntered(e -> 
                trailerBtn.setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: #ff6b6b; " +
                                  "-fx-font-weight: bold; -fx-background-radius: 15; " +
                                  "-fx-padding: 8 16; -fx-font-size: 11px; -fx-cursor: hand;"));
            
            trailerBtn.setOnMouseExited(e -> 
                trailerBtn.setStyle("-fx-background-color: white; -fx-text-fill: #ff6b6b; " +
                                  "-fx-font-weight: bold; -fx-background-radius: 15; " +
                                  "-fx-padding: 8 16; -fx-font-size: 11px; -fx-cursor: hand;"));
            
            trailerBtn.setOnAction(e -> {
                try {
                    System.out.println("Playing trailer: " + trailerPath);
                    finalLab.Service.playTrailer.show(trailerPath);
                } catch (Exception ex) {
                    System.err.println("Error playing trailer: " + ex.getMessage());
                    ex.printStackTrace();
                    mainApp.showAlert("Error", "Tidak dapat memutar trailer. File mungkin tidak ditemukan.");
                }
            });
            
            trailerArea.getChildren().add(trailerBtn);
        } else {
            // Jika tidak ada trailer, tampilkan label info
            Label noTrailerLabel = new Label("Trailer tidak tersedia");
            noTrailerLabel.setStyle("-fx-text-fill: rgba(255,255,255,0.7); -fx-font-size: 10px;");
            trailerArea.getChildren().add(noTrailerLabel);
        }
        posterArea.getChildren().addAll(badgeArea, spacer, titleArea, trailerArea);
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
        posterMap.put("Kimetsu no Yaiba: Infinity Castle", "kimetsu_no_yaiba.jpg");
        posterMap.put("Avengers: Endgame", "avengers_endgame.jpg");
        posterMap.put("20th Century Girl", "century_girls.jpg");
        posterMap.put("Spider-Man: No Way Home", "spiderman_no_way_home.jpg");
        posterMap.put("Pengabdi Setan 2", "pengabdi_setan.jpg");
        posterMap.put("The Batman", "the_batman.jpg");
        posterMap.put("Pengepungan di Bukit Duri", "bukit_duri.jpg");
        posterMap.put("Top Gun: Maverick", "top_gun_maverick.jpg");
        return posterMap.getOrDefault(movieTitle, "default_poster.jpg");
    }

    private String getTrailerPath(String movieTitle) {
        Map<String, String> trailerMap = new HashMap<>();
        trailerMap.put("Kimetsu no Yaiba: Infinity Castle", "/trailers/kimetsu_trailer.mp4");
        trailerMap.put("Avengers: Endgame", "/trailers/avengers_trailer.mp4");
        trailerMap.put("20th Century Girl", "/trailers/century_trailer.mp4");
        trailerMap.put("Spider-Man: No Way Home", "/trailers/spiderman_trailer.mp4");
        trailerMap.put("Pengabdi Setan 2", "/trailers/pengabdi_trailer.mp4");
        trailerMap.put("The Batman", "/trailers/batman_trailer.mp4");
        trailerMap.put("Pengepungan di Bukit Duri", "/trailers/bukitDuri_trailer.mp4");
        trailerMap.put("Top Gun: Maverick", "/trailers/topGun_trailer.mp4");
        String resourcePath = trailerMap.get(movieTitle);
        
        if (resourcePath != null) {
            System.out.println("Found trailer mapping for: " + movieTitle + " -> " + resourcePath);
            
            try {
                // Coba mendapatkan URL dari resource
                URL resourceUrl = getClass().getResource(resourcePath);
                if (resourceUrl != null) {
                    String validUri = resourceUrl.toExternalForm();
                    System.out.println("✓ Trailer file found with valid URI: " + validUri);
                    return validUri;
                } else {
                    System.err.println("✗ Trailer file not found in resources: " + resourcePath);
                    
                    // Coba alternatif path tanpa leading slash
                    String alternativePath = resourcePath.substring(1); // Remove leading "/"
                    URL altUrl = getClass().getResource("/" + alternativePath);
                    if (altUrl != null) {
                        String validUri = altUrl.toExternalForm();
                        System.out.println("✓ Found trailer with alternative path: " + validUri);
                        return validUri;
                    }
                }
            } catch (Exception e) {
                System.err.println("Error processing trailer path: " + e.getMessage());
            }
        } else {
            System.out.println("No trailer mapping found for: " + movieTitle);
        }
        
        return null;
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