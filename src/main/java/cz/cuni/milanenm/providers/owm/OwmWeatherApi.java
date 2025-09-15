package cz.cuni.milanenm.providers.owm;

import cz.cuni.milanenm.receive_json.extendsJsonData.JsonCurrent;
import cz.cuni.milanenm.receive_json.extendsJsonData.JsonCurrentPrague;
import cz.cuni.milanenm.receive_json.extendsJsonData.JsonForecast;
import cz.cuni.milanenm.spi.WeatherApi;

/**
 * {@link WeatherApi} backed by existing Json* fetchers (OpenWeatherMap).
 */
public final class OwmWeatherApi implements WeatherApi {

    @Override
    public String current(String city) {
        return new JsonCurrent(city).getWeatherJson();
    }

    @Override
    public String forecast(String city) {
        return new JsonForecast(city).getWeatherJson();
    }

    @Override
    public String currentPrague() {
        return new JsonCurrentPrague().getWeatherJson();
    }
}
