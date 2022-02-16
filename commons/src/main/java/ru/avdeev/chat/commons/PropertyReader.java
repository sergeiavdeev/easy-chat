package ru.avdeev.chat.commons;

import java.io.InputStream;
import java.util.Properties;

public class PropertyReader {

    private static PropertyReader instance;
    private Properties properties;

    private PropertyReader() {

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            properties = new Properties();
            properties.load(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static PropertyReader getInstance() {

        if (instance == null) {
            instance = new PropertyReader();
        }
        return instance;
    }

    public String get(String key) {
        return properties.getProperty(key);
    }
}
