package org.dustyroom.general;

import lombok.experimental.UtilityClass;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

@UtilityClass
public class LoadingTool {

    public static void download(URL url, File outputFile, String fileName) {
        System.setProperty("http.agent", "Mozilla/5.0 (X11; Linux x86_64; rv:60.0) Gecko/20100101 Firefox/81.0");
        try (InputStream in = url.openStream();
             OutputStream out = new FileOutputStream(outputFile)) {

            int length;
            byte[] buffer = new byte[1024];

            while ((length = in.read(buffer)) > -1) {
                out.write(buffer, 0, length);
            }
            out.flush();
            System.out.println("Finished: " + fileName);
        } catch (
                IOException ioEx) {
            if (ioEx.getMessage().contains("HTTP response code: 401")) {
                System.out.println("Login or password are invalid; ");
            } else if (ioEx.getMessage().contains("HTTP response code: 403")) {
                System.out.println("You are not authorized to get this file;");
            } else if (ioEx.getMessage().contains("HTTP response code: 404") || ioEx.getClass()
                                                                                    .getSimpleName()
                                                                                    .equals("FileNotFoundException")) {
                System.out.println("File not found; ");
            } else {
                System.out.println("Error was caught; ");
            }
        }
    }
}
