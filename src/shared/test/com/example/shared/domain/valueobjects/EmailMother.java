package com.example.shared.domain.valueobjects;

import java.util.UUID;

public final class EmailMother {
    private EmailMother() {
    }

    public static Email from(String value) {
        return new Email(value);
    }

    public static Email random() {
        String localPart = "user" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        return new Email(localPart + "@example.com");
    }
}

