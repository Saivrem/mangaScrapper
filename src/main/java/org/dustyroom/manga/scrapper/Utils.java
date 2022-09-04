package org.dustyroom.manga.scrapper;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.TreeSet;

@UtilityClass
public class Utils {

    private static final String s = File.separator;
    private static final String homeFolder = System.getProperty("user.home") + s + "mangaScrapping" + s;

    public static Path getPathAndCreateFolders(String mangaName, String inputString) {
        String volume = inputString.substring(inputString.lastIndexOf("/vol"), inputString.lastIndexOf("/"));
        String chapter = inputString.substring(inputString.lastIndexOf("/"));
        if (chapter.contains("?")) {
            chapter = chapter.substring(0, chapter.indexOf("?"));
        }
        Path path = Paths.get(String.format("%s%s%s%s", homeFolder, mangaName, volume, chapter));
        path.toFile().mkdirs();
        return path;
    }

    public static String getFileName(URL url) {
        String tmp = url.getFile();
        return tmp.substring(tmp.lastIndexOf("/") + 1);
    }

    public static String cleanHref(String href) {
        if (href.contains("?")) {
            href = href.substring(0, href.indexOf("?"));
        }
        if (href.contains(("#"))) {
            href = href.substring(0, href.indexOf("#"));
        }
        return href;
    }

    public static Set<String> extractPageLinks(String input) {
        Set<String> result = new TreeSet<>();
        String arrayOfElementArraysString = input.substring(input.indexOf("[["), input.lastIndexOf("]]"));
        for (String str : arrayOfElementArraysString.split("],\\[")) {
            String cleanedString = str.replaceAll("[\\[\"']", "");
            String[] elementArray = cleanedString.split(",");
            String domain = elementArray[0];
            String path = elementArray[2];
            if (StringUtils.isNoneBlank(domain, path)) {
                result.add(domain + path);
            }
        }
        return result;
    }
}
