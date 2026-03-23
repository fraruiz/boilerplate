package com.example.shared.domain.errors.client;

import com.example.shared.domain.errors.Error;

public class Forbidden extends Error {
    private static final String ERROR = "forbidden";

    public Forbidden(String message) {
        super(message, ERROR);
    }

    @Override
    public Integer code() {
        return 403;
    }
}
