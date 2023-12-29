package org.dustyroom.scrapping;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.dustyroom.scrapping.ForbiddenDomain.getFallbackIfNeeded;
import static org.dustyroom.utils.LoadingTool.download;
import static org.dustyroom.utils.LoggingUtils.*;

@Builder
@Slf4j
public class Scrapper {
    private final List<String> initializers = List.of("rm_h.readerInit( ", "rm_h.initReader( ");
    private final String s = File.separator;
    private String mangaPageLink;
    private boolean needMature;
    private String mangaName;
    private String proxy;
    private String targetDir;

    public void run() {
        LocalDateTime start = getCurrentTime();
        if (targetDir == null) {
            targetDir = System.getProperty("user.home") + s + "mangaScrapping" + s;
        }
        log.info("{} Star loading {} to {}", start, mangaName, targetDir);

        for (String chapterLink : getChapters()) {
            Path chapterFolder = prepareChapterFolder(targetDir, mangaName, chapterLink);
            if (chapterFolder == null) {
                continue;
            }
            for (String chapterPage : getChapterPages(chapterLink)) {
                String fileName = getFileName(chapterPage);
                download(chapterPage, chapterFolder.resolve(fileName));
            }
            zipChapter(chapterFolder);
        }

        cleanVolumeDirectories();

        LocalDateTime end = getCurrentTime();
        log.info("{} End loading {} to {}, time {}",
                end,
                mangaName,
                targetDir,
                timePassed(start, end)
        );
    }

    private void cleanVolumeDirectories() {
        try {
            Path clean = Path.of(targetDir + mangaName);
            Files.walk(clean).filter(Files::isDirectory).forEach(file -> {
                try {
                    Files.delete(file);
                } catch (IOException ignore) {
                }
            });
        } catch (IOException e) {
            log.warn("Can't remove volume dir {}", e.getMessage());
        }
    }

    private static void zipChapter(Path chapterFolder) {
        Path zipFilePath = Path.of(chapterFolder.getParent().toString() + "-ch" + chapterFolder.getFileName().toString() + ".zip");
        try (FileOutputStream fos = new FileOutputStream(zipFilePath.toFile());
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            Files.walk(chapterFolder)
                    .filter(Files::isRegularFile)
                    .forEach(file -> {
                        try {
                            Path relativePath = chapterFolder.relativize(file);
                            zos.putNextEntry(new ZipEntry(relativePath.toString()));
                            byte[] bytes = Files.readAllBytes(file);
                            zos.write(bytes, 0, bytes.length);
                            zos.closeEntry();
                            Files.delete(file);
                        } catch (IOException e) {
                            log.warn("Error during archiving {}", file);
                        }
                    });
            fos.flush();
            Files.delete(chapterFolder);
        } catch (IOException e) {
            log.warn("Error during archiving {}", chapterFolder);
        }
        log.info("Chapter is zipped to {}", zipFilePath);
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
            decodeAndLogException(e, "Can't get chapter pages; {}");
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
            decodeAndLogException(e, "Can't get chapters; {}");
            return Collections.emptySet();
        }
    }

    /**
     * Creates a folder to store chapter files, if folder already exists returns null which should mean - no need to process.
     *
     * @param workdir         I should rename this one, probably it's a target directory for all scrapping
     * @param mangaName       the name of manga to load (root dir for it should have same name)
     * @param fullChapterName probably it's what stands for tom number
     * @return chapter path or null if it already exists, hence, shan't be processed.
     */
    private Path prepareChapterFolder(String workdir, String mangaName, String fullChapterName) {
        String volume = fullChapterName.substring(fullChapterName.lastIndexOf("/vol"), fullChapterName.lastIndexOf("/"));
        String chapter = fullChapterName.substring(fullChapterName.lastIndexOf("/"));
        if (chapter.contains("?")) {
            chapter = chapter.substring(0, chapter.indexOf("?"));
        }

        Path path = Paths.get(String.format("%s%s%s%s", workdir, mangaName, volume, chapter));
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            decodeAndLogException(e, "Couldn't create folder; {}");
            return null;
        }
        return path;
    }

    private String getFileName(String chapterPage) {
        String rawName = chapterPage.substring(chapterPage.lastIndexOf("/") + 1);
        if (rawName.contains("?")) {
            rawName = rawName.substring(0, rawName.indexOf("?"));
        }

        StringBuilder fileNameBuilder = new StringBuilder();
        StringBuilder extensionBuilder = new StringBuilder();

        int extensionIndex = rawName.lastIndexOf(".");
        char[] rawNameChars = rawName.toCharArray();
        for (int i = extensionIndex - 1; i >= 0; i--) {
            char current = rawNameChars[i];
            if (!Character.isDigit(current)) continue;
            fileNameBuilder.append(rawNameChars[i]);
        }
        for (int i = extensionIndex; i < rawNameChars.length; i++) {
            char current = rawNameChars[i];
            if (!Character.isLetter(current) && current != '.') break;
            extensionBuilder.append(rawNameChars[i]);
        }
        return fileNameBuilder.reverse() + extensionBuilder.toString();
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
