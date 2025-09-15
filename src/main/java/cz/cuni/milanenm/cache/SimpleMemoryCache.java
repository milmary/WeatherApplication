package cz.cuni.milanenm.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple in-memory, thread-safe cache for {@code String} keys and values.
 * Each entry expires after the configured TTL.
 */
public final class SimpleMemoryCache implements Cache<String, String> {

    private static final SimpleMemoryCache INSTANCE = new SimpleMemoryCache();

    private static final class Entry {
        final String value;
        final long expiresAtMillis;
        Entry(String value, long expiresAtMillis) {
            this.value = value;
            this.expiresAtMillis = expiresAtMillis;
        }
    }

    private final Map<String, Entry> map = new ConcurrentHashMap<>();

    private SimpleMemoryCache() {}

    /**
     * Returns the global singleton instance.
     *
     * @return cache instance
     */
    public static SimpleMemoryCache getInstance() {
        return INSTANCE;
    }

    @Override
    public String get(String key) {
        Entry e = map.get(key);
        if (e == null) return null;
        if (e.expiresAtMillis < System.currentTimeMillis()) {
            map.remove(key);
            return null;
        }
        return e.value;
    }

    @Override
    public void put(String key, String value, long ttlMillis) {
        map.put(key, new Entry(value, System.currentTimeMillis() + ttlMillis));
    }
}
