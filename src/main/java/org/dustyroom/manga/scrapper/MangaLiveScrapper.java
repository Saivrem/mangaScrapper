package org.dustyroom.manga.scrapper;

import lombok.Builder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.dustyroom.manga.MangaUtils.cleanHref;
import static org.dustyroom.manga.MangaUtils.extractPageLinks;
import static org.dustyroom.manga.MangaUtils.getFileName;
import static org.dustyroom.manga.MangaUtils.prepareChapterFolder;
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
        try {
            Set<String> chapters = getChapters();
            String rootUrl = mangaPageLink.substring(0, mangaPageLink.lastIndexOf("/"));

            for (String chapter : chapters) {
                Path chapterFolder = prepareChapterFolder(targetDir, mangaName, chapter);
                Set<String> chapterPages = getChapterPages(rootUrl + chapter);
                for (String chapterPage : chapterPages) {
                    URL url = new URL(chapterPage);
                    String fileName = getFileName(url);
                    if (fileName.contains("?")) {
                        fileName = fileName.substring(0, fileName.indexOf("?"));
                    }

                    File outputFile = new File(chapterFolder.toFile(), fileName);
                    download(url, outputFile, fileName);
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private Set<String> getChapterPages(String chapter) throws IOException {
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
        return Collections.emptySet();
    }

    private String getInitializer(String data) {
        return initializers.stream().filter(data::contains).findFirst().orElse(null);
    }

    private Set<String> getChapters() throws IOException {
        Document document = Jsoup.connect(mangaPageLink).get();
        Elements allLinks = document.select("a[href]");
        Set<String> chapters = new TreeSet<>();
        for (Element link : allLinks) {
            if (link.classNames().contains("chapter-link")) {
                StringBuilder href = new StringBuilder(cleanHref(link.attr("href")));
                if (needMature) {
                    href.append("?mtr=1");
                }
                chapters.add(href.toString());
            }
        }
        return chapters;
    }
}
