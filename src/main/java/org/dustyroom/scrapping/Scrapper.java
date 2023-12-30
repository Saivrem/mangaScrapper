package org.dustyroom.scrapping;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dustyroom.model.Manga;
import org.dustyroom.utils.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.dustyroom.scrapping.ForbiddenDomain.getFallbackIfNeeded;
import static org.dustyroom.utils.FileUtils.*;
import static org.dustyroom.utils.LoadingTool.download;
import static org.dustyroom.utils.LoggingUtils.*;

@Builder
@Slf4j
public class Scrapper {
    private final List<String> initializers = List.of("rm_h.readerInit( ", "rm_h.initReader( ");

    private boolean needMature;
    private String targetDir;
    private List<String> blacklist;

    private Manga manga;

    public void run() {
        LocalDateTime start = getCurrentTime();
        targetDir = FileUtils.prepareDirectoryOrDefault(targetDir, "mangaScrapping");
        log.info("{} Star loading {} to {}", start, manga.getName(), targetDir);

        for (String chapterLink : getChapters()) {
            Path chapterFolder = prepareChapterFolder(targetDir, manga.getName(), chapterLink);
            if (chapterFolder == null) {
                continue;
            }
            for (String chapterPage : getChapterPages(chapterLink)) {
                String fileName = assembleFilename(chapterPage);
                if (notBlacklisted(fileName)) {
                    download(chapterPage, chapterFolder.resolve(fileName));
                }
            }
            if (manga.isZip()) {
                zipChapter(chapterFolder);
            }
        }
        if (manga.isZip()) {
            cleanVolumeDirectories(targetDir, manga.getName());
        }

        LocalDateTime end = getCurrentTime();
        log.info("{} End loading {} to {}, time {}",
                end,
                manga.getName(),
                targetDir,
                timePassed(start, end)
        );
    }

    private boolean notBlacklisted(String fileName) {
        if (blacklist == null) {
            return true;
        }
        return blacklist.stream().noneMatch(fileName::contains);
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
                        return extractPageLinks(data.substring(data.indexOf(initializer)), manga.getServer());
                    }
                }
            }
        } catch (IOException e) {
            decodeAndLogException(e, "Can't get chapter pages; {}");
        }
        return Collections.emptySet();
    }

    private String getInitializer(String data) {
        return initializers.stream().filter(data::contains).findFirst().orElse(null);
    }

    private Set<String> getChapters() {
        String mangaPage = manga.getPage();
        String rootUrl = mangaPage.substring(0, mangaPage.lastIndexOf("/"));
        try {
            Document document = Jsoup.connect(mangaPage).get();
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
            decodeAndLogException(e, "Can't get chapters; {}");
            return Collections.emptySet();
        }
    }

    private String cleanHref(String href) {
        if (href.contains("?")) {
            href = href.substring(0, href.indexOf("?"));
        }
        if (href.contains(("#"))) {
            href = href.substring(0, href.indexOf("#"));
        }
        return href;
    }

    private Set<String> extractPageLinks(String input, String proxy) {
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
