package org.dustyroom;

import org.dustyroom.configuration.MangaConfiguration;
import org.dustyroom.manga.scrapper.MangaLiveScrapper;
import org.dustyroom.utils.ObjectMapperUtils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        if (args.length > 0) {
            InputStream inputStream = new FileInputStream(args[0]);
            List<MangaConfiguration> mangaConfigurations = ObjectMapperUtils.readMangaConfiguration(inputStream);
            if (mangaConfigurations != null) {
                mangaConfigurations.stream()
                                   .filter(Objects::nonNull)
                                   .map(config -> MangaLiveScrapper.builder()
                                                                   .mangaName(config.getMangaName())
                                                                   .mangaPageLink(config.getMangaPageLink())
                                                                   .needMature(config.isMature())
                                                                   .proxy(config.getProxy())
                                                                   .targetDir(config.getTargetDir())
                                                                   .build())
                                   .forEach(executorService::submit);
            } else {
                System.out.printf("Wrong configuration file received %s\n", args[0]);
            }
        }
        executorService.shutdown();
    }
}