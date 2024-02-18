package org.dustyroom.utils;

import lombok.experimental.UtilityClass;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class DateUtils {

    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static LocalDateTime getCurrentTime() {
        return LocalDateTime.now();
    }

    public String format(LocalDateTime time) {
        return time.format(formatter);
    }

    public static String timePassed(LocalDateTime start, LocalDateTime end) {
        Duration between = Duration.between(start, end);
        long seconds = between.getSeconds();
        int hours = (int) (seconds / 3600);
        int minutes = (int) ((seconds % 3600) / 60);
        int remainingSeconds = (int) (seconds % 60);
        return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds);
    }
}
