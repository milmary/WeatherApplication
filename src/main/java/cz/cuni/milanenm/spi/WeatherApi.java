package cz.cuni.milanenm.spi;

/**
 * Minimal weather API contract used by the application.
 * Implementations are typically supplied via the {@link java.util.ServiceLoader}.
 */
public interface WeatherApi {

    /**
     * Returns a textual description of current weather for a city.
     *
     * @param city city name
     * @return current weather description (text/JSON) or error text
     */
    String current(String city);

    /**
     * Returns a textual multi-day forecast for a city.
     *
     * @param city city name
     * @return forecast description (text/JSON) or error text
     */
    String forecast(String city);

    /**
     * Shortcut for current weather in Prague.
     *
     * @return current weather description (text/JSON) or error text
     */
    String currentPrague();
}
