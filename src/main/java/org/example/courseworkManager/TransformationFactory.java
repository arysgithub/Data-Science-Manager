package org.example.courseworkManager;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TransformationFactory {

    public static DataTransformation createFilterTransformation(String column, Predicate<Object> condition) {
        return new DataTransformation() {
            @Override
            public List<Map<String, Object>> apply(List<Map<String, Object>> data) {
                return data.stream()
                        .filter(row -> condition.test(row.get(column)))
                        .collect(Collectors.toList());
            }

            @Override
            public String getName() {
                return "Filter";
            }

            @Override
            public String getDescription() {
                return "Filter data based on column value";
            }
        };
    }

    public static DataTransformation createSortTransformation(String column, boolean ascending) {
        return new DataTransformation() {
            @Override
            public List<Map<String, Object>> apply(List<Map<String, Object>> data) {
                return data.stream()
                        .sorted((row1, row2) -> {
                            Comparable val1 = (Comparable) row1.get(column);
                            Comparable val2 = (Comparable) row2.get(column);
                            return ascending ?
                                    compareNullSafe(val1, val2) :
                                    compareNullSafe(val2, val1);
                        })
                        .collect(Collectors.toList());
            }

            @Override
            public String getName() {
                return "Sort";
            }

            @Override
            public String getDescription() {
                return "Sort data by column " + (ascending ? "ascending" : "descending");
            }
        };
    }

    public static DataTransformation createAggregationTransformation(
            String groupByColumn, String aggregateColumn, AggregationType type) {
        return new DataTransformation() {
            @Override
            public List<Map<String, Object>> apply(List<Map<String, Object>> data) {
                return data.stream()
                        .collect(Collectors.groupingBy(
                                row -> row.get(groupByColumn),
                                Collectors.collectingAndThen(
                                        Collectors.mapping(
                                                row -> (Number) row.get(aggregateColumn),
                                                Collectors.toList()
                                        ),
                                        numbers -> {
                                            double result = type.aggregate(numbers);
                                            Map<String, Object> aggregatedRow = Map.of(
                                                    groupByColumn, numbers.get(0),
                                                    aggregateColumn, result
                                            );
                                            return aggregatedRow;
                                        }
                                )
                        ))
                        .values()
                        .stream()
                        .collect(Collectors.toList());
            }

            @Override
            public String getName() {
                return "Aggregate";
            }

            @Override
            public String getDescription() {
                return "Aggregate data using " + type.name().toLowerCase();
            }
        };
    }

    private static <T extends Comparable<T>> int compareNullSafe(T a, T b) {
        if (a == null && b == null) return 0;
        if (a == null) return -1;
        if (b == null) return 1;
        return a.compareTo(b);
    }

    public enum AggregationType {
        SUM {
            @Override
            double aggregate(List<Number> values) {
                return values.stream()
                        .mapToDouble(Number::doubleValue)
                        .sum();
            }
        },
        AVERAGE {
            @Override
            double aggregate(List<Number> values) {
                return values.stream()
                        .mapToDouble(Number::doubleValue)
                        .average()
                        .orElse(0.0);
            }
        },
        COUNT {
            @Override
            double aggregate(List<Number> values) {
                return values.size();
            }
        };

        abstract double aggregate(List<Number> values);
    }
}