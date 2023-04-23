package org.dustyroom.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.HttpStatusException;

@UtilityClass
@Slf4j
public class ExceptionLoggingUtils {

    public static void decodeException(Exception e, String customMessagePattern) {
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
}
