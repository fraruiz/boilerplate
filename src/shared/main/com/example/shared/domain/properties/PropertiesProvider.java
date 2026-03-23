package com.example.shared.domain.properties;

public interface PropertiesProvider {
    String execute(String key);
    String execute(String key, String defaultValue);
}
