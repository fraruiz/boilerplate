package com.example.analytics.events.domain;

import com.example.analytics.events.domain.valueobjects.AnalyticEventName;
import com.example.shared.domain.valueobjects.StringValueObjectMother;

public final class AnalyticEventNameMother {
    public static AnalyticEventName random() {
        return new AnalyticEventName(StringValueObjectMother.random());
    }
}
