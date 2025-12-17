package com.crawler.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/ui/main_view.fxml")
        );

        Scene scene = new Scene(loader.load(), 900, 600);

        MainController controller = loader.getController();

        primaryStage.setTitle("Web Crawler");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
