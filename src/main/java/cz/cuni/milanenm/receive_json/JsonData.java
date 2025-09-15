package cz.cuni.milanenm.receive_json;

/**
 * Abstract class representing weather data retrieved from a JSON response.
 * Classes that extend JsonData are responsible for implementing the method to fetch and
 * return the weather information.
 */
public abstract class JsonData {

    /**
     * Retrieves and returns the weather data in JSON format.
     *
     * @return A string containing the weather data.
     */
    public abstract String getWeatherJson();
}