package org.dustyroom.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Manga {

    /**
     * the name of folder where content will be stored
     */
    private String name;
    /**
     * Root manga page link
     */
    private String page;
    /**
     * sometimes original picture domain is protected,
     * you can set up proxy domain
     * check rm_h.servers in console on the site
     */
    private String server;
    private boolean zip;
    private boolean load;
}