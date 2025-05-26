package org.example.courseworkManager;

import org.jfree.chart.fx.ChartViewer;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.util.*;

public class VisualisationPane extends VBox {
    private final DataModel dataModel;
    private final ComboBox<String> xAxisComboBox;
    private final ComboBox<String> yAxisComboBox;
    private final ComboBox<ChartType> chartTypeComboBox;
    private ChartViewer chartViewer;

    public enum ChartType {
        SCATTER_PLOT("Scatter Plot"),
        LINE_CHART("Line Chart"),
        BAR_CHART("Bar Chart"),
        HISTOGRAM("Histogram");

        private final String displayName;

        ChartType(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    public VisualisationPane(DataModel dataModel) {
        this.dataModel = dataModel;
        this.setSpacing(10);

        // Create controls
        xAxisComboBox = new ComboBox<>();
        yAxisComboBox = new ComboBox<>();
        chartTypeComboBox = new ComboBox<>();
        chartTypeComboBox.getItems().addAll(ChartType.values());
        chartTypeComboBox.setValue(ChartType.SCATTER_PLOT);

        Button createChartBtn = new Button("Create Chart");
        createChartBtn.setOnAction(e -> createChart());

        // Create layout for controls
        HBox controlsBox = new HBox(10);
        controlsBox.getChildren().addAll(
                new Label("X Axis:"), xAxisComboBox,
                new Label("Y Axis:"), yAxisComboBox,
                new Label("Chart Type:"), chartTypeComboBox,
                createChartBtn
        );

        // Create chart area
        chartViewer = new ChartViewer(null);
        VBox.setVgrow(chartViewer, Priority.ALWAYS);

        getChildren().addAll(controlsBox, chartViewer);

        // Update columns when data changes
  //      dataModel.getData().addListener((javafx.collections.ListChangeListener.Change<?> c) -> {
//            updateColumns(); });
        // Update columns when data changes using DataModel listener
        dataModel.addListener(() -> {
            updateColumns();
            // Clear existing chart when data changes
            chartViewer.setChart(null);
        });

        // Initial column update
        updateColumns();
    }

    private void updateColumns() {
        List<String> columns = dataModel.getColumnNames();
        xAxisComboBox.getItems().setAll(columns);
        yAxisComboBox.getItems().setAll(columns);

        if (!columns.isEmpty()) {
            xAxisComboBox.setValue(columns.get(0));
            yAxisComboBox.setValue(columns.size() > 1 ? columns.get(1) : columns.get(0));
        }
    }

    private void createChart() {
        String xColumn = xAxisComboBox.getValue();
        String yColumn = yAxisComboBox.getValue();
        ChartType chartType = chartTypeComboBox.getValue();

        if (xColumn == null || yColumn == null) {
            showError("Please select columns for both axes.");
            return;
        }

        JFreeChart chart = null;

        switch (chartType) {
            case SCATTER_PLOT:
                chart = createScatterPlot(xColumn, yColumn);
                break;
            case LINE_CHART:
                chart = createLineChart(xColumn, yColumn);
                break;
            case BAR_CHART:
                chart = createBarChart(xColumn, yColumn);
                break;
            case HISTOGRAM:
                chart = createHistogram(xColumn);
                break;
        }

        if (chart != null) {
            chartViewer.setChart(chart);
        }
    }

    private JFreeChart createScatterPlot(String xColumn, String yColumn) {
        XYSeries series = new XYSeries("Data");

        for (Map<String, Object> row : dataModel.getData()) {
            Object xValue = row.get(xColumn);
            Object yValue = row.get(yColumn);

            if (xValue instanceof Number && yValue instanceof Number) {
                series.add(((Number) xValue).doubleValue(), ((Number) yValue).doubleValue());
            }
        }

        XYSeriesCollection dataset = new XYSeriesCollection(series);
        return ChartFactory.createScatterPlot(
                "Scatter Plot", xColumn, yColumn,
                dataset, PlotOrientation.VERTICAL,
                true, true, false
        );
    }

    private JFreeChart createLineChart(String xColumn, String yColumn) {
        XYSeries series = new XYSeries("Data");

        List<Map<String, Object>> sortedData = new ArrayList<>(dataModel.getData());
        sortedData.sort((a, b) -> {
            Number x1 = (Number) a.get(xColumn);
            Number x2 = (Number) b.get(xColumn);
            return Double.compare(x1.doubleValue(), x2.doubleValue());
        });

        for (Map<String, Object> row : sortedData) {
            Object xValue = row.get(xColumn);
            Object yValue = row.get(yColumn);

            if (xValue instanceof Number && yValue instanceof Number) {
                series.add(((Number) xValue).doubleValue(), ((Number) yValue).doubleValue());
            }
        }

        XYSeriesCollection dataset = new XYSeriesCollection(series);
        return ChartFactory.createXYLineChart(
                "Line Chart", xColumn, yColumn,
                dataset, PlotOrientation.VERTICAL,
                true, true, false
        );
    }

    private JFreeChart createBarChart(String xColumn, String yColumn) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        Map<Object, Double> aggregatedData = new HashMap<>();
        for (Map<String, Object> row : dataModel.getData()) {
            Object xValue = row.get(xColumn);
            Object yValue = row.get(yColumn);

            if (xValue != null && yValue instanceof Number) {
                String category = xValue.toString();
                double value = ((Number) yValue).doubleValue();
                aggregatedData.merge(category, value, Double::sum);
            }
        }

        for (Map.Entry<Object, Double> entry : aggregatedData.entrySet()) {
            dataset.addValue(entry.getValue(), "Data", entry.getKey().toString());
        }

        return ChartFactory.createBarChart(
                "Bar Chart", xColumn, yColumn,
                dataset, PlotOrientation.VERTICAL,
                true, true, false
        );
    }

    private JFreeChart createHistogram(String column) {
        List<Double> values = new ArrayList<>();

        for (Map<String, Object> row : dataModel.getData()) {
            Object value = row.get(column);
            if (value instanceof Number) {
                values.add(((Number) value).doubleValue());
            }
        }

        if (values.isEmpty()) {
            showError("No numeric data available for histogram.");
            return null;
        }

        HistogramDataset dataset = new HistogramDataset();
        dataset.addSeries(
                "Data",
                values.stream().mapToDouble(Double::doubleValue).toArray(),
                Math.min(50, values.size() / 2)
        );

        return ChartFactory.createHistogram(
                "Histogram", column, "Frequency",
                dataset, PlotOrientation.VERTICAL,
                true, true, false
        );
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}