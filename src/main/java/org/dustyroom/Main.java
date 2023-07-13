package org.dustyroom;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dustyroom.model.DownloadConfig;
import org.dustyroom.scrapping.Scrapper;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class Main {

    public static void main(String[] args) {
        if (args.length > 0) {
            DownloadConfig downloadConfig = readMangaConfiguration(args[0]);
            if (downloadConfig != null) {
                downloadConfig.getMangaList().stream()
                        .filter(Objects::nonNull)
                        .map(mangaProperties -> Scrapper.builder()
                                .mangaName(mangaProperties.getMangaName())
                                .mangaPageLink(mangaProperties.getMangaPageLink())
                                .needMature(downloadConfig.isMature())
                                .proxy(mangaProperties.getFallbackDomain())
                                .targetDir(downloadConfig.getTargetDir())
                                .build())
                        .forEach(Scrapper::run);
            } else {
                System.out.printf("Wrong configuration file received %s\n", args[0]);
            }
        }
    }

    private static DownloadConfig readMangaConfiguration(String configFile) {
        ObjectMapper objectMapper = new ObjectMapper();
        try (InputStream inputStream = new FileInputStream(configFile)) {
            TypeReference<DownloadConfig> typeReference = new TypeReference<>() {
            };
            return objectMapper.readValue(inputStream, typeReference);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}