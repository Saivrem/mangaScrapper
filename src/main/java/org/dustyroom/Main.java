package org.dustyroom;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dustyroom.scrapping.Scrapper;
import org.dustyroom.model.MangaProperties;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

public class Main {

    public static void main(String[] args) {
        if (args.length > 0) {
            List<MangaProperties> mangaProperties = readMangaConfiguration(args[0]);
            if (mangaProperties != null) {
                mangaProperties.stream()
                        .filter(Objects::nonNull)
                        .map(config -> Scrapper.builder()
                                .mangaName(config.getMangaName())
                                .mangaPageLink(config.getMangaPageLink())
                                .needMature(config.isMature())
                                .proxy(config.getProxy())
                                .targetDir(config.getTargetDir())
                                .build())
                        .forEach(Scrapper::run);
            } else {
                System.out.printf("Wrong configuration file received %s\n", args[0]);
            }
        }
    }

    private static List<MangaProperties> readMangaConfiguration(String configFile) {
        ObjectMapper objectMapper = new ObjectMapper();
        try (InputStream inputStream = new FileInputStream(configFile)) {
            TypeReference<List<MangaProperties>> typeReference = new TypeReference<>() {
            };
            return objectMapper.readValue(inputStream, typeReference);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}