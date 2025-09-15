package cz.cuni.milanenm.receive_json.extendsJsonData;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.cuni.milanenm.manage.ConfigSettings;
import cz.cuni.milanenm.manage.ReceiveDateTime;
import cz.cuni.milanenm.manage.WindDirection;
import static cz.cuni.milanenm.manage.ConstParam.*;
import cz.cuni.milanenm.receive_json.GettingJson;
import cz.cuni.milanenm.receive_json.JsonData;

/**
 * This class retrieves the weather forecast for a specified city.
 * It fetches and parses forecast data, including temperature, description, and wind information
 * at specific times (e.g., 09:00 and 18:00).
 */
public class JsonForecast extends JsonData {

    /**
     * Configuration settings for API URLs and keys.
     */
    private final static ConfigSettings settings = ConfigSettings.getInstance();
    private final String city;
    private String dateTime;
    private String feels;
    private String desc;
    private String wind;

    /**
     * Constructs a JsonForecast object for a specific city.
     *
     * @param city the name of the city for which the forecast data is requested
     */
    public JsonForecast(String city) {
        this.city = city.replace(" ", "+");
    }

    /**
     * Fetches and parses the weather forecast data from the API.
     *
     * @return A string representing the forecast data at specific times including temperature,
     *         description, and wind information.
     */
    @Override
    public String getWeatherJson() {

        JSONObject json = GettingJson.receiveJson(settings.getUrlForecast()
                + city + "&appid=" + settings.getApi() + "&units=metric&lang=en");

        JSONArray list = (JSONArray) json.get("list");

        StringBuilder data = new StringBuilder();

        for (Object o : list) {

            JSONObject forecast = (JSONObject) o;
            this.dateTime = ReceiveDateTime.formattingDateTime(forecast.get("dt_txt").toString()) + ": ";

            if (this.dateTime.contains("09:00") || this.dateTime.contains("18:00")) {

                JSONObject main = (JSONObject) forecast.get("main");
                this.feels = main.getInt("feels_like") + DEGREE + ", ";

                JSONObject desk = forecast.getJSONArray("weather").getJSONObject(0);
                this.desc = desk.get("description").toString().substring(0,1).toUpperCase() +
                        desk.get("description").toString().substring(1).toLowerCase() + ", ";

                JSONObject wind = forecast.getJSONObject("wind");
                this.wind = wind.get("speed").toString() + SPEED + " "
                        + WindDirection.directionSymb(wind.getDouble("deg"));

                data.append(this.dateTime);
                data.append(this.feels);
                data.append(this.desc);
                data.append(this.wind);
                data.append("\n");

            }
        }
        return data.toString();
    }
}