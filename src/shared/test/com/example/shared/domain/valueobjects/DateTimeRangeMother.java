package com.example.shared.domain.valueobjects;

import java.time.OffsetDateTime;

public final class DateTimeRangeMother {
    private DateTimeRangeMother() {
    }

    public static DateTimeRange between(OffsetDateTime start, OffsetDateTime end) {
        return new DateTimeRange(start, end);
    }

    public static DateTimeRange aroundNow() {
        OffsetDateTime start = OffsetDateTime.now().minusDays(1);
        OffsetDateTime end = OffsetDateTime.now().plusDays(1);
        return new DateTimeRange(start, end);
    }
}

