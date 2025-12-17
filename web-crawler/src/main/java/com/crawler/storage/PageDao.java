package com.crawler.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PageDao {

    private final Connection conn;

    public PageDao(Database db) {
        this.conn = db.getConnection();
    }

    public void savePage(String url,
                         String title,
                         String status,
                         int hits,
                         String filePath) {

        String sql = """
            INSERT INTO pages(url, title, status, hits, file_path, crawled_at)
            VALUES(?,?,?,?,?,datetime('now'))
            ON CONFLICT(url) DO UPDATE SET
                title = excluded.title,
                status = excluded.status,
                hits = excluded.hits,
                file_path = excluded.file_path,
                crawled_at = excluded.crawled_at;
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, url);
            ps.setString(2, title);
            ps.setString(3, status);
            ps.setInt(4, hits);
            ps.setString(5, filePath);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
