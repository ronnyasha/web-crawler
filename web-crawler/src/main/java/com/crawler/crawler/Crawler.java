package com.crawler.crawler;

import com.crawler.storage.PageDao;
import com.crawler.utils.HtmlCleaner;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

public class Crawler {

    private final HtmlFetcher fetcher = new HtmlFetcher();
    private final LinkExtractor extractor = new LinkExtractor();
    private final PageDao pageDao;
    private final Path pagesDir;

    public interface Listener {
        void onPageResult(String url, String title, String status, int hits);
        void onProgress(int processed, int total);
        void onFinish();
    }

    public Crawler(PageDao pageDao) {
        this.pageDao = pageDao;
        this.pagesDir = Paths.get("data", "pages");
        try {
            Files.createDirectories(pagesDir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start(String startUrl, String term, int maxPages, Listener listener) {
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();

        queue.add(startUrl);
        int processed = 0;

        while (!queue.isEmpty()
                && processed < maxPages
                && !Thread.currentThread().isInterrupted()) {

            String url = queue.poll();
            if (!visited.add(url)) {
                continue;
            }

            try {
                String html = fetcher.fetch(url);

                String cleaned = HtmlCleaner.clean(html);

                int hits = 0;
                if (!term.isEmpty()) {
                    hits = cleaned.toLowerCase()
                            .split(Pattern.quote(term.toLowerCase()), -1).length - 1;
                }

                String title = "";
                var titleMatcher = Pattern
                        .compile("<title>(.*?)</title>",
                                Pattern.CASE_INSENSITIVE | Pattern.DOTALL)
                        .matcher(cleaned);
                if (titleMatcher.find()) {
                    title = titleMatcher.group(1).trim();
                }

                String fileName = "page_" + (processed + 1) + ".html";
                Path filePath = pagesDir.resolve(fileName);
                Files.writeString(filePath, cleaned, StandardCharsets.UTF_8);

                if (pageDao != null) {
                    pageDao.savePage(url, title, "OK", hits, filePath.toString());
                }

                listener.onPageResult(url, title, "OK", hits);

                for (String link : extractor.extract(cleaned)) {
                    String normalized = normalizeLink(url, link);
                    if (!visited.contains(normalized) && queue.size() < maxPages) {
                        queue.add(normalized);
                    }
                }

            } catch (Exception e) {
                if (pageDao != null) {
                    pageDao.savePage(url, "", "ERR", 0, null);
                }
                listener.onPageResult(url, "", "ERR", 0);
            }

            processed++;
            listener.onProgress(processed, maxPages);
        }

        listener.onFinish();
    }

    private String normalizeLink(String baseUrl, String link) {
        if (link.startsWith("http://") || link.startsWith("https://")) {
            return link;
        }
        try {
            java.net.URI base = new java.net.URI(baseUrl);
            return base.resolve(link).toString();
        } catch (Exception e) {
            return link;
        }
    }
}
