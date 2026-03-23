package com.example.shared.domain.date;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public interface DateProvider {
    LocalDate dateNow();

    LocalDateTime dateTimeNow();

    OffsetDateTime offsetDateTimeNow();
}
