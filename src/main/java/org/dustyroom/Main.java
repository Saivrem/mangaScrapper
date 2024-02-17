package org.dustyroom;

import lombok.extern.slf4j.Slf4j;
import org.dustyroom.model.DownloadConfig;
import org.dustyroom.model.Manga;
import org.dustyroom.scrapping.Scrapper;

import java.util.Objects;

import static org.dustyroom.utils.MappingUtils.readMangaConfiguration;

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
}