package cz.cuni.milanenm.receive_json.extendsJsonData;

import lombok.Getter;
import org.json.JSONObject;

import cz.cuni.milanenm.manage.ConfigSettings;
import cz.cuni.milanenm.manage.ReceiveDateTime;
import cz.cuni.milanenm.manage.WindDirection;
import static cz.cuni.milanenm.manage.ConstParam.*;
import cz.cuni.milanenm.receive_json.GettingJson;
import cz.cuni.milanenm.receive_json.JsonData;

/**
 * This class retrieves the current weather data specifically for Prague.
 * It includes information such as temperature, wind speed and direction,
 * and sunrise/sunset times.
 */
@Getter
public class JsonCurrentPrague extends JsonData {

    /**
     * Configuration settings for API URLs and keys.
     */
    private final static ConfigSettings settings = ConfigSettings.getInstance();
    private String temp;
    private String desc;
    private String wind;
    private String sunrise;
    private String sunset;

    /**
     * Fetches and parses the current weather data for Prague from the API.
     *
     * @return A string representing the weather data including temperature,
     *         description, wind, and sunrise/sunset times.
     */
    @Override
    public String getWeatherJson() {
        try {
            // Receive the JSON response
            JSONObject json = GettingJson.receiveJson(settings.getUrlPrague() +
                    settings.getApi() + "&units=metric&lang=en");

            if (json == null) {
                return "Error: Unable to fetch weather data.";
            }

            // Extract weather details
            JSONObject jsonSpecific = json.getJSONObject("main");
            StringBuilder data = new StringBuilder();

            this.temp = jsonSpecific.getInt("feels_like") + DEGREE;
            jsonSpecific = json.getJSONArray("weather").getJSONObject(0);
            this.desc = jsonSpecific.get("description").toString().substring(0,1).toUpperCase() +
                    jsonSpecific.get("description").toString().substring(1).toLowerCase();
            jsonSpecific = json.getJSONObject("wind");
            this.wind = jsonSpecific.get("speed").toString() + SPEED + ", " +
                    WindDirection.directionSymb(jsonSpecific.getDouble("deg"));
            jsonSpecific = json.getJSONObject("sys");
            this.sunrise = ReceiveDateTime.getSunEventPrague(jsonSpecific.getLong("sunrise"));
            this.sunset = ReceiveDateTime.getSunEventPrague(jsonSpecific.getLong("sunset"));

            // Build the weather info string
            data.append("Temperature: ").append(this.temp).append("\n");
            data.append(this.desc).append("\n");
            data.append("Wind: ").append(this.wind).append("\n");
            data.append("Sunrise (GMT+2): ").append(this.sunrise).append("\n");
            data.append("Sunset (GMT+2): ").append(this.sunset);

            return data.toString();
        } catch (Exception e) {
            // Handle any exceptions that might occur
            e.printStackTrace();
            return "Error: Failed to load weather data.";
        }
    }
}
