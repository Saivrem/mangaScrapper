package org.dustyroom.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.dustyroom.utils.ExceptionLoggingUtils.decodeException;

@UtilityClass
@Slf4j
public class LoadingTool {

    static {
        System.setProperty("http.agent", "Mozilla/5.0 (X11; Linux x86_64; rv:60.0) Gecko/20100101 Firefox/81.0");
    }

    public static void download(String link, Path outputPath) {
        await(100);
        String logPattern = String.format("%s - %s - %s",
                outputPath.getParent().getParent().getFileName(),
                outputPath.getParent().getFileName(),
                outputPath.getFileName());
        if (Files.exists(outputPath)) {
            log.info(logPattern);
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
            log.info(logPattern);
        } catch (Exception e) {
            decodeException(e, "");
        }
    }

    public static void await(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
