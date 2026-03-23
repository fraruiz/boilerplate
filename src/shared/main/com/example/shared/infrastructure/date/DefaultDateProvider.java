package com.example.shared.infrastructure.date;

import com.example.shared.domain.date.DateProvider;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public final class DefaultDateProvider implements DateProvider {
    @Override
    public LocalDate dateNow() {
        return LocalDate.now();
    }

    @Override
    public LocalDateTime dateTimeNow() {
        return LocalDateTime.now();
    }

    @Override
    public OffsetDateTime offsetDateTimeNow() {
        return OffsetDateTime.now();
    }
}
