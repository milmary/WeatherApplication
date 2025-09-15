package cz.cuni.milanenm.manage;

/**
 * Conversion of wind direction (degrees) to compass text and arrow symbols.
 */
public final class WindDirection {

    private WindDirection() { /* no instances */ }

    private static final String[] DIR_TEXT = {
            "N", "NE", "E", "SE", "S", "SW", "W", "NW", "N"
    };

    private static final String[] DIR_SYMBOL = {
            "\u2193", "\u2199", "\u2190", "\u2196", "\u2191", "\u2197", "\u2192", "\u2198", "\u2193"
    };

    /**
     * Converts degrees (0..360) to a compass direction text.
     *
     * @param degree wind direction in degrees
     * @return one of {N, NE, E, SE, S, SW, W, NW}
     */
    public static String directionText(double degree) {
        int idx = (int) Math.round((degree % 360) / 45.0);
        return DIR_TEXT[idx];
    }

    /**
     * Converts degrees (0..360) to a compass arrow symbol.
     *
     * @param degree wind direction in degrees
     * @return arrow symbol pointing to the wind direction
     */
    public static String directionSymb(double degree) {
        int idx = (int) Math.round((degree % 360) / 45.0);
        return DIR_SYMBOL[idx];
    }
}
