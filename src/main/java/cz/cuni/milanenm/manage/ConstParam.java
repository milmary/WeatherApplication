package cz.cuni.milanenm.manage;

/**
 * String constants used in UI messages and weather formatting.
 */
public final class ConstParam {

    private ConstParam() { /* no instances */ }

    public static final String NOT_FOUND  = "The city is not found";
    public static final String ENTER_CITY = "Enter the name of the city";

    public static final String HELP =
            "Enter the city name in English or Czech\n" +
            "(with or without diacritics).\n" +
            "To specify the country, enter its abbreviation\n" +
            "in English, separated by a comma.\n" +
            "For example: Rome, It.\n";

    public static final String DEGREE = " \u2103";
    public static final String SPEED  = " m/s";
    public static final String SCALE  = " mmHg";
}
