package org.dustyroom.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@UtilityClass
public class ScrapperUtils {

    private final static int PAGE_ELEMENT_SIZE = 5;

    private static final String s = File.separator;

    public String prepareDirectoryOrDefault(String targetDir, String defaultDirectory) {
        if (targetDir == null) {
            return System.getProperty("user.home") + s + defaultDirectory + s;
        }
        return targetDir;
    }

    public String assembleFilename(String chapterPage) {
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
        return fileNameBuilder.isEmpty() ? rawName : fileNameBuilder.reverse() + extensionBuilder.toString();
    }

    public static String buildPageLink(String mirror, List<String> l) {
        // Technically, each List passed here should have 5 arguments:
        // root domain, empty string, page link, width and height
        // Hence, if list has fewer elements code is, most probably, not ready for it
        if (l.isEmpty() || l.size() < PAGE_ELEMENT_SIZE) {
            log.warn("Page element has {} elements which is less than expected {}", l.size(), PAGE_ELEMENT_SIZE);
            return null;
        }
        String domain = mirror != null ? mirror : l.get(0);
        String path = l.get(2);
        if (StringUtils.isNoneBlank(domain, path)) {
            return domain + path;
        }
        return null;
    }

    public static String stripPagesString(String i) {
        return i.substring(i.indexOf("[[", i.lastIndexOf("]]" + 2)))
                .replaceAll("'", "\"");
    }

    public static String cleanHref(String href) {
        return Optional.of(href)
                .map(h -> h.contains("?") ? h.substring(0, h.indexOf("?")) : h)
                .map(h -> h.contains("#") ? h.substring(0, h.indexOf("#")) : h)
                .orElse(href);
    }

    public static Set<String> extractPageLinks(String input, String mirror) {
        return Stream.of(input)
                .map(ScrapperUtils::stripPagesString)
                .map(MappingUtils::mapToPagesList)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .filter(Objects::nonNull)
                .map(l -> buildPageLink(mirror, l))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
}
