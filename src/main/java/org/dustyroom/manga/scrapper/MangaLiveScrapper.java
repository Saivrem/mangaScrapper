package org.dustyroom.manga.scrapper;

import lombok.Builder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.dustyroom.manga.MangaUtils.*;
import static org.dustyroom.utils.ExceptionLoggingUtils.decodeException;
import static org.dustyroom.utils.LoadingTool.download;

@Builder
public class MangaLiveScrapper {
    private final List<String> initializers = List.of("rm_h.readerInit( ", "rm_h.initReader( ");
    private final String s = File.separator;
    private String mangaPageLink;
    private boolean needMature;
    private String mangaName;
    private String proxy;
    private String targetDir;

    public void run() {
        if (targetDir == null) {
            targetDir = System.getProperty("user.home") + s + "mangaScrapping" + s;
        }

        for (String chapterLink : getChapters()) {
            Path chapterFolder = prepareChapterFolder(targetDir, mangaName, chapterLink);
            if (chapterFolder == null) {
                continue;
            }
            for (String chapterPage : getChapterPages(chapterLink)) {
                String fileName = getFileName(chapterPage);
                if (fileName == null) {
                    continue;
                }
                download(chapterLink, chapterFolder.resolve(fileName));
            }
        }
    }

    private Set<String> getChapterPages(String chapter) {
        try {
            Document document = Jsoup.connect(chapter).get();
            Elements scripts = document.select("script");
            for (Element script : scripts) {
                String data = script.data();
                if (script.attr("type").equals("text/javascript") && isNotBlank(data)) {
                    String initializer = getInitializer(data);
                    if (isNotBlank(initializer)) {
                        return extractPageLinks(data.substring(data.indexOf(initializer)), proxy);
                    }
                }
            }
        } catch (IOException e) {
            decodeException(e, "Can't get chapter pages; {}");
        }
        return Collections.emptySet();
    }

    private String getInitializer(String data) {
        return initializers.stream().filter(data::contains).findFirst().orElse(null);
    }

    private Set<String> getChapters() {
        String rootUrl = mangaPageLink.substring(0, mangaPageLink.lastIndexOf("/"));
        try {
            Document document = Jsoup.connect(mangaPageLink).get();
            Elements allLinks = document.select("a[href]");
            Set<String> chapters = new TreeSet<>();
            for (Element link : allLinks) {
                if (link.classNames().contains("chapter-link")) {
                    StringBuilder href = new StringBuilder(cleanHref(link.attr("href")));
                    if (needMature) {
                        href.append("?mtr=1");
                    }
                    chapters.add(rootUrl + href);
                }
            }
            return chapters;
        } catch (IOException e) {
            decodeException(e, "Can't get chapters; {}");
            return Collections.emptySet();
        }
    }
}
