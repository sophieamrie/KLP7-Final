package finalLab;

import javafx.application.Application;
import javafx.stage.Stage;
import finalLab.Controller.AuthController;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("🎬 Cinema Booking System");
        primaryStage.setResizable(false);

        AuthController authController = new AuthController(primaryStage);
        authController.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}