package com.example.shared.domain.logs;

import java.io.Serializable;
import java.util.Map;

public interface Logger {
    void info(String message);
    void info(String message, Map<String, Serializable> context);

    void warning(String message);
    void warning(String message, Map<String, Serializable> context);

    void critical(String message);
    void critical(String message, Map<String, Serializable> context);
}
