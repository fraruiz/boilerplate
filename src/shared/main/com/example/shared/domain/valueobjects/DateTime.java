package com.example.shared.domain.valueobjects;

import com.example.shared.domain.errors.client.InvalidArgument;

import java.time.OffsetDateTime;

public record DateTime(OffsetDateTime value) {
    public DateTime {
        if (value == null) {
            throw new InvalidArgument("created_at cannot be null");
        }
    }
}
