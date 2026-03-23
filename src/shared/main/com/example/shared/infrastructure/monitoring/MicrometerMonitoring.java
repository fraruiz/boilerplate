package com.example.shared.infrastructure.monitoring;

import com.example.shared.domain.monitoring.Monitoring;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public final class MicrometerMonitoring implements Monitoring {
    private final MeterRegistry registry;
    private final ConcurrentHashMap<String, AtomicLong> gaugeValues = new ConcurrentHashMap<>();

    public MicrometerMonitoring(MeterRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void incrementCounter(String aspect) {
        registry.counter(aspect).increment();
    }

    @Override
    public void incrementCounter(String aspect, Map<String, Serializable> context) {
        registry.counter(aspect, toTags(context)).increment();
    }

    @Override
    public void incrementGauge(String aspect) {
        getOrRegisterGauge(aspect, Tags.empty()).incrementAndGet();
    }

    @Override
    public void incrementGauge(String aspect, Map<String, Serializable> context) {
        getOrRegisterGauge(aspect, toTags(context)).incrementAndGet();
    }

    @Override
    public void decrementGauge(String aspect) {
        getOrRegisterGauge(aspect, Tags.empty()).decrementAndGet();
    }

    @Override
    public void decrementGauge(String aspect, Map<String, Serializable> context) {
        getOrRegisterGauge(aspect, toTags(context)).decrementAndGet();
    }

    private AtomicLong getOrRegisterGauge(String aspect, Tags tags) {
        String key = aspect + tags;
        return gaugeValues.computeIfAbsent(key, k -> {
            AtomicLong value = new AtomicLong(0);
            Gauge.builder(aspect, value, AtomicLong::doubleValue)
                .tags(tags)
                .register(registry);
            return value;
        });
    }

    private Tags toTags(Map<String, Serializable> context) {
        return Tags.of(context.entrySet().stream()
            .map(e -> Tag.of(e.getKey(), String.valueOf(e.getValue())))
            .toList());
    }
}
