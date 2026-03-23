package com.example.shared.domain.errors.client;

import com.example.shared.domain.errors.Error;

public final class MethodNotAllowed extends Error {
    private static final String ERROR = "method_not_allowed";

    public MethodNotAllowed(String message) {
        super(message, ERROR);
    }

    @Override
    public Integer code() {
        return 405;
    }
}

