package org.example.courseworkManager;


import javafx.stage.FileChooser;
import javafx.stage.Window;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Exports the current dataset from DataModel into either
 * CSV or JSON format using a file chooser. Headers preserved.
 */

public class DataExporter {

    public static void exportData(DataModel dataModel) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Data File");

        // Set initial directory from app config
        String lastDir = AppConfig.getInstance().getLastDirectory();
        if (lastDir != null) {
            fileChooser.setInitialDirectory(new File(lastDir));
        }

        // Set file extensions
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv"),
                new FileChooser.ExtensionFilter("JSON Files", "*.json")
        );

        // Show file chooser dialog
        Window window = javafx.stage.Window.getWindows().stream()
                .filter(Window::isShowing)
                .findFirst()
                .orElse(null);

        File file = fileChooser.showSaveDialog(window);

        if (file != null) {
            // Save the directory
            AppConfig.getInstance().setLastDirectory(file.getParent());

            try {
                List<String> headers = dataModel.getColumnNames();
                List<Map<String, Object>> data = dataModel.getData();

                if (file.getName().toLowerCase().endsWith(".csv")) {
                    // Export CSV
                    try (CSVPrinter printer = new CSVPrinter(
                            new FileWriter(file),
                            CSVFormat.DEFAULT.withHeader(headers.toArray(new String[0])))) {

                        for (Map<String, Object> row : data) {
                            Object[] values = headers.stream()
                                    .map(row::get)
                                    .toArray();
                            printer.printRecord(values);
                        }
                    }
                } else {
                    // Export JSON
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.writerWithDefaultPrettyPrinter()
                            .writeValue(file, data);
                }

            } catch (IOException e) {
                showError("Error exporting file", e.getMessage());
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
