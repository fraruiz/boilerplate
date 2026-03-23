package com.example.shared.domain.valueobjects;

import java.time.OffsetDateTime;

public final class DateTimeMother {
    private DateTimeMother() {
    }

    public static DateTime now() {
        return new DateTime(OffsetDateTime.now());
    }

    public static DateTime from(OffsetDateTime value) {
        return new DateTime(value);
    }
}

