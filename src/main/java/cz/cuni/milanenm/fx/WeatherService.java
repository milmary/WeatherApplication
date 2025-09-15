package cz.cuni.milanenm.fx;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.util.function.Supplier;

/**
 * One-shot background fetcher based on {@link Service}.
 * <p>
 * A {@link Supplier} must be provided via {@link #setSupplier(Supplier)} before starting.
 * The supplier is invoked on a background thread and returns a {@code String}.
 */
public class WeatherService extends Service<String> {

    private Supplier<String> supplier;

    /**
     * Sets the background job to run when the service starts.
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
