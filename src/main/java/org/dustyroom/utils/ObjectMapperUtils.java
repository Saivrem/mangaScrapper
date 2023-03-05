package org.dustyroom.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import org.dustyroom.configuration.MangaConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@UtilityClass
public class ObjectMapperUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static List<MangaConfiguration> readMangaConfiguration(InputStream inputStream) {
        try {
            TypeReference<List<MangaConfiguration>> typeReference = new TypeReference<>() {
            };
            return objectMapper.readValue(inputStream, typeReference);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
