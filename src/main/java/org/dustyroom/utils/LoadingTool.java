package org.dustyroom.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

@UtilityClass
@Slf4j
public class LoadingTool {

    static {
        System.setProperty("http.agent", "Mozilla/5.0 (X11; Linux x86_64; rv:60.0) Gecko/20100101 Firefox/81.0");
    }

    public static void download(String link, Path outputPath, String fileName) {
        if (Files.exists(outputPath)) {
            return;
        }
        try (InputStream in = new URL(link).openStream();
             OutputStream out = Files.newOutputStream(outputPath)) {
            int length;
            byte[] buffer = new byte[1024];
            while ((length = in.read(buffer)) != -1) {
                out.write(buffer, 0, length);
            }
            out.flush();
            log.info("Finished: " + fileName);
        } catch (IOException ioEx) {
            if (ioEx.getMessage().contains("HTTP response code: 401")) {
                log.warn("Login or password are invalid;");
            } else if (ioEx.getMessage().contains("HTTP response code: 403")) {
                log.warn("You are not authorized to get this file;");
            } else if (ioEx.getMessage().contains("HTTP response code: 404") || ioEx.getClass()
                    .getSimpleName()
                    .equals("FileNotFoundException")) {
                log.warn("File not found;");
            } else {
                log.warn("Error was caught;");
            }
        }
    }
}
