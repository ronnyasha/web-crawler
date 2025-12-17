package com.crawler.ui;

import com.crawler.crawler.Crawler;
import com.crawler.models.PageData;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import com.crawler.storage.Database;
import com.crawler.storage.PageDao;
import java.sql.SQLException;


public class MainController {

    @FXML private TextField startUrlField;
    @FXML private TextField termField;
    @FXML private TextField maxPagesField;

    @FXML private Button startButton;
    @FXML private Button stopButton;
    @FXML private ProgressBar progressBar;
    @FXML private Label statusLabel;
    @FXML private Label statsLabel;

    @FXML private TableView<PageData> resultsTable;
    @FXML private TableColumn<PageData, String> urlColumn;
    @FXML private TableColumn<PageData, String> titleColumn;
    @FXML private TableColumn<PageData, String> statusColumn;
    @FXML private TableColumn<PageData, Integer> hitsColumn;

    private final ObservableList<PageData> data = FXCollections.observableArrayList();
    private Crawler crawler;
    private Thread crawlThread;

    @FXML
    private void initialize() {
        urlColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUrl()));
        titleColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitle()));
        statusColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus()));
        hitsColumn.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getHits()).asObject());

        resultsTable.setItems(data);

        progressBar.setProgress(0);
        statusLabel.setText("Idle");
        stopButton.setDisable(true);

        try {
            Database db = Database.getInstance();
            PageDao pageDao = new PageDao(db);
            crawler = new Crawler(pageDao);
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Cannot init database: " + e.getMessage());
        }
    }

    @FXML
    private void onStartClicked() {
        String startUrl = startUrlField.getText().trim();
        String term = termField.getText().trim();
        String maxPagesText = maxPagesField.getText().trim();

        if (startUrl.isEmpty() || term.isEmpty() || maxPagesText.isEmpty()) {
            showError("Please fill all fields.");
            return;
        }

        int maxPages;
        try {
            maxPages = Integer.parseInt(maxPagesText);
        } catch (NumberFormatException e) {
            showError("Max pages must be a number.");
            return;
        }

        data.clear();
        progressBar.setProgress(0);
        statsLabel.setText("Pages: 0 | Hits: 0");
        statusLabel.setText("Running...");

        startButton.setDisable(true);
        stopButton.setDisable(false);

        crawlThread = new Thread(() -> {
            crawler.start(startUrl, term, maxPages, new Crawler.Listener() {
                @Override
                public void onPageResult(String url, String title, String status, int hits) {
                    javafx.application.Platform.runLater(() ->
                            data.add(new PageData(url, title, status, hits))
                    );
                }

                @Override
                public void onProgress(int processed, int total) {
                    javafx.application.Platform.runLater(() -> {
                        progressBar.setProgress((double) processed / total);
                        statsLabel.setText("Pages: " + processed + " | Hits: " + getTotalHits());
                    });
                }

                @Override
                public void onFinish() {
                    javafx.application.Platform.runLater(() -> {
                        startButton.setDisable(false);
                        stopButton.setDisable(true);
                        statusLabel.setText("Finished");
                    });
                }
            });
        });
        crawlThread.setDaemon(true);
        crawlThread.start();
    }

    @FXML
    private void onStopClicked() {
        if (crawlThread != null && crawlThread.isAlive()) {
            crawlThread.interrupt();
        }
        startButton.setDisable(false);
        stopButton.setDisable(true);
        statusLabel.setText("Stopped");
    }

    private int getTotalHits() {
        return data.stream()
                .mapToInt(PageData::getHits)
                .sum();
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    public void addTestRow() {
        data.add(new PageData("https://example.com", "Example Title", "OK", 5));
    }
}
