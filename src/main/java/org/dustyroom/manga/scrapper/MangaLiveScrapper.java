package org.dustyroom.manga.scrapper;

import lombok.Builder;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import static org.dustyroom.manga.scrapper.LoadingTool.download;
import static org.dustyroom.manga.scrapper.Utils.cleanHref;
import static org.dustyroom.manga.scrapper.Utils.extractPageLinks;
import static org.dustyroom.manga.scrapper.Utils.getFileName;
import static org.dustyroom.manga.scrapper.Utils.getPathAndCreateFolders;

@Builder
public class MangaLiveScrapper {

    private String mangaRoot;
    private boolean needMature;
    private String mangaName;

    public void scrap() throws Exception {
        Set<String> chapters = getChapters();
        String DOMAIN = mangaRoot.substring(0, mangaRoot.lastIndexOf("/"));

        for (String chapter : chapters) {
            Path path = getPathAndCreateFolders(mangaName, chapter);
            Set<String> chapterPages = getChapterPages(DOMAIN + chapter);
            for (String chapterPage : chapterPages) {
                URL url = new URL(chapterPage);
                String fileName = getFileName(url);
                if (fileName.contains("?")) {
                    fileName = fileName.substring(0, fileName.indexOf("?"));
                }

                File outputFile = new File(path.toFile(), fileName);
                download(url, outputFile, fileName);
            }
        }
    }

    private Set<String> getChapterPages(String chapter) throws Exception {
        Document document = Jsoup.connect(chapter).get();
        Elements scripts = document.select("script");
        for (Element script : scripts) {
            String data = script.data();
            String CHAPTERS_INIT = "rm_h.initReader( ";
            if (script.attr("type").equals("text/javascript")
                    && StringUtils.isNotBlank(data)
                    && data.contains(CHAPTERS_INIT)) {
                return extractPageLinks(data.substring(data.indexOf(CHAPTERS_INIT)));
            }
        }
        return Collections.emptySet();
    }

    private Set<String> getChapters() throws Exception {
        Document document = Jsoup.connect(mangaRoot).get();
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
