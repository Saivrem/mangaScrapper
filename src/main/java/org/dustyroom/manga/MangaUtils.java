package org.dustyroom.manga;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.TreeSet;

import static org.dustyroom.general.Constants.jarPath;
import static org.dustyroom.general.Constants.s;

@UtilityClass
public class MangaUtils {
    private static final String WORKDIR = jarPath.substring(0, (jarPath.lastIndexOf("/") + 1)) + "mangaScrapping" + s;

    public static Path prepareChapterFolder(String mangaName, String fullChapterName) {
        String volume = fullChapterName.substring(fullChapterName.lastIndexOf("/vol") + 1, fullChapterName.lastIndexOf("/"));
        String chapter = fullChapterName.substring(fullChapterName.lastIndexOf("/") + 1);
        if (chapter.contains("?")) {
            chapter = chapter.substring(0, chapter.indexOf("?"));
        }

        int chapterNumber = Integer.parseInt(chapter);
        int volumeNumber = Integer.parseInt(volume.substring(3));

        Path path = Paths.get(String.format("%s%s/vol%03d/ch%03d", WORKDIR, mangaName, volumeNumber, chapterNumber));
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
