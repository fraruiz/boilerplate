package com.example.shared.domain.valueobjects;

import java.util.UUID;

public final class UrlMother {
    private UrlMother() {
    }

    public static Url from(String value) {
        return new Url(value);
    }

    public static Url random() {
        return new Url("https://example.com/" + UUID.randomUUID());
    }
}

