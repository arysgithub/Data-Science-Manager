package org.example.courseworkManager;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class JsonImporter {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static List<Map<String, Object>> importJson(File file) throws IOException {
        return mapper.readValue(file, new TypeReference<List<Map<String, Object>>>() {});
    }

    public static List<Map<String, Object>> importJsonString(String jsonContent) throws IOException {
        return mapper.readValue(jsonContent, new TypeReference<List<Map<String, Object>>>() {});
    }
}