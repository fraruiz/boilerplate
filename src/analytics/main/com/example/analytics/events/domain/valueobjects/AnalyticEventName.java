package com.example.analytics.events.domain.valueobjects;

import com.example.shared.domain.valueobjects.StringValueObject;

public final class AnalyticEventName extends StringValueObject {
    public AnalyticEventName(String value) {
        super(value);

        super.ensureValueIsNotBlank("event name can not be null");
    }
}
