package com.crawler.storage;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;

public class Database {

    private static final String DB_DIR = "data";
    private static final String DB_FILE = "crawler.db";

    private static Database instance;
    private final Connection connection;

    private Database() throws SQLException {
        try {
            Path dir = Paths.get(DB_DIR);
            Files.createDirectories(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String url = "jdbc:sqlite:" + DB_DIR + "/" + DB_FILE;
        this.connection = DriverManager.getConnection(url);
        initSchema();
    }

    public static synchronized Database getInstance() throws SQLException {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    private void initSchema() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS pages (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                url TEXT UNIQUE,
                title TEXT,
                status TEXT,
                hits INTEGER,
                file_path TEXT,
                crawled_at TEXT
            );
            """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
