package org.dustyroom.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DownloadConfig {

    /**
     * Custom path if needed, default is ($HOME/mangaScrapping)
     */
    private String targetDir;
    /**
     * is Mature flag needed
     */
    private boolean mature;
    List<MangaProperties> mangaList;
}
