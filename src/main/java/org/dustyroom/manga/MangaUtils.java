package org.dustyroom.manga;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.TreeSet;

import static org.dustyroom.configuration.ForbiddenDomain.getFallbackIfNeeded;
import static org.dustyroom.utils.ExceptionLoggingUtils.decodeException;

@UtilityClass
public class MangaUtils {

    /**
     * Creates a folder to store chapter files, if folder already exists returns null which should mean - no need to process.
     *
     * @param workdir         I should rename this one, probably it's a target directory for all scrapping
     * @param mangaName       the name of manga to load (root dir for it should have same name)
     * @param fullChapterName probably it's what stands for tom number
     * @return chapter path or null if it already exists, hence, shan't be processed.
     */
    public static Path prepareChapterFolder(String workdir, String mangaName, String fullChapterName) {
        String volume = fullChapterName.substring(fullChapterName.lastIndexOf("/vol"), fullChapterName.lastIndexOf("/"));
        String chapter = fullChapterName.substring(fullChapterName.lastIndexOf("/"));
        if (chapter.contains("?")) {
            chapter = chapter.substring(0, chapter.indexOf("?"));
        }

        Path path = Paths.get(String.format("%s%s%s%s", workdir, mangaName, volume, chapter));
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            decodeException(e, "Couldn't create folder; {}");
            return null;
        }
        return path;
    }

    public static String getFileName(String chapterPage) {
        try {
            URL url = new URL(chapterPage);
            String fileName = url.getFile();
            fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
            if (fileName.contains("?")) {
                fileName = fileName.substring(0, fileName.indexOf("?"));
            }
            return fileName;
        } catch (MalformedURLException e) {
            decodeException(e, "Malformed Url was provided: {}");
            return null;
        }
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

    public static Set<String> extractPageLinks(String input, String proxy) {
        Set<String> result = new TreeSet<>();
        String arrayOfElementArraysString = input.substring(input.indexOf("[["), input.lastIndexOf("]]"));
        for (String str : arrayOfElementArraysString.split("],\\[")) {
            String cleanedString = str.replaceAll("[\\[\"']", "");
            String[] elementArray = cleanedString.split(",");
            String domain = (proxy != null) ? proxy : getFallbackIfNeeded(elementArray[0]);
            String path = elementArray[2];
            if (StringUtils.isNoneBlank(domain, path)) {
                result.add(domain + path);
            }
        }
        return result;
    }
}
