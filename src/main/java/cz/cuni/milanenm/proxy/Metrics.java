package cz.cuni.milanenm.proxy;

import java.util.concurrent.atomic.AtomicLong;

/**
 * In-memory metrics for Weather API calls.
 * Tracks API call count, cache hits, and the last error message seen.
 */
public final class Metrics {

    private static final Metrics INSTANCE = new Metrics();

    /**
     * Returns the global singleton instance.
     *
     * @return metrics instance
     */
    public static Metrics getInstance() {
        return INSTANCE;
    }

    /** Total Weather API invocations (including cache hits). */
    public final AtomicLong apiCalls = new AtomicLong();

    /** Number of responses served from cache. */
    public final AtomicLong cacheHits = new AtomicLong();

    private volatile String lastError = "";

    private Metrics() { }

    /**
     * @return last error message recorded, or empty string if none
     */
    public String getLastError() {
        return lastError;
    }

    /**
     * Updates the last error message (null becomes empty string).
     *
     * @param err error text or {@code null}
     */
    public void setLastError(String err) {
        this.lastError = (err == null) ? "" : err;
    }
}
