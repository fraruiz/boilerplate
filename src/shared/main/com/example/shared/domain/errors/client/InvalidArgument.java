package com.example.shared.domain.errors.client;

import com.example.shared.domain.errors.Error;

public class InvalidArgument extends Error {
    private static final String ERROR = "invalid_argument";

    public InvalidArgument(String message) {
        super(message, ERROR);
    }

    @Override
    public Integer code() {
        return 400;
    }
}
