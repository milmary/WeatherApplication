package cz.cuni.milanenm.controllers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.text.DateFormat;
import java.util.Calendar;

import cz.cuni.milanenm.manage.OpenScene;
import cz.cuni.milanenm.manage.ReceiveDateTime;

/**
 * Controller for the start scene.
 * Displays current date and a running clock, and navigates to other screens.
 */
public class ControllerStart {

    @FXML private Text currentDate;
    @FXML private Text currentTime;

    @FXML private Button getWorld;
    @FXML private Button getPrague;
    @FXML private Button getForecast;

    /**
     * Initializes the start screen, sets date/clock and button actions.
     */
    @FXML
    void initialize() {
        currentDate.setText(ReceiveDateTime.getCurrentDate());
        startClock();

        getPrague.setOnAction(e -> {
            e.consume();
            getPrague.getScene().getWindow().hide();
            OpenScene.openScene("/fxml/weather_current_prague.fxml");
        });

        getWorld.setOnAction(e -> {
            e.consume();
            getWorld.getScene().getWindow().hide();
            OpenScene.openScene("/fxml/weather_current_search.fxml");
        });

        getForecast.setOnAction(e -> {
            e.consume();
            getForecast.getScene().getWindow().hide();
            OpenScene.openScene("/fxml/weather_forecast.fxml");
        });
    }

    /**
     * Starts a timeline that updates the current time every second.
     */
    private void startClock() {
        final DateFormat format = DateFormat.getTimeInstance();
        final Timeline timeline = new Timeline(new KeyFrame(
                Duration.seconds(1),
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        final Calendar cal = Calendar.getInstance();
                        currentTime.setText(format.format(cal.getTime()));
                    }
                }
        ));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }
}
