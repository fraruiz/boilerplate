package com.example.shared.domain.valueobjects;

import com.github.javafaker.Faker;

public class StringValueObjectMother {
    public static String random() {
        return Faker.instance().app().name();
    }
}
