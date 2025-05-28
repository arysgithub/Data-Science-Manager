package org.example.courseworkManager;
 //Interface for data transformations
import java.util.List;
import java.util.Map;

public interface DataTransformation {
    void apply(DataModel model);
    String getDescription();
}