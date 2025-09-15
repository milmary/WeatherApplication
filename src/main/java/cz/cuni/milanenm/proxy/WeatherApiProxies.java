package cz.cuni.milanenm.proxy;

import cz.cuni.milanenm.cache.Cache;
import cz.cuni.milanenm.spi.WeatherApi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.Arrays;

/**
 * Factory for dynamic proxies that wrap a {@link WeatherApi} with:
 * <ul>
 *   <li>Simple TTL cache</li>
 *   <li>Basic metrics (counters and timing)</li>
 *   <li>Error capture (last error message)</li>
 * </ul>
 */
public final class WeatherApiProxies {

    private WeatherApiProxies() {}

    /**
     * Wraps a {@link WeatherApi} with caching and metrics.
     *
     * @param target concrete API implementation to call
     * @param cache  cache for String responses
     * @param m      metrics sink
     * @return proxied WeatherApi
     */
    public static WeatherApi wrapWithMetrics(WeatherApi target,
                                             Cache<String, String> cache,
                                             Metrics m) {
        return (WeatherApi) Proxy.newProxyInstance(
            WeatherApi.class.getClassLoader(),
            new Class<?>[]{WeatherApi.class},
            (proxy, method, args) -> {
                String key = method.getName() + (args == null ? "[]" : Arrays.toString(args));
                m.apiCalls.incrementAndGet();

                String cached = cache.get(key);
                if (cached != null) {
                    m.cacheHits.incrementAndGet();
                    return cached;
                }

                long t0 = System.nanoTime();
                try {
                    Object res = method.invoke(target, args);
                    if (res instanceof String) {
                        // cache for 5 minutes
                        cache.put(key, (String) res, 5 * 60 * 1000L);
                    }
                    return res;
                } catch (InvocationTargetException ite) {
                    Throwable cause = (ite.getCause() != null) ? ite.getCause() : ite;
                    m.setLastError(cause.getMessage());
                    throw cause;
                } finally {
                    long ms = (System.nanoTime() - t0) / 1_000_000;
                    System.out.println("[WeatherApi] " + method.getName() + " took " + ms + " ms");
                }
            }
        );
    }
}
