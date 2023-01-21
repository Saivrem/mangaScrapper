package org.dustyroom.configuration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MangaConfiguration {

    /**
     * the name of folder where content will be stored
     */
    private String mangaName;
    /**
     * Root manga page link
     */
    private String mangaPageLink;
    /**
     * is Mature flag needed
     */
    private boolean isMature;
    /**
     * sometimes original picture domain is protected,
     * you can setup proxy domain
     * check rm_h.servers in console on the site
     */
    private String proxy;

    /**
     * Custom path if needed, default is ($HOME/mangaScrapping)
     */
    private String targetDir;
}