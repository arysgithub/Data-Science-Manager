package org.example.courseworkManager;
//Interface for data transformations

public interface DataTransformation {
    void apply(DataModel model);

    String getDescription();
}