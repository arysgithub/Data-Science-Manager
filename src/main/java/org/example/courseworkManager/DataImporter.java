package org.example.courseworkManager;

import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Provides a UI file chooser to import data from CSV or JSON files
 * and load it into the DataModel. Infers headers and numeric values.
 */

public class DataImporter {

    public static void importData(DataModel dataModel) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import Data File");

        // Set initial directory from app config
        String lastDir = AppConfig.getInstance().getLastDirectory();
        if (lastDir != null) {
            fileChooser.setInitialDirectory(new File(lastDir));
        }

        // Set file extensions
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Data Files", "*.csv", "*.json"),
                new FileChooser.ExtensionFilter("CSV Files", "*.csv"),
                new FileChooser.ExtensionFilter("JSON Files", "*.json")
        );

        // Show file chooser dialog
        Window window = javafx.stage.Window.getWindows().stream()
                .filter(Window::isShowing)
                .findFirst()
                .orElse(null);

        File file = fileChooser.showOpenDialog(window);

        if (file != null) {
            // Save the directory
            AppConfig.getInstance().setLastDirectory(file.getParent());

            try {
                List<Map<String, Object>> data;
                List<String> headers;

                if (file.getName().toLowerCase().endsWith(".csv")) {
                    // Import CSV
                    try (CSVParser parser = CSVParser.parse(file,
                            java.nio.charset.StandardCharsets.UTF_8,
                            CSVFormat.DEFAULT.withHeader().withTrim())) {

                        headers = new ArrayList<>(parser.getHeaderMap().keySet());
                        data = new ArrayList<>();

                        for (CSVRecord record : parser) {
                            Map<String, Object> row = new HashMap<>();
                            for (String header : headers) {
                                String value = record.get(header);
                                // Try to parse as number if possible
                                try {
                                    row.put(header, Double.parseDouble(value));
                                } catch (NumberFormatException e) {
                                    row.put(header, value);
                                }
                            }
                            data.add(row);
                        }
                    }
                } else {
                    // Import JSON
                    data = JsonImporter.importJson(file);
                    if (!data.isEmpty()) {
                        headers = new ArrayList<>(data.get(0).keySet());
                    } else {
                        headers = new ArrayList<>();
                    }
                }

                dataModel.setData(data, headers);

            } catch (IOException e) {
                showError("Error importing file", e.getMessage());
            }
        }
    }

    private static void showError(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}