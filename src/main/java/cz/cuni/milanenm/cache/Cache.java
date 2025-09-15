package cz.cuni.milanenm.cache;

/**
 * Generic key-value cache interface with time-to-live support.
 *
 * @param <K> key type
 * @param <V> value type
 */
public interface Cache<K, V> {

    /**
     * Returns a value for the given key or {@code null} if absent or expired.
     *
     * @param key cache key
     * @return cached value or {@code null}
     */
    V get(K key);

    /**
     * Puts a value into the cache with a specified TTL (time to live).
     *
     * @param key       cache key
     * @param value     value to cache
     * @param ttlMillis time to live in milliseconds
     */
    void put(K key, V value, long ttlMillis);
}
