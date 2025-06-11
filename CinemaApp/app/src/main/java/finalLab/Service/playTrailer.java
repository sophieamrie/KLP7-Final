package finalLab.Service;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.geometry.Pos;

public class playTrailer {
    public static void show(String trailerPath) {
        Media media = new Media(trailerPath);
        MediaPlayer player = new MediaPlayer(media);
        MediaView mediaView = new MediaView(player);
        mediaView.setFitWidth(640);
        mediaView.setFitHeight(360);

        VBox layout = new VBox(mediaView);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout, 700, 400);

        Stage popup = new Stage();
        popup.setTitle("Trailer");
        popup.setScene(scene);
        popup.show();

        player.play();
    }
}