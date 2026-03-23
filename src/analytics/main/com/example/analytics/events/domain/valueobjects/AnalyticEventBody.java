package com.example.analytics.events.domain.valueobjects;

import java.io.Serializable;
import java.util.Map;

public record AnalyticEventBody(Map<String, Serializable> value) {
}
