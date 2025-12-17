package com.crawler.models;

public class PageData {
    private String url;
    private String title;
    private String status;
    private int hits;

    public PageData(String url, String title, String status, int hits) {
        this.url = url;
        this.title = title;
        this.status = status;
        this.hits = hits;
    }

    public String getUrl() { return url; }
    public String getTitle() { return title; }
    public String getStatus() { return status; }
    public int getHits() { return hits; }
}
