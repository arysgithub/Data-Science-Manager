package org.example.courseworkManager;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.*;

/**
 * AnalysisPane is a VBox that provides tools to:
 * 1. Calculate Pearson correlation between two numeric columns.
 * 2. Generate summary statistics for all columns.
 */

public class AnalysisPane extends VBox {
    private final DataModel dataModel;
    private final ComboBox<String> column1ComboBox;
    private final ComboBox<String> column2ComboBox;
    private final TextArea resultArea;

    public AnalysisPane(DataModel dataModel) {
        this.dataModel = dataModel;
        this.setSpacing(10);

        // Create controls
        column1ComboBox = new ComboBox<>();
        column2ComboBox = new ComboBox<>();
        resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setPrefRowCount(10);

        // Create buttons
        Button correlationBtn = new Button("Calculate Correlation");
        correlationBtn.setOnAction(e -> calculateCorrelation());

        Button summaryBtn = new Button("Generate Summary");
        summaryBtn.setOnAction(e -> generateSummary());

        // Create layout
        HBox controlsBox = new HBox(10);
        controlsBox.getChildren().addAll(
                new Label("Column 1:"), column1ComboBox,
                new Label("Column 2:"), column2ComboBox,
                correlationBtn, summaryBtn
        );

        getChildren().addAll(controlsBox, resultArea);


        // Update columns when data changes using DataModel listener
        dataModel.addListener(() -> {
            updateColumns();
            resultArea.clear();
        });

        // Initial column update
        updateColumns();
    }

    /**
     * Updates both dropdowns with current column names from the dataset.
     */
    private void updateColumns() {
        List<String> columns = dataModel.getColumnNames();
        column1ComboBox.getItems().setAll(columns);
        column2ComboBox.getItems().setAll(columns);

        if (!columns.isEmpty()) {
            column1ComboBox.setValue(columns.get(0));
            column2ComboBox.setValue(columns.size() > 1 ? columns.get(1) : columns.get(0));
        }
    }

    /**
     * Calculates Pearson's correlation coefficient between two numeric columns.
     * Uses Apache Commons Math.
     */
    private void calculateCorrelation() {
        String col1 = column1ComboBox.getValue();
        String col2 = column2ComboBox.getValue();

        if (col1 == null || col2 == null) {
            showError("Please select two columns for correlation analysis.");
            return;
        }

        List<Double> values1 = new ArrayList<>();
        List<Double> values2 = new ArrayList<>();

        // Extract and validate numeric values from both columns
        for (Map<String, Object> row : dataModel.getData()) {
            Object val1 = row.get(col1);
            Object val2 = row.get(col2);

            if (val1 instanceof Number && val2 instanceof Number) {
                values1.add(((Number) val1).doubleValue());
                values2.add(((Number) val2).doubleValue());
            }
        }

        if (values1.isEmpty() || values2.isEmpty()) {
            showError("Selected columns must contain numeric data for correlation analysis.");
            return;
        }

        // Convert to primitive arrays for library function
        double[] array1 = values1.stream().mapToDouble(Double::doubleValue).toArray();
        double[] array2 = values2.stream().mapToDouble(Double::doubleValue).toArray();

        // Calculate Pearson correlation
        PearsonsCorrelation correlation = new PearsonsCorrelation();
        double correlationValue = correlation.correlation(array1, array2);

        resultArea.setText(String.format("Correlation Analysis Results:\n\n" +
                        "Columns: %s and %s\n" +
                        "Pearson's Correlation Coefficient: %.4f\n\n" +
                        "Interpretation:\n" +
                        "- Values close to 1 indicate strong positive correlation\n" +
                        "- Values close to -1 indicate strong negative correlation\n" +
                        "- Values close to 0 indicate weak or no correlation",
                col1, col2, correlationValue));
    }

    /**
     * Generates a summary of all columns in the dataset including:
     * - Count of values and nulls
     * - Basic descriptive stats for numeric columns
     * - Unique values count for non-numeric
     */
    private void generateSummary() {
        StringBuilder summary = new StringBuilder();

        for (String column : dataModel.getColumnNames()) {
            summary.append("Column: ").append(column).append("\n");

            List<Object> values = new ArrayList<>();
            int nullCount = 0;

            for (Map<String, Object> row : dataModel.getData()) {
                Object value = row.get(column);
                if (value == null) {
                    nullCount++;
                } else {
                    values.add(value);
                }
            }

            summary.append("Total values: ").append(values.size()).append("\n");
            summary.append("Null values: ").append(nullCount).append("\n");

            if (!values.isEmpty()) {
                Object firstValue = values.get(0);
                if (firstValue instanceof Number) {
                    DescriptiveStatistics stats = new DescriptiveStatistics();
                    values.forEach(v -> stats.addValue(((Number) v).doubleValue()));

                    summary.append(String.format("Mean: %.2f\n", stats.getMean()));
                    summary.append(String.format("Median: %.2f\n", stats.getPercentile(50)));
                    summary.append(String.format("Std Dev: %.2f\n", stats.getStandardDeviation()));
                    summary.append(String.format("Min: %.2f\n", stats.getMin()));
                    summary.append(String.format("Max: %.2f\n", stats.getMax()));
                } else {
                    // For non-numeric columns, show unique values count
                    long uniqueValues = values.stream().distinct().count();
                    summary.append("Unique values: ").append(uniqueValues).append("\n");
                }
            }

            summary.append("\n");
        }

        resultArea.setText(summary.toString());
    }

    /**
     * Displays a basic error dialog with a given message.
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
