package org.dustyroom.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MangaProperties {

    /**
     * the name of folder where content will be stored
     */
    private String mangaName;
    /**
     * Root manga page link
     */
    private String mangaPageLink;
    /**
     * sometimes original picture domain is protected,
     * you can set up proxy domain
     * check rm_h.servers in console on the site
     */
    private String fallbackDomain;
}