package org.example.courseworkManager;


import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;
import java.util.Map;
import java.util.Optional;
import javafx.geometry.Insets;

public class DataViewPane extends VBox {
    private final DataModel dataModel;
    private final TableView<Map<String, Object>> tableView;
    private final ToolBar toolbar;

    public DataViewPane(DataModel dataModel) {
        this.dataModel = dataModel;
        this.setSpacing(10);
        this.setPadding(new Insets(10));


        // Create toolbar
        toolbar = createToolbar();

        // Create table view
        tableView = new TableView<>();
        tableView.setEditable(true);
        tableView.setItems(dataModel.getData());

         dataModel.addListener(() -> {
            tableView.setItems(dataModel.getData()); // rebind to new ObservableList
            updateColumns();                         // rebuild columns
            tableView.refresh();                     // repaint
        });
        // Add components to the layout
        getChildren().addAll(toolbar, tableView);
        VBox.setVgrow(tableView, Priority.ALWAYS);
    }

    private ToolBar createToolbar() {
        ToolBar toolbar = new ToolBar();

        // Data Cleaning Section
        Label cleaningLabel = new Label("Clean Data:");
        Button removeNullsBtn = new Button("Remove Nulls");
        removeNullsBtn.setOnAction(e -> {
            String selectedColumn = getSelectedColumn();
            if (selectedColumn != null) {
                dataModel.removeNullValues(selectedColumn);
            }
        });

        Button removeDuplicatesBtn = new Button("Remove Duplicates");
        removeDuplicatesBtn.setOnAction(e -> dataModel.removeDuplicates());

        // Transform Section
        Label transformLabel = new Label("Transform Data (Unchangable):");

        // Filter Button
        Button filterBtn = new Button("Filter");
        filterBtn.setOnAction(e -> showFilterDialog());

        // Sort Button
        Button sortBtn = new Button("Sort");
        sortBtn.setOnAction(e -> showSortDialog());

        // Aggregate Button
        Button aggregateBtn = new Button("Aggregate");
        aggregateBtn.setOnAction(e -> showAggregateDialog());

        // Statistics Section
        Label statsLabel = new Label("Analyze:");

        Button showStatsBtn = new Button("Show Statistics");
        showStatsBtn.setOnAction(e -> {
            TableColumn<Map<String, Object>, ?> selectedColumn = tableView.getFocusModel().getFocusedCell().getTableColumn();
            if (selectedColumn != null) {
                showStatistics(selectedColumn.getText());
              //  String selectedColumn = getSelectedColumn();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("No Column Selected");
                alert.setContentText("Please select a column to view its statistics.");
                alert.showAndWait();
            }
        });

            // Add separators between sections
        toolbar.getItems().addAll(
                cleaningLabel,
                removeNullsBtn,
                removeDuplicatesBtn,
                new Separator(),
                transformLabel,
                filterBtn,
                sortBtn,
                aggregateBtn,
                new Separator(),
                statsLabel,
                showStatsBtn
        );        return toolbar;
    }

    private void showFilterDialog() {
        Dialog<String[]> dialog = new Dialog<>();
        dialog.setTitle("Filter Data");
        dialog.setHeaderText("Select column and enter filter condition");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

        ComboBox<String> columnCombo = new ComboBox<>();
        columnCombo.getItems().addAll(dataModel.getColumnNames());
        TextField valueField = new TextField();

        grid.add(new Label("Column:"), 0, 0);
        grid.add(columnCombo, 1, 0);
        grid.add(new Label("Value greater than:"), 0, 1);
        grid.add(valueField, 1, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                return new String[]{columnCombo.getValue(), valueField.getText()};
            }
            return null;
        });

        Optional<String[]> result = dialog.showAndWait();
        result.ifPresent(res -> {
            try {
                String column = res[0];
                double threshold = Double.parseDouble(res[1]);
                DataTransformation filter = TransformationFactory.createFilterTransformation(
                        column,
                        value -> value instanceof Number &&
                                ((Number) value).doubleValue() > threshold
                );
                dataModel.applyTransformation(filter);
            } catch (NumberFormatException ex) {
                showError("Invalid number format");
            }
        });
    }

    private void showSortDialog() {
        Dialog<String[]> dialog = new Dialog<>();
        dialog.setTitle("Sort Data");
        dialog.setHeaderText("Select column and sort direction");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

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

    private void showAggregateDialog() {
        Dialog<String[]> dialog = new Dialog<>();
        dialog.setTitle("Aggregate Data");
        dialog.setHeaderText("Select grouping column and aggregation type");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

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

    private void updateColumns() {
        tableView.getColumns().clear();

        for (String columnName : dataModel.getColumnNames()) {
            TableColumn<Map<String, Object>, Object> column = new TableColumn<>(columnName);
            column.setCellValueFactory(new MapValueFactory(columnName));

            // Make columns editable
            column.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Object>() {
                @Override
                public String toString(Object object) {
                    return object != null ? object.toString() : "";
                }

                @Override
                public Object fromString(String string) {
                    Class<?> type = dataModel.getColumnTypes().get(columnName);
                    try {
                        if (type == Integer.class) {
                            return Integer.parseInt(string);
                        } else if (type == Double.class) {
                            return Double.parseDouble(string);
                        } else {
                            return string;
                        }
                    } catch (NumberFormatException e) {
                        return string;
                    }
                }
            }));

         /* column.setOnEditCommit(event -> {
                int row = event.getTablePosition().getRow();
                dataModel.updateValue(row, columnName, event.getNewValue());
            }); */
            System.out.println("about to print column: " + columnName);
            tableView.getColumns().add(column);
        }
    }

    private String getSelectedColumn() {
        TableColumn<Map<String, Object>, ?> column = tableView.getFocusModel().getFocusedCell().getTableColumn();
        return column != null ? column.getText() : null;
    }

    private void showStatistics(String column) {
        Map<String, Object> stats = dataModel.getBasicStats(column);
        if (!stats.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Column Statistics");
            alert.setHeaderText("Statistics for column: " + column);

            StringBuilder content = new StringBuilder();
            stats.forEach((key, value) ->
                    content.append(key).append(": ").append(value).append("\n")
            );

            alert.setContentText(content.toString());
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Statistics");
            alert.setContentText("No numeric data available for column: " + column);
            alert.showAndWait();
        }
    }
    public void forceUpdateTables() {
        updateColumns();
        tableView.refresh();
        System.out.println("forceUpdateColumns Done");
    }
}