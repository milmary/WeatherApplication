package cz.cuni.milanenm.providers;

import cz.cuni.milanenm.cache.Cache;
import cz.cuni.milanenm.cache.SimpleMemoryCache;
import cz.cuni.milanenm.proxy.Metrics;
import cz.cuni.milanenm.proxy.WeatherApiProxies;
import cz.cuni.milanenm.spi.WeatherApi;
import cz.cuni.milanenm.spi.WeatherProvider;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * Loads {@link WeatherProvider} via {@link ServiceLoader}, then wraps its {@link WeatherApi}
 * in a dynamic proxy that adds simple caching and metrics.
 */
public final class ProviderRegistry {
    private static volatile WeatherApi API;
    private ProviderRegistry(){}

    /**
     * Returns a cached {@link WeatherApi} instance (wrapped with cache and metrics).
     *
     * @return lazily initialized provider API
     * @throws IllegalStateException if no {@link WeatherProvider} is found on the classpath
     */
    public static synchronized WeatherApi getApi() {
        if (API != null) return API;
        ServiceLoader<WeatherProvider> loader = ServiceLoader.load(WeatherProvider.class);
        WeatherProvider chosen = null;
        for (WeatherProvider p : loader) {
            if ("OpenWeatherMap".equals(p.name())) { chosen = p; break; }
        }
        if (chosen == null) {
            Iterator<WeatherProvider> it = loader.iterator();
            if (it.hasNext()) chosen = it.next();
        }
        if (chosen == null) throw new IllegalStateException("No WeatherProvider found via ServiceLoader.");

        Cache<String,String> cache = SimpleMemoryCache.getInstance();
        Metrics metrics = Metrics.getInstance();
        API = WeatherApiProxies.wrapWithMetrics(chosen.api(), cache, metrics);
        return API;
    }
}
