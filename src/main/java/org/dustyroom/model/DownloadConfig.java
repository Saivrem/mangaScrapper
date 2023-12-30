package org.dustyroom.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DownloadConfig {

    /**
     * Simple identifier for which version of scrapper config is, not really used within software
     */
    String scrapperVersion;
    /**
     * Custom path if needed, default is ($HOME/mangaScrapping)
     */
    private String targetDir;
    /**
     * is Mature flag needed
     */
    private boolean mature;
    /**
     * filenames or parts with commercials and not related info
     * if filename will contain this word as substring or will be equal
     * such files will be skipped
     */
    private List<String> blacklist;
    /**
     * List of manga properties. One config could have more than one manga to load
     *
     * @see MangaProperties
     */
    private List<MangaProperties> mangaList;
}
