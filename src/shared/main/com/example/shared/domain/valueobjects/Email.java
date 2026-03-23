package com.example.shared.domain.valueobjects;

import com.example.shared.domain.errors.client.InvalidArgument;

import java.util.regex.Pattern;

public final class Email extends StringValueObject {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";

    public Email(String value) {
        super(value);
        if (!Pattern.compile(EMAIL_REGEX).matcher(value).matches()) {
            throw new InvalidArgument("Invalid email format");
        }
    }
}