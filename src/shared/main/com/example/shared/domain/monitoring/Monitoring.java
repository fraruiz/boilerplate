package com.example.shared.domain.monitoring;

import java.io.Serializable;
import java.util.Map;

public interface Monitoring {
    void incrementCounter(String aspect);
    void incrementCounter(String aspect, Map<String, Serializable> context);

    void incrementGauge(String aspect);
    void incrementGauge(String aspect, Map<String, Serializable> context);

    void decrementGauge(String aspect);
    void decrementGauge(String aspect, Map<String, Serializable> context);
}
