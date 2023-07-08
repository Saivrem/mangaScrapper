package org.dustyroom.scrapping;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum ForbiddenDomain {

    ONEWAY("one-way.work", "https://h1.rmr.rocks/");

    private final String domain;
    private final String fallback;

    public static String getFallbackIfNeeded(String currentDomain) {
        if (currentDomain == null) {
            return null;
        }
        return Arrays.stream(values()).filter(enumValue -> currentDomain.contains(enumValue.domain))
                     .findFirst()
                     .map(ForbiddenDomain::getFallback)
                     .orElse(currentDomain);
    }
}
