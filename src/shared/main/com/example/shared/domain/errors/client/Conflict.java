package com.example.shared.domain.errors.client;

import com.example.shared.domain.errors.Error;

public class Conflict extends Error {
    private static final String ERROR = "conflict";

    public Conflict(String message) {
        super(message, ERROR);
    }

    @Override
    public Integer code() {
        return 409;
    }
}
