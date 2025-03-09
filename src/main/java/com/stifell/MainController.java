package com.stifell;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

/**
 * @author stifell on 07.03.2025
 */
public class MainController {
    @FXML
    private ComboBox<String> driveComboBox;

    @FXML
    private Label labelTotalSpace, labelUsedSpace, labelFreeSpace, statusLabel;

    @FXML
    private Button buttonAnalysis;

    @FXML
    private MenuItem menuItemExit;

    @FXML
    private MenuItem menuItemSelectFile;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private GridPane diskInfoGrid;

    @FXML
    public void initialize() {
        // Заполняем ComboBox списком доступных дисков
        File[] roots = File.listRoots();
        if (roots != null) {
            for (File root : roots) {
                driveComboBox.getItems().add(root.getAbsolutePath());
            }
        }

//        if (!driveComboBox.getItems().isEmpty()) {
//            driveComboBox.getSelectionModel().selectFirst();
//        }

        driveComboBox.setPromptText("...");

        diskInfoGrid.setVisible(false);

        menuItemSelectFile.setOnAction(event -> handleSelectFolder());
        menuItemExit.setOnAction(event -> handleExit());
        buttonAnalysis.setOnAction(event -> handlePreAnalyze());
        driveComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()){
                diskInfoGrid.setVisible(true);
                handlePreAnalyze();
            }
        });

        progressBar.setVisible(false);
        statusLabel.setText("Ожидание выбора диска...");
    }

    @FXML
    private void handleSelectFolder() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Выберите папку для анализа");

        Stage stage = (Stage) statusLabel.getScene().getWindow(); // Получаем текущее окно
        File selectedDirectory = directoryChooser.showDialog(stage);

        if (selectedDirectory != null) {
            String folderPath = selectedDirectory.getAbsolutePath();
            System.out.println("Выбрана папка: " + folderPath);

            // Если выбранной папки ещё нет в ComboBox, добавляем её
            if (!driveComboBox.getItems().contains(folderPath)) {
                driveComboBox.getItems().add(folderPath);
            }
            // Выбираем папку в ComboBox
            driveComboBox.getSelectionModel().select(folderPath);
            handlePreAnalyze();
        }
    }

    @FXML
    private void handlePreAnalyze() {
        String selectedDrivePath = driveComboBox.getValue();
        if (selectedDrivePath == null || selectedDrivePath.isEmpty()) {
            return;
        }

        File selectedDrive = new File(selectedDrivePath);
        if (!selectedDrive.exists()) {
            return;
        }

        // Получаем информацию о диске
        long totalSpace = selectedDrive.getTotalSpace(); // байты
        long freeSpace = selectedDrive.getFreeSpace();   // байты
        long usedSpace = totalSpace - freeSpace;         // байты

        // Переводим в ГБ
        double totalGB = totalSpace / (1024.0 * 1024.0 * 1024.0);
        double freeGB  = freeSpace  / (1024.0 * 1024.0 * 1024.0);
        double usedGB  = usedSpace  / (1024.0 * 1024.0 * 1024.0);

        // Заполняем лейблы
        labelTotalSpace.setText(String.format("%.1f ГБ", totalGB));
        labelUsedSpace.setText(String.format("%.1f ГБ (%.1f%%)", usedGB, usedGB / totalGB * 100));
        labelFreeSpace.setText(String.format("%.1f ГБ (%.1f%%)", freeGB, freeGB / totalGB * 100));

        // Обновляем статус
        statusLabel.setText("Анализ завершён для диска: " + selectedDrivePath);
    }

    private void handleExit() {
        // Логика завершения работы приложения
        System.out.println("Выход из приложения");
        System.exit(0);
    }
}