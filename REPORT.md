# Data Science Manager - Project Report


## 1. Introduction


This JavaFX-based coursework allows users to load, manipulate, analyse, and visualise data through an intuitive chart GUI. 
The project uses the design patterns Singleton to maintain consistent application preferences across sections.
Observer to update UI components on data changes and Factory to instantiate transformation objects.
The system has a responsive UI, dynamic table generation, real-time statistics and chart visualisations powered by JFree Chart.
Included is JUnit tests for all core components of the system  
### Features:

1. **Data Import/Export**: 
    - Support for CSV and JSON file formats with automatic type detection, validation and error handling

2. **Data Transformation**:
    - Filtering
    - Sorting
    - Aggregation (Sum, Average, Count)

3. **Data CLeaning**:
    - Null value removal
    - Duplicate removal
    - Analyse Statistics for specific rows

4. **Data Analysis**:
    - Summary Statistics
    - Mean, Median, SD, Count, Min/Max calculations
    - Pearson correlation

5. **Data Charts**:
    - Scatter
    - Line chart
    - Bar charts
    - Histograms

6. **Undo/Redo Support**: 
    - Complete transformation history management

7. **Modern UI**: Tab-based interface for:
    - Data viewing and editing
    - Analysis
    - Visualization


## 2. Analysis of AI Support in Software Development

### 2.1 How AI Tools Were Used

1. **Code Generation and Refactoring**
   - Used ChatGPT to generate boilerplate code for the Observer pattern implementation:
     ```java
     // Observer Pattern Implementation
     public class DataModel {
         private List<DataModelListener> listeners = new ArrayList<>();
         
         public void addListener(DataModelListener listener) {
             listeners.add(listener);
         }
         
         private void notifyListeners() {
             for (DataModelListener listener : listeners) {
                 listener.onDataChanged();
             }
         }
     }
     ```
2. **Helped generate Junit test cases for core classes**
   - For example I asked AI on how to help me generate test cases for the DataModel class:
     ```java
     @Test
     public void testDataTransformation() {
         DataModel model = new DataModel();
         List<Map<String, Object>> testData = createTestData();
         model.setData(testData, Arrays.asList("name", "age", "score"));
         
         DataTransformation filter = TransformationFactory.createFilterTransformation(
             "score", value -> value instanceof Number && ((Number) value).doubleValue() > 80
         );
         model.applyTransformation(filter);
         
         assertEquals(2, model.getData().size());
     }
     ```

2. **Problem Solving and Debugging**
   - Used AI to identify and fix issues in my code, why it was not running and how to overcome runtime errors. Needed help with errors in the undo/redo implementation:
     ```java
     // Undo/Redo Implementation
     public class DataModel {
         private Stack<List<Map<String, Object>>> undoStack;
         private Stack<List<Map<String, Object>>> redoStack;
         
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
     }
     ```
   - Received suggestions for handling edge cases in data transformations
   - Debugged complex data type conversion issues

3. **Improve JavaFX UI structure and user flows**
4. **Learnt and Refined Mermaid diagrams for documentation**

### 2.2 Benefits of Using AI Tools

1. **Development Efficiency and debugging**
   - Reduced time spent on repetitive coding tasks by asking AI on how I could use the code more effectively for example, using OOP, global variables, public methods more efficiently. 
   - Quick access to best practices and design patterns
   - Faster problem resolution through AI-suggested solutions
   - Reduced time spent debugging by providing conceptual guidance.

2. **Code Quality**
   - Improved code structure through AI-suggested refactoring
   - Better error handling with AI recognising bad implementation and indicating solutions
   - Consistent coding style across the project

3. **Learning Enhancement**
   - Better understanding of design patterns through AI explanations
   - Helped clarify design pattern usage with code examples.
   - Exposure to alternative implementation approaches
   - Improved knowledge of Java best practices

### 2.3 Challenges and Limitations

1. **Code Understanding**
   - Sometimes difficult to understand AI-generated code without proper context
   - Required additional time to verify and adapt AI suggestions
   - Occasional need to modify AI-generated code to fit project requirements

2. **Dependency Management**
   - AI sometimes suggested outdated or incompatible libraries that sometimes were not needed 
   - Required manual verification of dependency versions
   - Needed to resolve conflicts between AI-suggested dependencies that ended up not working in the actual program and ended up giving errors 

3. **Project-Specific Context**
   - AI tools sometimes lacked understanding of project-specific requirements
   - Required manual adjustment of AI suggestions to match project architecture
   - Needed to maintain consistency with existing codebase

### 2.4 Overall Impact on Learning and Development

The use of AI tools significantly enhanced the development process by:
- Accelerating the learning curve for new concepts
- Providing immediate feedback on code quality
- Offering alternative perspectives on problem-solving
- Using AI encouraged a deeper understanding of software engineering practices. Rather than just accepting outputs, it promoted critical engagement, especially in understanding where design patterns fit in. It was a valuable aid in learning by doing and especially learning from mistakes as I had to do lots of querrying the AI.

## 3. Analysis of Software Patterns in the Project

### 3.1 How the Patterns Were Used
- **Singleton**: Used in `AppConfig` to manage user preferences consistently across the app.
- **Observer**: Implemented in `DataModel` to notify UI components when the dataset changes.
- **Factory**: Applied via `TransformationFactory` to create transformation objects (sort, filter, aggregate) based on user input.

1. **Observer Pattern**
   - Implemented in DataModel for UI updates:
     ```java
     // Observer Pattern in DataModel
     public interface DataModelListener {
         void onDataChanged();
     }
     
     public class DataViewPane implements DataModelListener {
         private final DataModel model;
         
         public DataViewPane(DataModel model) {
             this.model = model;
             model.addListener(this);
         }
         
         @Override
         public void onDataChanged() {
             updateTableView();
             updateStatistics();
         }
     }
     ```

2. **Factory Pattern**
   - Implemented in TransformationFactory:
     ```java
     // Factory Pattern Implementation
     public class TransformationFactory {
         public static DataTransformation createTransformation(String type, String column, Object... params) {
             switch(type) {
                 case "filter":
                     return new FilterTransformation(column, (Predicate<Object>) params[0]);
                 case "sort":
                     return new SortTransformation(column, (boolean) params[0]);
                 case "aggregate":
                     return new AggregateTransformation(column, (String) params[0]);
                 default:
                     throw new IllegalArgumentException("Unknown transformation type: " + type);
             }
         }
     }
     ```

3. **Singleton Pattern**
   - Used in AppConfig for configuration management:
     ```java
     // Singleton Pattern Implementation
     public class AppConfig {
         private static AppConfig instance;
         private Properties config;
         
         private AppConfig() {
             config = new Properties();
             loadConfig();
         }
         
         public static synchronized AppConfig getInstance() {
             if (instance == null) {
                 instance = new AppConfig();
             }
             return instance;
         }
         
         public String getProperty(String key) {
             return config.getProperty(key);
         }
     }
     ```

### 3.2 Benefits of Using Software Patterns

1. **Code Organization**
   - Clear separation of concerns demonstrated in the transformation system:
     ```java
     // Clean separation of transformation logic
     public interface DataTransformation {
         List<Map<String, Object>> apply(List<Map<String, Object>> data);
         String getDescription();
     }
     
     public class FilterTransformation implements DataTransformation {
         private final String column;
         private final Predicate<Object> condition;
         
         @Override
         public List<Map<String, Object>> apply(List<Map<String, Object>> data) {
             return data.stream()
                 .filter(row -> condition.test(row.get(column)))
                 .collect(Collectors.toList());
         }
     }
     ```

2. **Flexibility**
   - Easy to add new transformations
   - Simple to modify existing functionality
   - Reduced coupling between components
   - Simplified code maintenance and expansion.

3. **Testing**
   - Easier to write unit tests
   - Better isolation of components
   - Simplified mock object creation
   - Improved modularity and testability of the system.

### 3.3 Challenges and Limitations

1. **Pattern Implementation**
   - Initial complexity in setting up patterns
   - Learning curve for proper pattern usage
   - Balancing pattern usage with simplicity

2. **Performance**
   - Observer pattern overhead in large datasets
   - Factory pattern memory usage
   - Singleton pattern testing difficulties
- Initially complex to integrate Observer with dynamic UI elements.
- Understanding when to apply Factory vs. Strategy took iteration.
 - Required effort to ensure patterns were implemented meaningfully (not just for marks).

### 3.4 Overall Impact on Project

Patterns structured the codebase effectively and contributed to scalability. The Observer pattern was particularly helpful in creating a reactive UI. The project architecture would have been much less maintainable without them.
As a result, the use of design patterns:
- Improved code maintainability
- Enhanced project scalability
- Facilitated team collaboration
- Simplified future modifications

## 4. Ethical and Legal Considerations

### 4.1 Ethical Concerns

1. **AI Tool Usage**
   - AI was used as a guide, not a replacement for learning or original work, so I Maintained code ownership and understanding
   - I had to verify all AI-generated code and make sure it worked and integrated well. Sometimes I had to change and rechange them over and over again 
   - Ensured original work while using AI assistance

2. **Academic Integrity**
   - Used AI tools as learning aids, not replacements
   - Understood and modified all generated code
   - Maintained transparency in the development process

### 4.2 Data Handling and Privacy

1. **Data Protection**
   - Implemented secure data import/export:
     ```java
     // Secure data handling
     public class DataImporter {
         public List<Map<String, Object>> importData(File file) {
             try {
                 List<Map<String, Object>> data = new ArrayList<>();
                 // Validate file type and content
                 if (!isValidFile(file)) {
                     throw new SecurityException("Invalid file format");
                 }
                 // Process data with proper validation
                 return data;
             } catch (Exception e) {
                 throw new DataImportException("Failed to import data", e);
             }
         }
     }
     ```
   - No storage of sensitive information
   - Clear data handling policies
   - File I/O operations respect user privacy (e.g., default directory is user home).
   - No real-world or sensitive data was used.
   - Dummy datasets were generated for testing

2. **Privacy Measures**
   - Local data processing
   - No external data transmission
   - User control over data operations

### 4.3 Broader Ethical and Legal Implications

1. **Accessibility**
   - Implemented keyboard shortcuts
   - Provided clear error messages
   - Ensured intuitive user interface
   - Designed with a simple UI; could improve with keyboard navigation and screen reader support.

2. **Legal Considerations**
   - Third-party libraries like Jackson, JFreeChart, and Apache Commons are used under open-source licenses (Apache, MIT).
   - Used open-source libraries with appropriate licenses
   - Maintained proper attribution
   - Followed Java development best practices

## 5. Conclusion

The development of the Data Management Tool demonstrated the effective integration of AI tools and software design patterns in modern software development with clean code practices. Using design patterns not only helped in clarity but also future-proofed the system. The project highlighted the importance of:

1. **Balanced AI Usage**
   - Using AI as a tool, not a crutch
   - Maintaining code understanding
   - Leveraging AI for learning

2. **Pattern Implementation**
   - Choosing appropriate patterns
   - Understanding pattern trade-offs
   - Maintaining code simplicity

3. **Future Improvements**
   - Enhanced error handling
   - Improved performance optimisation
   - Better documentation practices

The experience gained from this project will significantly influence future software development practices, emphasising the importance of:
- Critical evaluation of AI-generated code
- Proper implementation of design patterns
- Ethical considerations in software development
- Continuous learning and improvement

**Some Future Recommendations**:
- Enhance accessibility and mobile responsiveness.
- Introduce the Strategy pattern for data export plugins.
- Explore responsible AI integration with local LLMs and ethical safeguards.

## 6. References

- Gamma, E., Helm, R., Johnson, R., & Vlissides, J. (1994). *Design Patterns: Elements of Reusable Object-Oriented Software*. Addison-Wesley.
- Oracle JavaFX Documentation: https://openjfx.io
- OpenAI ChatGPT (2024). https://chat.openai.com
- JFreeChart Documentation: https://sourceforge.net/projects/jfreechart/
- Apache Commons Math: https://commons.apache.org/proper/commons-math/
- Jackson JSON Processor: https://github.com/FasterXML/jackson

Actual hrs spent overall: 55
Which Artificial Intelligence tools used: ChatGPT

