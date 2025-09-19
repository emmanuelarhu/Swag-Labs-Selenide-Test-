package com.swaglabs.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Utility class for reading test data from JSON files
 */
public class TestDataReader {
    private static final Logger logger = LoggerFactory.getLogger(TestDataReader.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Reads JSON data from resources and returns as JsonNode
     *
     * @param fileName name of the JSON file in resources
     * @return JsonNode containing the data
     */
    public static JsonNode readJsonData(String fileName) {
        try (InputStream inputStream = TestDataReader.class.getClassLoader().getResourceAsStream(fileName)) {
            if (inputStream == null) {
                logger.error("File not found: {}", fileName);
                throw new RuntimeException("Test data file not found: " + fileName);
            }

            JsonNode jsonNode = objectMapper.readTree(inputStream);
            logger.info("Successfully read JSON data from: {}", fileName);
            return jsonNode;

        } catch (IOException e) {
            logger.error("Error reading JSON file {}: {}", fileName, e.getMessage());
            throw new RuntimeException("Error reading test data file: " + fileName, e);
        }
    }

    /**
     * Reads JSON data and converts to Map
     *
     * @param fileName name of the JSON file
     * @return Map containing the data
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> readJsonAsMap(String fileName) {
        try (InputStream inputStream = TestDataReader.class.getClassLoader().getResourceAsStream(fileName)) {
            if (inputStream == null) {
                logger.error("File not found: {}", fileName);
                throw new RuntimeException("Test data file not found: " + fileName);
            }

            Map<String, Object> data = objectMapper.readValue(inputStream, Map.class);
            logger.info("Successfully read JSON data as Map from: {}", fileName);
            return data;

        } catch (IOException e) {
            logger.error("Error reading JSON file as Map {}: {}", fileName, e.getMessage());
            throw new RuntimeException("Error reading test data file as Map: " + fileName, e);
        }
    }

    /**
     * Reads JSON data and converts to List
     *
     * @param fileName name of the JSON file
     * @return List containing the data
     */
    @SuppressWarnings("unchecked")
    public static List<Object> readJsonAsList(String fileName) {
        try (InputStream inputStream = TestDataReader.class.getClassLoader().getResourceAsStream(fileName)) {
            if (inputStream == null) {
                logger.error("File not found: {}", fileName);
                throw new RuntimeException("Test data file not found: " + fileName);
            }

            List<Object> data = objectMapper.readValue(inputStream, List.class);
            logger.info("Successfully read JSON data as List from: {}", fileName);
            return data;

        } catch (IOException e) {
            logger.error("Error reading JSON file as List {}: {}", fileName, e.getMessage());
            throw new RuntimeException("Error reading test data file as List: " + fileName, e);
        }
    }

    /**
     * Gets specific value from JSON data using path
     *
     * @param fileName name of the JSON file
     * @param path     dot-separated path to the value (e.g., "users.standard.username")
     * @return Object value at the specified path
     */
    public static Object getValueFromJson(String fileName, String path) {
        JsonNode jsonNode = readJsonData(fileName);
        String[] pathParts = path.split("\\.");

        JsonNode currentNode = jsonNode;
        for (String part : pathParts) {
            if (currentNode.has(part)) {
                currentNode = currentNode.get(part);
            } else {
                logger.warn("Path '{}' not found in JSON file: {}", path, fileName);
                return null;
            }
        }

        // Convert JsonNode to appropriate Java object
        if (currentNode.isTextual()) {
            return currentNode.asText();
        } else if (currentNode.isNumber()) {
            return currentNode.asDouble();
        } else if (currentNode.isBoolean()) {
            return currentNode.asBoolean();
        } else if (currentNode.isArray()) {
            List<Object> list = new ArrayList<>();
            for (JsonNode element : currentNode) {
                list.add(element.asText());
            }
            return list;
        }

        return currentNode.toString();
    }

    /**
     * Reads properties file and returns as Properties object
     *
     * @param fileName name of the properties file
     * @return Properties object containing the data
     */
    public static Properties readPropertiesFile(String fileName) {
        Properties properties = new Properties();

        try (InputStream inputStream = TestDataReader.class.getClassLoader().getResourceAsStream(fileName)) {
            if (inputStream == null) {
                logger.error("Properties file not found: {}", fileName);
                throw new RuntimeException("Properties file not found: " + fileName);
            }

            properties.load(inputStream);
            logger.info("Successfully read properties from: {}", fileName);
            return properties;

        } catch (IOException e) {
            logger.error("Error reading properties file {}: {}", fileName, e.getMessage());
            throw new RuntimeException("Error reading properties file: " + fileName, e);
        }
    }

    /**
     * Converts 2D array to Object array for TestNG DataProvider
     *
     * @param data 2D array of data
     * @return Object array suitable for TestNG
     */
    public static Object[][] convertToDataProviderFormat(Object[][] data) {
        if (data == null || data.length == 0) {
            logger.warn("Empty or null data provided for conversion");
            return new Object[0][0];
        }

        logger.info("Converting {} rows of data for TestNG DataProvider", data.length);
        return data;
    }

    /**
     * Reads CSV-like data from JSON and converts to DataProvider format
     *
     * @param fileName JSON file containing array of objects
     * @return Object array for TestNG DataProvider
     */
    public static Object[][] readJsonAsDataProvider(String fileName) {
        try {
            List<Object> jsonList = readJsonAsList(fileName);

            if (jsonList.isEmpty()) {
                logger.warn("Empty JSON array in file: {}", fileName);
                return new Object[0][0];
            }

            Object[][] dataProvider = new Object[jsonList.size()][];

            for (int i = 0; i < jsonList.size(); i++) {
                if (jsonList.get(i) instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> row = (Map<String, Object>) jsonList.get(i);
                    dataProvider[i] = row.values().toArray();
                } else if (jsonList.get(i) instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<Object> row = (List<Object>) jsonList.get(i);
                    dataProvider[i] = row.toArray();
                } else {
                    dataProvider[i] = new Object[]{jsonList.get(i)};
                }
            }

            logger.info("Successfully converted JSON to DataProvider format: {} rows", dataProvider.length);
            return dataProvider;

        } catch (Exception e) {
            logger.error("Error converting JSON to DataProvider format: {}", e.getMessage());
            throw new RuntimeException("Error converting JSON to DataProvider format", e);
        }
    }
}