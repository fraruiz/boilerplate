package com.example.shared.domain.valueobjects;

import java.util.UUID;

public class StringValueObjectMother {
    public static String random() {
        return UUID.randomUUID().toString();
    }
}
