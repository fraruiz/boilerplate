package com.example.shared.domain.valueobjects;

import com.example.shared.domain.errors.client.InvalidArgument;

public class Url extends StringValueObject {
    public Url(String value) {
        super(value);
        ensureIsValidUrl(value);
    }

    private void ensureIsValidUrl(String value) {
        try {
            new java.net.URI(value).toURL();
        } catch (Exception e) {
            throw new InvalidArgument("Invalid URL format: " + value);
        }
    }
}
