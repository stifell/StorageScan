package com.stifell;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.InputStream;
import java.net.URL;

/**
 * @author stifell on 06.03.2025
 */
public class MainApp extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Storage Scan");

        // Загружаем FXML
        URL fxmlLocation = getClass().getResource("/mainView.fxml");
        FXMLLoader loader = new FXMLLoader(fxmlLocation);
        Parent root = loader.load();

        InputStream icon = getClass().getResourceAsStream("/icon.png");
        if (icon != null) {
            stage.getIcons().add(new Image(icon));
        }

        Scene scene = new Scene(root);
        stage.setScene(scene);

        stage.setOnShown(event -> {
            double borderWidth = stage.getWidth() - scene.getWidth();
            double borderHeight = stage.getHeight() - scene.getHeight();
            stage.setMinWidth(800 + borderWidth);
            stage.setMinHeight(600 + borderHeight);
        });

        stage.show();
    }
}
