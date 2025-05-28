package org.example.courseworkManager;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for verifying the functionality of data transformations
 * created by the TransformationFactory (Factory Pattern).
 * These tests validate core transformations: filtering, sorting, and aggregations
 * (SUM, AVERAGE, COUNT) on a sample DataModel.
 */
public class TransformationFactoryTests {
    private DataModel dataModel;
    private List<Map<String, Object>> testData;

    // Sets up a reusable DataModel with predefined rows for testing.
    @BeforeEach
    void setUp() {
        dataModel = new DataModel();
        testData = new ArrayList<>();

        // Example dataset: 3 rows with numeric and categorical values
        Map<String, Object> row1 = Map.of("id", 1, "value", 10.5, "category", "A");
        Map<String, Object> row2 = Map.of("id", 2, "value", 20.5, "category", "B");
        Map<String, Object> row3 = Map.of("id", 3, "value", 30.5, "category", "A");

        testData.add(row1);
        testData.add(row2);
        testData.add(row3);
        // Set the data into the model with column headers
        dataModel.setData(testData, List.of("id", "value", "category"));
    }

    //Verifies that the filter transformation correctly filters values above 15.
    @Test
    void testFilterTransformation() {
        DataTransformation filter = TransformationFactory.createFilterTransformation(
                "value",
                value -> value instanceof Number && ((Number) value).doubleValue() > 15.0
        );

        filter.apply(dataModel);
        assertEquals(2, dataModel.getData().size());
        assertTrue(dataModel.getData().stream()
                .allMatch(row -> ((Number) row.get("value")).doubleValue() > 15.0));
    }

    //Tests ascending sort transformation on the "value" column.
    @Test
    void testSortTransformation() {
        DataTransformation sort = TransformationFactory.createSortTransformation("value", true);
        sort.apply(dataModel);

        List<Map<String, Object>> sortedData = dataModel.getData();
        assertEquals(3, sortedData.size());
        assertTrue(((Number) sortedData.get(0).get("value")).doubleValue() <
                ((Number) sortedData.get(1).get("value")).doubleValue());
        assertTrue(((Number) sortedData.get(1).get("value")).doubleValue() <
                ((Number) sortedData.get(2).get("value")).doubleValue());
    }

    //Tests SUM aggregation on the "value" column grouped by "category".
    @Test
    void testAggregationTransformation() {
        DataTransformation aggregate = TransformationFactory.createAggregationTransformation(
                "category",
                "value",
                TransformationFactory.AggregationType.SUM
        );

        aggregate.apply(dataModel);
        assertEquals(2, dataModel.getData().size()); // Should have 2 groups (A and B)

        // Find the sum for category A
        Optional<Map<String, Object>> categoryA = dataModel.getData().stream()
                .filter(row -> "A".equals(row.get("category")))
                .findFirst();

        assertTrue(categoryA.isPresent());
        assertEquals(41.0, (Double) categoryA.get().get("value"), 0.001); // 10.5 + 30.5
    }

    //Tests AVERAGE aggregation for the category group A.
    @Test
    void testAggregationAverage() {
        DataTransformation aggregate = TransformationFactory.createAggregationTransformation(
                "category",
                "value",
                TransformationFactory.AggregationType.AVERAGE
        );

        aggregate.apply(dataModel);

        // Find the average for category A
        Optional<Map<String, Object>> categoryA = dataModel.getData().stream()
                .filter(row -> "A".equals(row.get("category")))
                .findFirst();

        assertTrue(categoryA.isPresent());
        assertEquals(20.5, (Double) categoryA.get().get("value"), 0.001); // (10.5 + 30.5) / 2
    }

    //Tests COUNT aggregation to count items per category.
    @Test
    void testAggregationCount() {
        DataTransformation aggregate = TransformationFactory.createAggregationTransformation(
                "category",
                "value",
                TransformationFactory.AggregationType.COUNT
        );

        aggregate.apply(dataModel);

        // Find the count for category A
        Optional<Map<String, Object>> categoryA = dataModel.getData().stream()
                .filter(row -> "A".equals(row.get("category")))
                .findFirst();

        assertTrue(categoryA.isPresent());
        assertEquals(2.0, (Double) categoryA.get().get("value"), 0.001); // 2 items in category A
    }

    //Tests that aggregation on an empty dataset doesn't crash and returns no data.
    @Test
    void testAggregationOnEmptyData() {
        dataModel.clearData();
        DataTransformation aggregate = TransformationFactory.createAggregationTransformation(
                "category", "value", TransformationFactory.AggregationType.SUM);
        aggregate.apply(dataModel);
        assertEquals(0, dataModel.getData().size());
    }

    //Tests that sort transformation gracefully handles rows with null values.
    @Test
    void testSortWithNullValues() {
        Map<String, Object> rowWithNull = new HashMap<>();
        rowWithNull.put("id", 4);
        rowWithNull.put("value", null);
        rowWithNull.put("category", "C");
        dataModel.getData().add(rowWithNull);

        DataTransformation sort = TransformationFactory.createSortTransformation("value", true);
        sort.apply(dataModel);

        assertEquals(4, dataModel.getData().size()); // No crash = pass
    }
}