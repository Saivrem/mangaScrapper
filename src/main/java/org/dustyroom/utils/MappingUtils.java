package org.dustyroom.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.dustyroom.model.DownloadConfig;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@UtilityClass
public class MappingUtils {

    public static ObjectMapper objectMapper = new ObjectMapper();
    private final static TypeReference<DownloadConfig> downloadConfigType = new TypeReference<>() {
    };

    public static DownloadConfig readMangaConfiguration(String configFile) {
        try (InputStream inputStream = new FileInputStream(configFile)) {
            return objectMapper.readValue(inputStream, downloadConfigType);
        } catch (IOException e) {
            log.warn("Config could not be loaded {}", e.getMessage());
            return null;
        }
    }

    public static List<List<String>> mapToPagesList(String input) {
        try {
            return objectMapper.readValue(input, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
