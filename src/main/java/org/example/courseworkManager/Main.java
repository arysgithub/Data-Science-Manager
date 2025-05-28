package org.example.courseworkManager;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javafx.scene.layout.BorderPane;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.Optional;

/**
 * Entry point for the JavaFX-based Data Management Tool application.
 * This class sets up the GUI, integrates the data model, and defines user interaction features.
 */

public class Main extends Application {

    private DataModel dataModel;


    @Override
    public void start(Stage primaryStage) {
        dataModel = new DataModel();

        // Create the main layout
        BorderPane root = new BorderPane();
        // Create the menu bar
        MenuBar menuBar = createMenuBar();
        root.setTop(menuBar);

        // Create the main content area
        TabPane tabPane = new TabPane();

        // Data View Tab
        Tab dataTab = new Tab("Data View");
        dataTab.setContent(new DataViewPane(dataModel));
        dataTab.setClosable(false);

        // Analysis Tab
        Tab analysisTab = new Tab("Analysis");
        analysisTab.setContent(new AnalysisPane(dataModel));
        analysisTab.setClosable(false);

        // Visualization Tab for plotting data charts
        Tab visualizationTab = new Tab("Visualization");
        visualizationTab.setContent(new VisualisationPane(dataModel));
        visualizationTab.setClosable(false);

        tabPane.getTabs().addAll(dataTab, analysisTab, visualizationTab);
        root.setCenter(tabPane);

        // Create the scene
        Scene scene = new Scene(root, 1200, 750);
        primaryStage.setTitle("Data Management Tool");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Builds the top menu bar with File, Edit, and Help menus.
     */
    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();

        // File Menu - for importing/exporting/closing app
        Menu fileMenu = new Menu("File");
        MenuItem importItem = new MenuItem("Import Data...");
        importItem.setOnAction(e -> DataImporter.importData(dataModel));
        MenuItem exportItem = new MenuItem("Export Data...");
        exportItem.setOnAction(e -> DataExporter.exportData(dataModel));
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> System.exit(0));
        fileMenu.getItems().addAll(importItem, exportItem, new SeparatorMenuItem(), exitItem);

        // Edit Menu - clear data from app
        Menu editMenu = new Menu("Edit");
        MenuItem clearItem = new MenuItem("Clear Data");
        clearItem.setOnAction(e -> dataModel.clearData());
        editMenu.getItems().add(clearItem);

        // Help Menu
        Menu helpMenu = new Menu("Help");
        MenuItem aboutItem = new MenuItem("About");
        aboutItem.setOnAction(e -> showAboutDialog());
        helpMenu.getItems().add(aboutItem);

        menuBar.getMenus().addAll(fileMenu, editMenu, helpMenu);
        return menuBar;
    }


    /**
     * Shows a dialog allowing the user to sort data by a selected column and direction.
     */
    private void showSortDialog() {
        Dialog<String[]> dialog = new Dialog<>();
        dialog.setTitle("Sort Data");
        dialog.setHeaderText("Select column and sort direction");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        ComboBox<String> columnCombo = new ComboBox<>();
        columnCombo.getItems().addAll(dataModel.getColumnNames());
        ComboBox<String> directionCombo = new ComboBox<>();
        directionCombo.getItems().addAll("Ascending", "Descending");

        grid.add(new Label("Column:"), 0, 0);
        grid.add(columnCombo, 1, 0);
        grid.add(new Label("Direction:"), 0, 1);
        grid.add(directionCombo, 1, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Convert result from user into values for sorting
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                return new String[]{columnCombo.getValue(), directionCombo.getValue()};
            }
            return null;
        });

        Optional<String[]> result = dialog.showAndWait();
        result.ifPresent(res -> {
            String column = res[0];
            boolean ascending = "Ascending".equals(res[1]);
            DataTransformation sorter = TransformationFactory.createSortTransformation(column, ascending);
            dataModel.applyTransformation(sorter);
        });
    }

    /**
     * Shows a dialog to perform an aggregation (SUM, AVERAGE, COUNT) on grouped data.
     */
    private void showAggregateDialog() {
        Dialog<String[]> dialog = new Dialog<>();
        dialog.setTitle("Aggregate Data");
        dialog.setHeaderText("Select grouping column and aggregation type");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        ComboBox<String> groupByCombo = new ComboBox<>();
        groupByCombo.getItems().addAll(dataModel.getColumnNames());
        ComboBox<String> aggregateCombo = new ComboBox<>();
        aggregateCombo.getItems().addAll(dataModel.getColumnNames());
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("SUM", "AVERAGE", "COUNT");

        grid.add(new Label("Group By:"), 0, 0);
        grid.add(groupByCombo, 1, 0);
        grid.add(new Label("Aggregate Column:"), 0, 1);
        grid.add(aggregateCombo, 1, 1);
        grid.add(new Label("Aggregation Type:"), 0, 2);
        grid.add(typeCombo, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                return new String[]{
                        groupByCombo.getValue(),
                        aggregateCombo.getValue(),
                        typeCombo.getValue()
                };
            }
            return null;
        });

        Optional<String[]> result = dialog.showAndWait();
        result.ifPresent(res -> {
            String groupBy = res[0];
            String aggregateCol = res[1];
            TransformationFactory.AggregationType type =
                    TransformationFactory.AggregationType.valueOf(res[2]);

            DataTransformation aggregator = TransformationFactory.createAggregationTransformation(
                    groupBy, aggregateCol, type);
            dataModel.applyTransformation(aggregator);
        });
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }


    private void showAboutDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Data Management Tool");
        alert.setContentText("A tool for data scientists to load, clean, and analyze datasets.\nVersion 1.0");
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}