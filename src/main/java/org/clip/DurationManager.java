package org.clip;

/**
 * when storage in file like: "xxh_xxm_xxs";
 * do accumulation using "s" unit.
 */
public class DurationManager {
    /**
     * According to the learning duration to allocate different colorful time.
     * @param seconds : long value
     * @return css string
     */
    public static String getColor(long seconds) {
        if (seconds == 0) return "-fx-background-color: #E0E0E0;";  // 0h - 浅蓝
        if (seconds <= 0.5 * 3600) return "-fx-background-color: #BBDEFB;"; // 0-30m
        if (seconds <= 3600) return "-fx-background-color: #90CAF9;"; // 30m - 1h
        if (seconds <= 2 * 3600) return "-fx-background-color: #64B5F6;"; // 1h - 2h
        if (seconds <= 3 * 3600) return "-fx-background-color: #42A5F5;"; // 2h - 3h
        if (seconds <= 4 * 3600) return "-fx-background-color: #2196F3;"; // 3h - 4h
        if (seconds <= 5 * 3600) return "-fx-background-color: #1E88E5;"; // 4h - 5h
        if (seconds <= 7 * 3600) return "-fx-background-color: #1976D2;"; // 5h - 7h
        if (seconds <= 10 * 3600) return "-fx-background-color: #1565C0;"; // 7h - 10h
        return "-fx-background-color: #0D47A1;"; // 10h 以上
    }

    public static String getColor(String content) {
        long seconds = getSeconds(content);
        return getColor(seconds);
    }

    public static long secUnit(char unit) {
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
        // future `LearningDuration` file is empty, we give default value `0h`.
        if (existingDuration.isEmpty()) {
            return 0;
        }

        long seconds = 0;
        String[] components = existingDuration.split("_");

        // without `_`, the length of components is 1.
        if (components.length == 1) {
            int end = existingDuration.length() - 1;
            char unit = existingDuration.charAt(end);
            seconds = secUnit(unit) * Integer.parseInt(existingDuration.substring(0, end));
        } else {
            for (String component : components) {
                int end = component.length() - 1;
                char unit = component.charAt(end);
                int figure = Integer.parseInt(component.substring(0, end));
                seconds += figure * secUnit(unit);
            }
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

    private static String format(int num) {
        return num >= 10 ? String.valueOf(num) : "0" + num;
    }
    public static String getTimerString(long seconds) {
        int hour = (int) seconds / 3600;
        seconds %= 3600;

        int minute = (int) seconds / 60;
        seconds %= 60;

        return format(hour) + ":" + format(minute) + ":" + format((int) seconds);
    }
}
