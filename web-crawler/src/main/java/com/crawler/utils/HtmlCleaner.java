package com.crawler.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class HtmlCleaner {

    public static String clean(String html) {

        Document doc = Jsoup.parse(html);

        // видаляємо script, style, meta, noscript
        doc.select("script, style, meta, noscript, link").remove();

        // видаляємо header, nav, footer, aside
        doc.select("header, nav, footer, aside").remove();

        // видаляємо типові рекламні блоки
        doc.select("[id*=ad], [class*=ad], [id*=promo], [class*=promo], [id*=banner], [class*=banner]").remove();

        // прибрати пусті div та інші пусті елементи
        for (Element el : doc.select("*")) {
            if (el.text().trim().isEmpty() && el.children().isEmpty()) {
                el.remove();
            }
        }

        return doc.html();
    }
}
