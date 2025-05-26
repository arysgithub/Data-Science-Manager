package org.example.courseworkManager;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.*;

public class DataModel {
    private ObservableList<Map<String, Object>> data;
    private List<String> columnNames;
    private SimpleObjectProperty<Map<String, Class<?>>> columnTypes;
    private final List<DataModelListener> listeners;
    private Stack<List<Map<String, Object>>> undoStack;
    private Stack<List<Map<String, Object>>> redoStack;

    public interface DataModelListener {
        void onDataChanged();
    }

    public DataModel() {
        data = FXCollections.observableArrayList();
        columnNames = new ArrayList<>();
        columnTypes = new SimpleObjectProperty<>(new HashMap<>());
        listeners = new ArrayList<>();
        undoStack = new Stack<>();
        redoStack = new Stack<>();
    }

    public void addListener(DataModelListener listener) {
        listeners.add(listener);
    }

    public void removeListener(DataModelListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        for (DataModelListener listener : listeners) {
            listener.onDataChanged();
        }
    }

    private void saveState() {
        undoStack.push(new ArrayList<>(data));
        redoStack.clear();
    }

    public void undo() {
        if (!undoStack.isEmpty()) {
            redoStack.push(new ArrayList<>(data));
            data.setAll(undoStack.pop());
            notifyListeners();
        }
    }

    public void redo() {
        if (!redoStack.isEmpty()) {
            undoStack.push(new ArrayList<>(data));
            data.setAll(redoStack.pop());
            notifyListeners();
        }
    }

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

    public void applyTransformation(DataTransformation transformation) {
        saveState();
        List<Map<String, Object>> transformedData = transformation.apply(new ArrayList<>(data));
        data.setAll(transformedData);
        notifyListeners();
    }

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

    public void updateValue(int rowIndex, String column, Object value) {
        if (rowIndex >= 0 && rowIndex < data.size()) {
            saveState();
            Map<String, Object> row = data.get(rowIndex);
            row.put(column, value);
            notifyListeners();
        }
    }

    public Map<String, Object> getBasicStats(String column) {
        Map<String, Object> stats = new HashMap<>();
        List<Number> numericValues = new ArrayList<>();

        // Collect numeric values
        for (Map<String, Object> row : data) {
            Object value = row.get(column);
            if (value instanceof Number) {
                numericValues.add((Number) value);
            }
        }

        if (!numericValues.isEmpty()) {
            DoubleSummaryStatistics summary = numericValues.stream()
                    .mapToDouble(Number::doubleValue)
                    .summaryStatistics();

            stats.put("count", summary.getCount());
            stats.put("mean", summary.getAverage());
            stats.put("min", summary.getMin());
            stats.put("max", summary.getMax());
            stats.put("sum", summary.getSum());
        }

        return stats;
    }

    public void removeNullValues(String column) {
        saveState();
        data.removeIf(row -> row.get(column) == null);
        notifyListeners();
    }

    public void removeDuplicates() {
        saveState();
        Set<Map<String, Object>> uniqueRows = new LinkedHashSet<>(data);
        data.clear();
        data.addAll(uniqueRows);
        notifyListeners();
    }
}