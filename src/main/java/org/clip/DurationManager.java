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

        // 新的一天，“xxh_xxm_xxs” 为空，需要过滤避免数组越界问题；且注意split返回数组长度为1
        // without any records.
        if (components.length == 1) return 0;

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
