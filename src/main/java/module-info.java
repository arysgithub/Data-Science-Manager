module org.example.courseworkManager {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires javafx.graphics;
    requires commons.csv;

    requires com.fasterxml.jackson.databind;
    requires commons.math3;

    requires org.jfree.jfreechart;
    requires org.jfree.chart.fx;
    requires java.prefs;


    opens org.example.courseworkManager to javafx.fxml;
    exports org.example.courseworkManager;
}