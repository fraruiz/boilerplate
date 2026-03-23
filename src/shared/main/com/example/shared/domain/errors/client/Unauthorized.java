package com.example.shared.domain.errors.client;

import com.example.shared.domain.errors.Error;

public class Unauthorized extends Error {
    private static final String ERROR = "unauthorized";

    public Unauthorized(String message) {
        super(message, ERROR);
    }

    @Override
    public Integer code() {
        return 401;
    }
}
