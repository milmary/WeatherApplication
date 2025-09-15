package cz.cuni.milanenm.providers.owm;

import cz.cuni.milanenm.spi.WeatherApi;
import cz.cuni.milanenm.spi.WeatherProvider;

/**
 * {@link WeatherProvider} implementation for OpenWeatherMap that adapts the Json* classes.
 */
public final class OpenWeatherMapProvider implements WeatherProvider {

    private final WeatherApi api = new OwmWeatherApi();

    @Override
    public String name() {
        return "OpenWeatherMap";
    }

    @Override
    public WeatherApi api() {
        return api;
    }
}
