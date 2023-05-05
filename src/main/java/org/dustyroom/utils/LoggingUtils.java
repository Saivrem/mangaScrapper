package org.dustyroom.utils;

import lombok.experimental.UtilityClass;

import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;

@UtilityClass
public class LoggingUtils {

    public static LocalDateTime getCurrentTime() {
        return LocalDateTime.now();
    }

    public static String timePassed(LocalDateTime start, LocalDateTime end) {
        Duration between = Duration.between(start, end);
        long seconds = between.getSeconds();
        int hours = (int) (seconds / 3600);
        int minutes = (int) ((seconds % 3600) / 60);
        int remainingSeconds = (int) (seconds % 60);
        return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds);
    }

    public static String getFileLoggingString(Path outputPath) {
        return String.format("%s : %s - %s - %s",
                outputPath.getParent().getParent().getParent().getFileName(),
                outputPath.getParent().getParent().getFileName(),
                outputPath.getParent().getFileName(),
                outputPath.getFileName());
    }
}
