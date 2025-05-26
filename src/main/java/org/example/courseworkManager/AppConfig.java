package org.example.courseworkManager;

import java.util.prefs.Preferences;

public class AppConfig {
    private static AppConfig instance;
    private final Preferences preferences;

    private AppConfig() {
        preferences = Preferences.userNodeForPackage(AppConfig.class);
    }

    public static synchronized AppConfig getInstance() {
        if (instance == null) {
            instance = new AppConfig();
        }
        return instance;
    }

    public void setLastDirectory(String path) {
        preferences.put("lastDirectory", path);
    }

    public String getLastDirectory() {
        return preferences.get("lastDirectory", System.getProperty("user.home"));
    }

    public void setPreferredChartType(String chartType) {
        preferences.put("preferredChartType", chartType);
    }

    public String getPreferredChartType() {
        return preferences.get("preferredChartType", "SCATTER_PLOT");
    }
}