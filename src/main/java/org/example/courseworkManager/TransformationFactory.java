package org.example.courseworkManager;

import java.util.List;
import java.util.Map;
import java.util.*;
import java.util.function.Predicate;

/**
 * Factory class for creating different types of DataTransformation instances.
 * Implements the Factory design pattern to abstract and centralize creation logic.
 */
public class TransformationFactory {

    // Enum for supported aggregation types
    public enum AggregationType {
        SUM, AVERAGE, COUNT
    }

    /**
     * Creates a transformation that filters rows based on a predicate condition.
     */
    public static DataTransformation createFilterTransformation(String column, Predicate<Object> condition) {
        return new DataTransformation() {
            @Override
            public void apply(DataModel model) {
                model.getData().removeIf(row -> !condition.test(row.get(column)));
            }

            @Override
            public String getDescription() {
                return "Filter data on column: " + column;
            }
        };
    }

    /**
     * Creates a transformation that sorts data by the specified column in ascending or descending order.
     */
    public static DataTransformation createSortTransformation(String column, boolean ascending) {
        return new DataTransformation() {
            @Override
            public void apply(DataModel model) {
                List<Map<String, Object>> sortedData = new ArrayList<>(model.getData());
                sortedData.sort((a, b) -> {
                    Object valA = a.get(column);
                    Object valB = b.get(column);

                    if (valA == null && valB == null) return 0;
                    if (valA == null) return ascending ? -1 : 1;
                    if (valB == null) return ascending ? 1 : -1;

                    if (valA instanceof Comparable && valB instanceof Comparable) {
                        int comparison = ((Comparable) valA).compareTo(valB);
                        return ascending ? comparison : -comparison;
                    }

                    return 0;
                });
                model.getData().setAll(sortedData);
            }

            @Override
            public String getDescription() {
                return "Sort " + (ascending ? "ascending" : "descending") + " by column: " + column;
            }
        };
    }

    /**
     * Creates a transformation that aggregates data by a grouping column.
     */
    public static DataTransformation createAggregationTransformation(String groupByColumn, String aggregateColumn, AggregationType type) {
        return new DataTransformation() {
            @Override
            public void apply(DataModel model) {
                Map<Object, List<Number>> groups = new HashMap<>();

                // Group the data
                for (Map<String, Object> row : model.getData()) {
                    Object groupKey = row.get(groupByColumn);
                    Object value = row.get(aggregateColumn);

                    if (value instanceof Number) {
                        groups.computeIfAbsent(groupKey, k -> new ArrayList<>())
                                .add((Number) value);
                    }
                }

                // Calculate aggregations
                List<Map<String, Object>> result = new ArrayList<>();
                for (Map.Entry<Object, List<Number>> entry : groups.entrySet()) {
                    Map<String, Object> newRow = new HashMap<>();
                    newRow.put(groupByColumn, entry.getKey());

                    double aggregateValue;
                    switch (type) {
                        case SUM:
                            aggregateValue = entry.getValue().stream()
                                    .mapToDouble(Number::doubleValue)
                                    .sum();
                            break;
                        case AVERAGE:
                            aggregateValue = entry.getValue().stream()
                                    .mapToDouble(Number::doubleValue)
                                    .average()
                                    .orElse(0.0);
                            break;
                        case COUNT:
                            aggregateValue = entry.getValue().size();
                            break;
                        default:
                            throw new IllegalStateException("Unknown aggregation type: " + type);
                    }

                    newRow.put(aggregateColumn, aggregateValue);
                    result.add(newRow);
                }

                model.getData().setAll(result);
            }

            @Override
            public String getDescription() {
                return String.format("%s %s grouped by %s", type, aggregateColumn, groupByColumn);
            }
        };
    }
}