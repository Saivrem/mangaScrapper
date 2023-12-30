package org.dustyroom;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.dustyroom.model.DownloadConfig;
import org.dustyroom.model.Manga;
import org.dustyroom.scrapping.Scrapper;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

@Slf4j
public class Main {

    public static void main(String[] args) {
        if (args.length > 0) {
            DownloadConfig downloadConfig = readMangaConfiguration(args[0]);
            if (downloadConfig != null) {
                downloadConfig.getMangaList().stream()
                        .filter(Objects::nonNull)
                        .filter(Manga::isLoad)
                        .map(manga -> Scrapper.builder()
                                .manga(manga)
                                .needMature(downloadConfig.isMature())
                                .targetDir(downloadConfig.getTargetDir())
                                .blacklist(downloadConfig.getBlacklist())
                                .build())
                        .forEach(Scrapper::run);
            }
        } else {
            log.warn("No config provided");
        }
    }

    private static DownloadConfig readMangaConfiguration(String configFile) {
        ObjectMapper objectMapper = new ObjectMapper();
        try (InputStream inputStream = new FileInputStream(configFile)) {
            TypeReference<DownloadConfig> typeReference = new TypeReference<>() {
            };
            return objectMapper.readValue(inputStream, typeReference);
        } catch (IOException e) {
            log.warn("Config could not be loaded {}", e.getMessage());
            return null;
        }
    }
}