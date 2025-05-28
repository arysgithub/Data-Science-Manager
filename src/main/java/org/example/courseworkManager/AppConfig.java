package org.example.courseworkManager;

import java.util.prefs.Preferences;

/**
 * AppConfig is a singleton utility class that handles application preferences,
 * such as remembering the last opened directory and preferred chart type.
 */
public class AppConfig {
    private static AppConfig instance;     // Singleton instance
    // Java Preferences API to store simple user config data
    private final Preferences preferences;

    private AppConfig() {
        preferences = Preferences.userNodeForPackage(AppConfig.class);
    }

    /**
     * Returns the single instance of AppConfig.
     * Thread-safe lazy initialization.
     */
    public static synchronized AppConfig getInstance() {
        if (instance == null) {
            instance = new AppConfig();
        }
        return instance;
    }

    // Stores the last directory path used by the user.
    public void setLastDirectory(String path) {
        preferences.put("lastDirectory", path);
    }

    //Retrieves the last directory path, or defaults to user's home if not set.
    public String getLastDirectory() {
        return preferences.get("lastDirectory", System.getProperty("user.home"));
    }

    //Saves the preferred chart type as a string (e.g., "SCATTER_PLOT").
    public void setPreferredChartType(String chartType) {
        preferences.put("preferredChartType", chartType);
    }

    //Retrieves the preferred chart type, defaulting to "SCATTER_PLOT".
    public String getPreferredChartType() {
        return preferences.get("preferredChartType", "SCATTER_PLOT");
    }
}