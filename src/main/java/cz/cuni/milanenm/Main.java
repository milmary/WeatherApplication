package cz.cuni.milanenm;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;

/**
 * Application entry point.
 * Loads the start screen at the FXML's preferred size and keeps the window non-resizable.
 */
public class Main extends Application {

    /**
     * Initializes primary stage sized to the root FXML.
     *
     * @param stage primary JavaFX stage
     * @throws IOException if FXML cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/fxml/weather_start.fxml"));
        Scene scene = new Scene(loader.load());      // no explicit width/height

        stage.setTitle("Weather");
        try (InputStream iconStream = getClass().getResourceAsStream("/img/icon.png")) {
            if (iconStream != null) {
                stage.getIcons().add(new Image(iconStream));
            }
        }

        stage.setScene(scene);
        stage.sizeToScene();       // match FXML preferred size
        stage.setResizable(false); // prevent manual/window-system resizing
        stage.show();
    }

    /**
     * Main entry point.
     *
     * @param args CLI arguments
     */
    public static void main(String[] args) {
        launch();
    }
}
