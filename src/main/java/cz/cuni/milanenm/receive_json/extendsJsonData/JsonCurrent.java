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
 * This class represents the current weather data for a specified city.
 * It retrieves weather information such as temperature, pressure, wind, sunrise, and sunset times.
 * The data is fetched from a weather API using the city name.
 */
@Getter
public class JsonCurrent extends JsonData {

    /**
     * Configuration settings for API URLs and keys.
     */
    private final static ConfigSettings settings = ConfigSettings.getInstance();
    /**
     * Name of the city for which the weather data is requested.
     */
    private final String city;
    private String feels;
    private String press;
    private String desc;
    private String wind;
    private String sunrise;
    private String sunset;

    /**
     * Constructs a JsonCurrent object for a specific city.
     *
     * @param city the name of the city
     */
    public JsonCurrent(String city) {
        this.city = city.replace(" ", "+");
    }

    /**
     * Fetches and parses the current weather data from the API.
     *
     * @return A string representing the weather data including temperature, pressure,
     *         description, wind, and sunrise/sunset times.
     */
    @Override
    public String getWeatherJson() {

        JSONObject json = GettingJson.receiveJson(settings.getUrlCurrent()
                + city + "&appid=" + settings.getApi() + "&units=metric&lang=en");

        JSONObject jsonSpecific = json.getJSONObject("main");

        StringBuilder data = new StringBuilder();

        this.feels = jsonSpecific.getInt("feels_like") + DEGREE;

        this.press = jsonSpecific.getDouble("pressure") * 0.75 + SCALE;

        jsonSpecific = json.getJSONArray("weather").getJSONObject(0);

        this.desc = jsonSpecific.get("description").toString().substring(0,1).toUpperCase() +
                jsonSpecific.get("description").toString().substring(1).toLowerCase();

        jsonSpecific = json.getJSONObject("wind");
        this.wind = jsonSpecific.get("speed").toString() + SPEED + ", " +
                WindDirection.directionText(jsonSpecific.getDouble("deg")) +
                WindDirection.directionSymb(jsonSpecific.getDouble("deg"));

        jsonSpecific = json.getJSONObject("sys");
        this.sunrise = ReceiveDateTime.getSunEventPrague(jsonSpecific.getLong("sunrise"));

        jsonSpecific = json.getJSONObject("sys");
        this.sunset = ReceiveDateTime.getSunEventPrague(jsonSpecific.getLong("sunset"));

        data.append("Temperature: ").append(this.feels).append("\n");
        data.append("Pressure: ").append(this.press).append("\n");
        data.append(this.desc).append("\n");
        data.append("Wind: ").append(this.wind).append("\n");
        data.append("Sunrise (GMT+2): ").append(this.sunrise).append("\n");
        data.append("Sunset (GMT+2): ").append(this.sunset);
        return data.toString();
    }
}