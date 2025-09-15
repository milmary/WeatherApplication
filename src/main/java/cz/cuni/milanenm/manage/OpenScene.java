package cz.cuni.milanenm.manage;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;

/**
 * Utility for opening additional windows sized to their FXML roots.
 */
public final class OpenScene {

    private OpenScene() {}

    /**
     * Loads an FXML and opens it in a new, non-resizable stage.
     *
     * @param window classpath path to the FXML (e.g. "/fxml/weather_current_search.fxml")
     */
    public static void openScene(String window) {
        FXMLLoader loader = new FXMLLoader(OpenScene.class.getResource(window));
        Parent root;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load FXML: " + window, e);
        }

        Stage stage = new Stage();
        stage.setTitle("Weather");

        try (InputStream iconStream = OpenScene.class.getResourceAsStream("/img/icon.png")) {
            if (iconStream != null) {
                stage.getIcons().add(new Image(iconStream));
            }
        } catch (Exception ignored) { }

        stage.setScene(new Scene(root));
        stage.sizeToScene();       // exact FXML size
        stage.setResizable(false); // avoid any stretching/letterboxing from window resizing
        stage.show();
    }
}
