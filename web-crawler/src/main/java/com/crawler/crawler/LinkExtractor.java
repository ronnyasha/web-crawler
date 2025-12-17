package com.crawler.crawler;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LinkExtractor {

    private static final Pattern LINK_PATTERN =
            Pattern.compile("<a\\s+(?:[^>]*?\\s+)?href=[\"']([^\"']+)[\"']", Pattern.CASE_INSENSITIVE);

    public Set<String> extract(String html) {
        Set<String> links = new HashSet<>();
        Matcher matcher = LINK_PATTERN.matcher(html);

        while (matcher.find()) {
            String link = matcher.group(1);

            // Ігноруємо якорі, mailto, tel
            if (link.startsWith("#")) continue;
            if (link.startsWith("mailto:")) continue;
            if (link.startsWith("tel:")) continue;

            links.add(link);
        }

        return links;
    }
}
