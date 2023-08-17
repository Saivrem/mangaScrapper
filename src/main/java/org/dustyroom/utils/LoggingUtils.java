package org.dustyroom.utils;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.HttpStatusException;

import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
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

    public static void decodeAndLogException(Exception e, String customMessagePattern) {
        if (e instanceof HttpStatusException) {
            switch (((HttpStatusException) e).getStatusCode()) {
                case 429:
                    log.error("Service requests limit exceeded, application will be closed");
                    System.exit(1);
                case 401:
                case 403:
                case 404:
                    log.warn(e.getMessage());
                default:
                    log.error(e.getMessage());
                    break;
            }
        }
        if (customMessagePattern != null) {
            log.warn(customMessagePattern, e.getMessage());
        }
        log.warn(e.getMessage());
    }

    @SneakyThrows
    public static void await(int ms) {
        Thread.sleep(ms);
    }
}
