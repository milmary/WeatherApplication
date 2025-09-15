package cz.cuni.milanenm.manage;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Date/time formatting utilities used by the UI and providers.
 */
public final class ReceiveDateTime {

    private ReceiveDateTime() { /* no instances */ }

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("dd.MM.yyyy, EEEE", Locale.getDefault());

    private static final DateTimeFormatter TIME_FMT =
            DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault());

    private static final ZoneId PRAGUE_TZ = ZoneId.of("Europe/Prague");

    /**
     * @return current date formatted as {@code dd.MM.yyyy, EEEE}
     */
    public static String getCurrentDate() {
        return LocalDate.now().format(DATE_FMT);
    }

    /**
     * @return current time formatted as {@code HH:mm}
     */
    public static String getCurrentTime() {
        return LocalTime.now().format(TIME_FMT);
    }

    /**
     * Formats a Unix epoch seconds value as a local Prague time string {@code HH:mm}.
     * Intended for sunrise/sunset fields from providers.
     *
     * @param sunTime epoch seconds
     * @return formatted time
     */
    public static String getSunEventPrague(long sunTime) {
        return Instant.ofEpochSecond(sunTime)
                .atZone(PRAGUE_TZ)
                .format(TIME_FMT);
    }

    /**
     * Converts a provider timestamp {@code yyyy-MM-dd HH:mm:ss} to {@code dd.MM.yyyy HH:mm}.
     *
     * @param oldDateTime input date-time string
     * @return formatted value in {@code dd.MM.yyyy HH:mm}
     * @throws IllegalArgumentException if parsing fails
     */
    public static String formattingDateTime(String oldDateTime) {
        try {
            LocalDateTime dt = LocalDateTime.parse(
                    oldDateTime,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ROOT)
            );
            return dt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm", Locale.getDefault()));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date-time: " + oldDateTime, e);
        }
    }
}
