package org.example.courseworkManager;

import java.util.List;
import java.util.Map;

public interface DataTransformation {
    List<Map<String, Object>> apply(List<Map<String, Object>> data);
    String getName();
    String getDescription();
}