package org.dustyroom.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.dustyroom.scrapping.ForbiddenDomain.getFallbackIfNeeded;

@UtilityClass
public class ScrapperUtils {
    public static String buildPageLink(String proxy, List<String> l) {
        String domain = (proxy != null) ? proxy : getFallbackIfNeeded(l.get(0));
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
        return Stream.of(href)
                .map(h -> h.contains("?") ? h.substring(0, h.indexOf("?")) : h)
                .map(h -> h.contains("#") ? h.substring(0, h.indexOf("#")) : h)
                .findFirst().orElse(href);
    }

    public static Set<String> extractPageLinks(String input, String proxy) {
        return Stream.of(input)
                .map(ScrapperUtils::stripPagesString)
                .map(MappingUtils::mapToPagesList)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .map(l -> buildPageLink(proxy, l))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
}
