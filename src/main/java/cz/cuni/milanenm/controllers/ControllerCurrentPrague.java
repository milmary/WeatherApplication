package cz.cuni.milanenm.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.text.Text;
import javafx.util.Duration;

import cz.cuni.milanenm.manage.OpenScene;
import cz.cuni.milanenm.manage.ReceiveDateTime;
import cz.cuni.milanenm.fx.WeatherRefreshService;
import cz.cuni.milanenm.fx.WeatherService;
import cz.cuni.milanenm.providers.ProviderRegistry;
import cz.cuni.milanenm.spi.WeatherApi;

/**
 * Controller for the "Current Weather / Prague" screen.
 * Fetches current weather for Prague and auto-refreshes periodically.
 */
public class ControllerCurrentPrague {

    @FXML private Text cityName;
    @FXML private Text dataTime;
    @FXML private Text weatherInfo;
    @FXML private Button toReturn;
    @FXML private ProgressIndicator spinner;

    private final WeatherService service = new WeatherService();
    private final WeatherRefreshService refresher = new WeatherRefreshService();
    private final WeatherApi api = ProviderRegistry.getApi();

    /**
     * Initializes UI bindings, triggers the first fetch, and configures auto-refresh.
     */
    @FXML
    void initialize() {
        cityName.setText("Prague");
        dataTime.setText(ReceiveDateTime.getCurrentDate() + ", " + ReceiveDateTime.getCurrentTime());

        spinner.visibleProperty().bind(service.runningProperty());
        spinner.managedProperty().bind(spinner.visibleProperty());
        toReturn.disableProperty().bind(service.runningProperty());

        service.setOnSucceeded(e -> { e.consume(); weatherInfo.setText(service.getValue()); });
        service.setOnFailed(e -> { e.consume(); weatherInfo.setText("Weather data not available."); });
        service.setSupplier(api::currentPrague);
        service.start();

        refresher.setPeriod(Duration.minutes(10));
        refresher.setOnSucceeded(e -> { e.consume(); weatherInfo.setText(refresher.getValue()); });
        refresher.setOnFailed(e -> { e.consume(); /* ignore */ });
        refresher.setSupplier(api::currentPrague);
        refresher.start();

        toReturn.setOnAction(e -> {
            e.consume();
            service.cancel();
            refresher.cancel();
            toReturn.getScene().getWindow().hide();
            OpenScene.openScene("/fxml/weather_start.fxml");
        });
    }
}
