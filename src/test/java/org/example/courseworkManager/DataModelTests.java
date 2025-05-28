package org.example.courseworkManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the DataModel class, which manages dataset storage, transformation,
 * statistics, undo/redo history, and event listener notifications (Observer Implementation).
 * These tests verify core logic including state management, statistical analysis,
 * transformation applications, and the Observer pattern's notification mechanism.
 */

public class DataModelTests {
    private DataModel dataModel;
    private List<Map<String, Object>> testData;

    //set up test data and initialise the data model before each test
    @BeforeEach
    void setUp() {
        dataModel = new DataModel();
        testData = new ArrayList<>();

        testData.add(Map.of("id", 1, "value", 10.5, "category", "A"));
        testData.add(Map.of("id", 2, "value", 20.5, "category", "B"));
        testData.add(Map.of("id", 3, "value", 30.5, "category", "A"));

        dataModel.setData(testData, List.of("id", "value", "category"));
    }

    // Tests statistical summary calculations for a numeric column
    @Test
    void testBasicStats() {
        Map<String, Object> stats = dataModel.getBasicStats("value");

        assertEquals(3L, stats.get("count"));
        assertEquals(61.5, (Double) stats.get("sum"), 0.01);
        assertEquals(20.5, (Double) stats.get("mean"), 0.01);
        assertEquals(10.5, (Double) stats.get("min"), 0.01);
        assertEquals(30.5, (Double) stats.get("max"), 0.01);
        assertEquals(20.5, (Double) stats.get("median"), 0.01);
        assertEquals(8.16, (Double) stats.get("standardDeviation"), 0.01);  // Approx value for SD

    }

    // Tests that statistical calculations return empty results for empty datasets
    @Test
    void testEmptyStats() {
        dataModel.clearData();
        Map<String, Object> stats = dataModel.getBasicStats("value");
        assertTrue(stats.isEmpty());
    }

    // Confirms correct detection of column data types
    @Test
    void testColumnTypes() {
        Map<String, Class<?>> types = dataModel.getColumnTypes();
        assertEquals(Integer.class, types.get("id"));
        assertEquals(Double.class, types.get("value"));
        assertEquals(String.class, types.get("category"));
    }

    // Ensures the correct column names are returned
    @Test
    void testColumnNames() {
        List<String> columns = dataModel.getColumnNames();
        assertTrue(columns.contains("id"));
        assertTrue(columns.contains("value"));
        assertTrue(columns.contains("category"));
        assertEquals(3, columns.size());
    }

    // Verifies that setData replaces data correctly
    @Test
    void testDataUpdate() {
        List<Map<String, Object>> newData = List.of(Map.of("id", 4, "value", 40.5, "category", "C"));
        dataModel.setData(newData, List.of("id", "value", "category"));
        assertEquals(1, dataModel.getData().size());
        assertEquals(4, dataModel.getData().get(0).get("id"));
    }

    // Ensures rows with null values in the target column are removed
    @Test
    void testRemoveNullValues() {
        Map<String, Object> rowWithNull = new HashMap<>();
        rowWithNull.put("id", 4);
        rowWithNull.put("value", null);
        rowWithNull.put("category", "C");

        List<Map<String, Object>> extended = new ArrayList<>(dataModel.getData());
        extended.add(rowWithNull);

        dataModel.setData(extended, dataModel.getColumnNames());
        dataModel.removeNullValues("value");

        for (Map<String, Object> row : dataModel.getData()) {
            assertNotNull(row.get("value"));
        }
    }

    // Ensures only unique rows are kept when removing duplicates
    @Test
    void testRemoveDuplicates() {
        Map<String, Object> duplicate = Map.of("id", 1, "value", 10.5, "category", "A");
        dataModel.getData().add(duplicate); // Duplicate row
        dataModel.removeDuplicates();

        long count = dataModel.getData().stream()
                .filter(row -> row.equals(duplicate))
                .count();
        assertEquals(1, count); // Only 1 should remain
    }

    // Tests undo and redo functionality after a data change
    @Test
    void testUndoRedo() {
        int originalSize = dataModel.getData().size();

        // Add new row via setData (to save state)
        List<Map<String, Object>> updated = new ArrayList<>(dataModel.getData());
        Map<String, Object> newRow = new HashMap<>();
        newRow.put("id", 99);
        newRow.put("value", 99.9);
        newRow.put("category", "Z");
        updated.add(newRow);

        dataModel.setData(updated, dataModel.getColumnNames());  // triggers saveState()

        assertEquals(originalSize + 1, dataModel.getData().size());

        dataModel.undo();
        assertEquals(originalSize, dataModel.getData().size());

        dataModel.redo();
        assertEquals(originalSize + 1, dataModel.getData().size());
    }

    // Applies a transformation to filter out rows and verifies result
    @Test
    void testApplyFilterTransformation() {
        DataTransformation filter = TransformationFactory.createFilterTransformation(
                "value",
                val -> ((Double) val) > 15.0
        );
        dataModel.applyTransformation(filter);

        assertEquals(2, dataModel.getData().size());
        assertTrue(dataModel.getData().stream().allMatch(row -> ((Double) row.get("value")) > 15.0));
    }

    //Validates that applying a transformation through DataModel saves state and can be undone.
    @Test
    void testApplyTransformationSavesState() {
        int originalSize = dataModel.getData().size();

        DataTransformation filter = TransformationFactory.createFilterTransformation(
                "value",
                value -> value instanceof Number && ((Number) value).doubleValue() > 15.0
        );

        dataModel.applyTransformation(filter);
        assertTrue(dataModel.getData().size() < originalSize);

        dataModel.undo(); // Should restore original data
        assertEquals(originalSize, dataModel.getData().size());
    }

    //Ensures that calling undo with no prior states doesn't crash the program.
    @Test
    void testUndoBoundary() {
        // No transformation yet, nothing to undo
        assertDoesNotThrow(() -> dataModel.undo());
    }

    //Ensures redo can be called safely even if nothing was undone.
    @Test
    void testRedoBoundary() {
        // Nothing undone, redo stack should be empty
        assertDoesNotThrow(() -> dataModel.redo());
    }

    //Validates that listeners get triggered after a data change.
    @Test
    void testListenerNotification() {
        final boolean[] notified = {false};

        dataModel.addListener(() -> notified[0] = true);

        // Extract column order from the first row
        List<String> columnOrder = new ArrayList<>();
        if (!dataModel.getData().isEmpty()) {
            columnOrder.addAll(dataModel.getData().get(0).keySet());
        }

        // Re-set the same data to trigger the listener
        dataModel.setData(new ArrayList<>(dataModel.getData()), columnOrder);

        assertTrue(notified[0]);
    }
}
