package com.crawler.parser;

public class HtmlCleaner {

    public String clean(String html) {
        if (html == null) return "";

        // Видаляємо <script>...</script>
        html = html.replaceAll("(?is)<script.*?>.*?</script>", "");

        // Видаляємо <style>...</style>
        html = html.replaceAll("(?is)<style.*?>.*?</style>", "");

        // Видаляємо коментарі <!-- -->
        html = html.replaceAll("(?is)<!--.*?-->", "");

        return html;
    }
}
