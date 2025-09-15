package cz.cuni.milanenm.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple DAO for persisting and reading city search history (H2 database).
 * <p>
 * Database location: {@code ~/.appweather/history}
 */
public final class HistoryDao {

    // File DB in user home:
    private static final String URL =
            "jdbc:h2:file:" + System.getProperty("user.home")
            + "/.appweather/history"
            + ";AUTO_SERVER=TRUE;DB_CLOSE_DELAY=-1;MODE=LEGACY;DATABASE_TO_UPPER=false";

    private static final String USER = "sa";
    private static final String PASS = ""; // default H2 password

    static {
        try (Connection c = DriverManager.getConnection(URL, USER, PASS);
             Statement st = c.createStatement()) {
            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS history(
                    id IDENTITY PRIMARY KEY,
                    city VARCHAR(100) NOT NULL,
                    searched_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                )
                """);
            // Optional index for faster ordering by time
            st.executeUpdate("""
                CREATE INDEX IF NOT EXISTS idx_history_searched_at
                ON history(searched_at DESC)
                """);
        } catch (SQLException ex) {
            throw new RuntimeException("H2 init failed", ex);
        }
    }

    private HistoryDao() {
        // no instances
    }

    /**
     * Inserts a searched city into history with the current timestamp.
     *
     * @param city non-blank city name
     */
    public static void insertCity(String city) {
        if (city == null) return;
        String trimmed = city.trim();
        if (trimmed.isEmpty()) return;

        try (Connection c = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO history(city, searched_at) VALUES(?, CURRENT_TIMESTAMP)")) {
            ps.setString(1, trimmed);
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("[History] insert failed: " + ex.getMessage());
        }
    }

    /**
     * Returns the most recent city names, newest first.
     *
     * @param limit maximum number of rows to return; values &lt; 1 are coerced to 1
     * @return list of recent city names
     */
    public static List<String> lastCities(int limit) {
        List<String> out = new ArrayList<>();
        try (Connection c = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = c.prepareStatement(
                     "SELECT city FROM history ORDER BY searched_at DESC LIMIT ?")) {
            ps.setInt(1, Math.max(1, limit));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(rs.getString(1));
                }
            }
        } catch (SQLException ex) {
            System.err.println("[History] read failed: " + ex.getMessage());
        }
        return out;
    }

    /**
     * Removes all rows from history.
     */
    public static void clearAll() {
        try (Connection c = DriverManager.getConnection(URL, USER, PASS);
             Statement st = c.createStatement()) {
            st.executeUpdate("TRUNCATE TABLE history");
        } catch (SQLException ex) {
            System.err.println("[History] clear failed: " + ex.getMessage());
        }
    }
}
