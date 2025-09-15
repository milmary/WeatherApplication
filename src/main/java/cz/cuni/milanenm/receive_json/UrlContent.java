package cz.cuni.milanenm.receive_json;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * Small HTTP helper for fetching textual content from a URL.
 * <p>
 * Uses a shared {@link HttpClient} with short timeouts. On any failure, a
 * {@link RuntimeException} is thrown so JavaFX {@code Service.onFailed} can react.
 */
public final class UrlContent {

    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    private UrlContent() {
        // utility
    }

    /**
     * Performs a blocking HTTP GET and returns the response body as UTF-8 text.
     *
     * @param urlAddress absolute URL to fetch
     * @return response body as UTF-8 string
     * @throws RuntimeException if the request fails or returns a non-2xx status
     */
    public static String getUrlContent(String urlAddress) {
        try {
            if (urlAddress == null || urlAddress.isBlank()) {
                throw new IllegalArgumentException("urlAddress is null/blank");
            }

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(urlAddress))
                    .timeout(Duration.ofSeconds(8))
                    .header("User-Agent", "AppWeather/1.0 (+java.net.http)")
                    .GET()
                    .build();

            HttpResponse<byte[]> resp = CLIENT.send(req, HttpResponse.BodyHandlers.ofByteArray());
            int code = resp.statusCode();
            if (code / 100 != 2) {
                throw new RuntimeException("HTTP " + code + " for " + urlAddress);
            }
            return new String(resp.body(), StandardCharsets.UTF_8);

        } catch (Exception ex) {
            System.err.println("[HTTP] fetch failed: " + ex);
            throw new RuntimeException(ex);
        }
    }
}
