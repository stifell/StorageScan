package com.stifell;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;

/**
 * @author stifell on 07.03.2025
 */
public class MainController {

    @FXML
    private MenuItem menuItemExit;

    @FXML
    private MenuItem menuItemSelectFile;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label statusLabel;

    @FXML
    public void initialize() {
        // Привязка обработчиков событий для меню
        menuItemSelectFile.setOnAction(event -> handleSelectFile());
        menuItemExit.setOnAction(event -> handleExit());

        // Пример начальной инициализации
        statusLabel.setText("Ожидание команды...");
        progressBar.setProgress(0);
    }

    private void handleSelectFile() {
        // Логика для выбора файла, например, вызов диалога выбора файла
        System.out.println("Выбрана опция 'Выбрать файл'");
        statusLabel.setText("Файл выбран");
    }

    private void handleExit() {
        // Логика завершения работы приложения
        System.out.println("Выход из приложения");
        System.exit(0);
    }
}
