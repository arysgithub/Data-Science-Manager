# 📊 Data-Science-Manager 
This project allows users to load csv and json files, manipulate, analyse, and visualise data from the files and then export them, through an intuitive chart GUI 

## 📌 Project Overview 
This JavaFX-based application with a Gradle build allows users to **load**, **manipulate**, **analyse**, and **visualise** tabular data through a clean and intuitive user interface. 

![Java](https://img.shields.io/badge/Java-ED8B00?style=flat&logo=java&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-02303A?style=flat&logo=gradle&logoColor=white)
![JUnit](https://img.shields.io/badge/JUnit-25A162?style=flat&logo=java&logoColor=white)
![IntelliJ IDEA](https://img.shields.io/badge/IDE-IntelliJIDEA-blue?style=flat&logo=intellij-idea)
![Git](https://img.shields.io/badge/Git-F05032?style=flat&logo=git&logoColor=white)

![Status](https://img.shields.io/badge/Status-Completed-brightgreen)
![License](https://img.shields.io/badge/License-MIT-green)
![Last Commit](https://img.shields.io/github/last-commit/arysgithub/Data-Science-Manager)


 
It incorporates: 
- **Singleton Pattern**: To ensure a single source of truth for application configuration.
- **Observer Pattern**: To notify UI components when the underlying data changes.
- **Factory Pattern**: For creating various types of data transformations dynamically.
Features include dynamic table rendering, undo/redo support, real-time statistics, and chart visualisation using **JFreeChart**. A suite of **JUnit 5** unit tests has been added for validating core functionality.

---

## 1. Introduction 

This JavaFX-based coursework allows users to load, manipulate, analyse, and visualise data through an intuitive chart GUI. The project utilises the Singleton design pattern to maintain consistent application preferences across sections. Observer to update UI components on data changes and Factory to instantiate transformation objects. The system has a responsive UI, dynamic table generation, real-time statistics and chart visualisations powered by JFree Chart. Included is JUnit tests for all core components of the system 

## ✅ Key Features 

### 📂 Data Import & Export
- Supports **CSV** and **JSON** formats
- Automatic type detection, validation & error handling

### 🔧 Data Transformation
- Filtering rows by condition
- Sorting columns (ascending/descending)
- Aggregation (Sum, Average, Count)
- Full **undo/redo** history for transformation actions

### 🧹 Data Cleaning
- Null value removal
- Duplicate row removal
- Column statistics inspection

### 📈 Data Analysis
- Summary statistics (Mean, Median, Standard Deviation, Min, Max)
- Pearson correlation coefficient between columns

### 📊 Data Visualisation
- Chart types: **Scatter**, **Line**, **Bar**, **Histogram**
- Chart generation via JFreeChart

### 🖥️ User Interface
- Tab-based UI:
  - **Data View**
  - **Analysis**
  - **Visualisation**
- Live updates via observer pattern
- Intuitive controls and responsive layout

---

## 💻 Usage Guide

### 📁 Import Data
- `File` → `Import Data` → Select `.csv` or `.json` file

### 🧽 Clean & Transform
- Use cleaning tools to:
  - Remove nulls
  - Remove duplicates
- Apply transformations:
  - Filter, Sort, Aggregate
  - Use Undo/Redo as needed

### 📊 Analyse Data
- Navigate to **Analysis** tab
- Select columns and run:
  - Summary statistics
  - Correlation checks

### 📉 Visualise
- Navigate to **Visualisation** tab
- Select chart type and data columns
- Click "Create Chart"

### 💾 Export Data
- `File` → `Export Data` → Choose format

---

## 🧰 Technologies Used

| Tool              | Version     |
|-------------------|-------------|
| Java              | 17+         |
| JavaFX            | 17.0.2      |
| Gradle            | Bundled     |
| JUnit             | 5.8.2       |
| JFreeChart        | 1.5.3+      |
| IDE               | IntelliJ IDEA |
| Version Control   | Git         |

## 📐 Software Design Patterns Used

- **Singleton Pattern**: Implemented in `AppConfig.java` for managing application-wide settings like chart type and last used directory.
- **Observer Pattern**: `DataModel` notifies listeners (such as UI panes) when data is changed.
- **Factory Pattern**: `TransformationFactory.java` dynamically creates `DataTransformation` implementations based on input.

---
## Application Screenshots 

<img width="1498" height="410" alt="management 4" src="https://github.com/user-attachments/assets/8851287a-d6ea-4d25-ac2f-ffc9e553af4a" />
<img width="1497" height="610" alt="management 3" src="https://github.com/user-attachments/assets/df1ed09b-5be9-4f67-bf68-bf3f164e5920" />
<img width="1486" height="567" alt="management 2" src="https://github.com/user-attachments/assets/5f1091d0-e453-42cd-a283-b9ca15a01357" />
<img width="1498" height="757" alt="management 1" src="https://github.com/user-attachments/assets/cc7ff344-07c1-4dd9-9fc4-8b49e69b52ca" />
<img width="1496" height="965" alt="management 5" src="https://github.com/user-attachments/assets/07755b5f-2345-4a65-8186-47eff40c4904" />


---

## 📘 Assumptions

- CSV/JSON files are syntactically correct
- All data fits in memory (no streaming implemented)
- Numeric data is required for most analysis/chart types
- Undo/Redo applies only to transformations, not direct user edits

---

## 🧪 Testing

- JUnit 5 used for unit testing
- Core classes tested:
  - `DataModel`
  - `TransformationFactory`
  - `AppConfig`
  - `JsonImporter`
- Tests include:
  - Statistical calculations
  - Data transformation correctness
  - Edge case handling

---

## 2. Requirements (implemented priority order)
1. Load and display tabular data (CSV/JSON)
2. Apply transformations: Filter, Sort, Aggregate. Undo/Redo system for non-destructive changes
3. Statistical analysis and summary
4. Data visualisation with multiple chart types
5. Export modified datasets

## 3.Design

### Architecture Diagram (Mermaid Syntax Diagram)

```mermaid
graph TD
Main --> |Uses|DataModel
   Main --> DataViewPane
   Main --> AnalysisPane
   Main --> VisualisationPane
   Main --> AppConfig

   DataModel -->|Observer Pattern| UIComponents
   DataModel --> |Interacts|TransformationFactory
   DataModel --> DataExporter
   DataImporter -->|Updates| DataModel
   JsonImporter --> DataImporter

   TransformationFactory -->|Factory Pattern| DataTransformation
   AppConfig -->|Singleton Pattern| AppConfig

   DataViewPane --> |Interacts|DataModel
   AnalysisPane --> |Interacts|DataModel
   VisualisationPane -->|Interacts| DataModel

```






### Mermaid Class Diagram

```mermaid
classDiagram
 class DataModel {
       +List<Map<String, Object>> data
       +List<String> columnNames
       +void setData(List<Map<String, Object>>, List<String>)
       +List<Map<String, Object>> getData()
       +void applyTransformation(DataTransformation)
       +Map<String, Object> getBasicStats(String column)
       +void undo()
       +void redo()
       +void addListener(Runnable)
    }
    class DataTransformation {
        <<interface>>
        +void apply(DataModel)
        +String getDescription()
    }

    class TransformationFactory {
       +createSortTransformation(String column, boolean ascending)
       +createFilterTransformation(String column, Predicate<Object> condition)
       +createAggregationTransformation(String groupByColumn, String targetColumn, AggregationType type)
    }

    class DataImporter {
        +static void importData(DataModel)
    }

    class DataExporter {
        +static void exportData(DataModel)
    }

    class JsonImporter {
        +static List<Map<String, Object>> importJson(File)
        +static List<Map<String, Object>> importJsonString(String)
    }

    class AppConfig {
        +static getInstance()
        +getLastDirectory()
        +setLastDirectory(String)
        +getPreferredChartType()
        +setPreferredChartType(String)
    }

    class Main
    class DataViewPane{
       +updateColumns()
       +showFilterDialog()
       +showSortDialog()
    }
    class AnalysisPane{
       +generateSummary()
       +calculateCorrelation()
    }
    class VisualisationPane{
       +createChart()
    }

    DataModel --> DataTransformation : applies
    DataModel --> "1..*" Runnable : observers
    TransformationFactory --> DataTransformation : factory
    DataImporter --> DataModel : modifies
    DataExporter --> DataModel : reads
    JsonImporter --> DataImporter : supports
    Main --> AppConfig : Singleton
    Main --> DataViewPane
    Main --> AnalysisPane
    Main --> VisualisationPane
    DataViewPane --> DataModel : interacts
    AnalysisPane --> DataModel : interacts
    VisualisationPane --> DataModel : interacts


```

## 🚀 Setup Instructions
## Requirements

1. **Core Requirements**:
    - Java 17 or higher
    - Gradle (Bundled with IntelliJ or via CLI)
    - JavaFX 17.0.2
    - JUnit 5.8.2 (for testing)
    - IntelliJ IDEA
    - Git for version control

2. **Clone the GitLab Repository**
   ```bash
   git clone https://csgitlab.reading.ac.uk/sc016258/CS1OP-CW1

3. **Open in IntelliJ IDEA**
    - Launch IntelliJ IDEA
    - Select 'Open' or `File -> Open` > select the cloned folder
    - Select `build.gradle` file
    - Click "Open as Project" and Select "Trust Project" if prompted
    - IntelliJ will automatically detect the Gradle build and import dependencies.

4. **Configure JDK**
    - Go to `File -> Project Structure`
    - Set SDK to JDK 17 or later
    - Set Language Level to match your JDK

5. **Build the Project**
    - Wait for Gradle to sync dependencies
    - Click Gradle refresh
    - Build using the Gradle task:
      ```bash
      ./gradlew build
      ```

6. **Run the Application**
    - Locate `src/main/java/org/example/courseworkManager/Main.java` or `Main.java` in the project explorer
    - Right-click and select "Run 'Main.main()'"
    - Alternatively, use the Gradle task:
      ```bash
      ./gradlew run
      ```

### Troubleshooting
- If JavaFX modules are not found, ensure they are properly included in the Gradle build file
- For Gradle sync issues, try `File -> Invalidate Caches / Restart`
- If JavaFX libraries are missing, add the following to your build.gradle

 **Actual Hours Spent Total**: 55
 **Artificial Intelligence Tools Used**: Chat GPT for design pattern implementation 
 MIT Licensed
