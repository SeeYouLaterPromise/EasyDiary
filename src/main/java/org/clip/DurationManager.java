package org.clip;

/**
 * when storage in file like: "xxh_xxm_xxs";
 * do accumulation using "s" unit.
 */
public class DurationManager {

    public static int switchHelper(char unit) {
        switch (unit) {
            case 'h': return 3600;
            case 'm': return 60;
            case 's': return 1;
        }
        return 0;
    }

    /**
     * xxh_xxm_xxs -> xxxxxs
     * @param existingDuration: format as "xxh_xxm_xxs"
     * @return seconds in unit 's'
     */
    public static long getSeconds(String existingDuration) {
        int seconds = 0;
        String[] components = existingDuration.split("_");
        for (String component : components) {
            int end = component.length() - 1;
            char unit = component.charAt(end);
            int figure = Integer.parseInt(component.substring(0, end));
            seconds += figure * switchHelper(unit);
        }
        return seconds;
    }

    public static String SecondsToString(long seconds) {
        int hour = (int) seconds / 3600;
        seconds %= 3600;

        int minute = (int) seconds / 60;
        seconds %= 60;

        return hour + "h_" + minute + "m_" + seconds + "s";
    }
}
