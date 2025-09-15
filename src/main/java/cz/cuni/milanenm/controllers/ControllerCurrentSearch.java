package cz.cuni.milanenm.controllers;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;
import javafx.util.Duration;

import cz.cuni.milanenm.db.HistoryDao;
import cz.cuni.milanenm.manage.OpenScene;
import cz.cuni.milanenm.manage.ReceiveDateTime;
import cz.cuni.milanenm.fx.WeatherService;
import cz.cuni.milanenm.providers.ProviderRegistry;
import cz.cuni.milanenm.spi.WeatherApi;

import static cz.cuni.milanenm.manage.ConstParam.ENTER_CITY;
import static cz.cuni.milanenm.manage.ConstParam.HELP;
import static cz.cuni.milanenm.manage.ConstParam.NOT_FOUND;

/**
 * Controller for searching current weather by city.
 * Provides a simple history list and manual search trigger.
 */
public class ControllerCurrentSearch {

    private String citySet;

    @FXML private TextField cityName;
    @FXML private Text exceptionField;
    @FXML private Text weatherInfo;

    @FXML private Button getWeather;
    @FXML private Button clear;
    @FXML private Button toReturn;
    @FXML private Button help;
    @FXML private Button clearHistory;

    @FXML private ListView<String> historyList;
    @FXML private ProgressIndicator spinner;

    private final WeatherService service = new WeatherService();
    private WeatherApi api;

    /**
     * Initializes bindings, handlers, and loads recent history.
     */
    @FXML
    void initialize() {
        api = ProviderRegistry.getApi();

        // Keep the weather text within the left column (avoid overlap with history)
        weatherInfo.setWrappingWidth(248);

        spinner.visibleProperty().bind(service.runningProperty());
        spinner.managedProperty().bind(spinner.visibleProperty());
        getWeather.disableProperty().bind(service.runningProperty());

        getWeather.setOnAction(e -> { e.consume(); setWeatherInfo(); });
        clear.setOnAction(e -> { e.consume(); reset(); });
        help.setOnAction(e -> { e.consume(); showHelp(); });
        toReturn.setOnAction(e -> {
            e.consume();
            service.cancel();
            toReturn.getScene().getWindow().hide();
            OpenScene.openScene("/fxml/weather_start.fxml");
        });

        cityName.setOnKeyPressed(e -> { if (e.getCode() == KeyCode.ENTER) setWeatherInfo(); });

        // Select in history => copy to input (no auto search)
        historyList.setOnMouseClicked(ev -> {
            ev.consume();
            String sel = historyList.getSelectionModel().getSelectedItem();
            if (sel != null) cityName.setText(sel);
        });
        historyList.setOnKeyPressed(ev -> {
            if (ev.getCode() == KeyCode.ENTER) {
                String sel = historyList.getSelectionModel().getSelectedItem();
                if (sel != null) cityName.setText(sel);
            }
        });

        clearHistory.setOnAction(ev -> {
            ev.consume();
            try {
                HistoryDao.clearAll();
            } catch (Exception ignored) {}
            refreshHistory();
            historyList.getItems().clear();
            historyList.getSelectionModel().clearSelection();
        });

        refreshHistory();
    }

    /**
     * Re-populates the history list from the database.
     */
    private void refreshHistory() {
        var items = FXCollections.observableArrayList(HistoryDao.lastCities(15));
        historyList.setItems(items);
    }

    /**
     * Validates input, invokes the weather service, updates UI, and writes history.
     */
    private void setWeatherInfo() {
        if (cityName.getText().isEmpty()) {
            showErrors(ENTER_CITY);
            return;
        }
        exceptionField.setText("");
        citySet = cityName.getText().trim();

        service.cancel();
        service.reset();
        service.setSupplier(() -> api.current(citySet));

        service.setOnSucceeded(ev -> {
            ev.consume();
            final String data = service.getValue();
            weatherInfo.setText("\nWeather at " + ReceiveDateTime.getCurrentTime() + ", " +
                    ReceiveDateTime.getCurrentDate() + ":\n" + data);
            try {
                if (!citySet.isBlank()) HistoryDao.insertCity(citySet);
            } catch (Exception ignored) {}
            refreshHistory();
            historyList.getSelectionModel().clearSelection();
        });

        service.setOnFailed(ev -> {
            ev.consume();
            showErrors(NOT_FOUND);
        });

        service.start();
    }

    /**
     * Shows a simple help dialog.
     */
    private void showHelp() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Help");
        alert.setHeaderText(null);
        alert.setContentText(HELP);
        alert.showAndWait();
    }

    /**
     * Displays a fading error message.
     *
     * @param message error text to display
     */
    private void showErrors(String message) {
        exceptionField.setText(message);
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.35), exceptionField);
        fadeIn.setToValue(1);
        fadeIn.setFromValue(0);
        fadeIn.play();
        fadeIn.setOnFinished(event -> {
            event.consume();
            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(event2 -> {
                event2.consume();
                FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.8), exceptionField);
                fadeOut.setToValue(0);
                fadeOut.setFromValue(1);
                fadeOut.play();
            });
            pause.play();
        });
    }

    /**
     * Clears input, messages, and cancels ongoing work.
     */
    private void reset() {
        service.cancel();
        cityName.clear();
        weatherInfo.setText("");
        exceptionField.setText("");
        historyList.getSelectionModel().clearSelection();
    }
}
