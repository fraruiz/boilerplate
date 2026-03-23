package com.example.shared.domain.errors.client;

import com.example.shared.domain.errors.Error;

public class NotFound extends Error {
    private static final String ERROR = "not_found";

    public NotFound(String message) {
        super(message, ERROR);
    }

    @Override
    public Integer code() {
        return 404;
    }
}
