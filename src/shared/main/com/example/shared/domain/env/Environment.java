package com.example.shared.domain.env;

public interface Environment {
    String execute(String key);
    String execute(String key, String defaultValue);
}
