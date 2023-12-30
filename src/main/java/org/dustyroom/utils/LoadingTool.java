package org.dustyroom.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static org.dustyroom.utils.LoggingUtils.*;

@Slf4j
@UtilityClass
public class LoadingTool {

    static {
        System.setProperty("http.agent", "Mozilla/5.0 (X11; Linux x86_64; rv:60.0) Gecko/20100101 Firefox/81.0");
    }

    public static void download(String link, Path outputPath) {
        await(100);

        if (Files.exists(outputPath)) {
            log.debug("File exists: {}", getFileLoggingString(outputPath));
            return;
        }
        try (InputStream in = new URL(link).openStream()) {
            Files.copy(in, outputPath, StandardCopyOption.REPLACE_EXISTING);
            log.debug("Downloaded: {}", getFileLoggingString(outputPath));
        } catch (Exception e) {
            decodeAndLogException(e, "");
        }
    }
}
