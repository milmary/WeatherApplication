package cz.cuni.milanenm.spi;

/**
 * Service Provider Interface (SPI) for pluggable weather providers.
 * Instances are loaded via {@link java.util.ServiceLoader}.
 */
public interface WeatherProvider {

    /**
     * Provider display name.
     *
     * @return provider name
     */
    String name();

    /**
     * Factory for the weather API facade.
     *
     * @return a {@link WeatherApi} implementation
     */
    WeatherApi api();
}
