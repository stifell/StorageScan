package com.stifell;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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
    private VBox statisticsVBox;

    private DirectorySizeCalculator analyzer = new DirectorySizeCalculator();
    private ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
    private HashMap<String, Long> sizes;
    private PieChart pieChart;

    @FXML
    public void initialize() {
        // Заполняем ComboBox списком доступных дисков
        File[] roots = File.listRoots();
        if (roots != null) {
            for (File root : roots) {
                driveComboBox.getItems().add(root.getAbsolutePath());
            }
        }

        driveComboBox.setPromptText("...");
        diskInfoGrid.setVisible(false);

        menuItemSelectFile.setOnAction(event -> handleSelectFolder());
        menuItemExit.setOnAction(event -> handleExit());
        buttonAnalysis.setOnAction(event -> buildPieChart());
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
        String folder = driveComboBox.getValue();
        if (folder == null || folder.isEmpty()) {
            return;
        }

        File selectedFolder = new File(folder);
        if (!selectedFolder.exists()) {
            return;
        }

        // Получаем информацию о диске
        long totalSpace = selectedFolder.getTotalSpace(); // байты
        long freeSpace = selectedFolder.getFreeSpace();   // байты
        long usedSpace = totalSpace - freeSpace;         // байты

        // Переводим в ГБ
        double totalGB = totalSpace / (1024.0 * 1024.0 * 1024.0);
        double freeGB  = freeSpace  / (1024.0 * 1024.0 * 1024.0);
        double usedGB  = usedSpace  / (1024.0 * 1024.0 * 1024.0);

        // Заполняем лейблы
        labelTotalSpace.setText(String.format("%.1f ГБ", totalGB));
        labelUsedSpace.setText(String.format("%.1f ГБ (%.1f%%)", usedGB, usedGB / totalGB * 100));
        labelFreeSpace.setText(String.format("%.1f ГБ (%.1f%%)", freeGB, freeGB / totalGB * 100));
    }

    private void buildPieChart(){
        String folder = driveComboBox.getValue();
        if (folder == null || folder.isEmpty()) {
            return;
        }

        long startTime = System.currentTimeMillis();

        sizes = analyzer.calculateFolderSize(Path.of(folder));

        long elapsedTimeMillis = System.currentTimeMillis() - startTime;
        double elapsedTimeSeconds = elapsedTimeMillis / 1000.0;
        System.out.println("Время расчета: " + elapsedTimeSeconds + " с");

        pieChart = new PieChart(pieChartData);
        pieChart.setTitle("Статистика по папкам: " + folder);
        pieChart.prefWidthProperty().bind(statisticsVBox.widthProperty());
        pieChart.prefHeightProperty().bind(statisticsVBox.heightProperty());
        pieChart.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        VBox.setVgrow(pieChart, Priority.ALWAYS);

        fillPieChart(folder);

        statisticsVBox.getChildren().clear();
        statisticsVBox.getChildren().add(pieChart);

        statisticsVBox.setStyle("-fx-border-color: red; -fx-border-width: 2;");
        statusLabel.setText("Анализ завершён для диска: " + folder);
    }

    private void fillPieChart(String path) {
        pieChart.setTitle("Статистика по папкам: " + path);
        // Фильтруем записи, чтобы получить элементы, находящиеся непосредственно в текущей директории
        var filteredEntries = sizes.entrySet().parallelStream()
                .filter(entry -> {
                    Path parent = Path.of(entry.getKey()).getParent();
                    return parent != null && parent.toString().equals(path);
                })
                .collect(Collectors.toList());

        // Вычисляем суммарный размер выбранных элементов (в байтах)
        double totalSize = filteredEntries.parallelStream()
                .mapToDouble(Map.Entry::getValue)
                .sum();

        // Создаем новый список данных для диаграммы
        ObservableList<PieChart.Data> newData = FXCollections.observableArrayList();
        for (var entry : filteredEntries) {
            String fullPath = entry.getKey();
            String displayName = new File(fullPath).getName();
            if (displayName.isEmpty()) {
                displayName = fullPath;
            }
            // Вычисляем размер в ГБ
            double sizeGB = entry.getValue() / (1024.0 * 1024.0 * 1024.0);
            // Вычисляем процент от общего размера текущей директории
            double percentage = totalSize > 0 ? (entry.getValue() * 100.0 / totalSize) : 0;
            // Формируем строку для отображения: имя (размер ГБ, процент)
            String label = String.format("%s(%.2f ГБ,%.1f%%)", displayName, sizeGB, percentage);

            PieChart.Data data = new PieChart.Data(label, entry.getValue());
            // При клике по сегменту переходим к выбранной директории (используем полный путь)
            data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    newNode.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
                        File file = new File(fullPath);
                        if (file.isDirectory()) { // Переход только если это папка
                            fillPieChart(fullPath);
                        }
                    });
                }
            });
            newData.add(data);
        }
        pieChart.setData(newData);
    }

    private void handleExit() {
        // Логика завершения работы приложения
        System.out.println("Выход из приложения");
        System.exit(0);
    }
}