package com.example.shared.infrastructure.monitoring;

import com.example.shared.domain.monitoring.Monitoring;

import java.io.Serializable;
import java.util.Map;

public final class DataDogMonitoring implements Monitoring {

    @Override
    public void incrementCounter(String aspect) {

    }

    @Override
    public void incrementCounter(String aspect, Map<String, Serializable> context) {

    }

    @Override
    public void incrementGauge(String aspect) {

    }

    @Override
    public void incrementGauge(String aspect, Map<String, Serializable> context) {

    }

    @Override
    public void decrementGauge(String aspect) {

    }

    @Override
    public void decrementGauge(String aspect, Map<String, Serializable> context) {

    }
}
