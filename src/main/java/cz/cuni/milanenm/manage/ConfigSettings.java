package cz.cuni.milanenm.manage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import lombok.Data;
import lombok.Getter;

/**
 * Loads configuration from {@code config.properties} on the classpath.
 * Provides API key and endpoint URLs required by the weather providers.
 */
@Data
public class ConfigSettings {
    /** Properties file name on the classpath. */
    public static final String FILE_NAME = "config.properties";

    /** Singleton instance. */
    @Getter
    private static ConfigSettings instance = new ConfigSettings();

    private Properties properties;

    /** API key/token. */
    private String api;

    /** URL for the "current Prague" endpoint. */
    private String urlPrague;

    /** URL for the "current city" endpoint. */
    private String urlCurrent;

    /** URL for the "forecast" endpoint. */
    private String urlForecast;

    /*
      Initializes the ConfigSettings instance by loading properties from the file.
      Verifies that all required properties are present.
     */
    {
        try {
            properties = new Properties();

            try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(FILE_NAME)) {
                properties.load(inputStream);
            } catch (IOException e) {
                throw new IOException(String.format("Error loading properties file '%s'", FILE_NAME));
            }

            api = properties.getProperty("api");
            if (api == null) throw new RuntimeException("Api value is null");

            urlPrague = properties.getProperty("urlPrague");
            if (urlPrague == null) throw new RuntimeException("Url value is null");

            urlCurrent = properties.getProperty("urlCurrent");
            if (urlCurrent == null) throw new RuntimeException("Url value is null");

            urlForecast = properties.getProperty("urlForecast");
            if (urlForecast == null) throw new RuntimeException("Url value is null");

        } catch (RuntimeException | IOException e) {
            throw new RuntimeException("App initialisation error: " + e.getMessage());
        }
    }
}
