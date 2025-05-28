package org.example.courseworkManager;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;

/**
 * DataModel manages the core dataset, column info, and supports transformations,
 * undo/redo operations, and listener notifications.
 * This is part of the Observer pattern implementation.
 */

public class DataModel {
    private ObservableList<Map<String, Object>> data;
    private List<String> columnNames;
    private SimpleObjectProperty<Map<String, Class<?>>> columnTypes;
    // A list of listeners (observers) interested in changes to the data
    private final List<DataModelListener> listeners;
    private Stack<List<Map<String, Object>>> undoStack;
    private Stack<List<Map<String, Object>>> redoStack;


    // Functional interface for notifying UI components of data changes.
    public interface DataModelListener {
        void onDataChanged();
    }

    //Constructor initializes collections and state.
    public DataModel() {
        data = FXCollections.observableArrayList();
        columnNames = new ArrayList<>();
        columnTypes = new SimpleObjectProperty<>(new HashMap<>());
        listeners = new ArrayList<>();
        undoStack = new Stack<>();
        redoStack = new Stack<>();
    }

    /**
     * Adds a listener that will be notified whenever the data changes.
     */
    public void addListener(DataModelListener listener) {
        listeners.add(listener);
    }

    public void removeListener(DataModelListener listener) {
        listeners.remove(listener);
    }

    /**
     * Notifies all registered listeners that data has changed.
     * This method is called after any update to the dataset.
     */
    private void notifyListeners() {
        for (DataModelListener listener : listeners) {
            listener.onDataChanged();
        }
    }

    /**
     * Saves current data state for undo.
     */
    private void saveState() {
        undoStack.push(new ArrayList<>(data));
        redoStack.clear();
    }

    /**
     * Reverts to the previous data state.
     */
    public void undo() {
        if (!undoStack.isEmpty()) {
            redoStack.push(new ArrayList<>(data));
            data.setAll(undoStack.pop());
            notifyListeners();
        }
    }

    /**
     * Reapplies a previously undone action.
     */
    public void redo() {
        if (!redoStack.isEmpty()) {
            undoStack.push(new ArrayList<>(data));
            data.setAll(redoStack.pop());
            notifyListeners();
        }
    }

    /**
     * Sets new data and headers, updates types, and notifies listeners.
     */
    public void setData(List<Map<String, Object>> newData, List<String> headers) {
        saveState();
        data.clear();
        data.addAll(newData);
        // data = FXCollections.observableArrayList(newData);
        //  notifyListeners();

        columnNames.clear();
        columnNames.addAll(headers);

        // Infer column types
        Map<String, Class<?>> types = new HashMap<>();
        if (!data.isEmpty()) {
            Map<String, Object> firstRow = data.get(0);
            for (String column : headers) {
                Object value = firstRow.get(column);
                if (value != null) {
                    types.put(column, value.getClass());
                } else {
                    types.put(column, Object.class);
                }
            }
        }
        columnTypes.set(types);
        notifyListeners();
    }

    /**
     * Applies a data transformation (e.g., filter, sort) and saves history.
     */

    public void applyTransformation(DataTransformation transformation) {
        saveState();
        transformation.apply(this);  // Pass the whole DataModel to let the transformation modify it
        notifyListeners();
    }

    /**
     * Clears all data, column names, and types.
     */
    public void clearData() {
        saveState();
        data.clear();
        columnNames.clear();
        columnTypes.get().clear();
        notifyListeners();
    }

    public ObservableList<Map<String, Object>> getData() {
        return data;
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    public Map<String, Class<?>> getColumnTypes() {
        return columnTypes.get();
    }

    /**
     * Updates a specific value in the dataset.
     */
    public void updateValue(int rowIndex, String column, Object value) {
        if (rowIndex >= 0 && rowIndex < data.size()) {
            saveState();
            Map<String, Object> row = data.get(rowIndex);
            row.put(column, value);
            notifyListeners();
        }
    }

    /**
     * Calculates basic statistics (count, mean, min, max, sum, median, std deviation).
     */
    public Map<String, Object> getBasicStats(String column) {
        Map<String, Object> stats = new HashMap<>();
        List<Double> numericValues = new ArrayList<>();

        // Collect numeric values and convert to double
        for (Map<String, Object> row : data) {
            Object value = row.get(column);
            if (value instanceof Number) {
                numericValues.add(((Number) value).doubleValue());
            }
        }

        if (!numericValues.isEmpty()) {
            // Summary statistics (count, mean, min, max, sum)
            DoubleSummaryStatistics summary = numericValues.stream()
                    .mapToDouble(Double::doubleValue)
                    .summaryStatistics();

            stats.put("count", summary.getCount());
            stats.put("mean", summary.getAverage());
            stats.put("min", summary.getMin());
            stats.put("max", summary.getMax());
            stats.put("sum", summary.getSum());

            // Median calculation
            Collections.sort(numericValues);
            int size = numericValues.size();
            double median;
            if (size % 2 == 0) {
                median = (numericValues.get(size / 2 - 1) + numericValues.get(size / 2)) / 2.0;
            } else {
                median = numericValues.get(size / 2);
            }
            stats.put("median", median);

            // Standard deviation calculation
            double mean = summary.getAverage();
            double variance = numericValues.stream()
                    .mapToDouble(val -> Math.pow(val - mean, 2))
                    .sum() / summary.getCount();
            double stdDev = Math.sqrt(variance);
            stats.put("standardDeviation", stdDev);
        }

        return stats;
    }

    /**
     * Removes rows with null values in a given column.
     */
    public void removeNullValues(String column) {
        saveState();
        data.removeIf(row -> row.get(column) == null);
        notifyListeners();
    }

    /**
     * Removes duplicate rows from the dataset.
     */
    public void removeDuplicates() {
        saveState();
        Set<Map<String, Object>> uniqueRows = new LinkedHashSet<>(data);
        data.clear();
        data.addAll(uniqueRows);
        notifyListeners();
    }
}