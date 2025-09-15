package cz.cuni.milanenm.fx;

import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;

import java.util.function.Supplier;

/**
 * Periodic background refresher based on {@link ScheduledService}.
 * <p>
 * A {@link Supplier} must be provided via {@link #setSupplier(Supplier)} before starting.
 * Each run invokes the supplier on a background thread and returns a {@code String} result.
 */
public class WeatherRefreshService extends ScheduledService<String> {

    private Supplier<String> supplier;

    /**
     * Sets the background job to run on each schedule tick.
     *
     * @param supplier non-null job that returns a {@code String}
     */
    public void setSupplier(Supplier<String> supplier) {
        this.supplier = supplier;
    }

    @Override
    protected Task<String> createTask() {
        final Supplier<String> job = this.supplier;
        return new Task<>() {
            @Override
            protected String call() {
                if (job == null) throw new IllegalStateException("No supplier set.");
                return job.get();
            }
        };
    }
}
