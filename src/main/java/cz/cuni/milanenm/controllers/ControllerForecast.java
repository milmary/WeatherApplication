package cz.cuni.milanenm.controllers;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.application.Platform;
import javafx.scene.control.Label;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.cuni.milanenm.manage.OpenScene;
import cz.cuni.milanenm.fx.WeatherService;
import cz.cuni.milanenm.providers.ProviderRegistry;
import cz.cuni.milanenm.spi.WeatherApi;

import static cz.cuni.milanenm.manage.ConstParam.ENTER_CITY;
import static cz.cuni.milanenm.manage.ConstParam.HELP;
import static cz.cuni.milanenm.manage.ConstParam.NOT_FOUND;

/**
 * Controller for the 5-day forecast screen with a compact temperature chart.
 * Plots temperatures at 09:00 and 18:00 for up to 5 days and shows raw forecast text.
 */
public class ControllerForecast {

    private String citySet;

    @FXML public Button toReturn;
    @FXML public Button help;
    @FXML public Button getWeather;
    @FXML public Button clear;

    @FXML public TextField cityName;
    @FXML public Text exceptionField;
    @FXML public Text forecast;
    @FXML public ProgressIndicator spinner;

    @FXML private LineChart<String, Number> forecastChart;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;

    private final XYChart.Series<String, Number> series09 = new XYChart.Series<>();
    private final XYChart.Series<String, Number> series18 = new XYChart.Series<>();

    private final WeatherService service = new WeatherService();
    private final WeatherApi api = ProviderRegistry.getApi();

    private static final DateTimeFormatter DAY_FMT = DateTimeFormatter.ofPattern("d.M");

    /**
     * Initializes bindings, configures the chart, and wires UI actions.
     */
    @FXML
    void initialize() {
        spinner.visibleProperty().bind(service.runningProperty());
        spinner.managedProperty().bind(spinner.visibleProperty());
        getWeather.disableProperty().bind(service.runningProperty());

        // Compact chart: legend inside on the right; no title; fixed Y range
        forecastChart.setAnimated(false);
        forecastChart.setLegendVisible(true);
        forecastChart.setLegendSide(Side.RIGHT);
        forecastChart.setCreateSymbols(true);
        forecastChart.setTitle(null);

        series09.setName("09:00");
        series18.setName("18:00");
        forecastChart.getData().setAll(series09, series18);

        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(-40);
        yAxis.setUpperBound(40);
        yAxis.setTickUnit(10);
        yAxis.setLabel("°C");

        xAxis.setLabel(null);

        Platform.runLater(() -> {
            Label yLab = (Label) yAxis.lookup(".axis-label");
            if (yLab != null) yLab.setStyle("-fx-text-fill: white;");

            // If you ever show an X-axis title too, make it white as well:
            Label xLab = (Label) xAxis.lookup(".axis-label");
            if (xLab != null) xLab.setStyle("-fx-text-fill: white;");
        });

        service.setOnSucceeded(e -> {
            e.consume();
            String text = service.getValue();
            forecast.setText(text);

            try {
                int count = updateChartFromText(text);
                if (count == 0) {
                    series09.getData().clear();
                    series18.getData().clear();
                    series09.getData().add(new XYChart.Data<>("–", 0));
                }
                forecastChart.requestLayout();
            } catch (Exception ex) {
                series09.getData().clear();
                series18.getData().clear();
            }
        });

        service.setOnFailed(e -> { e.consume(); showErrors(NOT_FOUND); });

        cityName.setOnKeyPressed(e -> { if (e.getCode() == KeyCode.ENTER) setWeatherInfo(); });
        help.setOnAction(e -> { e.consume(); showHelp(); });
        getWeather.setOnAction(e -> { e.consume(); setWeatherInfo(); });

        toReturn.setOnAction(e -> {
            e.consume();
            service.cancel();
            toReturn.getScene().getWindow().hide();
            OpenScene.openScene("/fxml/weather_start.fxml");
        });

        clear.setOnAction(e -> { e.consume(); reset(); });
    }

    /**
     * Opens a modal with help information.
     */
    private void showHelp() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Help");
        alert.setHeaderText(null);
        alert.setContentText(HELP);
        alert.showAndWait();
    }

    /**
     * Shows an animated error message.
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
     * Triggers the forecast fetch for the entered city and updates the chart and text.
     */
    private void setWeatherInfo() {
        if (cityName.getText().isEmpty()) {
            showErrors(ENTER_CITY);
            return;
        }
        try {
            exceptionField.setText("");
            citySet = cityName.getText().trim();

            service.cancel();
            service.reset();
            service.setSupplier(() -> api.forecast(citySet));
            service.start();
        } catch (Exception e) {
            showErrors(NOT_FOUND);
        }
    }

    /**
     * Clears input, text and chart; cancels any running task.
     */
    private void reset() {
        cityName.setText("");
        forecast.setText("");
        series09.getData().clear();
        series18.getData().clear();
        service.cancel();
    }

    // ------------------------ Parsing helpers ------------------------

    // Tolerant line matcher (ISO yyyy-MM-dd or EU dd.MM.yyyy), hour can be "9" or "09".
    private static final Pattern ANY_LINE =
        Pattern.compile(
            "(?:(?<iso>(?<Y>\\d{4})-(?<M>\\d{2})-(?<D>\\d{2}))|(?<eu>(?<d>\\d{1,2})[./](?<m>\\d{1,2})[./](?<y>\\d{4})))" +
            "\\s*[ T,]*(?<H>\\d{1,2}):(?<Min>\\d{2})(?::\\d{2})?.*?(?<T>-?\\d+(?:[.,]\\d+)?)\\s*(?:°?\\s*[cC])?",
            Pattern.CASE_INSENSITIVE
        );

    // OpenWeatherMap-like JSON: prefer main.temp, fallback to any "temp".
    private static final Pattern OWM_JSON_MAIN_TEMP =
        Pattern.compile("\"dt_txt\"\\s*:\\s*\"([^\"]+)\".*?\"main\"\\s*:\\s*\\{[^}]*?\"temp\"\\s*:\\s*(-?\\d+(?:\\.\\d+)?)",
                        Pattern.DOTALL);
    private static final Pattern OWM_JSON_ANY_TEMP =
        Pattern.compile("\"dt_txt\"\\s*:\\s*\"([^\"]+)\".*?\"temp\"\\s*:\\s*(-?\\d+(?:\\.\\d+)?)",
                        Pattern.DOTALL);

    /**
     * Fills the two series from raw provider text or JSON.
     *
     * @param text raw provider output
     * @return number of data points added
     */
    private int updateChartFromText(String text) {
        series09.getData().clear();
        series18.getData().clear();
        if (text == null || text.isBlank()) return 0;

        Map<LocalDate, DayTemps> byDay = new TreeMap<>();

        // 1) Text lines
        for (String line : text.split("\\R+")) {
            parseAnyLine(line.trim(), byDay);
        }

        // 2) JSON (main.temp or any temp)
        if (byDay.isEmpty()) {
            int found = parseOwmJson(text, OWM_JSON_MAIN_TEMP, byDay);
            if (found == 0) parseOwmJson(text, OWM_JSON_ANY_TEMP, byDay);
        }

        // 3) Limit to 5 days and add to chart (same x label for both series)
        int points = 0;
        List<Map.Entry<LocalDate, DayTemps>> days = new ArrayList<>(byDay.entrySet());
        if (days.size() > 5) days = days.subList(0, 5);

        for (var e : days) {
            LocalDate d = e.getKey();
            DayTemps t = e.getValue();
            String label = d.format(DAY_FMT);
            if (t.temp09 != null) { series09.getData().add(new XYChart.Data<>(label, t.temp09)); points++; }
            if (t.temp18 != null) { series18.getData().add(new XYChart.Data<>(label, t.temp18)); points++; }
        }
        return points;
    }

    private void parseAnyLine(String line, Map<LocalDate, DayTemps> byDay) {
        if (line.isEmpty()) return;
        Matcher m = ANY_LINE.matcher(line);
        if (!m.find()) return;

        Integer hour = safeInt(m.group("H"));
        Integer min  = safeInt(m.group("Min"));
        Double  temp = safeDouble(m.group("T"));
        if (hour == null || min == null || temp == null) return;
        if (!(hour == 9 || hour == 18)) return;

        LocalDate date;
        if (m.group("iso") != null) {
            int Y = safeInt(m.group("Y"));
            int M = safeInt(m.group("M"));
            int D = safeInt(m.group("D"));
            date = LocalDate.of(Y, M, D);
        } else if (m.group("eu") != null) {
            int d = safeInt(m.group("d"));
            int mm = safeInt(m.group("m"));
            int y = safeInt(m.group("y"));
            date = LocalDate.of(y, mm, d);
        } else {
            return;
        }

        DayTemps day = byDay.computeIfAbsent(date, k -> new DayTemps());
        if (hour == 9 && day.temp09 == null) day.temp09 = temp;
        if (hour == 18 && day.temp18 == null) day.temp18 = temp;
    }

    private int parseOwmJson(String text, Pattern p, Map<LocalDate, DayTemps> byDay) {
        int added = 0;
        Matcher mj = p.matcher(text);
        while (mj.find()) {
            String dtTxt = mj.group(1); // yyyy-MM-dd HH:mm:ss
            double temp = Double.parseDouble(mj.group(2));
            LocalDateTime dt = tryParseDtTxt(dtTxt);
            if (dt == null) continue;
            int h = dt.getHour();
            if (h == 9 || h == 18) {
                DayTemps day = byDay.computeIfAbsent(dt.toLocalDate(), k -> new DayTemps());
                if (h == 9 && day.temp09 == null) { day.temp09 = temp; added++; }
                if (h == 18 && day.temp18 == null) { day.temp18 = temp; added++; }
            }
        }
        return added;
    }

    private static LocalDateTime tryParseDtTxt(String dt) {
        try {
            return LocalDateTime.parse(dt, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (Exception ignored) { }
        try {
            return LocalDateTime.parse(dt, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception ignored) { }
        return null;
    }

    private static Integer safeInt(String s) {
        try { return Integer.parseInt(s); } catch (Exception e) { return null; }
    }
    private static Double safeDouble(String s) {
        try { return Double.parseDouble(s.replace(',', '.')); } catch (Exception e) { return null; }
    }

    private static final class DayTemps {
        Double temp09;
        Double temp18;
    }
}
